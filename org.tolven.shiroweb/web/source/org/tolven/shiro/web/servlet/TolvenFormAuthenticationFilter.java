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

import javax.naming.InitialContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.tolven.naming.TolvenContext;
import org.tolven.naming.WebContext;
import org.tolven.shiro.authc.UsernamePasswordRealmToken;

/**
 * This filter allows separate INI configuration for a FormAuthenticationFilter
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenFormAuthenticationFilter extends FormAuthenticationFilter {

    public static final String DEFAULT_REALM_PARAM = "realm";

    private String realmParam = DEFAULT_REALM_PARAM;

    public TolvenFormAuthenticationFilter() {
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        String realm = getRealm(request);
        boolean rememberMe = false; //isRememberMe(request);
        String host = getHost(request);
        return new UsernamePasswordRealmToken(username, password, realm, rememberMe, host);
    }

    protected String getRealm(ServletRequest request) {
        return WebUtils.getCleanParam(request, getRealmParam());
    }

    public String getRealmParam() {
        return realmParam;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        super.onLoginFailure(token, e, request, response);
        try {
            WebUtils.issueRedirect(request, response, "/public/loginFailed.jsp");
        } catch (Exception ex) {
            throw new RuntimeException("Could not redirect to loginFailed.jsp", ex);
        }
        return false;
    }

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        try {
            InitialContext ictx = new InitialContext();
            TolvenContext tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
            String webContextId = (String) ictx.lookup("java:comp/env/webContextId");
            WebContext webContext = (WebContext) tolvenContext.getWebContext(webContextId);
            String loginPath = webContext.getHtmlLoginPath();
            if (loginPath == null) {
                throw new RuntimeException("No WebContext loginUrl found for contextPath: " + webContextId);
            }
            setLoginUrl(loginPath);
            return super.onPreHandle(request, response, mappedValue);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get loginUrl", ex);
        }
    }

    public void setRealmParam(String realmParam) {
        this.realmParam = realmParam;
    }

}
