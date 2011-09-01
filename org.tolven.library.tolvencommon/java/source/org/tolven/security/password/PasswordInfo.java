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
package org.tolven.security.password;

/**
 * This is a transient class holds password information and relies for protection on those who can obtain the instance of Passwords which contains them.
 * 
 * @author Joseph Isaac
 *
 */
public final class PasswordInfo {

    private String refId;
    private String type;
    transient private char[] password;

    public PasswordInfo(String refId, String type) {
        this.refId = refId;
        this.type = type;
    }

    /**
     * Return the refId of this password.
     * @return
     */
    public String getRefId() {
        return refId;
    }

    /**
     * Returns a string representing the type of the password
     * @return
     */
    public String getType() {
        return type;
    }
    
    /**
     * Return the password, but only to members of this package. To access the password, the call has to be made via a Passwords instance
     * @param token
     * @return
     */
    char[] getPassword() {
        return password;
    }
    
    /**
     * This method uses package level security to protect the password
     * @param password
     */
    void setPassword(char[] password) {
        this.password = password;
    }
    
}
