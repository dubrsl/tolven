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
package org.tolven.shiro.realm.ldap;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.tolven.naming.LdapRealmContext;
import org.tolven.naming.TolvenContext;

public class TolvenJndiLdapContextFactory implements LdapContextFactory {

    private String ldapJndi;
    private Logger logger = Logger.getLogger(TolvenJndiLdapContextFactory.class);
    private String realm;

    public TolvenJndiLdapContextFactory(String realm) {
        setRealm(realm);
    }

    @Override
    public LdapContext getLdapContext(Object principal, Object credentials) throws NamingException {
        LdapContext ctx = getSystemLdapContext();
        ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, principal);
        ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, credentials);
        try {
            //AUTHENTICATE CREDENTIALS BY CREATING AN INITIAL LDAP CONTEXT
            new InitialLdapContext(ctx.getEnvironment(), null);
        } catch (AuthenticationException ex) {
            throw new AuthenticationException("Access denied: " + principal);
        }
        return ctx;
    }

    @Override
    @Deprecated
    public LdapContext getLdapContext(String username, String password) throws NamingException {
        return getLdapContext((Object) username, password);
    }

    public String getLdapJndi() {
        if (ldapJndi == null) {
            LdapRealmContext ldapRealmContext = getLdapRealmContext();
            ldapJndi = ldapRealmContext.getJndiName();
        }
        return ldapJndi;
    }

    protected LdapRealmContext getLdapRealmContext() {
        try {
            InitialContext ictx = new InitialContext();
            TolvenContext tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
            return (LdapRealmContext) tolvenContext.getRealmContext(getRealm());
        } catch (Exception ex) {
            throw new RuntimeException("Could not get LdapRealmContext", ex);
        }
    }

    public String getRealm() {
        return realm;
    }

    @Override
    public LdapContext getSystemLdapContext() throws NamingException {
        try {
            InitialLdapContext ictx = new InitialLdapContext();
            LdapContext ctx = (LdapContext) ictx.lookup(getLdapJndi());
            /* Glassfish does not pass through these properties, even though it is aware of them?
             * Without com.sun.jndi.ldap.LdapCtxFactory, authentication is NOT carried out
             */
            ctx.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            ctx.addToEnvironment("java.naming.ldap.attributes.binary", "userPKCS12");
            if (logger.isDebugEnabled()) {
                String providerURL = (String) ctx.getEnvironment().get(Context.PROVIDER_URL);
                logger.debug("LDAP providerURL=" + providerURL);
            }
            return ctx;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to lookup " + getLdapJndi(), ex);
        }
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

}
