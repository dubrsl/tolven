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
 * @version $Id: DefaultJndiManager.java 1779 2011-07-22 04:31:50Z joe.isaac $
 */
package org.tolven.naming;

import java.util.Properties;

public class DefaultJndiManager implements JndiManager {

    public static final String[] TOLVEN_SUFFIXES = new String[] {
            ".ssocookie.name",
            ".ssocookie.domain",
            ".ssocookie.secure",
            ".ssocookie.path",
            ".ssocookie.maxAge",
            ".gatekeeper.web.html.id",
            ".gatekeeper.web.rs.id",
            ".gatekeeper.web.ws.id", };

    public static final String[] WEB_SUFFIXES = new String[] {
            ".web.contextPath",
            ".web.html.loginPath",
            ".web.html.loginUrl",
            ".web.rs.interface",
            ".web.rs.loginPath",
            ".web.rs.loginUrl",
            ".web.sslPort",
            ".web.ws.loginPath",
            ".web.ws.loginUrl" };

    public static final String[] REALM_SUFFIXES = new String[] {
            ".realm.class",
            ".realm.ldap.env",
            ".realm.ldap.jndiName",
            ".realm.ldap.baseDN",
            ".realm.ldap.basePeopleName",
            ".realm.ldap.baseRolesName",
            ".realm.ldap.userSubstitutionToken",
            ".realm.ldap.userDnTemplate",
            ".realm.ldap.principalDNPrefix",
            ".realm.ldap.roleDNPrefix",
            ".realm.ldap.sessionAttributes",
            ".realm.ldap.anonymousUser",
            ".realm.ldap.anonymousUserPassword" };

    public static final String[] QUEUE_SUFFIXES = new String[] {
            ".queue.user",
            ".queue.password",
            ".queue.realm" };

    public DefaultJndiManager() {
    }

    @Override
    public Properties getJndiProperties(Properties srcProperties) {
        Properties jndiProperties = new Properties();
        updateTolvenId(jndiProperties, srcProperties);
        updateWebIds(jndiProperties, srcProperties);
        updateRealmIds(jndiProperties, srcProperties);
        updateQueueIds(jndiProperties, srcProperties);
        return jndiProperties;
    }

    private void updateTolvenId(Properties jndiProperties, Properties srcProperties) {
        String tolvenId = srcProperties.getProperty(JndiManager.TOLVEN_ID_REF);
        if (tolvenId == null) {
            throw new RuntimeException("Could not find property " + JndiManager.TOLVEN_ID_REF + " in source for jndi properties");
        }
        jndiProperties.setProperty(JndiManager.TOLVEN_ID_REF, tolvenId);
        addProperties(tolvenId, TOLVEN_SUFFIXES, jndiProperties, srcProperties);
    }

    private void updateWebIds(Properties jndiProperties, Properties srcProperties) {
        String ids = srcProperties.getProperty(JndiManager.WEB_IDS_REF);
        if (ids == null) {
            throw new RuntimeException("Could not find property " + JndiManager.WEB_IDS_REF + " in source for jndi properties");
        }
        jndiProperties.setProperty(JndiManager.WEB_IDS_REF, ids);
        String[] webIds = ids.split(",");
        for (String webId : webIds) {
            addProperties(webId, WEB_SUFFIXES, jndiProperties, srcProperties);
        }
    }

    private void updateRealmIds(Properties jndiProperties, Properties srcProperties) {
        String ids = srcProperties.getProperty(JndiManager.REALM_IDS_REF);
        if (ids == null) {
            throw new RuntimeException("Could not find property " + JndiManager.REALM_IDS_REF + " in source for jndi properties");
        }
        jndiProperties.setProperty(JndiManager.REALM_IDS_REF, ids);
        String[] realmIds = ids.split(",");
        if (realmIds.length == 0) {
            throw new RuntimeException("At least one value must exist for jndi property realm.ids");
        }
        for (String realmId : realmIds) {
            addProperties(realmId, REALM_SUFFIXES, jndiProperties, srcProperties);
        }
    }

    private void updateQueueIds(Properties jndiProperties, Properties srcProperties) {
        String ids = srcProperties.getProperty(JndiManager.QUEUE_IDS_REF);
        if (ids == null) {
            throw new RuntimeException("Could not find property " + JndiManager.QUEUE_IDS_REF + " in source for jndi properties");
        }
        jndiProperties.setProperty(JndiManager.QUEUE_IDS_REF, ids);
        String[] queueIds = ids.split(",");
        if (queueIds.length == 0) {
            throw new RuntimeException("At least one value must exist for jndi property queue.ids");
        }
        for (String queueId : queueIds) {
            addProperties(queueId, QUEUE_SUFFIXES, jndiProperties, srcProperties);
        }
    }

    private void addProperties(String prefix, String[] suffixes, Properties jndiProperties, Properties srcProperties) {
        for (String suffix : suffixes) {
            String nameKey = prefix + suffix;
            for (String srcPropertyName : srcProperties.stringPropertyNames()) {
                if (srcPropertyName.startsWith(nameKey)) {
                    String value = srcProperties.getProperty(srcPropertyName);
                    if (value != null) {
                        if (value.contains("${")) {
                            throw new RuntimeException(nameKey + " contains undefined properties: " + value);
                        }
                        jndiProperties.setProperty(srcPropertyName, value);
                    }
                }
            }
        }
    }

}
