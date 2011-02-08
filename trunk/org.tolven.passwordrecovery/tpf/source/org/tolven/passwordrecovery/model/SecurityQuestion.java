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
package org.tolven.passwordrecovery.model;

import java.io.Serializable;

/**
 * 
 * @author Joseph Isaac
 * 
 * This entity contains the security questions with their purpose.
 *
 */
public class SecurityQuestion implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;
    private String purpose;
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
