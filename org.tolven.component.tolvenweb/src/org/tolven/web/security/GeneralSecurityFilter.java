/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.web.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * When a user attempts to access a secure resource whose URL is matched by the GeneralSecurityFilter, the request is
 * intercepted and the following checks made:
 * 
 * @author Joseph Isaac
 */
public class GeneralSecurityFilter implements Filter {

    public static final String INVITATION_ID = "invitationId";
    public static final String ACCOUNT_ID = "accountId";
    public static final String TOLVENUSER = "tolvenUser";
    public static final String ACCOUNTUSER = "accountUser";
    public static final String ACCOUNTUSERS = "accountUsers";
    public static final String ACCOUNTUSER_ID = "accountUserId";
    public static final String PROPOSED_ACCOUNTUSER_ID = "proposedAccountUserId";
    public static final String PROPOSED_DEFAULT_ACCOUNT = "proposedDefaultAccount"; // true | false
    public static final String ACCOUNT_HOME = "accountHome";
    public static final String PROPOSED_ACCOUNT_HOME = "proposedAccountHome";
    public static final String TOLVENUSER_ID = "TolvenUserId";
    public static final String VESTIBULE_PASS = "vestibulePass"; // true | false
    public static final String VESTIBULE_REDIRECT = "vestibuleRedirect";
    public static final String USER_CONTEXT = "userContext";
    public static final String TOLVEN_RESOURCEBUNDLE = "tolvenResourceBundle";
    public static final String USER_KEYS_OPTIONAL = "tolven.security.user.keysOptional";

    @Resource
    private VestibuleProcessor vestibuleProcessor;

    private Logger logger = Logger.getLogger(GeneralSecurityFilter.class);

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        try {
            String userContext = (String) sessionWrapper.getAttribute(GeneralSecurityFilter.USER_CONTEXT);
            boolean inVestibule = userContext == null || "vestibule".equals(userContext);
            if (inVestibule) {
                boolean hasVestibulePass = "true".equals((String) sessionWrapper.getAttribute(VESTIBULE_PASS));
                if (hasVestibulePass) {
                    vestibuleProcessor.refreshVestibule(request);
                } else {
                    /*
                     * Register with vestibule to get a vestibule pass
                     */
                    String vestibuleRedirect = vestibuleProcessor.enterVestibule(request);
                    if (vestibuleRedirect != null) {
                        response.sendRedirect(request.getContextPath() + vestibuleRedirect);
                        return;
                    }
                }
            } else {
                vestibuleProcessor.refreshAccount(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            String message = "Security Exception for " + request.getRequestURI();
            String logoutURL = request.getScheme() + ":" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath();
            try {
                logger.info("LOGOUT: " + message);
                sessionWrapper.logout();
                logger.info(request.getUserPrincipal() + " logged out");
                response.sendRedirect(logoutURL);
            } catch (IOException e) {
                throw new RuntimeException("Could not redirect to: " + logoutURL, e);
            }
            throw new ServletException(message, ex);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("TOLVEN_PERF: " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();
        }
        chain.doFilter(servletRequest, servletResponse);
        if (logger.isDebugEnabled()) {
            logger.debug("TOLVEN_PERF: downstream: " + (System.currentTimeMillis() - start) + " " + request.getMethod() + " " + request.getRequestURI());
        }
    }

    public void destroy() {
    }

}