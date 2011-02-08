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
package org.tolven.security.bean;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.security.auth.login.LoginException;

import org.tolven.logging.TolvenLogger;
import org.tolven.security.CertificateHelper;
import org.tolven.security.LDAPLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.security.hash.SSHA;

/**
 * This is the bean class for the LDAPBean enterprise bean.
 * Created Mar 26, 2006 5:25:44 PM
 * @author John Churin
 */
@Stateless()
@Local(LDAPLocal.class)
public class LDAPBean implements LDAPLocal {

    private static final String ROLE_ATTRIBUTE_ID_PROPERTY = "roleAttributeID";
    private static final String PRINCIPAL_DN_PREFIX_PROPERTY = "principalDNPrefix";
    private static final String PRINCIPAL_DN_SUFFIX_PROPERTY = "principalDNSuffix";
    private static final String ROLES_CTX_DN_PROPERTY = "rolesCtxDN";

    private CertificateHelper certificateHelper = new CertificateHelper();
    private static Properties ldapProperties;

    public String getBaseDN() {
        return ldapProperties.getProperty(PRINCIPAL_DN_SUFFIX_PROPERTY);
    }

    public String getUIDField() {
        return ldapProperties.getProperty(PRINCIPAL_DN_PREFIX_PROPERTY) + "=";
    }

    public CertificateHelper getCertificateHelper() {
        return certificateHelper;
    }
    
    public LdapContext getCtx() {
        throw new RuntimeException("Calls to LDAP no longer supported");
    }

