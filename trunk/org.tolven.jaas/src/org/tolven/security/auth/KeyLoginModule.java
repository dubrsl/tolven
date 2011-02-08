/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.security.auth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.acl.Group;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.log4j.Logger;
import org.tolven.security.TolvenPrincipal;
import org.tolven.security.acl.TolvenGroup;
import org.tolven.security.hash.SSHA;
import org.tolven.security.key.DefaultUserKeyRing;
import org.tolven.security.key.UserKeyRing;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.security.key.UserPublicKey;

/**
 * This class is responsible for authenticating against LDAP and adding credentials and roles to the Subject.
 * It retrieves the user's PKCS12 KeyStore from LDAP and places the UserPrivateKey and UserPublicKey in 
 * the Subject's privateCredentials and publicCredentials respectively.
 * 
 * @author Joseph Isaac
 */

/*
 * Note that it is important that this class does not make use of EJB beans during the intial login process, since
 * it is responsible for authenticating, and would be caught in a recursive loop.
 */
public class KeyLoginModule implements LoginModule {

    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";

    private static final String PRINCIPAL_DN_PREFIX = "principalDNPrefix";
    private static final String PRINCIPAL_DN_SUFFIX = "principalDNSuffix";
    private static final String JAAS_SECURITY_DOMAIN = "jaasSecurityDomain";
    private static final String ROLES_CTX_DN = "rolesCtxDN";
    private static final String ROLE_ATTRIBUTE_ID = "roleAttributeID";

    private Subject subject;
    private Group rolesGroup;
    private CallbackHandler callbackHandler;
    private String principalName;
    private char[] password;
    private byte[] userPKCS12;
    private Map sharedState;
    private Map<String, ?> options;

    private String principalDNPrefix;
    private String principalDNSuffix;
    private String jaasSecurityDomain;
    private String rolesCtxDN;
    private String roleAttributeID;
    
    private Logger logger = Logger.getLogger(KeyLoginModule.class);

    private String getPrincipalDNPrefix() {
        return principalDNPrefix;
    }

    private String getPrincipalDNSuffix() {
        return principalDNSuffix;
    }

    private String getJaasSecurityDomain() {
        return jaasSecurityDomain;
    }

    private String getRolesCtxDN() {
        return rolesCtxDN;
    }

    private String getRoleAttributeID() {
        return roleAttributeID;
    }

