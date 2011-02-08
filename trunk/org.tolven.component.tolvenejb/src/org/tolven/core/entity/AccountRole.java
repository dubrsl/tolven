/*
 *  Copyright (C) 2008 Tolven Inc 
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A list of possible roles for an account
 * @author John Churin
 *
 */

@Entity
@Table
public class AccountRole implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

	@ManyToOne
	private Account account;

	@Column
    private String role;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
//	@EmbeddedId
//	private AccountRoleKey key;

	@Column
    private String title;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean equals(Object obj) {
        if (!(obj instanceof AccountRole)) return false;
        if (!(getRole().equals(((AccountRole)obj).getRole()))) return false;
        return true;
    }

    public String toString() {
        return "AccountRole: " + getAccount().getId() + " " + getRole();
    }

    public int hashCode() {
    	return getRole().hashCode();
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

//	public AccountRoleKey getKey() {
//		return key;
//	}
//
//	public void setKey(AccountRoleKey key) {
//		this.key = key;
//	}
	
//	public boolean equals(Object obj) {
//        if (!(obj instanceof AccountRole)) return false;
//        if (this.getKey().equals(((AccountRole)obj).getKey())) return false;
//        return true;
//    }
//
//    public String toString() {
//        return getKey().toString();
//    }
//
//    public int hashCode() {
//    	return getKey().hashCode();
//    }
//
}
