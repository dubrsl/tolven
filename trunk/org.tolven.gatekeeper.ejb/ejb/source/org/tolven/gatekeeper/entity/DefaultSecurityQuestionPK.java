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
 * @version $Id: DefaultSecurityQuestionPK.java 2592 2011-08-22 08:44:53Z joe.isaac $
 */
package org.tolven.gatekeeper.entity;

import java.io.Serializable;

/**
 * Primary key class for DefaultSecurityQuestion entity
 * 
 * @author Joseph Isaac
 *
 */
public class DefaultSecurityQuestionPK implements Serializable {

    private String purpose;
    private String question;

    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof DefaultSecurityQuestionPK)) {
            return false;
        }
        DefaultSecurityQuestionPK other = (DefaultSecurityQuestionPK) otherOb;
        return ((purpose == null ? other.purpose == null : purpose.equals(other.purpose)) &&
                (question == null ? other.question == null : question.equals(other.question)));
    }

    public String getPurpose() {
        return purpose;
    }

    public String getQuestion() {
        return question;
    }

    public int hashCode() {
        return ((purpose == null ? 0 : purpose.hashCode()) ^
                (question == null ? 0 : question.hashCode()));
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

}
