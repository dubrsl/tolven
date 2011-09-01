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
package org.tolven.gatekeeper.bean;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;
import org.tolven.gatekeeper.CertificateHelper;
import org.tolven.gatekeeper.LDAPLocal;
import org.tolven.naming.LdapRealmContext;
import org.tolven.naming.TolvenContext;
import org.tolven.naming.TolvenPerson;

/**
 * Interface to communicate with LDAP based on a realm lookup of the Directory service
 * 
 * @author Joseph Isaac
 *
 */
@Stateless()
@Local(LDAPLocal.class)
public class LDAPBean implements LDAPLocal {

    private CertificateHelper certificateHelper = null;

    private Logger logger = Logger.getLogger(LDAPBean.class);

    /**
     * Change user password
     * 
     * @param uid
     * @param realm
     * @param oldPassword
     * @param newPassword
     * @throws AuthenticationException 
     */
    @Override
    public void changeUserPassword(String uid, String realm, char[] oldPassword, char[] newPassword) throws AuthenticationException {
        TolvenPerson tolvenPerson = null;
        try {
            tolvenPerson = findTolvenPerson(uid, realm);
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Change password for TolvenPerson: " + uid + " in realm: " + realm + " by: " + uid + " is denied");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to change password for TolvenPerson: " + uid + " in realm " + realm + " by: " + uid, ex);
        }
        if (tolvenPerson == null) {
            throw new AuthenticationException("Change password for TolvenPerson: " + uid + " in realm: " + realm + " by: " + uid + " is denied");
        }
        LdapContext ctx = null;
        try {
            tolvenPerson.setUserPassword(new String(newPassword));
            byte[] bytes = tolvenPerson.getUserPKCS12();
            if (bytes != null) {
                byte[] userCertificateBytes;
                byte[] newKeyStoreBytes;
                try {
                    KeyStore keyStore = CertificateHelper.getKeyStore(bytes, oldPassword);
                    CertificateHelper.changeKeyStorePassword(keyStore, oldPassword, newPassword);
                    userCertificateBytes = CertificateHelper.getX509CertificateByteArray(keyStore);
                    newKeyStoreBytes = CertificateHelper.toByteArray(keyStore, newPassword);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new AuthenticationException("Access denied");
                }
                tolvenPerson.setUserCertificate(userCertificateBytes);
                tolvenPerson.setUserPKCS12(newKeyStoreBytes);
            }
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            String tolvenPersonDN = ldapRealmContext.getDN(tolvenPerson.getUid());
            tolvenPerson.setDn(tolvenPersonDN);
            ctx = getLadpContext(ldapRealmContext, tolvenPerson.getUid(), oldPassword);
            Attributes attrs = new BasicAttributes(true);
            attrs.put(tolvenPerson.getAttribute("userCertificate"));
            attrs.put(tolvenPerson.getAttribute("userPassword"));
            attrs.put(tolvenPerson.getAttribute("userPKCS12"));
            ctx.modifyAttributes(tolvenPerson.getDn(), DirContext.REPLACE_ATTRIBUTE, attrs);
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Change password for TolvenPerson: " + uid + " in realm: " + realm + " by: " + uid + " is denied");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to change password for TolvenPerson: " + uid + " in realm " + realm + " by: " + uid, ex);
        } finally {
            close(ctx, realm);
        }
    }

    private void close(LdapContext ctx, String realm) {
        try {
            if (ctx != null) {
                ctx.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not close ldap context for realm: " + realm, ex);
        }
    }

    /**
     * Create a TolvenPerson
     * 
     * @param tolvenPerson
     * @param userPKCS12
     * @param realm
     * @param userPassword
     * @param admin
     * @param adminPassword
     * @return
     * @throws AuthenticationException 
     */
    @Override
    public TolvenPerson createTolvenPerson(TolvenPerson tolvenPerson, String uid, String realm, char[] uidPassword, String base64UserPKCS12, String admin, char[] adminPassword) throws AuthenticationException {
        LdapContext ctx = null;
        try {
            if (base64UserPKCS12 == null) {
                /*
                 * TODO gatekeeper needs access to a bean like TolvenProperties, rather than use System.getProperty() directly
                 */
                String userKeysOptional = System.getProperty("tolven.security.user.keysOptional");
                if (Boolean.parseBoolean(userKeysOptional)) {
                    logger.info("tolven.security.user.keysOptional=true, so no user credentials will be generated for " + uid + " in realm: " + realm);
                } else {
                    getCertificateHelper().createCredentials(tolvenPerson, uidPassword);
                    logger.info("Generating user credentials for " + uid + " in realm: " + realm);
                }
            } else {
                getCertificateHelper().updateCredentials(tolvenPerson, base64UserPKCS12, uidPassword);
            }
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            ctx = getLadpContext(ldapRealmContext, admin, adminPassword);
            String tolvenPersonDN = ldapRealmContext.getDN(tolvenPerson.getUid());
            tolvenPerson.setDn(tolvenPersonDN);
            ctx.createSubcontext(tolvenPerson.getDn(), tolvenPerson.dirAttributes(false));
            logger.info("Added " + tolvenPerson.getDn() + " to LDAP realm: " + realm + " for admin: " + admin);
            return tolvenPerson;
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Creation of TolvenPerson: " + tolvenPerson.getUid() + " in realm: " + realm + " by admin: " + admin + " is denied");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create TolvenPerson: " + tolvenPerson.getUid() + " in realm " + realm + " for admin " + admin, ex);
        } finally {
            close(ctx, realm);
        }
    }

    private List<TolvenPerson> findTolvenPerson(LdapContext ctx, String peopleBaseName, String principalLdapName, String realm, int maxResults, int timeLimit) throws AuthenticationException {
        NamingEnumeration<SearchResult> namingEnum = null;
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setCountLimit(maxResults);
        ctls.setTimeLimit(timeLimit);
        ArrayList<TolvenPerson> searchResults = new ArrayList<TolvenPerson>(10);
        try {
            namingEnum = ctx.search(peopleBaseName, principalLdapName, ctls);
            while (namingEnum.hasMore()) {
                SearchResult rslt = namingEnum.next();
                searchResults.add(new TolvenPerson(rslt));
            }
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Search for TolvenPerson: " + principalLdapName + " in realm: " + realm + " is denied");
        } catch (Exception ex) {
            throw new RuntimeException("Could not search for TolvenPerson: " + principalLdapName + " in realm: " + realm + ": ", ex);
        }
        return searchResults;
    }

    /**
     * Find a TolvenPerson
     * 
     * @param uid
     * @param realm
     * @return
     * @throws AuthenticationException
     */
    @Override
    public TolvenPerson findTolvenPerson(String uid, String realm) throws AuthenticationException {
        LdapContext ctx = null;
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            ctx = getLadpContext(ldapRealmContext, ldapRealmContext.getAnonymousUser(), ldapRealmContext.getAnonymousUserPassword().toCharArray());
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctls.setCountLimit(1);
            String principalLdapName = ldapRealmContext.getPrincipalName(uid);
            String basePeopleName = ldapRealmContext.getBasePeopleName();
            List<TolvenPerson> tolvenPersons = findTolvenPerson(ctx, basePeopleName, principalLdapName, realm, 1, 1000);
            if (tolvenPersons.isEmpty()) {
                return null;
            } else {
                return tolvenPersons.get(0);
            }
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Search for TolvenPerson: " + uid + " in realm: " + realm + " is denied");
        } catch (Exception ex) {
            throw new RuntimeException("Could not find user " + uid + " in realm " + realm, ex);
        } finally {
            close(ctx, realm);
        }
    }

    private CertificateHelper getCertificateHelper() {
        if (certificateHelper == null) {
            certificateHelper = new CertificateHelper();
        }
        return certificateHelper;
    }

    private LdapContext getLadpContext(LdapRealmContext ldapRealmContext, String principal, char[] password) throws NamingException {
        LdapContext ctx = getSystemLdapContext(ldapRealmContext);
        String principalDN = ldapRealmContext.getDN(principal);
        ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, principalDN);
        ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, new String(password));
        try {
            //AUTHENTICATE CREDENTIALS BY CREATING AN INITIAL LDAP CONTEXT
            new InitialLdapContext(ctx.getEnvironment(), null);
        } catch (AuthenticationException ex) {
            throw new AuthenticationException("Access denied");
        }
        return ctx;
    }

    private LdapRealmContext getLdapRealmContext(String realm) {
        try {
            InitialContext ictx = new InitialContext();
            TolvenContext tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
            return (LdapRealmContext) tolvenContext.getRealmContext(realm);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get LdapRealmContext", ex);
        }
    }

    private LdapContext getSystemLdapContext(LdapRealmContext ldapRealmContext) {
        String ldapJndiName = ldapRealmContext.getJndiName();
        if (ldapJndiName == null) {
            throw new RuntimeException("The ldapJndiName in the LdapRealmContext is null");
        }
        try {
            InitialLdapContext ictx = new InitialLdapContext();
            LdapContext ctx = (LdapContext) ictx.lookup(ldapJndiName);
            /* Glassfish does not pass through these properties, even though it is aware of them?
             * Without com.sun.jndi.ldap.LdapCtxFactory, authentication is NOT carried out
             */
            ctx.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY,  "com.sun.jndi.ldap.LdapCtxFactory");
            ctx.addToEnvironment("java.naming.ldap.attributes.binary", "userPKCS12");
            if (logger.isDebugEnabled()) {
                String providerURL = (String) ctx.getEnvironment().get(Context.PROVIDER_URL);
                logger.debug("LDAP providerURL=" + providerURL);
            }
            return ctx;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to lookup " + ldapJndiName, ex);
        }
    }

    @Override
    public void resetUserPassword(String uid, String realm, char[] newPassword, String admin, char[] adminPassword) throws AuthenticationException {
        TolvenPerson tolvenPerson = null;
        try {
            tolvenPerson = findTolvenPerson(uid, realm);
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Reset password for TolvenPerson: " + uid + " in realm: " + realm + " by: " + uid + " is denied");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to reset password for TolvenPerson: " + uid + " in realm " + realm + " by: " + uid, ex);
        }
        if (tolvenPerson == null) {
            throw new AuthenticationException("Reset password for TolvenPerson: " + uid + " in realm: " + realm + " by: " + uid + " is denied");
        }
        LdapContext ctx = null;
        try {
            tolvenPerson.setUserPassword(new String(newPassword));
            /*
             * TODO gatekeeper needs access to a bean like TolvenProperties, rather than use System.getProperty() directly
             */
            String userKeysOptional = System.getProperty("tolven.security.user.keysOptional");
            if (Boolean.parseBoolean(userKeysOptional)) {
                logger.info("tolven.security.user.keysOptional=true, so no user credentials generated for " + tolvenPerson.getDn() + " in realm: " + realm);
            } else {
                getCertificateHelper().createCredentials(tolvenPerson, newPassword);
                logger.info("Generated user credentials for " + tolvenPerson.getDn() + " in realm: " + realm);
            }
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            String tolvenPersonDN = ldapRealmContext.getDN(tolvenPerson.getUid());
            tolvenPerson.setDn(tolvenPersonDN);
            ctx = getLadpContext(ldapRealmContext, admin, adminPassword);
            ctx.modifyAttributes(tolvenPerson.getDn(), DirContext.REPLACE_ATTRIBUTE, tolvenPerson.dirAttributes(true));
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Change password for TolvenPerson: " + uid + " in realm: " + realm + " by: " + uid + " is denied");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to change password for TolvenPerson: " + uid + " in realm " + realm + " by: " + uid, ex);
        } finally {
            close(ctx, realm);
        }
    }

    /**
     * 
     * @param uid
     * @param realm
     * @param password
     * @return
     */
    public boolean verifyPassword(String uid, String realm, char[] password) {
        LdapContext ctx = null;
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            //Creating the InitialLdapContext results in password verification
            ctx = getLadpContext(ldapRealmContext, uid, password);
            return true;
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to verify password for uid: " + uid + " in realm " + realm + " by: " + uid, ex);
        } finally {
            close(ctx, realm);
        }
    }

}
