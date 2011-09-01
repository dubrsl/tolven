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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * 
 * @author Joseph Isaac
 * 
 * This entity contains the security questions with their purpose.
 *
 */
@Entity
@IdClass(DefaultSecurityQuestionPK.class)
public class DefaultSecurityQuestion implements SecurityQuestion, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column
    private String purpose;

    @Id
    @Column
    private String question;

    public DefaultSecurityQuestion() {
    }

    public DefaultSecurityQuestion(String question, String purpose) {
        this.question = question;
        this.purpose = purpose;
    }

    @Override
    public String getPurpose() {
        return purpose;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

}
