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
 * @version $Id: AbstractVestibule.java 1312 2011-06-11 05:11:10Z joe.isaac $
 */
package org.tolven.web.security;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;

public abstract class AbstractVestibule implements Vestibule {

    @EJB
    private ActivationLocal activationBean;

    @EJB
    private TolvenPropertiesLocal propertyBean;

    Logger logger = Logger.getLogger(this.getClass());

    public String getName() {
        return getClass().getName();
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

    protected Long getSessionTolvenUserId() {
        String idString = getSessionProperty(GeneralSecurityFilter.TOLVENUSER_ID);
        if (idString == null || idString.length() == 0) {
            return null;
        } else {
            return Long.parseLong(idString);
        }
    }

    protected void setSessionTolvenUserId(Long tolvenUserId) {
        setSessionProperty(GeneralSecurityFilter.TOLVENUSER_ID, String.valueOf(tolvenUserId));
    }

    protected Long getSessionProposedAccountUserId() {
        String idString = getSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID);
        if (idString == null || idString.length() == 0) {
            return null;
        } else {
            return Long.parseLong(idString);
        }
    }

    protected void removeSessionProposedAccountUserId(ServletRequest servletRequest) {
        removeSessionAttribute(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, servletRequest);
    }

    protected void setSessionProposedAccountUserId(Long proposedAccountUserId) {
        setSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, String.valueOf(proposedAccountUserId));
    }

    protected PrivateKey getUserPrivateKey() {
        String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        return sessionWrapper.getUserPrivateKey(keyAlgorithm);
    }

    protected X509Certificate getUserX509Certificate() {
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        return sessionWrapper.getUserX509Certificate();
    }

    protected PublicKey getUserPublicKey() {
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        return sessionWrapper.getUserPublicKey();
    }

    protected TolvenUser findPrincipalTolvenUser(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Principal principal = request.getUserPrincipal();
        return activationBean.findUser(principal.getName());
    }

    protected AccountUser findDefaultAccountUser(TolvenUser user) {
        return activationBean.findDefaultAccountUser(user);
    }

    protected String getSessionProperty(String name) {
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        return (String) sessionWrapper.getAttribute(name);
    }

    protected void setSessionProperty(String name, String value) {
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        sessionWrapper.setAttribute(name, value);
    }

    protected void removeSessionProperty(String name) {
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        sessionWrapper.removeAttribute(name);
    }

}
