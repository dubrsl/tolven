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
 * @version $Id: GatekeeperSecurityException.java 3245 2011-09-14 09:12:36Z joe.isaac $
 */
package org.tolven.exeption;

public abstract class GatekeeperSecurityException extends RuntimeException {

    public static final String MESSAGE_HEADER = "Message:";
    public static final String REALM_HEADER = "Realm:";
    public static final String SEPARATOR = " ";
    public static final String USER_HEADER = "User:";

    public static GatekeeperSecurityException getRootGatekeeperException(Exception ex) {
        Throwable root = null;
        Throwable t = ex;
        do {
            if (t instanceof GatekeeperSecurityException) {
                root = t;
            }
            t = t.getCause();
        } while (t != null);
        return (GatekeeperSecurityException) root;
    }

    public static GatekeeperSecurityException getTopGatekeeperException(Exception ex) {
        Throwable t = ex;
        do {
            if (t instanceof GatekeeperSecurityException) {
                return (GatekeeperSecurityException) t;
            }
            t = t.getCause();
        } while (t != null);
        return null;
    }

    public GatekeeperSecurityException() {
    }

    public GatekeeperSecurityException(String message) {
        super(message);
    }

    public GatekeeperSecurityException(String message, String userId, String realm) {
        super(message);
        setUserId(userId);
        setRealm(realm);
    }

    public GatekeeperSecurityException(String message, String userId, String realm, Throwable cause) {
        super(message, cause);
        setUserId(userId);
        setRealm(realm);
    }

    public GatekeeperSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatekeeperSecurityException(Throwable cause) {
        super(cause);
    }

    public GatekeeperSecurityException(String userId, String realm, Throwable cause) {
        super(cause);
        setUserId(userId);
        setRealm(realm);
    }

    public abstract String getExceptionHeader();

    public String getFormattedMessage() {
        return getExceptionHeader() + SEPARATOR + USER_HEADER + getUserId() + SEPARATOR + REALM_HEADER + SEPARATOR + MESSAGE_HEADER + getMessage();
    }

    public abstract String getRealm();

    public abstract String getUserId();

    public abstract void setRealm(String realm);

    public abstract void setUserId(String userId);

}
