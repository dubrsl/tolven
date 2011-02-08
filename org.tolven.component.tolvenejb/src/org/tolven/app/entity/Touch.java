/*
 * Copyright (C) 2010 Tolven Inc

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
 * @author <your name>
 * @version $Id: Touch.java,v 1.1 2010/03/06 05:34:11 jchurin Exp $
 */  

package org.tolven.app.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.tolven.core.entity.Account;
/**
 * TouchIf specifies specific placeholder instances that should be re-processed (touched) if 
 * the specified focal placeholder is changed. Note that this is based on instances of placeholders.
 * For example, updating the patient placeholder probably won't cause all encounter placeholder to be updated, only those that are
 * active or recently active. Therefore, entries in this table that have become obsolete are marked as 
 * deleted. A previously deleted entry can become active again. In this case, a new row is added.
 * @author John Churin
 *
 */
@Entity
public class Touch implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
    
    @Column
    private Boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuData focalPlaceholder;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuData updatePlaceholder;

    
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Touch ) {
			Touch other = (Touch) obj;
			if (getFocalPlaceholder().equals(other.getFocalPlaceholder()) &&
				getAccount().equals(other.getAccount()) &&
				getUpdatePlaceholder().equals(other.getUpdatePlaceholder())) {
					return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getFocalPlaceholder().hashCode();
	}

	@Override
	public String toString() {
		return "TouchIf (account " + getAccount().getId() + "), focalPlaceholder: " + getFocalPlaceholder() + " updatePlaceholder: " + getUpdatePlaceholder();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public MenuData getFocalPlaceholder() {
		return focalPlaceholder;
	}

	public void setFocalPlaceholder(MenuData focalPlaceholder) {
		this.focalPlaceholder = focalPlaceholder;
	}
	/**
	 * Get the placeholder that needs to be reevaluated if the focal placeholder is updated.
	 * @return
	 */
	public MenuData getUpdatePlaceholder() {
		return updatePlaceholder;
	}

	public void setUpdatePlaceholder(MenuData updatePlaceholder) {
		this.updatePlaceholder = updatePlaceholder;
	}
    
}
