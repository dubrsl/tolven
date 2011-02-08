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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * An OID descriptor. Since OIDs are unique in the world, one would expect that an OID can be the primary key of this entity.
 * The problem is that the source of the information can vary. For example, two different users could declare that they have a certain OID.
 * Tolven has no way to be sure which user actually owns that OID. Only OIDs that Tolven uses for it's operational purpose can
 * be validated by Tolven. Using a surrogate primary key also allows OIDs assigned incorrectly to be corrected without disrupting the rest of the
 * data model.
 * @author John Churin
 */
@Entity
@Table
public class OID implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

    @Column
    private String oid;

    @Column
    private String name;
    
    @ManyToOne
    private OID parent;
    
    @Column
    private String description;

    @ManyToOne
    @JoinColumn
    private Account owner;
    
    /** Creates a new instance of OID */
    public OID() {
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof OID)) return false;
        if (this.getId()==((OID)obj).getId()) return true;
        return false;
    }
    
    public String toString() {
        return "Oid: " + getId();
    }
    
    public int hashCode() {
        return new Long( getId()).hashCode();
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * The BaseName is the name of the oid upon which this OID is based. If the base name's OID changes, then
     * all child 
     */

    public OID getParent() {
        return parent;
    }

    public void setParent(OID parent) {
        this.parent = parent;
    }

    /**
     * The owner account for this OID. If null, then the Oid is a system-defined OID.
     */
    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
