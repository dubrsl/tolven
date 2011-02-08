/*
 *  Copyright (C) 2007 Tolven Inc 
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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This entity represents allowable points of exchange between accounts. There are two types of objects 
 * due to the boundary between accounts.  These two types of entries generally work in pairs. Both sides must have active entries
 * for a message to exchange. 
 * A sender must be willing to send to a receiving account and
 * a receiver must be willing to receive from a sending account. Further, the roles may, but are not required,
 * to be reversed. If the reverse is not provided, then all communication between two parties will be one way.
 * Example of a complete, two-way channel: 
 * <ul>
 * <li>Patient is interested in sending to a provider</li>
 * <li>provider allows patient to send provider data</li>
 * <li>Provider is interested in sending to a patient</li>
 * <li>Patient allows provider to send patient data</li>
 * </ul>
 * <p>This entity has a separate id. This allows, over time, entries to be added and then subsequently
 * discontinued. In that case, a new agreement is a new row in the table. But only one row for each 
 * account/otherAccount/direction combination should exist at a time with a status of active.
 * @author John Churin
 */
@Entity
@Table
public class AccountExchange implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Direction {SEND, RECEIVE};
	
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

    @ManyToOne
    private Account account;
	
	@Enumerated( EnumType.STRING )
	private Direction direction;
	
    @ManyToOne
    private Account otherAccount;
    
    @Column
    private String name;

    @Column
    private String status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date effectiveTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date expirationTime;

    /**
     * The account that owns this exchange
     * @return
     */
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	/**
	 * The direction of the exchange (SEND or RECEIVE)
	 * @return
	 */
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * The effective time of this exchange agreement
	 * @return
	 */
	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	/**
	 * The effective time of this exchange agreement
	 * @return
	 */
	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
	/**
	 * Unique id of this exchange record
	 * @return
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	/**
	 * The distal account. In the case of SEND, the account we want to send to. In the case
	 * of RECEIVE, the account we will allow to send to us.
	 * @return
	 */
	public Account getOtherAccount() {
		return otherAccount;
	}

	public void setOtherAccount(Account otherAccount) {
		this.otherAccount = otherAccount;
	}
	/**
	 * NEW means an exchange is pending. ACTIVE means the exchange agreement is active. This does not make any assumption about the
	 * other party. For example, while Account A says it is willing to receive from Account B, Account B may not
	 * be willing to send information to Account A. The status of the "other" end point can change
	 * independently of this account. Any other status suggests an exchange that is no longer ACTIVE and thus
	 * is not considered for exchange.
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * The name of the connection. 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
