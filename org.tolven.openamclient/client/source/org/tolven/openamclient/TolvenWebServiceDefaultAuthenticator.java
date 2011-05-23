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
 * @version $Id: TolvenWebServiceDefaultAuthenticator.java 389 2011-03-20 15:37:44Z joe.isaac $
 */
package org.tolven.openamclient;

import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.filter.AmFilterRequestContext;
import com.sun.identity.agents.filter.WebServiceDefaultAuthenticator;

public class TolvenWebServiceDefaultAuthenticator extends WebServiceDefaultAuthenticator {

    public TolvenWebServiceDefaultAuthenticator() throws AgentException {
        super();
    }

    public SSOToken getUserToken(HttpServletRequest request, String requestMessage, String remoteAddress, String remoteHost, AmFilterRequestContext ctx) {
        SSOToken currentToken = null;
        try {
            currentToken = SSOTokenManager.getInstance().createSSOToken(request);
            boolean isValid = SSOTokenManager.getInstance().isValidToken(currentToken);
            if (!isValid) {
                throw new RuntimeException("SSOToken is not valid");
            }
        } catch (Exception ex) {
            currentToken = null;
            //would have use hasValidToken(request) if SSOTokenManager had it
        }
        SSOToken validationToken = super.getUserToken(request, requestMessage, remoteAddress, remoteHost, ctx);
        if (currentToken == null) {
            currentToken = validationToken;
            HttpServletResponse response = ctx.getHttpServletResponse();
            Cookie cookie = null;
            try {
                cookie = new Cookie("iPlanetDirectoryPro", URLEncoder.encode(currentToken.getTokenID().toString(), "UTF-8"));
            } catch (Exception ex) {
                throw new RuntimeException("Could not set SSOToken cookie in HttpServletResponse", ex);
            }
            response.addCookie(cookie);
        } else {
            try {
                SSOTokenManager.getInstance().destroyToken(validationToken);
            } catch (Exception ex) {
                throw new RuntimeException("Could not invalidate SSOToken", ex);
            }
        }
        return currentToken;
    }
}
