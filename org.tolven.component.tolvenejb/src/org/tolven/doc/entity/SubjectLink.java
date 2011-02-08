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
package org.tolven.doc.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>A SubjectLink record associates a document with an Account, User, Sponsor, or HealthRecord.
 * In a traditional design, this one-to-one relationship would simply be stored as a column, not an entity.
 * However, for security purposes, we provide an extra link between document and it's subject. </p>
 * <p> Additionally, the ids are encrypted, but in different ways. All documents are encrypted so that only the
 * subject is able to read it. Thus, encrypted inside the document is a link to the subject.</p>
 * <p>Subject rows are stored in plain text. The subject id (account, healthRecord, etc) provides no way to
 * discover which documents are associated with that subject. Therefore, if one could gain access to a document,
 * it would be unreadable because it is encrypted. And, if one had a Tolven ID for a subject, one could not
 * find which documents are associated with that subject.
 * 
 * @author John Churin
 */
@Entity
@Table
public class SubjectLink {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="DOC_SEQ_GEN")
    private Long id;

    private String documentId;
    private String documentIdMethod;
    private String subjectId;
    private String subjectIdMethod;
    
    /**
     * Creates a new instance of SubjectLink
     */
    public SubjectLink() {
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof SubjectLink)) return false;
        if (this.getId().longValue()==((SubjectLink)obj).getId().longValue()) return true;
        return false;
    }

    public String toString() {
        return "Subject: " + getId();
    }

    public int hashCode() {
        return getId().hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
