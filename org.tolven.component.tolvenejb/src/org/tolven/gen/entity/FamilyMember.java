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
package org.tolven.gen.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Tie a person to a FamilyUnit in the specified role
 * @author John Churin
 *
 */
@Entity
@Table
public class FamilyMember implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum FamilyMemberRole { MOTHER, FATHER, CHILD, GRANDMOTHER,
        GRANDFATHER }

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="GEN_SEQ_GEN")
    private long id;

    @ManyToOne
    private FamilyUnit familyUnit;
    
    @ManyToOne
    private VirtualPerson person;
    
    @Column
    private int memberNumber;
    
    @Column
    private FamilyMemberRole role;

    public String toString()
    {
        StringBuffer sb = new StringBuffer(100);
        sb.append( getRole( )); sb.append( ": ");
        sb.append( getPerson( ).getFirst()); sb.append( " ");
        sb.append( getPerson( ).getMiddle()); sb.append( " ");
        sb.append( getPerson( ).getLast()); sb.append( " ");
        sb.append( getPerson( ).getGender()); sb.append( " ");
        sb.append( getPerson( ).getFormattedDob()); sb.append( " ");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof FamilyMember ) {
            if (this.getMemberNumber()==((FamilyMember)obj).getMemberNumber()) return true;
        }
        return false;
    }

    public int hashCode() {
        return (int) (getId() % Integer.MAX_VALUE);
    }

	public FamilyUnit getFamilyUnit() {
		return familyUnit;
	}

	public void setFamilyUnit(FamilyUnit familyUnit) {
		this.familyUnit = familyUnit;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public VirtualPerson getPerson() {
		return person;
	}

	public void setPerson(VirtualPerson person) {
		this.person = person;
	}

	public FamilyMemberRole getRole() {
		return role;
	}

	public void setRole(FamilyMemberRole role) {
		this.role = role;
	}

	public int getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(int memberNumber) {
		this.memberNumber = memberNumber;
	}

}
