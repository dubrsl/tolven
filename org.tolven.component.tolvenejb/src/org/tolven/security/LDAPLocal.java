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

package org.tolven.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.security.auth.login.LoginException;



/**
 * This is the business interface for LDAP enterprise bean.
 */
@Deprecated
public interface LDAPLocal {
    /**
    * Add a new person to LDAP DB. Note: We let LDAP take care of replica propagation.
     * @throws GeneralSecurityException 
     * @throws NamingException 
    */
    @Deprecated
    public void addPerson(TolvenPerson tp );
    
    /**
    * Return true if the person exists in LDAP. The UID attribute is used for the search and therefore, it should be unique.
     * @throws GeneralSecurityException 
    */

    @Deprecated
    public boolean entryExists( String uid );
    /**
     * Given a Principal, return a TolvenPerson 
     * @throws GeneralSecurityException 
     */

    @Deprecated
    public TolvenPerson createTolvenPerson( String principal );

    /**
      * Update a person to LDAP.
      */
    @Deprecated
     public void updatePerson( TolvenPerson tp );

     /**
      * Delete a UID from LDAP
      */
    @Deprecated
     public void deleteUser( String uid );
     
      /**
      * Delete a person from LDAP.
      */
    @Deprecated
     public void deletePerson( TolvenPerson tp );

     /**
     * Search for matching names. If not connected yet, we'll connect to LDAP now.
     * @throws GeneralSecurityException 
     */
    @Deprecated
    public List<TolvenPerson> search( String criteria, int maxResults, int timeLimit);

    @Deprecated
    public X509Certificate findUserCertificate(String principalName) throws IOException, GeneralSecurityException;

    @Deprecated
    public boolean hasUserCredentials(String principalName);

    @Deprecated
    public void changeUserPassword(String principalName, char[] oldPassword, char[] newPassword) throws IOException, GeneralSecurityException;

    @Deprecated
    public void changeUserPassword(TolvenPerson tolvenPerson, char[] oldPassword, char[] newPassword) throws IOException, GeneralSecurityException;

    @Deprecated
    public PublicKey getPublicKey(String principalName) throws IOException, GeneralSecurityException;

    @Deprecated
    public void createCredentials(TolvenPerson tolvenPerson);

    @Deprecated
    public TolvenPerson findTolvenPerson(String principalName);

    @Deprecated
    public boolean verifyPassword(String principalName, char[] password);

    @Deprecated
    public DirContext authenticate(String ldapPrincipalName, char[] ldapPassword) throws LoginException;

    @Deprecated
    public List<String> findAvailableRoles();
}
