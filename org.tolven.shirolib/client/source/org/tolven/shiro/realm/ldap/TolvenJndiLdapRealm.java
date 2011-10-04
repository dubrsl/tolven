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
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.tolven.exeption.GatekeeperAuthenticationException;
import org.tolven.ldap.LdapManager;
import org.tolven.ldap.PasswordExpiring;
import org.tolven.naming.LdapRealmContext;
import org.tolven.naming.TolvenContext;
import org.tolven.shiro.authc.UsernamePasswordRealmToken;

public class TolvenJndiLdapRealm extends JndiLdapRealm {

    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";

    private Logger logger = Logger.getLogger(TolvenJndiLdapRealm.class);

    public TolvenJndiLdapRealm() {
        setContextFactory(null);
    }

    @Override
    public LdapContextFactory getContextFactory() {
        return new TolvenJndiLdapContextFactory(getRealm());
    }

    protected LdapContext getLdapContext(String principal, char[] password, String realm) {
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            String principalDN = ldapRealmContext.getDN(principal);
            TolvenJndiLdapContextFactory ldapContextFactory = new TolvenJndiLdapContextFactory(realm);
            return ldapContextFactory.getLdapContext(principalDN, password);
        } catch (NamingException ex) {
            throw new RuntimeException("Could not get ldap context for: " + principal + " in realm: " + realm, ex);
        }
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

    protected LdapRealmContext getLdapRealmContext(String realm) {
        try {
            InitialContext ictx = new InitialContext();
            TolvenContext tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
            return (LdapRealmContext) tolvenContext.getRealmContext(realm);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get LdapRealmContext", ex);
        }
    }

    protected String getRealm() {
        return getName();
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

    protected void injectSessionAttributes(Attributes attrs, Session session, String principal, char[] password) throws NamingException {
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
        injectUserCredentials(session, principal, password);
    }

    protected void injectUserCredentials(Session session, Object principal, char[] password) throws AuthenticationException {
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
        UsernamePasswordRealmToken uprToken = (UsernamePasswordRealmToken) token;
        String uid = (String) uprToken.getPrincipal();
        char[] password = uprToken.getPassword();
        String realm = uprToken.getRealm();
        LdapManager ldapManager = null;
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            ldapManager = ldapRealmContext.getLdapManager(uid, password);
            ldapManager.checkPassword();
            Session session = SecurityUtils.getSubject().getSession();
            boolean passwordExpired = ldapManager.isPasswordExpired();
            if (passwordExpired) {
                session.setAttribute(GatekeeperAuthenticationException.PASSWORD_EXPIRED, passwordExpired);
            } else {
                session.removeAttribute(GatekeeperAuthenticationException.PASSWORD_EXPIRED);
            }
            String formattedExpiration = null;
            PasswordExpiring passwordExpiring = ldapManager.getPasswordExpiring();
            if (passwordExpiring != null) {
                formattedExpiration = passwordExpiring.getFormattedExpiration();
                session.setAttribute(GatekeeperAuthenticationException.PASSWORD_EXPIRING, formattedExpiration);
            } else {
                session.removeAttribute(GatekeeperAuthenticationException.PASSWORD_EXPIRING);
            }
            String sessionAttributes = ldapRealmContext.getSessionAttributes();
            if (sessionAttributes == null) {
                sessionAttributes = "";
            }
            String[] sessionAttributeIds = sessionAttributes.split(",");
            if (!passwordExpired) {
                Attributes attributes = ldapManager.getAttributes(sessionAttributeIds);
                injectSessionAttributes(attributes, session, uid, password);
            }
            return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        } finally {
            if (ldapManager != null) {
                ldapManager.disconnect();
            }
        }
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
         * The problem is that the ldapContextFactory, instead of the ldapContext is passed to this method, where the former ensures authentication upstream
         * using the password which is not supplied here.
         * However, this method is for authorization and not authentication, which might explain it.
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
