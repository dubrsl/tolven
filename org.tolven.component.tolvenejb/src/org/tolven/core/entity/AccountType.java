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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * AccountType.java
 *
 * Created on April 6, 2006, 12:30 PM
 *
 * AccountType controls basic, low-level behavior of an account. There's a small number of AccountTypes.
 * However, users can configure accounts with many other preferences that expand on the basic account-type.
 * @author John Churin
 */
@Entity
@Table
public class AccountType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private Long id;

    @Column
    private String knownType;
    
    @Column
    private String homePage;

    @Column
    private String createAccountPage;

    @Column
    private String logo;

    @Column
    private String CSS;
    
    @Column
    private boolean readOnly;
    
    @Column
    private Boolean creatable;
    
    @Column
    private String longDesc;
   
    @Column
    private String status;

//    @OneToOne()
//    private Account accountTemplate;

    @Lob
    @Column
    private byte[] rules;

    @Version
    @Column
    private Integer version;
    
    /** Creates a new instance of AccountType */
    public AccountType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    /**
     * This is the unique name of the account-type
     */
    public String getKnownType() {
        return knownType;
    }

    public void setKnownType(String knownType) {
        this.knownType = knownType;
    }
    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * The account used as the template for a new account for this AccountType.
     * the menustructure from this account is copied to the new account.
     * The selected account should not have users (AccountUser).
     * @return
     */
//	public Account getAccountTemplate() {
//		return accountTemplate;
//	}
//
//	public void setAccountTemplate(Account accountTemplate) {
//		this.accountTemplate = accountTemplate;
//	}
    /**
     * Specify if this account-type can be created, typically by users. In other words, should it 
     * be allowed to display in the drop-down list.
     */
	public boolean isCreatable() {
		return creatable;
	}

	public void setCreatable(boolean creatable) {
		this.creatable = creatable;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getCSS() {
		return CSS;
	}

	public void setCSS(String css) {
		CSS = css;
	}

	/**
	 * Rule source associated with this AccountType.
	 * @return
	 */
	public byte[] getRules() {
		return rules;
	}

	public void setRules(byte[] rules) {
		this.rules = rules;
	}
	
	/**
	 * A Version number incremented by the persistence manager. Therefore, 
	 * no set method is provided publicly.
	 * @return The version number of the entity
	 */
	public int getVersion() {
		return version;
	}

	public String getCreateAccountPage() {
		return createAccountPage;
	}

	public void setCreateAccountPage(String createAccountPage) {
		this.createAccountPage = createAccountPage;
	}

	@Override
	public String toString() {
		return getId() + " (" + getKnownType() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AccountType)) return false;
		AccountType other = (AccountType)obj;
		return (other.getId()==getId());
	}

	@Override
	public int hashCode() {
		return Long.valueOf(getId()).hashCode();
	}

}
