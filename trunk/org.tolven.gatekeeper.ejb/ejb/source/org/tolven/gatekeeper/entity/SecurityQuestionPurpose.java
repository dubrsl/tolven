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
package org.tolven.gatekeeper.entity;

/**
 * 
 * @author Joseph Isaac
 * 
 * This enumeration defines the purpose of a security question e.g. password_recovery.
 *
 */
public enum SecurityQuestionPurpose {
    
    LOGIN_PASSWORD_BACKUP("login_password_recovery");

    private final String value;

    SecurityQuestionPurpose(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SecurityQuestionPurpose fromValue(String v) {
        for (SecurityQuestionPurpose c : SecurityQuestionPurpose.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
