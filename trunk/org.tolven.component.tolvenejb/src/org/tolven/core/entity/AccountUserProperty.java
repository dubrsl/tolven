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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A general property associated with an account
 * @author John Churin
 *
 */

@Entity
@Table
public class AccountUserProperty implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

	@ManyToOne
	private AccountUser accountUser;

	@Column
    private String propertyName;

	@Column
    private String shortStringValue;

	@Lob
	@Column
    private String longStringValue;

	public String getLongStringValue() {
        return longStringValue;
    }

    public void setLongStringValue(String longStringValue) {
        this.longStringValue = longStringValue;
    }

    public AccountUser getAccountUser() {
		return accountUser;
	}

	public void setAccountUser(AccountUser accountUser) {
		this.accountUser = accountUser;
	}

	public boolean equals(Object obj) {
        if (obj instanceof AccountUserProperty) {
            AccountUserProperty other = (AccountUserProperty) obj;
            if (getAccountUser().getId() == other.getAccountUser().getId()
            && getPropertyName().equals(other.getPropertyName())) return true;
        }
        return false;
    }

    public String toString() {
        return "AccountUserProperty: " + getAccountUser().getId() + " " + getPropertyName() + ": " + getPropertyValue();
    }

    public int hashCode() {
    	return getPropertyName().hashCode();
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		if (shortStringValue!=null) {
			return shortStringValue;
		} else {
			return longStringValue;
		}
	}

	public void setPropertyValue(String propertyValue) {
		if (propertyValue == null) {
			this.shortStringValue = null;
			this.longStringValue = null;
		} else if (propertyValue.length()>255) {
			this.longStringValue = propertyValue;
			this.shortStringValue = null;
		} else {
			this.longStringValue = null;
			this.shortStringValue = propertyValue;
		}
	}

}
