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
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.exeption.GatekeeperAuthenticationException;
import org.tolven.exeption.GatekeeperAuthorizationException;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * Filter checks that the user is in the vestibule and can proceed, or redirects out of the Account when necessary
 * 
 * @author Joseph Isaac
 */
public class AccountFilter extends AuthorizationFilter {

    @EJB
    private ActivationLocal activationBean;

    private Logger logger = Logger.getLogger(AccountFilter.class);

    public void destroy() {
    }

    @Override
    protected void cleanup(ServletRequest request, ServletResponse response, Exception existing) throws ServletException, IOException {
        try {
            TolvenRequest.getInstance().clear();
        } finally {
            super.cleanup(request, response, existing);
        }
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

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object chain) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        request.setAttribute(GeneralSecurityFilter.TIME_NOW, TolvenRequest.getInstance().getNow());
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        String principal = (String) sessionWrapper.getPrincipal();
        if (principal == null) {
            throw new GatekeeperAuthenticationException("Principal cannot be null in the Vestibule", principal, sessionWrapper.getRealm());
        }
        String userContext = (String) sessionWrapper.getAttribute(GeneralSecurityFilter.USER_CONTEXT);
        String redirect = null;
        if (userContext == null) {
            throw new GatekeeperAuthorizationException("userContext has not been set when vestibule was first entered", principal, sessionWrapper.getRealm());
        } else if ("vestibule".equals(userContext)) {
            //The vestibule must be exited before an account URL can be processed
            //TODO This should be configurable, based on userContext
            redirect = "/vestibule/selectAccount.jsf";
        } else if ("account".equals(userContext)) {
            String accountUserIdString = (String) sessionWrapper.getAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID);
            if (accountUserIdString == null) {
                throw new GatekeeperAuthorizationException("No accountUser Id found in session", principal, sessionWrapper.getRealm());
            }
            AccountUser accountUser = getActivationBean().findAccountUser(Long.parseLong(accountUserIdString));
            if (accountUser == null) {
                throw new GatekeeperAuthorizationException("Could not find accountUser " + accountUserIdString, principal, sessionWrapper.getRealm());
            }
            TolvenUser user = getActivationBean().findUser(principal);
            if (user == null) {
                throw new GatekeeperAuthorizationException("No TolvenUser found", principal, sessionWrapper.getRealm());
            }
            if (accountUser.getUser().getId() != user.getId()) {
                throw new GatekeeperAuthorizationException("Tolven User Id: " + user.getId() + " not authorized to use account: " + accountUser.getAccount().getId(), principal, sessionWrapper.getRealm());
            }
            TolvenRequest tolvenRequest = TolvenRequest.getInstance();
            /*
             * Initialize tolvenRequest with TolvenUser and AccountUser
             */
            tolvenRequest.initializeTolvenUser(user);
            tolvenRequest.initializeAccountUser(accountUser);
            //
            request.setAttribute(GeneralSecurityFilter.TOLVENUSER, user); //backward compatibility
            request.setAttribute(GeneralSecurityFilter.ACCOUNTUSER, accountUser); //backward compatibility
            //The accountUser Id is required only for legacy, since the accountUser is in the request
            request.setAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID, accountUser.getId());
        } else {
            throw new RuntimeException("Unrecognized userContext: " + userContext);
        }
        if (redirect == null) {
            return true;
        } else {
            request.setAttribute(GeneralSecurityFilter.TOLVEN_REDIRECT, redirect);
            return false;
        }
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