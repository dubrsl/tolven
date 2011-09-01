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
 * @version $Id: DefaultPasswordBackupPK.java 2592 2011-08-22 08:44:53Z joe.isaac $
 */
package org.tolven.gatekeeper.entity;

import java.io.Serializable;

/**
 * Primary key class for DefaultPasswordBackup entity
 * 
 * @author Joseph Isaac
 *
 */
public class DefaultPasswordBackupPK implements Serializable {

    private String passwordPurpose;
    private String realm;
    private String securityQuestion;
    private String userId;

    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof DefaultPasswordBackupPK)) {
            return false;
        }
        DefaultPasswordBackupPK other = (DefaultPasswordBackupPK) otherOb;
        return ((passwordPurpose == null ? other.passwordPurpose == null : passwordPurpose.equals(other.passwordPurpose)) &&
                (realm == null ? other.realm == null : realm.equals(other.realm)) &&
                (securityQuestion == null ? other.securityQuestion == null : securityQuestion.equals(other.securityQuestion)) &&
                (userId == null ? other.userId == null : userId.equals(other.userId)));
    }

    public String getPasswordPurpose() {
        return passwordPurpose;
    }

    public String getRealm() {
        return realm;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getUserId() {
        return userId;
    }

    public int hashCode() {
        return ((passwordPurpose == null ? 0 : passwordPurpose.hashCode()) ^
                (realm == null ? 0 : realm.hashCode()) ^
                (securityQuestion == null ? 0 : securityQuestion.hashCode()) ^
                (userId == null ? 0 : userId.hashCode()));
    }

    public void setPasswordPurpose(String passwordPurpose) {
        this.passwordPurpose = passwordPurpose;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
