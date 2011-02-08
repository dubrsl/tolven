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
package org.tolven.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Specifies all the valid answers for a question defined in Question entity. 
 * 
 * @author Sashikanth Vema
 */
@Entity
@Table
public class QuestionAnswer implements Serializable {
    
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

    @ManyToOne
    @JoinColumn
    private Question question;

    @Column
    private String answer;
    
    /**
     * Construct an empty QuestionAnswer. 
     */
    public QuestionAnswer() {
    }


    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    /**
     * The meaningless, unique ID for this association. Leave Id null for a new record. The EntityManager will assign a unique Id when it is persisted. There may be 
     * more than one AccountUser association for a single combination of Account and TolvenUser. Typically only one is valid at any given point in time.
     */
    public long getId() {
        return id;
    }

    public void setId(long val) {
        this.id = val;
    }

	public boolean equals(Object obj) {
        if (!(obj instanceof QuestionAnswer)) return false;
        if (this.getId()==((QuestionAnswer)obj).getId()) return true;
        return false;
    }

    public String toString() {
        return "QuestionAnswer: " + getId();
    }

    public int hashCode() {
    	if (getId()==0) throw new IllegalStateException( "id not yet established in QuestionAnswer object");
        return new Long( getId()).hashCode();
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
  
} // class QuestionAnswer
