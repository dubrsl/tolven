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
import java.security.PrivateKey;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.tolven.app.MenuLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.TolvenRequest;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.exeption.GatekeeperAuthenticationException;
import org.tolven.exeption.GatekeeperAuthorizationException;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * Filter checks that the user is in the vestibule and can proceed, or redirects out of the Vestibule when necessary
 * 
 * @author Joseph Isaac
 */
public class ExitVestibuleFilter extends AuthorizationFilter {

    @EJB
    private ActivationLocal activationBean;

    private Logger logger = Logger.getLogger(ExitVestibuleFilter.class);

    @EJB
    private MenuLocal menuBean;

    @EJB
    private TolvenPropertiesLocal propertyBean;

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

    private MenuLocal getMenuBean() {
        if (menuBean == null) {
            String jndiName = null;
            try {
                InitialContext ctx = new InitialContext();
                jndiName = "java:app/tolvenEJB/MenuBean!org.tolven.app.MenuLocal";
                if (logger.isDebugEnabled()) {
                    logger.debug("JNDI lookup: " + jndiName);
                }
                menuBean = (MenuLocal) ctx.lookup(jndiName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found: " + jndiName);
                }
            } catch (NamingException e) {
                throw new RuntimeException("Failed to lookup " + jndiName, e);
            }
        }
        return menuBean;
    }

    private TolvenPropertiesLocal getPropertyBean() {
        if (propertyBean == null) {
            String jndiName = null;
            try {
                InitialContext ctx = new InitialContext();
                jndiName = "java:app/tolvenEJB/TolvenProperties!org.tolven.core.TolvenPropertiesLocal";
                if (logger.isDebugEnabled()) {
                    logger.debug("JNDI lookup: " + jndiName);
                }
                propertyBean = (TolvenPropertiesLocal) ctx.lookup(jndiName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found: " + jndiName);
                }
            } catch (NamingException e) {
                throw new RuntimeException("Failed to lookup " + jndiName, e);
            }
        }
        return propertyBean;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object chain) throws Exception {
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        String principal = (String) sessionWrapper.getPrincipal();
        if (principal == null) {
            throw new GatekeeperAuthenticationException("Principal cannot be null in the Vestibule", principal, sessionWrapper.getRealm());
        }
        String userContext = (String) sessionWrapper.getAttribute(GeneralSecurityFilter.USER_CONTEXT);
        String redirect = null;
        if (userContext == null) {
            throw new RuntimeException("userContext has not been set when vestibule was first entered");
        } else if ("account".equals(userContext)) {
            //An account must be exited before a vestibule URL can be processed
            //TODO This should be configurable, based on userContext
            redirect = "/private/application.jsf";
        } else if ("vestibule".equals(userContext)) {
            String proposedAccountUserIdString = (String) sessionWrapper.getAttribute(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID);
            if (proposedAccountUserIdString == null) {
                //No exit functionality will be executed
            } else {
                Long accountUserId = Long.parseLong(proposedAccountUserIdString);
                AccountUser accountUser = getActivationBean().findAccountUser(accountUserId);
                if (accountUser == null) {
                    throw new GatekeeperAuthorizationException("accountUser does not exist with Id: " + accountUserId, principal, sessionWrapper.getRealm());
                }
                String userKeysOptional = getPropertyBean().getProperty(GeneralSecurityFilter.USER_KEYS_OPTIONAL);
                if (!Boolean.parseBoolean(userKeysOptional)) {
                    String keyAlgorithm = getPropertyBean().getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
                    PrivateKey privateKey = sessionWrapper.getUserPrivateKey(keyAlgorithm);
                    if (privateKey == null) {
                        throw new GatekeeperAuthorizationException("User requires a UserPrivateKey to log into account: " + accountUser.getAccount().getId(), principal, sessionWrapper.getRealm());
                    }
                }
                // Save ACCOUNTUSER in session for subsequent request so the security filters can intercept appropriately
                sessionWrapper.setAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID, String.valueOf(accountUser.getId()));
                sessionWrapper.setAttribute(GeneralSecurityFilter.ACCOUNT_ID, String.valueOf(accountUser.getAccount().getId()));
                sessionWrapper.removeAttribute(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID);
                //SAFETY CHECK HERE - Don't trust the accountUserId alone, it must match user.
                TolvenUser user = getActivationBean().findUser(principal);
                if (accountUser.getUser().getId() != user.getId()) {
                    throw new GatekeeperAuthorizationException("User does not belong to account: " + accountUser.getId(), principal, sessionWrapper.getRealm());
                }
                // Record the time when the user logged into this particular account
                TolvenRequest tolvenRequest = TolvenRequest.getInstance();
                accountUser.setLastLoginTime(tolvenRequest.getNow());
                String rememberDefaultAccount = (String) sessionWrapper.getAttribute(GeneralSecurityFilter.REMEMBER_DEFAULT_ACCOUNT);
                if ("true".equals(rememberDefaultAccount)) {
                    getActivationBean().setDefaultAccountUser(accountUser);
                    sessionWrapper.removeAttribute(GeneralSecurityFilter.REMEMBER_DEFAULT_ACCOUNT);
                }
                Account account = accountUser.getAccount();
                if (account.isDisableAutoRefresh() == null || account.isDisableAutoRefresh() == false) {
                    getMenuBean().updateMenuStructure(account);
                }
                sessionWrapper.setAttribute(GeneralSecurityFilter.USER_CONTEXT, "account");
                logger.info("VESTIBULE_EXIT_TO_ACCOUNT: " + accountUser.getId());
                redirect = "/private/application.jsf";
            }
        } else {
            throw new RuntimeException("Unrecognized userContext: " + userContext);
        }
        if (redirect == null) {
            return true;
        } else {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
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