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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * If tolven.register.referenceRequired=true, then new users must provide the referenceCode from
 * a sponsorship record in order to create a new account. This is not intended as a security feature.
 * An account administrator can sponsor new users into the system by creating one or more sponsorship
 * records and distributing the referenceCode to user in whatever way they find appropriate.
 * This is different from the invitation mechanism which is generally used for targeting specific known
 * people to become users. Using the sponsorship mechanism generally means the sponsor does not know who the
 * user is or if it does known the user, it may not known which email account, if any, will be used. 
 * Sponsorship does <b>not</b> mean the user becomes a user of the sponsoring account.
 * @author John Churin
 */

@Entity
@Table
public class Sponsorship implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

    @ManyToOne
    private Account account;

    @Column
    private String title;

    @Column
    private String referenceCode;

    /**
	 * This ReferenceCode can be supplied to users without the sponsoring account knowing the user id.
	 * @return
	 */
	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	/**
	 * The account sponsoring this/these users.
	 * @return
	 */
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
