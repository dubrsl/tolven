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

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OBSOLETE: DocType is a Document. A DocType specifies how a particular Document Instance will be handled. 
 * @author John Churin
 */
@Entity
@Table
public class DocType implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="DOC_SEQ_GEN")
    private long id;

    /** Creates a new instance of DocType */
    public DocType() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof DocType)) return false;
        if (this.getId()==((DocType)obj).getId()) return true;
        return false;
    }

    public String toString() {
        return "DocType: " + getId();
    }

    public int hashCode() {
        return new Long( getId()).hashCode();
    }
}
