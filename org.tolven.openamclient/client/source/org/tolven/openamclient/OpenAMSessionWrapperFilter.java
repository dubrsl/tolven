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
 * @version $Id: OpenAMSessionWrapperFilter.java 1530 2011-06-30 09:43:03Z joe.isaac $
 */
package org.tolven.openamclient;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.tolven.session.OpenAMSessionWrapper;
import org.tolven.session.TolvenSessionWrapperThreadLocal;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;

/**
 * A Filter which places a TolvenSessionWrapper into TolvenSessionWrapperThreadLocal
 * 
 * @author Joseph Isaac
 *
 */
public class OpenAMSessionWrapperFilter implements Filter {

    private SSOTokenManager ssoTokenManager;

    private Logger logger = Logger.getLogger(OpenAMSessionWrapperFilter.class);

    private SSOTokenManager getTokenManager() {
        if (ssoTokenManager == null) {
            try {
                ssoTokenManager = SSOTokenManager.getInstance();
            } catch (SSOException ex) {
                throw new RuntimeException("Could not get an instance of SSOTokenManager", ex);
            }
        }
        return ssoTokenManager;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            SSOToken ssoToken = null;
            try {
                ssoToken = getTokenManager().createSSOToken(request);
                Enumeration<String> e = request.getAttributeNames();
                while (e.hasMoreElements()) {
                    String name = e.nextElement();
                    Object obj = request.getAttribute(name);
                    if (obj instanceof HashSet) {
                        HashSet<Object> h = (HashSet<Object>) obj;
                        if (!h.isEmpty()) {
                            Object obj2 = h.iterator().next();
                            if (obj2 instanceof String) {
                                ssoToken.setProperty(name, (String) obj2);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                /*
                 * OpenAM provides on easy means of knowing whether this request is a not enforced url or not
                 * So, we have to assume that the main AgentFilter let it get this far, and just not set a seesion
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug(ex.getMessage());
                }
            }
            TolvenSessionWrapperThreadLocal.set(new OpenAMSessionWrapper(ssoToken));
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            TolvenSessionWrapperThreadLocal.remove();
        }
    }

}
