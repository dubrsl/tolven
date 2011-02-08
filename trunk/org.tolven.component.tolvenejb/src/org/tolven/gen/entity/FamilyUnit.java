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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.tolven.gen.entity.FamilyMember.FamilyMemberRole;

/**
 * Identifies a family unit. In this case, everyone lives together. Family Member a part of the family unit.
 * This is part of the virtual person generator. Therefore, these are all imaginary people.
 * @author John Churin
 *
 */
@Entity
@Table
public class FamilyUnit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="GEN_SEQ_GEN")
    private long id;

    @Column
    private String familyName;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String zip;

	// Bidirectional relationship that we don't own, but can navigate to.
	@OneToMany(mappedBy = "familyUnit", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<FamilyMember> members = null;

	public String toString()
    {
        StringBuffer sb = new StringBuffer(100);
    	sb.append("\n");
        sb.append( getFamilyName( )); sb.append( " family at ");
        sb.append( getAddress( )); sb.append( ", ");
        sb.append( getCity( )); sb.append( ", ");
        sb.append( getState( )); sb.append( "  ");
        sb.append( getZip( )); sb.append( " ");
    	sb.append("\n");
        for ( FamilyMember member: getMembers()) {
        	sb.append("    ");
        	sb.append(member.toString());
        	sb.append("\n");
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof FamilyUnit ) {
            if (getId()==((FamilyUnit)obj).getId()) return true;
        }
        return false;
    }

    public int hashCode() {
        return (int) (getId() % Integer.MAX_VALUE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public Set<FamilyMember> getMembers() {
		return members;
	}

	public void setMembers(Set<FamilyMember> members) {
		this.members = members;
	}

	/**
	 * Add a family member to this family unit and also set the reference in the member to this family unit.
	 * @param member
	 */
	public void addMember( FamilyMember member ) {
		if (this.members==null) {
			this.members = new HashSet<FamilyMember>( 5 );
		}
		this.members.add( member );
		member.setMemberNumber(this.members.size());
		member.setFamilyUnit( this );
	}

	/**
	 * Add a person to this family unit in the specified role. 
	 * @param member
	 */
	public FamilyMember addMember( VirtualPerson person, FamilyMemberRole role ) {
		FamilyMember member = new FamilyMember();
		member.setPerson( person );
		member.setRole( role );
		this.addMember( member );
		return member;
	}

}
