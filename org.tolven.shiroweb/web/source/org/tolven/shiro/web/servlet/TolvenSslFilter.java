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

import org.apache.shiro.web.filter.authz.SslFilter;
import org.tolven.naming.TolvenContext;
import org.tolven.naming.WebContext;

public class TolvenSslFilter extends SslFilter {

    public TolvenSslFilter() {
    }

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        try {
            InitialContext ictx = new InitialContext();
            TolvenContext tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
            String webContextId = (String) ictx.lookup("java:comp/env/webContextId");
            WebContext webContext = (WebContext) tolvenContext.getWebContext(webContextId);
            String sslPort = webContext.getSslPort();
            if (sslPort == null) {
                throw new RuntimeException("No WebContext sslPort found for contextPath: " + webContextId);
            }
            setPort(Integer.parseInt(sslPort));
            return super.onPreHandle(request, response, mappedValue);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get sslPort", ex);
        }
    }

}
