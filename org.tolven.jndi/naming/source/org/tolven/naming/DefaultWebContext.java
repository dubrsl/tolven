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
package org.tolven.naming;

import java.util.Properties;

public class DefaultWebContext extends DefaultTolvenJndiContext implements WebContext {

    public static final String CONTEXTPATH_SUFFIX = ".web.contextPath";
    public static final String HTML_LOGINPATH_SUFFIX = ".web.html.loginPath";
    public static final String HTML_LOGINURL_SUFFIX = ".web.html.loginUrl";
    public static final String RS_CONTEXTPATH_SUFFIX = ".web.rs.contextPath";
    public static final String RS_INTERFACE_SUFFIX = ".web.rs.interface";
    public static final String RS_LOGINPATH_SUFFIX = ".web.rs.loginPath";
    public static final String RS_LOGINURL_SUFFIX = ".web.rs.loginUrl";
    public static final String SSLPORT_SUFFIX = ".web.sslPort";
    public static final String WS_CONTEXTPATH_SUFFIX = ".web.html.contextPath";
    public static final String WS_LOGINPATH_SUFFIX = ".web.ws.loginPath";
    public static final String WS_LOGINURL_SUFFIX = ".web.ws.loginUrl";

    private Properties mapping;

    public DefaultWebContext() {
    }

    @Override
    public String getContextPath() {
        return getMapping().getProperty(getContextId() + CONTEXTPATH_SUFFIX);
    }

    @Override
    public String getHtmlLoginPath() {
        return getMapping().getProperty(getContextId() + HTML_LOGINPATH_SUFFIX);
    }

    @Override
    public String getHtmlLoginUrl() {
        return getMapping().getProperty(getContextId() + HTML_LOGINURL_SUFFIX);
    }

    public Properties getMapping() {
        if (mapping == null) {
            mapping = new Properties();
        }
        return mapping;
    }

    @Override
    public String getRsInterface() {
        return getMapping().getProperty(getContextId() + RS_INTERFACE_SUFFIX);
    }

    @Override
    public String getRsLoginPath() {
        return getMapping().getProperty(getContextId() + RS_LOGINPATH_SUFFIX);
    }

    @Override
    public String getRsLoginUrl() {
        return getMapping().getProperty(getContextId() + RS_LOGINURL_SUFFIX);
    }

    @Override
    public String getSslPort() {
        return getMapping().getProperty(getContextId() + SSLPORT_SUFFIX);
    }

    @Override
    public String getWsLoginPath() {
        return getMapping().getProperty(getContextId() + WS_LOGINPATH_SUFFIX);
    }

    @Override
    public String getWsLoginUrl() {
        return getMapping().getProperty(getContextId() + WS_LOGINURL_SUFFIX);
    }

    public void setMapping(Properties mapping) {
        this.mapping = mapping;
    }

}
