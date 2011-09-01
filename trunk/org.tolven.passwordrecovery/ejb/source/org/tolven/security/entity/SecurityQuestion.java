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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Joseph Isaac
 * 
 * This entity contains the security questions with their purpose.
 *
 */
@Entity
@Table
public class SecurityQuestion implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CORE_SEQ_GEN")
    private Long id;

    @Column
    private String purpose;

    @Column
    private String question;

    public SecurityQuestion() {
    }
    
    public SecurityQuestion(String question, String purpose) {
        this.question = question;
        this.purpose = purpose;
    }

    public Long getId() {
        return id;
    }
    
    public String getPurpose() {
        return purpose;
    }

    public String getQuestion() {
        return question;
    }

    public String getPurposeAndQuestion() {
        return getPurpose() + ":" + getQuestion();
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    
}
