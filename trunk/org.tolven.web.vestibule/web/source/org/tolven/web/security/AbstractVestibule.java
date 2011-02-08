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
 * @version $Id$
 */
package org.tolven.web.security;

import java.security.Principal;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.tolven.core.ActivationLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;

public abstract class AbstractVestibule implements Vestibule {

    private InitialContext context;

    // This class is not eligible for EJB injection, and must look it up
    private ActivationLocal activationBean;

    Logger logger = Logger.getLogger(this.getClass());

    public String getName() {
        return getClass().getName();
    }

    protected InitialContext getContext() {
        if (context == null) {
            try {
                context = new InitialContext();
            } catch (NamingException ex) {
                throw new RuntimeException("Could not create an InitialContext", ex);
            }
        }
        return context;
    }

    protected ActivationLocal getActivationBean() {
        if (activationBean == null) {
            try {
                activationBean = (ActivationLocal) getContext().lookup("java:global/tolven/tolvenEJB/ActivationBean!org.tolven.core.ActivationLocal");
            } catch (NamingException ex) {
                throw new RuntimeException("Could not lookup java:global/tolven/tolvenEJB/ActivationBean!org.tolven.core.ActivationLocal", ex);
            }
        }
        return activationBean;
    }

    protected Object getSessionAttribute(String name, ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        return session.getAttribute(name);
    }

    protected void setSessionAttribute(String name, Object obj, ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        session.setAttribute(name, obj);
    }

    protected void removeSessionAttribute(String name, ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        session.removeAttribute(name);
    }

    protected Map<String, Object> getVestibuleContext(ServletRequest servletRequest) {
        Map<String, Object> contextMap = (Map<String, Object>) getSessionAttribute(getName(), servletRequest);
        if (contextMap == null) {
            contextMap = new HashMap<String, Object>();
            setSessionAttribute(getName(), contextMap, servletRequest);
        }
        return contextMap;
    }

    protected Long getSessionTolvenUserId(ServletRequest servletRequest) {
        return (Long) getSessionAttribute(GeneralSecurityFilter.TOLVENUSER_ID, servletRequest);
    }

    protected void setSessionTolvenUserId(Long tolvenUserId, ServletRequest servletRequest) {
        setSessionAttribute(GeneralSecurityFilter.TOLVENUSER_ID, tolvenUserId, servletRequest);
    }

    protected AccountUser findSessionAccountUser(ServletRequest servletRequest) {
        Long accountUserId = (Long) getSessionAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID, servletRequest);
        if (accountUserId == null) {
            return null;
        } else {
            return getActivationBean().findAccountUser(accountUserId);
        }
    }

    protected Long getSessionProposedAccounId(ServletRequest servletRequest) {
        return (Long) getSessionAttribute(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, servletRequest);
    }

    protected void setSessionProposedAccounId(Long proposedAccountUserId, ServletRequest servletRequest) {
        setSessionAttribute(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, proposedAccountUserId, servletRequest);
    }

    protected String getSessionProposedAccountHome(ServletRequest servletRequest) {
        return (String) getSessionAttribute(GeneralSecurityFilter.PROPOSED_ACCOUNT_HOME, servletRequest);
    }

    protected void setSessionProposedAccountHome(String proposedAccountHome, ServletRequest servletRequest) {
        setSessionAttribute(GeneralSecurityFilter.PROPOSED_ACCOUNT_HOME, proposedAccountHome, servletRequest);
    }

    protected Locale getSessionLocale(ServletRequest servletRequest) {
        return (Locale) getSessionAttribute(GeneralSecurityFilter.TOLVENLOCALE, servletRequest);
    }

    protected void setSessionLocale(Locale locale, ServletRequest servletRequest) {
        setSessionAttribute(GeneralSecurityFilter.TOLVENLOCALE, locale, servletRequest);
    }

    protected void removeSessionLocale(ServletRequest servletRequest) {
        removeSessionAttribute(GeneralSecurityFilter.TOLVENLOCALE, servletRequest);
    }

    protected Date getTolvenNow(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Date now = (Date) request.getAttribute(SecurityFilter.TOLVEN_NOW);
        if (now == null) {
            now = new Date();
            logger.info("Transaction filter missing, now not available. Using new Date()");
        }
        return now;
    }

    protected PrivateKey getUserPrivateKey(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        return (PrivateKey) request.getAttribute("userPrivateKey");
    }

    protected TolvenUser findPrincipalTolvenUser(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Principal principal = request.getUserPrincipal();
        return getActivationBean().findUser(principal.getName());
    }

    protected void loginUser(TolvenUser user, ServletRequest servletRequest) {
        getActivationBean().loginUser(user.getLdapUID(), getTolvenNow(servletRequest));
    }

    protected AccountUser findDefaultAccountUser(TolvenUser user) {
        return getActivationBean().findDefaultAccountUser(user);
    }

}
