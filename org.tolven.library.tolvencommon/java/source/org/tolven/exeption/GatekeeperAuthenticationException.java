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
 * @version $Id: GatekeeperAuthenticationException.java 3245 2011-09-14 09:12:36Z joe.isaac $
 */
package org.tolven.exeption;

public class GatekeeperAuthenticationException extends GatekeeperSecurityException {

    public static final String PASSWORD_EXPIRED = "passwordExpired";
    public static final String PASSWORD_EXPIRING = "passwordExpiring";

    private String realm;
    private String userId;

    public GatekeeperAuthenticationException() {
    }

    public GatekeeperAuthenticationException(String message) {
        super(message);
    }

    public GatekeeperAuthenticationException(String message, String userId, String realm) {
        super(message, userId, realm);
    }

    public GatekeeperAuthenticationException(String message, String userId, String realm, Throwable cause) {
        super(message, userId, realm, cause);
    }

    public GatekeeperAuthenticationException(String userId, String realm, Throwable cause) {
        super(userId, realm, cause);
    }

    public GatekeeperAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatekeeperAuthenticationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getExceptionHeader() {
        return "Authentication Exception:";
    }

    @Override
    public String getRealm() {
        return realm;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
