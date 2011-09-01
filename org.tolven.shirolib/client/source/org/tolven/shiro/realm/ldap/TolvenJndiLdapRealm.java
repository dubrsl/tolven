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

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.tolven.naming.LdapRealmContext;
import org.tolven.naming.TolvenContext;

public class TolvenJndiLdapRealm extends JndiLdapRealm {

    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";

    private Logger logger = Logger.getLogger(TolvenJndiLdapRealm.class);

    public TolvenJndiLdapRealm() {
        setContextFactory(null);
    }

    protected String getRealm() {
        return getName();
    }

    @Override
    protected AuthenticationInfo createAuthenticationInfo(AuthenticationToken token, Object ldapPrincipal, Object ldapCredentials, LdapContext ldapContext) throws NamingException {
        AuthenticationInfo authInfo = super.createAuthenticationInfo(token, ldapPrincipal, ldapCredentials, ldapContext);
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String principal = (String) token.getPrincipal();
        char[] password = (char[]) token.getCredentials();
        initializeSessionAttributes(session, principal, password, ldapContext);
        return authInfo;
    }

    @Override
    public LdapContextFactory getContextFactory() {
        LdapRealmContext ldapRealmContext = getLdapRealmContext();
        String ldapJndiName = ldapRealmContext.getJndiName();
        if (ldapJndiName == null) {
            throw new RuntimeException("The ldapJndiName in the LdapRealmContext is null");
        }
        return new TolvenJndiLdapContextFactory(ldapJndiName);
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

    @Override
    protected String getUserDnPrefix() {
        if (super.getUserDnPrefix() == null) {
            setUserDnTemplate(getLdapRealmContext().getUserDnTemplate());
        }
        return super.getUserDnPrefix();
    }

    @Override
    protected String getUserDnSuffix() {
        if (super.getUserDnSuffix() == null) {
            setUserDnTemplate(getLdapRealmContext().getUserDnTemplate());
        }
        return super.getUserDnSuffix();
    }

    protected void initializeSessionAttributes(Session session, String principal, char[] password, LdapContext ldapContext) throws NamingException {
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setCountLimit(1);
        String sessionAttributes = getLdapRealmContext().getSessionAttributes();
        if (sessionAttributes == null) {
            sessionAttributes = "";
        }
        ctls.setReturningAttributes(sessionAttributes.split(","));
        ctls.setCountLimit(1);
        if (logger.isDebugEnabled()) {
            String providerURL = (String) ldapContext.getEnvironment().get(Context.PROVIDER_URL);
            logger.debug("Search LDAP: " + providerURL);
            logger.debug("Search LDAP " + providerURL + " for: " + principal + " and attributes: " + sessionAttributes);
        }
        NamingEnumeration<SearchResult> namingEnum = null;
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext();
            String principalLdapName = ldapRealmContext.getPrincipalName(principal);
            namingEnum = ldapContext.search(ldapRealmContext.getBasePeopleName(), principalLdapName, ctls);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not search ldap for: " + principal);
        }
        if (!namingEnum.hasMoreElements()) {
            throw new RuntimeException("Could not find ldap principal: " + principal);
        }
        SearchResult rslt = namingEnum.next();
        String dn = rslt.getNameInNamespace();
        if (logger.isDebugEnabled()) {
            logger.debug("Found ldap principal=" + dn);
        }
        try {
            session.setAttribute("dn", dn);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not set dn attribute of session: " + session.getId(), ex);
        }
        Attributes attrs = rslt.getAttributes();
        NamingEnumeration<String> namingEnumIDs = attrs.getIDs();
        while (namingEnumIDs.hasMoreElements()) {
            String attrID = null;
            try {
                attrID = namingEnumIDs.next();
            } catch (NamingException ex) {
                throw new RuntimeException("Could not obtain next enumeration", ex);
            }
            // Deal with RFC4523
            if ("userCertificate".equals(attrID)) {
                Object obj = null;
                try {
                    obj = attrs.get(attrID).get();
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not get attribute userCertificate", ex);
                }
                session.setAttribute("userCertificate;binary", obj);
            } else {
                session.setAttribute(attrID, attrs.get(attrID).get());
            }
        }
        initialUserCredentials(session, principal, password);
    }

