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
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.tolven.exeption.GatekeeperSecurityException;
import org.tolven.gatekeeper.LdapLocal;
import org.tolven.ldap.LdapException;
import org.tolven.ldap.LdapManager;
import org.tolven.naming.LdapRealmContext;
import org.tolven.naming.TolvenContext;
import org.tolven.naming.TolvenPerson;
import org.tolven.security.cert.CertificateHelper;
import org.tolven.shiro.realm.ldap.TolvenJndiLdapContextFactory;

/**
 * Interface to communicate with LDAP based on a realm lookup of the Directory service
 * 
 * @author Joseph Isaac
 *
 */
@Stateless()
@Local(LdapLocal.class)
public class LdapBean implements LdapLocal {

    private Logger logger = Logger.getLogger(LdapBean.class);

    /**
     * Change userPassword
     * 
     * @param uid
     * @param oldPassword
     * @param realm
     * @param newPassword
     */
    @Override
    public void changeUserPassword(String uid, char[] oldPassword, String realm, char[] newPassword) {
        LdapManager ldapManager = null;
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            ldapManager = ldapRealmContext.getLdapManager(uid, oldPassword);
            ldapManager.changePassword(newPassword);
        } finally {
            if (ldapManager != null) {
                ldapManager.disconnect();
            }
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
     * Create a TolvenPerson, supplying the uid, realm, userPassword and userPKCS12 explicitly, although
     * tolvenPerson may contain those, as well as other attributes
     * 
     * @param tolvenPerson
     * @param uid
     * @param uidPassword
     * @param realm
     * @param base64UserPKCS12
     * @param admin
     * @param adminPassword
     * @return
     */
    @Override
    public char[] createTolvenPerson(TolvenPerson tolvenPerson, String uid, char[] uidPassword, String realm, String base64UserPKCS12, String admin, char[] adminPassword) {
        LdapManager ldapManager = null;
        try {
            if (base64UserPKCS12 != null) {
                updateUserCredentials(tolvenPerson, uidPassword, base64UserPKCS12);
            }
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            ldapManager = ldapRealmContext.getLdapManager(admin, adminPassword);
            String tolvenPersonDN = ldapRealmContext.getDN(tolvenPerson.getUid());
            char[] generatedPassword = ldapManager.createUser(tolvenPersonDN, uidPassword, tolvenPerson.dirAttributes(false));
            logger.info(admin + " added " + tolvenPersonDN + " to LDAP realm: " + realm);
            return generatedPassword;
        } catch (GatekeeperSecurityException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create TolvenPerson: " + tolvenPerson.getUid() + " in realm " + realm + " for admin " + admin, ex);
        } finally {
            if (ldapManager != null) {
                ldapManager.disconnect();
            }
        }
    }
    
    private void updateUserCredentials(TolvenPerson tolvenPerson, char[] userPassword, String base64UserPKCS12) {
        if (userPassword == null) {
            throw new RuntimeException("A base64UserPKCS12 has been supplied without the accompanying user password");
        }
        byte[] userPKCS12Bytes = null;
        try {
            userPKCS12Bytes = Base64.decodeBase64(base64UserPKCS12.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException("Could not convert base64UserPKCS12 to bytes", ex);
        }
        KeyStore userPKCS12KeyStore = CertificateHelper.getKeyStore(userPKCS12Bytes, userPassword);
        byte[] certBytes = CertificateHelper.getX509CertificateByteArray(userPKCS12KeyStore);
        tolvenPerson.setAttributeValue("userPKCS12", userPKCS12Bytes);
        tolvenPerson.setAttributeValue("userCertificate", certBytes);
    }

    /**
     * Create a TolvenPerson, supplying the uid and realm explicitly, although tolvenPerson may contain those, as well as other attributes
     * The userPassword and credentials will be generated automatically
     * @param tolvenPerson
     * @param uid
     * @param realm
     * @param admin
     * @param adminPassword
     * @return
     */
    public char[] createTolvenPerson(TolvenPerson tolvenPerson, String uid, String realm, String admin, char[] adminPassword) {
        return createTolvenPerson(tolvenPerson, uid, null, realm, null, admin, adminPassword);
    }

    private List<TolvenPerson> findTolvenPerson(LdapContext ctx, String peopleBaseName, String principalLdapName, String realm, int maxResults, int timeLimit) {
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
        } catch (GatekeeperSecurityException ex) {
            throw ex;
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
     */
    @Override
    public TolvenPerson findTolvenPerson(String uid, String realm) {
        LdapContext ctx = null;
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            ctx = getLadpContext(ldapRealmContext.getAnonymousUser(), ldapRealmContext.getAnonymousUserPassword().toCharArray(), realm);
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
        } catch (GatekeeperSecurityException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not find user " + uid + " in realm " + realm, ex);
        } finally {
            close(ctx, realm);
        }
    }

    private LdapContext getLadpContext(String principal, char[] password, String realm) {
        TolvenJndiLdapContextFactory ldapContextFactory = new TolvenJndiLdapContextFactory(realm);
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            String principalDN = ldapRealmContext.getDN(principal);
            return ldapContextFactory.getLdapContext(principalDN, password);
        } catch (GatekeeperSecurityException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not get System ldap context", ex);
        }
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

    /**
     * Reset userPassword
     * 
     * @param uid
     * @param realm
     * @param admin
     * @param adminPassword
     * @return
     */
    @Override
    public char[] resetUserPassword(String uid, String realm, String admin, char[] adminPassword) {
        LdapManager ldapManager = null;
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            ldapManager = ldapRealmContext.getLdapManager(admin, adminPassword);
            String userDN = ldapRealmContext.getDN(uid);
            return ldapManager.resetPassword(userDN);
        } finally {
            if (ldapManager != null) {
                ldapManager.disconnect();
            }
        }
    }

    /**
     * Verify password.
     * @param uid
     * @param password
     * @param realm
     * @return
     */
    @Override
    public boolean verifyPassword(String uid, char[] password, String realm) {
        LdapManager ldapManager = null;
        try {
            LdapRealmContext ldapRealmContext = getLdapRealmContext(realm);
            ldapManager = ldapRealmContext.getLdapManager(uid, password);
            ldapManager.checkPassword();
            return true;
        } catch (LdapException ex) {
            if (ex.getLDAPErrorCode() == LdapException.PASSWORD_VALIDATION) {
                return false;
            } else {
                throw ex;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to verify password for uid: " + uid + " in realm " + realm + " by: " + uid, ex);
        } finally {
            if (ldapManager != null) {
                ldapManager.disconnect();
            }
        }
    }

}
