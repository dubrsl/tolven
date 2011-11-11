/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Joseph Isaac
 */
package org.tolven.shiro.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.naming.InitialContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.tolven.exeption.TolvenSessionNotFoundException;
import org.tolven.naming.TolvenContext;
import org.tolven.session.SecretKeyThreadLocal;
import org.tolven.session.ShiroSessionWrapper;
import org.tolven.session.TolvenSessionWrapperThreadLocal;
import org.tolven.shiro.web.config.TolvenFilterChainResolverFactory;
import org.tolven.shiro.web.mgt.TolvenWebSecurityManager;

public class TolvenShiroFilter extends AbstractShiroFilter {

    private AesCipherService aesCipherService;

    private Logger logger = Logger.getLogger(TolvenShiroFilter.class);

    public TolvenShiroFilter() {
    }

    @Override
    public void init() throws Exception {
        setSecurityManager(new TolvenWebSecurityManager());
        TolvenFilterChainResolverFactory filterChainResolverFactory = new TolvenFilterChainResolverFactory();
        filterChainResolverFactory.setFilterConfig(getFilterConfig());
        FilterChainResolver resolver = filterChainResolverFactory.getInstance();
        setFilterChainResolver(resolver);
        if (logger.isDebugEnabled()) {
            logger.debug("Set FilterChainResolver: " + resolver.getClass());
        }
    }

    @Override
    protected void executeChain(ServletRequest request, ServletResponse response, FilterChain origChain) throws IOException, ServletException {
        try {
            TolvenSessionWrapperThreadLocal.set(new ShiroSessionWrapper());
            super.executeChain(request, response, origChain);
        } finally {
            TolvenSessionWrapperThreadLocal.remove();
        }
    }

