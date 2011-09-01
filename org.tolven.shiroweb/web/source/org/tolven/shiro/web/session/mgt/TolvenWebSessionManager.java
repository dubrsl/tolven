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
package org.tolven.shiro.web.session.mgt;

import java.io.Serializable;

import javax.naming.InitialContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.tolven.naming.TolvenContext;
import org.tolven.session.DefaultTolvenSessionFactory;
import org.tolven.session.SecretKeyThreadLocal;
import org.tolven.shiro.session.TolvenSessionDAO;

public class TolvenWebSessionManager extends DefaultWebSessionManager {

    private Logger logger = Logger.getLogger(TolvenWebSessionManager.class);

    public TolvenWebSessionManager() {
        updateSessionIdTemplateCookie();
        setSessionFactory(new DefaultTolvenSessionFactory());
        setSessionDAO(new TolvenSessionDAO());
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String extendedSessionId = (String) super.getSessionId(request, response);
        return SecretKeyThreadLocal.getSessionId(extendedSessionId);
    }

    @Override
    protected void onStart(Session session, SessionContext context) {
        super.onStart(session, context);
        HttpServletRequest request = WebUtils.getHttpRequest(context);
        HttpServletResponse response = WebUtils.getHttpResponse(context);
        //Remove cookie added by super class
        Cookie template = getSessionIdCookie();
        Cookie cookie = new SimpleCookie(template);
        cookie.removeFrom(request, response);
        /*
         * Now place the secret key in a cookie by combining it with the sessionId using a
         * two way algorithm
         */
        if (logger.isDebugEnabled()) {
            logger.debug("Creating secret key cookie for cookie template name: " + template.getName());
        }
        String sessionId = session.getId().toString();
        cookie.setValue(SecretKeyThreadLocal.getExtendedSessionId(sessionId, SecretKeyThreadLocal.get()));
        cookie.saveTo(request, response);
        if (logger.isDebugEnabled()) {
            logger.debug("Saved secret key cookie to response for session: " + sessionId);
        }
    }

    protected void updateSessionIdTemplateCookie() {
        Cookie cookie = getSessionIdCookie();
        TolvenContext tolvenContext = null;
        String jndiName = "tolvenContext";
        try {
            InitialContext ictx = new InitialContext();
            tolvenContext = (TolvenContext) ictx.lookup(jndiName);
        } catch (Exception ex) {
            throw new RuntimeException("Could not look up " + jndiName, ex);
        }
        if (tolvenContext.getSsoCookieName() != null) {
            cookie.setName(tolvenContext.getSsoCookieName());
        }
        if (tolvenContext.getSsoCookieDomain() != null) {
            cookie.setDomain(tolvenContext.getSsoCookieDomain());
        }
        if (tolvenContext.getSsoCookiePath() != null) {
            cookie.setPath(tolvenContext.getSsoCookiePath());
        }
        if (tolvenContext.getSsoCookieSecure() != null) {
            cookie.setSecure(Boolean.parseBoolean(tolvenContext.getSsoCookieSecure()));
        }
        if (tolvenContext.getSsoCookieMaxAge() != null) {
            cookie.setMaxAge(Integer.parseInt(tolvenContext.getSsoCookieMaxAge()));
        }
    }

}
