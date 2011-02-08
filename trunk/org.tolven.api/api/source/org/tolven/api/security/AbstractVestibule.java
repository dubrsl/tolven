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
package org.tolven.api.security;

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
import javax.ws.rs.core.NewCookie;

import org.apache.log4j.Logger;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;

import com.iplanet.sso.SSOToken;

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

    protected Long getSessionTolvenUserId(ServletRequest servletRequest) {
        String idString = getSessionProperty(GeneralSecurityFilter.TOLVENUSER_ID, servletRequest);
        if (idString == null || idString.length() == 0) {
            return null;
        } else {
            return Long.parseLong(idString);
        }
    }

    protected void setSessionTolvenUserId(Long tolvenUserId, ServletRequest servletRequest) {
        setSessionProperty(GeneralSecurityFilter.TOLVENUSER_ID, String.valueOf(tolvenUserId), servletRequest);
    }

    protected Long getSessionProposedAccountUserId(ServletRequest servletRequest) {
        String idString = getSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, servletRequest);
        if (idString == null || idString.length() == 0) {
            return null;
        } else {
            return Long.parseLong(idString);
        }
    }

    protected void removeSessionProposedAccountUserId(ServletRequest servletRequest) {
        removeSessionAttribute(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, servletRequest);
    }

    protected void setSessionProposedAccountUserId(Long proposedAccountUserId, ServletRequest servletRequest) {
        setSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, String.valueOf(proposedAccountUserId), servletRequest);
    }

    protected PrivateKey getUserPrivateKey(ServletRequest servletRequest) {
        String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        return TolvenSSO.getInstance().getUserPrivateKey((HttpServletRequest) servletRequest, keyAlgorithm);
    }

    protected X509Certificate getUserX509Certificate(ServletRequest servletRequest) {
        return TolvenSSO.getInstance().getUserX509Certificate((HttpServletRequest) servletRequest);
    }

    protected PublicKey getUserPublicKey(ServletRequest servletRequest) {
        return TolvenSSO.getInstance().getUserPublicKey((HttpServletRequest) servletRequest);
    }
    
    protected TolvenUser findPrincipalTolvenUser(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Principal principal = request.getUserPrincipal();
        return activationBean.findUser(principal.getName());
    }

    protected AccountUser findDefaultAccountUser(TolvenUser user) {
        return activationBean.findDefaultAccountUser(user);
    }

    protected SSOToken getSSOToken(ServletRequest servletRequest) {
        return TolvenSSO.getInstance().getSSOToken((HttpServletRequest) servletRequest);
    }

    protected String getSessionProperty(String name, ServletRequest servletRequest) {
        return TolvenSSO.getInstance().getSessionProperty(name, getSSOToken(servletRequest));
    }

    protected void setSessionProperty(String name, String value, ServletRequest servletRequest) {
        TolvenSSO.getInstance().setSessionProperty(name, value, getSSOToken(servletRequest));
    }

    protected void removeSessionProperty(String name, ServletRequest servletRequest) {
        TolvenSSO.getInstance().setSessionProperty(name, null, getSSOToken(servletRequest));
    }

    protected NewCookie getSSOCookie(ServletRequest servletRequest) {
        return TolvenSSO.getInstance().getSSOCookie(getSSOToken(servletRequest));
    }

}
