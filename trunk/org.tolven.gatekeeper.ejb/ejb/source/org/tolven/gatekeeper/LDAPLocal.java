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
package org.tolven.gatekeeper;

import javax.naming.AuthenticationException;

import org.tolven.naming.TolvenPerson;

/**
 * Interface to communicate with LDAP based on a realm lookup of the Directory service
 * 
 * @author Joseph Isaac
 *
 */
public interface LDAPLocal {

    /**
     * Change userPassword
     * 
     * @param uid
     * @param realm
     * @param oldPassword
     * @param newPassword
     * @throws AuthenticationException
     */
    public void changeUserPassword(String uid, String realm, char[] oldPassword, char[] newPassword) throws AuthenticationException;

    /**
     * Create a TolvenPerson, supplying the uid, realm, userPassword and userPKCS12 explicitly, although
     * tolvenPerson may contain those, as well as other attributes
     * 
     * @param tolvenPerson
     * @param uid
     * @param realm
     * @param uidPassword
     * @param base64UserPKCS12
     * @param admin
     * @param adminPassword
     * @return
     * @throws AuthenticationException
     */
    public TolvenPerson createTolvenPerson(TolvenPerson tolvenPerson, String uid, String realm, char[] uidPassword, String base64UserPKCS12, String admin, char[] adminPassword) throws AuthenticationException;

    /**
     * Find a TolvenPerson
     * 
     * @param uid
     * @param realm
     * @return
     * @throws AuthenticationException
     */
    public TolvenPerson findTolvenPerson(String uid, String realm) throws AuthenticationException;
    
    /**
     * Reset userPassword
     * 
     * @param uid
     * @param realm
     * @param newPassword
     * @param admin
     * @param adminPassword
     * @return
     */
    public void resetUserPassword(String uid, String realm, char[] newPassword, String admin, char[] adminPassword) throws AuthenticationException;

    /**
     * Verify password.
     * 
     * @param uid
     * @param realm
     * @param password
     * @return
     */
    public boolean verifyPassword(String uid, String realm, char[] password);
    
}
