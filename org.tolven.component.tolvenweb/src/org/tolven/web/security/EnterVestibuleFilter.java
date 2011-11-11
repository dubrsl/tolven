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

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenRequest;
import org.tolven.core.entity.TolvenUser;
import org.tolven.exeption.GatekeeperAuthenticationException;
import org.tolven.exeption.GatekeeperAuthorizationException;
import org.tolven.security.LoginLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * Filter checks that the user is in the vestibule and can proceed, or redirects out of the Vestibule when necessary
 * 
 * @author Joseph Isaac
 */
public class EnterVestibuleFilter extends AuthorizationFilter {

    @EJB
    private ActivationLocal activationBean;

    private Logger logger = Logger.getLogger(EnterVestibuleFilter.class);

    @EJB
    private LoginLocal loginBean;

    @Override
    protected void cleanup(ServletRequest request, ServletResponse response, Exception existing) throws ServletException, IOException {
        try {
            TolvenRequest.getInstance().clear();
        } finally {
            super.cleanup(request, response, existing);
        }
    }

    public void destroy() {
    }

    private ActivationLocal getActivationBean() {
        if (activationBean == null) {
            String jndiName = null;
            try {
                InitialContext ctx = new InitialContext();
                jndiName = "java:app/tolvenEJB/ActivationBean!org.tolven.core.ActivationLocal";
                if (logger.isDebugEnabled()) {
                    logger.debug("JNDI lookup: " + jndiName);
                }
                activationBean = (ActivationLocal) ctx.lookup(jndiName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found: " + jndiName);
                }
            } catch (NamingException e) {
                throw new RuntimeException("Failed to lookup " + jndiName, e);
            }
        }
        return activationBean;
    }

    private LoginLocal getLoginBean() {
        if (loginBean == null) {
            String jndiName = null;
            try {
                InitialContext ctx = new InitialContext();
                jndiName = "java:app/tolvenEJB/LoginBean!org.tolven.security.LoginLocal";
                if (logger.isDebugEnabled()) {
                    logger.debug("JNDI lookup: " + jndiName);
                }
                loginBean = (LoginLocal) ctx.lookup(jndiName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found: " + jndiName);
                }
            } catch (NamingException e) {
                throw new RuntimeException("Failed to lookup " + jndiName, e);
            }
        }
        return loginBean;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object chain) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        request.setAttribute(GeneralSecurityFilter.TIME_NOW, TolvenRequest.getInstance().getNow());
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        String principal = (String) sessionWrapper.getPrincipal();
        if (principal == null) {
            throw new GatekeeperAuthenticationException("Principal cannot be null in the Vestibule", principal, sessionWrapper.getRealm());
        }
        TolvenUser user = getActivationBean().findUser(principal);
        TolvenRequest tolvenRequest = TolvenRequest.getInstance();
        String userContext = (String) sessionWrapper.getAttribute(GeneralSecurityFilter.USER_CONTEXT);
        if (userContext == null) {
            logger.info("VESTIBULE_ENTERED: " + principal);
            if (user == null) {
                //Activate the user
                TolvenPerson tp = new TolvenPerson();
                tp.setUid(principal);
                user = getLoginBean().activate(tp, tolvenRequest.getNow());
            }
            if (user == null) {
                throw new RuntimeException("Could not activate user: " + principal);
            }
            getActivationBean().loginUser(user.getLdapUID(), tolvenRequest.getNow());
            sessionWrapper.setAttribute(GeneralSecurityFilter.USER_CONTEXT, "vestibule");
        }
        if (user == null) {
            throw new GatekeeperAuthorizationException("No TolvenUser found", principal, sessionWrapper.getRealm());
        }
        /*
         * Finally set the all clear for the user, by providing the necessary TolvenUserId
         */
        sessionWrapper.setAttribute(GeneralSecurityFilter.TOLVENUSER_ID, String.valueOf(user.getId()));
        /*
         * Initialize tolvenRequest with TolvenUser
         */
        tolvenRequest.initializeTolvenUser(user);
        request.setAttribute(GeneralSecurityFilter.TOLVENUSER, user); // backward compatibility only
        //
        return true;
    }

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        String redirect = (String) request.getAttribute(GeneralSecurityFilter.TOLVEN_REDIRECT);
        if (StringUtils.hasText(redirect)) {
            WebUtils.issueRedirect(request, response, redirect);
        } else {
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return false;
    }

}