    @Override
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain chain) throws ServletException, IOException {
        double beginNanoTime = System.nanoTime();
        UserTransaction ut = null;
        try {
            long start = 0;
            if (logger.isDebugEnabled()) {
                start = System.currentTimeMillis();
            }
            InitialContext ctx = new InitialContext();
            while (true) {
                ut = (UserTransaction) ctx.lookup("UserTransaction");
                if (ut.getStatus() == Status.STATUS_NO_TRANSACTION) {
                    ut.begin();
                    break;
                } else if (ut.getStatus() == Status.STATUS_ACTIVE) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("***** Transaction already active *******");
                    }
                    break;
                } else {
                    logger.error("***** Transaction not in a good state [" + ut.getStatus() + "] trying again *******");
                    ut.setRollbackOnly();
                    ut.rollback();
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("TOLVEN_PERF: " + (System.currentTimeMillis() - start));
                start = System.currentTimeMillis();
            }
            /*
             * Now let Shiro do the downstream work
            * NB: TolvenSessionWrapperThreadLocal is not set at this point since only after this filter
            * is the session guaranteed to have been created and ready for use in thread local
             */
            try {
                byte[] secretKey = getSecretKey(servletRequest, servletResponse);
                SecretKeyThreadLocal.set(secretKey);
                try {
                    super.doFilterInternal(servletRequest, servletResponse, chain);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    if (cause != null && cause instanceof TolvenSessionNotFoundException) {
                        logger.warn(cause.getMessage());
                        if (ut != null && (ut.getStatus() == Status.STATUS_ACTIVE)) {
                            //roll back any changes that might have occurred downstream of the filter and begin transaction again
                            logger.warn("rolling back transaction and beginning transaction again with SSO cookie removed");
                            ut.rollback();
                            ut.begin();
                        }
                        HttpServletRequest origRequest = (HttpServletRequest) servletRequest;
                        final String cookieTemplateName = getCookieTemplate().getName();
                        HttpServletRequest req = new HttpServletRequestWrapper(origRequest) {
                            public Cookie[] getCookies() {
                                Cookie[] origCookies = super.getCookies();
                                int origNumCookies = origCookies.length;
                                Cookie[] cookies = new Cookie[origNumCookies - 1];
                                int index = 0;
                                for (int i = 0; i < origNumCookies; i++) {
                                    Cookie origCookie = origCookies[i];
                                    if (!cookieTemplateName.equals(origCookie.getName())) {
                                        cookies[index] = origCookie;
                                        index++;
                                    }
                                }
                                return cookies;
                            }
                        };
                        HttpServletResponse resp = (HttpServletResponse) servletResponse;
                        super.doFilterInternal(req, resp, chain);
                    } else {
                        throw ex;
                    }
                }
            } finally {
                /*
                 * The only location guaranteed to ensure that the session secret key is removed from SecretKeyThreadLocal
                 */
                SecretKeyThreadLocal.remove();
            }
            if (true) {
                if (logger.isDebugEnabled()) {
                    logger.debug("TOLVEN_PERF: downstream: " + (System.currentTimeMillis() - start));
                }
            }
            if (ut != null) {
                if (ut.getStatus() == Status.STATUS_ACTIVE) {
                    if (logger.isDebugEnabled()) {
                        start = System.currentTimeMillis();
                    }
                    ut.commit();
                    if (logger.isDebugEnabled()) {
                        logger.debug("TOLVEN_PERF: commit: " + (System.currentTimeMillis() - start));
                    }
                } else
                    ut.rollback();
                // We need to put the web page commit *after* the database commit
                // otherwise it is possible that the web page can query for uncommitted work in the next interaction.
                // The situation actually occurs in underpowered configurations.
                Writer writer = (Writer) servletRequest.getAttribute("activeWriter");
                if (writer != null)
                    writer.close();
                ut = null;
            }
        } catch (Exception e) {
            try {
                if (ut != null && (ut.getStatus() == Status.STATUS_ACTIVE)) {
                    ut.rollback();
                    ut = null;
                }
            } catch (Exception e2) {
                logger.error("*************** !!!!!TRANSACTION ROLLBACK EXCEPTION!!!! *******************");
                throw new ServletException(e2);
            }
            Throwable cause = e.getCause();
            String message = "";
            if (cause != null) {
                message = cause.getMessage();
                logger.error(message);
            }
            throw new ServletException("Exception caught in Transaction: " + message, e);
        } finally {
            try {
                if (ut != null) {// && (ut.getStatus()==Status.STATUS_ACTIVE)
                    ut.rollback();
                    ut = null;
                }
                HttpSession session = ((HttpServletRequest) servletRequest).getSession(false);
                if (session != null) {
                    Double elapsed = (Double) session.getAttribute("elapsedMilli");
                    if (elapsed == null)
                        elapsed = 0.0;
                    elapsed += (System.nanoTime() - beginNanoTime) / 1000000;
                    session.setAttribute("elapsedMilli", elapsed);
                }
            } catch (Exception e) {
                logger.error("*************** !!!!!TRANSACTION ROLLBACK EXCEPTION!!!! *******************");
                throw new ServletException(e);
            }
        }
    }

    private AesCipherService getAesCipherService() {
        if (aesCipherService == null) {
            aesCipherService = new AesCipherService();
        }
        return aesCipherService;
    }

    private SimpleCookie getCookieTemplate() {
        TolvenContext tolvenContext = null;
        String jndiName = "tolvenContext";
        try {
            InitialContext ictx = new InitialContext();
            tolvenContext = (TolvenContext) ictx.lookup(jndiName);
        } catch (Exception ex) {
            throw new RuntimeException("Could not look up " + jndiName, ex);
        }
        SimpleCookie cookie = new SimpleCookie(tolvenContext.getSsoCookieName());
        cookie.setDomain(tolvenContext.getSsoCookieDomain());
        cookie.setPath(tolvenContext.getSsoCookiePath());
        cookie.setSecure(Boolean.parseBoolean(tolvenContext.getSsoCookieSecure()));
        cookie.setMaxAge(Integer.parseInt(tolvenContext.getSsoCookieMaxAge()));
        return cookie;
    }

    private byte[] getSecretKey(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String extendedSessionId = getCookieTemplate().readValue(httpRequest, httpResponse);
        byte[] secretKey = null;
        if (extendedSessionId == null) {
            secretKey = getAesCipherService().generateNewKey().getEncoded();
            if (logger.isDebugEnabled()) {
                logger.debug("Generated session secret key");
            }
        } else {
            secretKey = SecretKeyThreadLocal.getSecretKey(extendedSessionId);
        }
        return secretKey;
    }

}