    protected void initialUserCredentials(Session session, Object principal, char[] password) throws AuthenticationException {
        KeyStore keyStore = null;
        byte[] userPKCS12 = (byte[]) session.getAttribute("userPKCS12");
        if (userPKCS12 == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No userPKCS12 found for: " + principal);
            }
            //No userPKCS12 to process
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("userPKCS12 found for: " + principal);
        }
        try {
            keyStore = KeyStore.getInstance(TOLVEN_CREDENTIAL_FORMAT_PKCS12);
            ByteArrayInputStream bais = null;
            try {
                bais = new ByteArrayInputStream(userPKCS12);
                keyStore.load(bais, password);
            } finally {
                if (bais != null)
                    bais.close();
            }
        } catch (Exception ex) {
            throw new AuthenticationException("Could not obtain user's private keystore: " + principal, ex);
        }
        String alias = null;
        PrivateKey userPrivateKey = null;
        try {
            Enumeration<String> aliases = keyStore.aliases();
            if (!aliases.hasMoreElements()) {
                throw new RuntimeException(getClass() + ": userPKCS12 contains no aliases for principal " + principal);
            }
            alias = aliases.nextElement();
            userPrivateKey = (PrivateKey) keyStore.getKey(alias, password);
        } catch (Exception ex) {
            throw new AuthenticationException("Could not obtain user's private key: " + principal, ex);
        }
        if (userPrivateKey == null) {
            throw new RuntimeException(getClass() + ": userPKCS12 contains no key with alias " + alias + " for " + principal);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Obtained user private key for: " + principal);
        }
        PKCS8EncodedKeySpec userPKCS8EncodedKey = new PKCS8EncodedKeySpec(userPrivateKey.getEncoded());
        session.setAttribute("userPKCS8EncodedKey", userPKCS8EncodedKey.getEncoded());
        if (logger.isDebugEnabled()) {
            logger.debug("Stored user private key in session under userPKCS8EncodedKey for: " + principal);
        }
        Certificate[] certificateChain = null;
        try {
            certificateChain = keyStore.getCertificateChain(alias);
        } catch (KeyStoreException ex) {
            throw new RuntimeException("Could not obtain user's keystore certificate chain: " + principal, ex);
        }
        if (certificateChain == null || certificateChain.length == 0) {
            throw new RuntimeException("LDAP's userPKCS12 contains no certificate with alias " + alias + " for " + principal);
        }
        X509Certificate certificate = (X509Certificate) certificateChain[0];
        if (logger.isDebugEnabled()) {
            logger.debug("Obtained user certificate for: " + principal);
        }
        try {
            session.setAttribute("userX509Certificate", certificate.getEncoded());
            if (logger.isDebugEnabled()) {
                logger.debug("Stored user certificate in session under userX509Certificate for: " + principal);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not get encoded X509 certificate: " + principal, ex);
        }

    }

    @Override
    protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token, LdapContextFactory ldapContextFactory) throws NamingException {
        return super.queryForAuthenticationInfo(token, ldapContextFactory);
    }

    @Override
    protected AuthorizationInfo queryForAuthorizationInfo(PrincipalCollection principals, LdapContextFactory ldapContextFactory) throws NamingException {
        LdapRealmContext ldapRealmContext = getLdapRealmContext();
        String principal = (String) principals.getPrimaryPrincipal();
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setReturningAttributes(new String[] { ldapRealmContext.getRoleDNPrefix() });
        ctls.setTimeLimit(10000);
        String shortPrincipalDN = getUserDnPrefix() + "=" + principal;
        String longPrincipalDN = shortPrincipalDN + "," + getUserDnSuffix();
        Object[] filterArgs = { longPrincipalDN };
        /*
         * TODO This can't be the right place to do this, because getSystemLdapContext() does not authenticate this context for a search.
         * The problem is that the ldapContextFactory, instead of the ldapContext is passed to this method, where the former ensures authentication upstream.
         * However, this method is for authorization and not authentication, which might explain it.
         * So, perhaps session attribute gathering belongs elsewhere?
         */
        LdapContext ldapContext = ldapContextFactory.getSystemLdapContext();
        NamingEnumeration<SearchResult> namingEnum = ldapContext.search(ldapRealmContext.getBaseRolesName(), "(uniqueMember={0})", filterArgs, ctls);
        String roleName = null;
        Set<String> roles = new HashSet<String>();
        StringBuffer buff = new StringBuffer();
        buff.append("[");
        while (namingEnum.hasMore()) {
            SearchResult rslt = namingEnum.next();
            Attributes attrs = rslt.getAttributes();
            Attribute rolesAttr = attrs.get(ldapRealmContext.getRoleDNPrefix());
            for (int i = 0; i < rolesAttr.size(); i++) {
                roleName = (String) rolesAttr.get(i);
                roles.add(roleName);
                buff.append(roleName);
                if (i < rolesAttr.size() - 1) {
                    buff.append(",");
                }
            }
        }
        buff.append("]");
        if (logger.isDebugEnabled()) {
            logger.debug(longPrincipalDN + "has roles: " + buff.toString());
        }
        return new SimpleAuthorizationInfo(roles);
    }

}