    private byte[] getUserPKCS12() {
        return userPKCS12;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        //logger.debug("initialize()");
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        //logger.debug("begin login");
        checkRequiredModuleOptions();
        try {
            String passwordStackingOption = (String) options.get("password-stacking");
            if ("useFirstPass".equals(passwordStackingOption)) {
                principalName = (String) sharedState.get("javax.security.auth.login.name");
                Object obj = sharedState.get("javax.security.auth.login.password");
                if (obj instanceof char[]) {
                    password = (char[]) obj;
                } else if (obj instanceof String) {
                    password = ((String) obj).toCharArray();
                }
            }
            if (principalName == null || password == null) {
                /*
                 * If either the principalName or password is null, then even with useFirstPass, use the callback mechanism
                 */
                if (callbackHandler == null) {
                    throw new LoginException("No CallbackHandler");
                }
                NameCallback nc = new NameCallback("User name: ");
                PasswordCallback pc = new PasswordCallback("Password: ", false);
                Callback[] callback = { nc, pc };
                callbackHandler.handle(callback);
                principalName = nc.getName();
                password = pc.getPassword();
            }
            /*
             * Safety Check
             */
            if (principalName == null) {
                throw new LoginException("null principalName not permitted");
            }
            if (password == null) {
                throw new LoginException("null password not permitted");
            }
            /*
             * Authenticate user against LDAP, which will retrieve the userPKCS12 if it exists.
             */
            DirContext ctx = ldapAuthenticate(principalName, password);
            if (ctx != null) {
                ctx.close();
            }
            /*
             * Use sharedState to allow the login module so that this module determines which principalName and password are applicable
             */
            sharedState.put("javax.security.auth.login.name", principalName);
            sharedState.put("javax.security.auth.login.password", password);
        } catch (UnsupportedCallbackException e) {
            LoginException le = new LoginException("CallbackHandler does not support: " + e.getCallback() + ": " + e.getMessage());
            throw le;
        } catch (Exception e) {
            LoginException le = new LoginException("Authentication Failed: " + e.getMessage());
            throw le;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    @Override
    public boolean commit() throws LoginException {
        //logger.debug("begin commit");
        try {
            addRoles();
            addCredentials();
            StringBuffer buff = new StringBuffer();
            buff.append("login for " + principalName);
            buff.append(" with roles: ");
            java.util.Enumeration<?> enumeration = rolesGroup.members();
            while (enumeration.hasMoreElements()) {
                buff.append(((Principal)enumeration.nextElement()).getName());
                if(enumeration.hasMoreElements()) {
                    buff.append(',');
                }
            }
            logger.debug(buff.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new LoginException(ex.getMessage());
        }
        return true;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    @Override
    public boolean abort() throws LoginException {
        removeAllCredentials();
        return true;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    @Override
    public boolean logout() throws LoginException {
        logger.debug("logout for " + principalName);
        removeAllCredentials();
        return true;
    }

    /**
     * Check that all the required login module options are available
     * @throws LoginException
     */
    private void checkRequiredModuleOptions() throws LoginException {
        jaasSecurityDomain = (String) options.get(JAAS_SECURITY_DOMAIN);
        if (jaasSecurityDomain == null) {
            throw new LoginException("The KeyLoginModule requires the module option: " + JAAS_SECURITY_DOMAIN);
        }
        principalDNPrefix = (String) options.get(PRINCIPAL_DN_PREFIX);
        if (principalDNPrefix == null) {
            throw new LoginException("The KeyLoginModule requires the module option: " + PRINCIPAL_DN_PREFIX);
        }
        principalDNSuffix = (String) options.get(PRINCIPAL_DN_SUFFIX);
        if (principalDNSuffix == null) {
            throw new LoginException("The KeyLoginModule requires the module option: " + PRINCIPAL_DN_SUFFIX);
        }
        rolesCtxDN = (String) options.get(ROLES_CTX_DN);
        if (rolesCtxDN == null) {
            throw new LoginException("The KeyLoginModule requires the module option: " + ROLES_CTX_DN);
        }
        roleAttributeID = (String) options.get(ROLE_ATTRIBUTE_ID);
        if (roleAttributeID == null) {
            throw new LoginException("The KeyLoginModule requires the module option: " + ROLE_ATTRIBUTE_ID);
        }
    }

    /**
     * Authenticate the principalName and password against LDAP, and if successful return a context
     * @param ldapPrincipalName
     * @param ldapPassword
     * @return
     * @throws NamingException
     * @throws LoginException
     */
    private DirContext ldapAuthenticate(String ldapPrincipalName, char[] ldapPassword) throws NamingException, LoginException {
        /*
         * The InitialContext must first be primed with the user's credentials as well as actually carrying out a search. This provides authentication.
         * Ensure that ldapPrincipalName and ldapPassword does not get mixed with the instance variable
         * Context.SECURITY_AUTHENTICATION set to "simple" must also be specified, otherwise the default is "none" i.e. no authentication at all
         */
        InitialContext ictx = new InitialContext();
        LdapContext ctx = (LdapContext) ictx.lookup(getJaasSecurityDomain());
        String shortPrincipalDN = getPrincipalDNPrefix() + "=" + ldapPrincipalName;
        String longPrincipalDN = shortPrincipalDN + "," + getPrincipalDNSuffix();
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setCountLimit(1);
        ctls.setTimeLimit(1000);
        NamingEnumeration<SearchResult> namingEnum = null;
        /*
         * Search for the principal and credentials
         */
        String ldapDN = null;
        try {
            namingEnum = ctx.search(getPrincipalDNSuffix(), shortPrincipalDN, ctls);
            while (namingEnum.hasMore()) {
                SearchResult rslt = namingEnum.next();
                Attributes attrs = rslt.getAttributes();
                String dn = rslt.getNameInNamespace();
                if (longPrincipalDN.equals(dn)) {
                    ldapDN = dn;
                    boolean authenticated = SSHA.checkPassword(ldapPassword, (byte[]) attrs.get("userPassword").get());
                    if(!authenticated) {
                        throw new LoginException("Authentication Failed");
                    }
                }
                Attribute keystore = attrs.get("userPKCS12");
                if (keystore != null) {
                    userPKCS12 = (byte[]) keystore.get();
                }
                break;
            }
        } finally {
            if (namingEnum != null) {
                namingEnum.close();
            }
        }
        if (ldapDN == null) {
            throw new LoginException("Authentication Failed");
        }
        return ctx;
    }

    /**
     * Add roles to the Subject
     * @throws LoginException
     * @throws NamingException
     */
    private void addRoles() throws LoginException, NamingException {
        /*
         * Ensure there is only one Group called Roles to hold roles
         */
        subject.getPrincipals().add(new TolvenPrincipal(principalName));
        for (Group subjectGroup : subject.getPrincipals(Group.class)) {
            if ("Roles".equalsIgnoreCase(subjectGroup.getName())) {
                rolesGroup = subjectGroup;
                break;
            }
        }
        if (rolesGroup == null) {
            rolesGroup = new TolvenGroup("Roles");
            subject.getPrincipals().add(rolesGroup);
        }
        /*
         * Search LDAP for roles, which are located under rolesCtxDN, each DN containing a set of user DNs
         */
        DirContext ctx = null;
        NamingEnumeration<SearchResult> namingEnum = null;
        try {
            ctx = ldapAuthenticate(principalName, password);
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctls.setReturningAttributes(new String[] { getRoleAttributeID() });
            ctls.setTimeLimit(10000);
            String shortPrincipalDN = getPrincipalDNPrefix() + "=" + principalName;
            String longPrincipalDN = shortPrincipalDN + "," + getPrincipalDNSuffix();
            Object[] filterArgs = { longPrincipalDN };
            namingEnum = ctx.search(getRolesCtxDN(), "(uniqueMember={0})", filterArgs, ctls);
            String roleName = null;
            while (namingEnum.hasMore()) {
                SearchResult sr = (SearchResult) namingEnum.next();
                Attributes attrs = sr.getAttributes();
                Attribute roles = attrs.get(getRoleAttributeID());
                for (int i = 0; i < roles.size(); i++) {
                    roleName = (String) roles.get(i);
                    addRole(roleName);
                }
            }
        } finally {
            if (namingEnum != null) {
                namingEnum.close();
            }
            if (ctx != null) {
                ctx.close();
            }
        }
        /*
         * Until a migration occurs to give Tolven users the following roles, they are added here for the period of the login,
         * but only if LDAP admin has not added roles.
         */
        boolean hasLDAPRoles = false;
        java.util.Enumeration<?> enumeration = rolesGroup.members();
        while (enumeration.hasMoreElements()) {
            hasLDAPRoles = true;
            break;
        }
        if (hasLDAPRoles) {
            /*
             * We will only use the roles provided by the LDAP admin
             */
        } else {
            if (principalName.equals("admin")) {
                addRole("tolvenAdmin");
                addRole("tolvenTrimBrowser");
                addRole("tolvenTrimUpload");
                addRole("tolvenAdminApp");
                addRole("tolvenWriteAdminApp");
                addRole("tolvenAdminAppDLQ");
                addRole("tolvenWriteRule");
            } else {
                addRole("tolvenWriteInvitation");
                addRole("tolvenWriteRule");
                addRole("tolvenWriteGen");
                addRole("tolvenWeb");
                addRole("tolvenWS");
                addRole("tolvenMobile");
                addRole("tolvenBrowse");
            }
        }
    }

    /**
     * Add role to Subject
     */
    private void addRole(String roleName) {
        Principal role = new TolvenPrincipal(roleName);
        if (!subjectHasRole(role)) {
            rolesGroup.addMember(role);
        }
    }

    private boolean subjectHasRole(Principal role) {
        for (Group subjectGroup : subject.getPrincipals(Group.class)) {
            if ("Roles".equals(subjectGroup.getName()) && subjectGroup.isMember(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add UserPublicKey and UserPrivateKey to the Subject
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws LoginException
     */
    private void addCredentials() throws IOException, GeneralSecurityException, LoginException {
        // Obtain the UserKeyPair if it exists
        UserKeyRing userKeyRing = findUserKeyRing(principalName, password);
        if (userKeyRing == null) {
            /*
             * If the user has no UserKeyRing, then they currently have no UserPrivateKey or UserPublicKey
             * Examples of such users are the Configuration Manager admin
             */
            return;
        }
        UserPrivateKey userPrivateKey = userKeyRing.getUserPrivateKey();
        UserPublicKey userPublicKey = userKeyRing.getUserPublicKey();
        /*
         * Populate the Subject
         * UserPrivateKey: Ensure there is only one UserPrivateKey in a Subject by removing any that might be there
         */
        Object obj = null;
        for (Iterator<Object> iter = subject.getPrivateCredentials().iterator(); iter.hasNext();) {
            obj = iter.next();
            if (obj instanceof UserPrivateKey) {
                iter.remove();
            }
        }
        subject.getPrivateCredentials().add(userPrivateKey);
        /*
         * UserPublicKey: Ensure there is only one UserPublicKey in a Subject by removing any that might be there
         */
        for (Iterator<Object> iter = subject.getPublicCredentials().iterator(); iter.hasNext();) {
            obj = iter.next();
            if (obj instanceof UserPublicKey) {
                iter.remove();
            }
        }
        subject.getPublicCredentials().add(userPublicKey);
    }

    /**
     * Remove all security related information
     * @throws LoginException
     */
    private void removeAllCredentials() throws LoginException {
        callbackHandler = null;
        principalName = null;
        rolesGroup = null;
        password = null;
        userPKCS12 = null;
        sharedState = null;
        options = null;
        try {
            if (subject != null) {
                // Remove UserPrivateKey
                for (Iterator<UserPrivateKey> iter = subject.getPrivateCredentials(UserPrivateKey.class).iterator(); iter.hasNext();) {
                    iter.next();
                    iter.remove();
                }
                // Remove all UserPublicKey
                for (Iterator<UserPublicKey> iter = subject.getPublicCredentials(UserPublicKey.class).iterator(); iter.hasNext();) {
                    iter.next();
                    iter.remove();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new LoginException(ex.getMessage());
        } finally {
            subject = null;
        }
    }

    /**
     * Find the principal's UserKeyRing which holds both the UserPublicKey and UserPrivateKey
     * @param principalName
     * @param password
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private UserKeyRing findUserKeyRing(String principalName, char[] password) throws IOException, GeneralSecurityException {
        KeyStore keyStore = null;
        if (getUserPKCS12() != null) {
            keyStore = KeyStore.getInstance(TOLVEN_CREDENTIAL_FORMAT_PKCS12);
            ByteArrayInputStream bais = null;
            try {
                bais = new ByteArrayInputStream(getUserPKCS12());
                keyStore.load(bais, password);
            } finally {
                if (bais != null)
                    bais.close();
            }
        }
        UserKeyRing userKeyRing = null;
        if (keyStore == null) {
            /*
             * Users have limited functionality without a keystore, but they can still login
             */
        } else {
            /*
             * Retrieve PublicKey and PrivateKey from the KeyStore and place them in a UserKeyRing
             */
            Enumeration<String> aliases = keyStore.aliases();
            if (!aliases.hasMoreElements()) {
                throw new LoginException(getClass() + ": LDAP's userPKCS12 contains no aliases for principal " + principalName);
            }
            String alias = aliases.nextElement();
            Certificate[] certificateChain = keyStore.getCertificateChain(alias);
            if (certificateChain == null || certificateChain.length == 0) {
                throw new LoginException(getClass() + ": LDAP's userPKCS12 contains no certificate with alias " + alias + " for " + principalName);
            }
            X509Certificate certificate = (X509Certificate) certificateChain[0];
            UserPublicKey userPublicKey = UserPublicKey.getInstance();
            PublicKey publicKey = certificate.getPublicKey();
            userPublicKey.setPublicKey(publicKey);
            UserPrivateKey userPrivateKey = UserPrivateKey.getInstance();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);
            if (privateKey == null) {
                throw new LoginException(getClass() + ": LDAP's userPKCS12 contains no key with alias " + alias + " for " + principalName);
            }
            userPrivateKey.setPrivateKey(privateKey);
            userKeyRing = new DefaultUserKeyRing(userPrivateKey, userPublicKey, certificateChain);
        }
        return userKeyRing;
    }

}
