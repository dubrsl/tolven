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

import org.tolven.naming.TolvenPerson;

/**
 * Interface to communicate with LDAP based on a realm lookup of the Directory service
 * 
 * @author Joseph Isaac
 *
 */
public interface LdapLocal {

    /**
     * Change userPassword
     * 
     * @param uid
     * @param oldPassword
     * @param realm
     * @param newPassword
     */
    public void changeUserPassword(String uid, char[] oldPassword, String realm, char[] newPassword);

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
    public char[] createTolvenPerson(TolvenPerson tolvenPerson, String uid, char[] uidPassword, String realm, String base64UserPKCS12, String admin, char[] adminPassword);

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
    public char[] createTolvenPerson(TolvenPerson tolvenPerson, String uid, String realm, String admin, char[] adminPassword);

    /**
     * Find a TolvenPerson
     * 
     * @param uid
     * @param realm
     * @return
     */
    public TolvenPerson findTolvenPerson(String uid, String realm);

    /**
     * Reset userPassword
     * 
     * @param uid
     * @param realm
     * @param admin
     * @param adminPassword
     * @return
     */
    public char[] resetUserPassword(String uid, String realm, String admin, char[] adminPassword);

    /**
     * Verify password.
     * @param uid
     * @param password
     * @param realm
     * @return
     */
    public boolean verifyPassword(String uid, char[] password, String realm);

}
