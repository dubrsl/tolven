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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DefaultTolvenContext implements TolvenContext, Serializable {

    public static final String GATEKEEPER_WEB_HTML_ID_SUFFIX = ".gatekeeper.web.html.id";
    public static final String GATEKEEPER_WEB_RS_ID_SUFFIX = ".gatekeeper.web.rs.id";
    public static final String GATEKEEPER_WEB_WS_ID_SUFFIX = ".gatekeeper.web.ws.id";
    public static final String SSOCOOKIE_DOMAIN_SUFFIX = ".ssocookie.domain";
    public static final String SSOCOOKIE_MAXAGE_SUFFIX = ".ssocookie.maxAge";

    public static final String SSOCOOKIE_NAME_SUFFIX = ".ssocookie.name";
    public static final String SSOCOOKIE_PATH_SUFFIX = ".ssocookie.path";
    public static final String SSOCOOKIE_SECURE_SUFFIX = ".ssocookie.secure";

    private Properties mapping;

    public DefaultTolvenContext() {
    }

    @Override
    public String getContextId() {
        String ctxId = getMapping().getProperty(JndiManager.TOLVEN_ID_REF);
        if(ctxId == null) {
            throw new RuntimeException(JndiManager.TOLVEN_ID_REF + " is null");
        }
        return getMapping().getProperty(JndiManager.TOLVEN_ID_REF);
    }

    @Override
    public Object getHtmlGatekeeperWebContext() {
        DefaultWebContext ctx = new DefaultWebContext();
        String ctxId = getMapping().getProperty(getContextId() + GATEKEEPER_WEB_HTML_ID_SUFFIX);
        if(ctxId == null) {
            throw new RuntimeException(getContextId() + GATEKEEPER_WEB_HTML_ID_SUFFIX + " is null");
        }
        ctx.setContextId(ctxId);
        ctx.setMapping(getMapping());
        return ctx;
    }

    public Properties getMapping() {
        if (mapping == null) {
            mapping = new Properties();
        }
        return mapping;
    }

    @Override
    public Object getQueueContext(String ctxId) {
        if(ctxId == null) {
            throw new RuntimeException("QueueContext id is null");
        }
        DefaultQueueContext ctx = new DefaultQueueContext();
        ctx.setContextId(ctxId);
        ctx.setMapping(getMapping());
        return ctx;
    }

    @Override
    public List<String> getQueueIds() {
        return Arrays.asList(getMapping().getProperty(JndiManager.QUEUE_IDS_REF).split(","));
    }

    @Override
    public Object getRealmContext(String ctxId) {
        if(ctxId == null) {
            throw new RuntimeException("RealmContext id is null");
        }
        DefaultLdapRealmContext ctx = new DefaultLdapRealmContext();
        ctx.setContextId(ctxId);
        ctx.setMapping(getMapping());
        return ctx;
    }

    @Override
    public List<String> getRealmIds() {
        String realmIds = getMapping().getProperty(JndiManager.REALM_IDS_REF);
        if (realmIds == null) {
            return new ArrayList<String>();
        } else {
            return Arrays.asList(getMapping().getProperty(JndiManager.REALM_IDS_REF).split(","));
        }
    }

    @Override
    public Object getRsGatekeeperWebContext() {
        DefaultWebContext ctx = new DefaultWebContext();
        String ctxId = getMapping().getProperty(getContextId() + GATEKEEPER_WEB_RS_ID_SUFFIX);
        if(ctxId == null) {
            throw new RuntimeException(getContextId() + GATEKEEPER_WEB_RS_ID_SUFFIX + " is null");
        }
        ctx.setContextId(ctxId);
        ctx.setMapping(getMapping());
        return ctx;
    }

    @Override
    public String getSsoCookieDomain() {
        return getMapping().getProperty(getContextId() + SSOCOOKIE_DOMAIN_SUFFIX);
    }

    @Override
    public String getSsoCookieMaxAge() {
        return getMapping().getProperty(getContextId() + SSOCOOKIE_MAXAGE_SUFFIX);
    }

    @Override
    public String getSsoCookieName() {
        return getMapping().getProperty(getContextId() + SSOCOOKIE_NAME_SUFFIX);
    }

    @Override
    public String getSsoCookiePath() {
        return getMapping().getProperty(getContextId() + SSOCOOKIE_PATH_SUFFIX);
    }

    @Override
    public String getSsoCookieSecure() {
        return getMapping().getProperty(getContextId() + SSOCOOKIE_SECURE_SUFFIX);
    }

    @Override
    public Object getWebContext(String ctxId) {
        if(ctxId == null) {
            throw new RuntimeException("WebContext id is null");
        }
        DefaultWebContext ctx = new DefaultWebContext();
        ctx.setContextId(ctxId);
        ctx.setMapping(getMapping());
        return ctx;
    }

    @Override
    public List<String> getWebIds() {
        return Arrays.asList(getMapping().getProperty(JndiManager.WEB_IDS_REF).split(","));
    }

    @Override
    public Object getWsGatekeeperWebContext() {
        DefaultWebContext ctx = new DefaultWebContext();
        String ctxId = getMapping().getProperty(getContextId() + GATEKEEPER_WEB_WS_ID_SUFFIX);
        if(ctxId == null) {
            throw new RuntimeException(getContextId() + GATEKEEPER_WEB_WS_ID_SUFFIX + " is null");
        }
        ctx.setContextId(ctxId);
        ctx.setMapping(getMapping());
        return ctx;
    }

    public void setMapping(Properties mapping) {
        this.mapping = mapping;
    }

}
