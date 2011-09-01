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
package org.tolven.app.bean;

import org.tolven.core.entity.Account;

/**
 * A handy class used in rules to tell us about the nature of the message.
 * @author John Churin
 *
 */
public final class Mode {
	/**
	 * A Local (to the account) transaction is being processed. 
	 */
	public static final String LOCAL = "local";
	/**
	 * There is no transaction to process for a review. This mode applies when the state of a placeholder (usually a patient)
	 * changes and so the current state of the patient as a whole is reviewed.
	 * Simple transaction-oriented state changes can be handled in local or inbound modes such as adding a new patient to a patient list.
	 * A review is more appropriate when a combination of events determine changes to indexes. For example:
	 * <ul>
	 * <li>A combination of problems and patient demographics determine if a patient should be placed on a particular disease registry.</li>
	 * <li>Removing a patient from an inpatient roster requires a number of states to be considered.</li>
	 * <li>Maintaining a list of "today's patients" requires a review of patients, appointments, and staff</li>
	 * </ul>
	 */
	public static final String REVIEW = "review";
	/**
	 * An inbound transaction (from another account) is being processed. 
	 * In effect, a preprocessing step such as to add the transaction to the new activity list.
	 * However, the transaction can also be fully processed at this point such as for 
	 * research, emergency room network, or public health account that automatically accepts
	 * transactions.
	 */
	public static final String INBOUND = "inbound";
	/**
	 * An outbound transaction (to another account) is being processed. This is in effect a post-processing activity.
	 * In Tolven, there are limited functions that can be performed at this point because the "message" should be
	 * fully formed and approved by the user prior to being submitted.
	 * However, this is a good place to perform technical operations such as to print, email, or route the
	 * message to its destination.
	 */
	public static final String OUTBOUND = "outbound";
	
	private Account thisAccount;
	private long fromAccount; 
	private long toAccount;
	public Mode( Account thisAccount, long fromAccount, long toAccount ) {
		this.thisAccount = thisAccount;
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
	}

	public String getDirection( ) {
		if (fromAccount==0 || fromAccount==thisAccount.getId()) return LOCAL;
		if (fromAccount!=thisAccount.getId()) return INBOUND;
		return OUTBOUND;
	}
	
	public Account getThisAccount() {
		return thisAccount;
	}
	
	public long getFromAccount() {
		return fromAccount;
	}
	
	public long getToAccount() {
		return toAccount;
	}
	
}