    /**
     * Search for matching names. If not connected yet, we'll connect to LDAP now.
     */
    public List<TolvenPerson> search(String criteria, int maxResults, int timeLimit) {
        NamingEnumeration<SearchResult> namingEnum = null;
        SearchControls ctls = new SearchControls();
        //      ctls.setReturningAttributes(_dnOnly );
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setCountLimit(maxResults);
        ctls.setTimeLimit(timeLimit);
        ArrayList<TolvenPerson> searchResults = new ArrayList<TolvenPerson>(10);
        try {
            namingEnum = getCtx().search(getBaseDN(), criteria, ctls);
            while (namingEnum.hasMore()) {
                SearchResult rslt = namingEnum.next();
                searchResults.add(new TolvenPerson(rslt));
            }
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), LDAPBean.class);
            //NB If the exception is thrown and makes it out to a remote call, a non-serializable error occurs masking the original error anyway
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        }
        //        TolvenLogger.info( searchResults, LDAPBean.class );
        return searchResults;

    }

    /**
     * Given a Principal, return a TolvenPerson 
     */
    public TolvenPerson createTolvenPerson(String principal) {
        try {
            NamingEnumeration<SearchResult> namingEnum = null;
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctls.setCountLimit(1);
            namingEnum = getCtx().search(getBaseDN(), getUIDField() + principal, ctls);
            TolvenPerson tp = null;
            if (namingEnum.hasMore()) {
                tp = new TolvenPerson(namingEnum.next());
            }
            return tp;
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), LDAPBean.class);
            //NB If the exception is thrown and makes it out to a remote call, a non-serializable error occurs masking the original error anyway
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        }
    }

    /**
     * Return true if the person specified in the supplied uid is in LDAP.
     */
    public boolean entryExists(String uid) {
        try {
            NamingEnumeration<SearchResult> namingEnum = null;
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctls.setCountLimit(1);
            namingEnum = getCtx().search(getBaseDN(), getUIDField() + uid, ctls);
            if (namingEnum.hasMore())
                return true;
            return false;
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), LDAPBean.class);
            //NB If the exception is thrown and makes it out to a remote call, a non-serializable error occurs masking the original error anyway
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        }
    }

    /**
      * Add a new person to LDAP DB. Note: We let LDAP take care of replica propagation.
      */
    public void addPerson(TolvenPerson tp) {
        try {
            tp.generateDN(getBaseDN()); // add full DistinguisingName to TolvenPerson
            TolvenLogger.info("[LDAPBean] Adding " + tp.getDn() + " to LDAP", LDAPBean.class);
            getCtx().createSubcontext(tp.getDn(), tp.dirAttributes(false));
            TolvenLogger.info("Added " + tp.getDn() + " to LDAP", LDAPBean.class);
            //            // Add to the GEN group
            //            BasicAttributes attrs = new BasicAttributes("uniqueMember", newPerson.getNameInNamespace());
            //            getCtx().modifyAttributes(genGroupDN, DirContext.ADD_ATTRIBUTE, attrs);
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), LDAPBean.class);
            //NB If the exception is thrown and makes it out to a remote call, a non-serializable error occurs masking the original error anyway
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        }
    }

    /**
     * Update a person to LDAP.
     */
    public void updatePerson(TolvenPerson tp) {
        try {
            tp.generateDN(getBaseDN());
            getCtx().modifyAttributes(tp.getDn(), DirContext.REPLACE_ATTRIBUTE, tp.dirAttributes(true));
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), LDAPBean.class);
            //NB If the exception is thrown and makes it out to a remote call, a non-serializable error occurs masking the original error anyway
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        }
    }

    /**
     * Delete a UID from LDAP
     */
    public void deleteUser(String uid) {
        String path = getUIDField() + uid + "," + getBaseDN();
        try {
            getCtx().destroySubcontext(path);
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), LDAPBean.class);
            //NB If the exception is thrown and makes it out to a remote call, a non-serializable error occurs masking the original error anyway
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        }
    }

    /**
    * Delete a person from LDAP.
    */
    public void deletePerson(TolvenPerson tp) {
        try {
            // Remove from Gen group
            //            BasicAttributes attrs = new BasicAttributes("uniqueMember", tp.getDn());
            //            getCtx().modifyAttributes(genGroupDN, DirContext.REMOVE_ATTRIBUTE, attrs);
            getCtx().destroySubcontext(tp.getDn());
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), LDAPBean.class);
            //NB If the exception is thrown and makes it out to a remote call, a non-serializable error occurs masking the original error anyway
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        }

    }

    public TolvenPerson findTolvenPerson(String principalName) {
        TolvenPerson tolvenPerson = findTolvenPersonIncludingPassword(principalName);
        if (tolvenPerson != null) {
            tolvenPerson.setUserPassword(null);
        }
        return tolvenPerson;
    }

    private TolvenPerson findTolvenPersonIncludingPassword(String principalName) {
        String uidPath = getUIDField() + principalName;
        List<TolvenPerson> tolvenPersons = search(uidPath, 1, 1000);
        if (tolvenPersons.isEmpty()) {
            return null;
        } else {
            return tolvenPersons.get(0);
        }
    }

    public PublicKey getPublicKey(String principalName) throws IOException, GeneralSecurityException {
        TolvenPerson tolvenPerson = findTolvenPersonIncludingPassword(principalName);
        if (tolvenPerson == null) {
            return null;
        } else {
            if (tolvenPerson.getUserCertificate() == null) {
                return null;
            }
            X509Certificate x509Certificate = CertificateHelper.getX509Certificate(tolvenPerson.getUserCertificate());
            return x509Certificate.getPublicKey();
        }
    }

    public void createCredentials(TolvenPerson tolvenPerson) {
        getCertificateHelper().createCredentials(tolvenPerson);
    }

    public X509Certificate findUserCertificate(String principalName) throws IOException, GeneralSecurityException {
        TolvenPerson tolvenPerson = findTolvenPersonIncludingPassword(principalName);
        byte[] bytes = tolvenPerson.getUserCertificate();
        if (bytes == null) {
            return null;
        } else {
            return CertificateHelper.getX509Certificate(bytes);
        }
    }

    public boolean hasUserCredentials(String principalName) {
        TolvenPerson tolvenPerson = findTolvenPersonIncludingPassword(principalName);
        if (tolvenPerson == null) {
            throw new RuntimeException("User '" + principalName + " does not exist");
        }
        return tolvenPerson.getUserPKCS12() != null;
    }

    public void changeUserPassword(String principalName, char[] oldPassword, char[] newPassword) throws IOException, GeneralSecurityException {
        TolvenPerson tolvenPerson = findTolvenPersonIncludingPassword(principalName);
        tolvenPerson.setUserPassword(new String(newPassword));
        if (tolvenPerson == null) {
            throw new RuntimeException("No user exists: " + principalName);
        }
        changeUserPassword(tolvenPerson, oldPassword, newPassword);
    }

    public void changeUserPassword(TolvenPerson tolvenPerson, char[] oldPassword, char[] newPassword) throws IOException, GeneralSecurityException {
        byte[] bytes = tolvenPerson.getUserPKCS12();
        if (bytes == null) {
            throw new RuntimeException("User: '" + tolvenPerson.getDn() + "' has no user PKCS12 keystore");
        }
        KeyStore keyStore = CertificateHelper.getKeyStore(bytes, oldPassword);
        X509Certificate userCertificate = CertificateHelper.getX509Certificate(tolvenPerson.getUserCertificate());
        if (userCertificate == null) {
            throw new RuntimeException("No userCertificate found for user=" + tolvenPerson.getUid());
        }
        String alias = tolvenPerson.getUid();
        X509Certificate keyStoreUserCertificate = (X509Certificate) keyStore.getCertificate(alias);
        if (keyStoreUserCertificate == null) {
            throw new RuntimeException("No certificate found in userPKCS12 keystore for user=" + tolvenPerson.getUid() + " with alias: " + alias);
        }
        CertificateHelper.changeKeyStorePassword(keyStore, alias, oldPassword, newPassword);
        byte[] newKeyStoreBytes = CertificateHelper.toByteArray(keyStore, newPassword);
        tolvenPerson.setUserPassword(new String(newPassword));
        tolvenPerson.setUserPKCS12(newKeyStoreBytes);
        updatePerson(tolvenPerson);
    }

    /**
     * Verify the password of a user by attempting to login to the tolvenLDAP domain with the supplied principalName and password
     */
    public boolean verifyPassword(String principalName, char[] password) {
        try {
            authenticate(principalName, password);
        } catch (Exception ex) {
            return false; 
        }
        return true;
    }

    /**
     * Authenticate the principalName and password against LDAP, and if successful return a context
     * @param ldapPrincipalName
     * @param ldapPassword
     * @return
     * @throws NamingException
     */
    public DirContext authenticate(String ldapPrincipalName, char[] ldapPassword) throws LoginException {
        /*
         * The InitialContext must first be primed with the user's credentials as well as actually carrying out a search. This provides authentication.
         * Ensure that ldapPrincipalName and ldapPassword does not get mixed with the instance variable
         * Context.SECURITY_AUTHENTICATION set to "simple" must also be specified, otherwise the default is "none" i.e. no authentication at all
         */
        NamingEnumeration<SearchResult> namingEnum = null;
        try {
            String shortPrincipalDN = "uid=" + ldapPrincipalName;
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctls.setCountLimit(1);
            ctls.setTimeLimit(1000);
            /*
             * Search for the principal's TolvenPerson to see if it's there and exercise authentication retrieving it
             */
            TolvenPerson tolvenPerson = null;
            namingEnum = getCtx().search(getBaseDN(), shortPrincipalDN, ctls);
            SearchResult rslt = null;
            while (namingEnum.hasMore()) {
                rslt = namingEnum.next();
                tolvenPerson = new TolvenPerson(rslt);
            }
            if (tolvenPerson == null) {
                throw new LoginException("Authentication Failed");
            }
            Attributes attrs = rslt.getAttributes();
            boolean authenticated = SSHA.checkPassword(ldapPassword, (byte[]) attrs.get("userPassword").get());
            if(!authenticated) {
                throw new LoginException("Authentication Failed");
            }
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), LDAPBean.class);
            //NB If the exception is thrown and makes it out to a remote call, a non-serializable error occurs masking the original error anyway
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        } finally {
            try {
                if (namingEnum != null) {
                    namingEnum.close();
                }
            } catch (NamingException ex) {
                //does not matter
            }
        }
        return getCtx();
    }

    public List<String> findAvailableRoles() {
        List<String> roleNames = new ArrayList<String>();
        NamingEnumeration<SearchResult> namingEnum = null;
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String roleAttributeId = ldapProperties.getProperty(ROLE_ATTRIBUTE_ID_PROPERTY);
        ctls.setReturningAttributes(new String[] { roleAttributeId });
        ctls.setTimeLimit(10000);
        String rolesCtxDN = ldapProperties.getProperty(ROLES_CTX_DN_PROPERTY);
        try {
            namingEnum = getCtx().search(rolesCtxDN, "(cn=*)", ctls);
            while (namingEnum.hasMore()) {
                SearchResult sr = (SearchResult) namingEnum.next();
                Attributes attrs = sr.getAttributes();
                Attribute roles = attrs.get(roleAttributeId);
                for (int i = 0; i < roles.size(); i++) {
                    String roleName = (String) roles.get(i);
                    roleNames.add(roleName);
                }
            }
        } catch (NamingException ex) {
            throw new RuntimeException("Could not find available roles", ex);
        }
        return roleNames;
    }
    
    static {
        String propertyFileName = LDAPBean.class.getSimpleName() + ".properties";
        try {
            ldapProperties = new Properties();
            InputStream in = LDAPBean.class.getResourceAsStream(propertyFileName);
            if (in != null) {
                ldapProperties.load(in);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load ldap properties from: " + propertyFileName, ex);
        }
        if (ldapProperties.getProperty(ROLE_ATTRIBUTE_ID_PROPERTY) == null) {
            throw new RuntimeException("Could not find property: " + ROLE_ATTRIBUTE_ID_PROPERTY + " in " + propertyFileName);
        }
        if (ldapProperties.getProperty(ROLES_CTX_DN_PROPERTY) == null) {
            throw new RuntimeException("Could not find property: " + ROLES_CTX_DN_PROPERTY + " in " + propertyFileName);
        }
        if (ldapProperties.getProperty(PRINCIPAL_DN_PREFIX_PROPERTY) == null) {
            throw new RuntimeException("Could not find property: " + PRINCIPAL_DN_PREFIX_PROPERTY + " in " + propertyFileName);
        }
        if (ldapProperties.getProperty(PRINCIPAL_DN_SUFFIX_PROPERTY) == null) {
            throw new RuntimeException("Could not find property: " + PRINCIPAL_DN_SUFFIX_PROPERTY + " in " + propertyFileName);
        }
    }

}
