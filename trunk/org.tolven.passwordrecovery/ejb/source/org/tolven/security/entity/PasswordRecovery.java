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
package org.tolven.security.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author Joseph Isaac
 * 
 * This entity contains encrypted passwords, which can be used when a user has forgotten their main password.
 * Access is based on security questions/answers specific to the user, and the purpose for the password.
 *
 */
@Entity
@Table
public class PasswordRecovery {
    
    private static final long serialVersionUID = 1L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date creation;
    
    @Lob
    @Basic
    @Column
    private byte[] encryptedPassword;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CORE_SEQ_GEN")
    private long id;
    
    @Column
    private int iterationCount;

    @Column
    private String securityQuestion;

    @Column
    private String passwordPurpose;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column
    private byte[] salt;

    @Column
    private String status;

    @Column
    private long userId;

    public Date getCreation() {
        return creation;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }
    
    public Long getId() {
        return id;
    }
    
    public int getIterationCount() {
        return iterationCount;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getPasswordPurpose() {
        return passwordPurpose;
    }
    
    public byte[] getSalt() {
        return salt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public long getUserId() {
        return userId;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }
    
    public void setEncryptedPassword(byte[] encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setPasswordPurpose(String passwordPurpose) {
        this.passwordPurpose = passwordPurpose;
    }
    
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
}
