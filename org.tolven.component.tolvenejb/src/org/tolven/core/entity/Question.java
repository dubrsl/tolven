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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/** 
 * Entity to represent a question asked during user login.
 * @author Sashikanth Vema
 */
@Entity
@Table
public class Question implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

    @Column
    private String question;

    @Column
    private String type;

    @OneToMany(mappedBy = "question", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionAnswer> answers = null;
    
    
    /**
     * Construct an empty Question.
     */
    public Question() {
    }
    /**
     * The unique internal Tolven ID of the question. This ID has no meaning other than uniqueness. 
     * Leave Id null for a new record. The EntityManager will assign a unique Id when it is persisted.
     */
    public long getId() {
        return id;
    }

    public void setId(long val) {
        this.id = val;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Question)) return false;
        if (this.getId()==((Question)obj).getId()) return true;
        return false;
    }

    public String toString() {
        return "Question: " + getId();
    }

    public int hashCode() {
    	if (getId()==0) throw new IllegalStateException( "id not yet established in Question object");
        return new Long( getId()).hashCode();
    }
    /**
     * A String containing the question.
     */
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }   
    
    public List<QuestionAnswer> getAnswers() {
        return answers;
    }
     
    public void setAnswers(List<QuestionAnswer> answers) {
        this.answers = answers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }   

} // class Question
