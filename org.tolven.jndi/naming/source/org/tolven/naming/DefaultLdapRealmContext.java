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

public class DefaultLdapRealmContext extends DefaultTolvenJndiContext implements LdapRealmContext {

    public static final String ANONYMOUSUSER_PASSWORD_SUFFIX = ".realm.ldap.anonymousUserPassword";
    public static final String ANONYMOUSUSER_SUFFIX = ".realm.ldap.anonymousUser";
    public static final String BASE_DN_SUFFIX = ".realm.ldap.baseDN";
    public static final String BASE_PEOPLE_NAME_SUFFIX = ".realm.ldap.basePeopleName";
    public static final String BASE_ROLES_NAME_SUFFIX = ".realm.ldap.baseRolesName";
    public static final String JNDI_NAME_SUFFIX = ".realm.ldap.jndiName";
    public static final String PRINCIPAL_DN_PREFIX_SUFFIX = ".realm.ldap.principalDNPrefix";
    public static final String ROLE_DN_PREFIX_SUFFIX = ".realm.ldap.roleDNPrefix";
    public static final String SESSION_ATTRIBUTES_SUFFIX = ".realm.ldap.sessionAttributes";
    public static final String USER_DN_TEMPLATE_SUFFIX = ".realm.ldap.userDnTemplate";
    public static final String USER_SUBSTITUTION_TOKEN_SUFFIX = ".realm.ldap.userSubstitutionToken";

    public static final String REALM_CLASS_SUFFIX = ".realm.class";
    public static final String REALM_IDS = "realm.ids";

    private Properties mapping;

    public DefaultLdapRealmContext() {
    }

    @Override
    public String getAnonymousUser() {
        return getMapping().getProperty(getContextId() + ANONYMOUSUSER_SUFFIX);
    }

    @Override
    public String getAnonymousUserPassword() {
        return getMapping().getProperty(getContextId() + ANONYMOUSUSER_PASSWORD_SUFFIX);
    }

    @Override
    public String getBaseDN() {
        return getMapping().getProperty(getContextId() + BASE_DN_SUFFIX);
    }

    @Override
    public String getBasePeopleName() {
        return getMapping().getProperty(getContextId() + BASE_PEOPLE_NAME_SUFFIX);
    }

    @Override
    public String getBaseRolesName() {
        return getMapping().getProperty(getContextId() + BASE_ROLES_NAME_SUFFIX);
    }

    @Override
    public String getDN(String principal) {
        return getPrincipalName(principal) + "," + getBasePeopleName();
    }

    @Override
    public String getJndiName() {
        return getMapping().getProperty(getContextId() + JNDI_NAME_SUFFIX);
    }

    public Properties getMapping() {
        if (mapping == null) {
            mapping = new Properties();
        }
        return mapping;
    }

    @Override
    public String getPrincipalDNPrefix() {
        return getMapping().getProperty(getContextId() + PRINCIPAL_DN_PREFIX_SUFFIX);
    }

    @Override
    public String getPrincipalName(String principal) {
        return getPrincipalDNPrefix() + "=" + principal;
    }

    @Override
    public Object getRealmClass() {
        String classname = getMapping().getProperty(getContextId() + REALM_CLASS_SUFFIX);
        if (classname == null) {
            throw new RuntimeException("Could not find class using property: " + getContextId() + REALM_CLASS_SUFFIX);
        }
        try {
            Class<?> clazz = Class.forName(classname);
            return clazz.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Could not instantiate class: " + classname, ex);
        }
    }

    @Override
    public String getRoleDNPrefix() {
        return getMapping().getProperty(getContextId() + ROLE_DN_PREFIX_SUFFIX);
    }

    @Override
    public String getSessionAttributes() {
        return getMapping().getProperty(getContextId() + SESSION_ATTRIBUTES_SUFFIX);
    }

    @Override
    public String getUserDnTemplate() {
        return getMapping().getProperty(getContextId() + USER_DN_TEMPLATE_SUFFIX);
    }

    @Override
    public String getUserSubstitutionToken() {
        return getMapping().getProperty(getContextId() + USER_SUBSTITUTION_TOKEN_SUFFIX);
    }

    public void setMapping(Properties mapping) {
        this.mapping = mapping;
    }

}
