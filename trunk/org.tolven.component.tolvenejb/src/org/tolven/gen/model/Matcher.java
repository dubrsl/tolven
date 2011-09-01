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
package org.tolven.gen.model;

import java.util.Date;

import org.apache.commons.math.random.RandomData;

/**
 * Attempt to match 
 * @author John Churin
 *
 */
public abstract class Matcher {
	private double probability;
	
	public Matcher( double probability ) {
		setProbability( probability );
	}

	/**
	 * See if this source matches the patient's data. This method only determines that
	 * a patient is potentially qualified. It doesn't mean the specified patient will indeed receive this
	 * source of data.
	 * One of the criteria a source can consider is if a patient can have the condition more than once.
	 * So one source might say the patient never having had mumps will have a certain chance of getting mumps.
	 * Then another source will look for patients with mumps and apply a low probability of
	 * getting it again. 
	 * Match criteria is hierarchical. 
	 * @param patient
	 * @return
	 */
	protected abstract boolean match ( GenMedical patient );

	protected abstract Date getEarliest( GenMedical patient );
	protected abstract Date getLatest( GenMedical patient );

	/**
	 * Calculate a reasonable event time given that this matcher matched. 
	 * It probably can't be earlier than the DOB+lowAge and the lesser of DOB+highAge 
	 * and now. It also can't be before the previous occurance of this event + minimum spacing.
	 * @param patient
	 * @return
	 */
	public Date getEventTime( GenMedical patient, RandomData rng, Date minTime ) {
		Date eventLow = getEarliest( patient );
		if (minTime!=null && minTime.after(eventLow)) eventLow = minTime;
		Date eventHigh = getLatest( patient );
		Date eventNow = patient.getNow();
		long msHighOffset;
		if ( eventHigh.after(eventNow))	msHighOffset = eventNow.getTime()- eventLow.getTime();
		else msHighOffset = eventHigh.getTime() - eventLow.getTime();
		Date eventTime;
		if (msHighOffset > 0) {
			long msOffset = rng.nextLong(eventLow.getTime(), eventLow.getTime()+msHighOffset);
			eventTime = new Date(msOffset);
		} else {
			eventTime = eventLow;
		}
		return eventTime;
	}

	/**
	 * The probability (between 0.0 and 1.0 that a patient with matching criteria will
	 * actually match. For example, if the patient has risk factory for diabetes including demographics,
	 * in other word, the match function returns true, then a random number is chosen between 
	 * 0.0 and 1.0. If it is less than this value, then next will return true.
	 * @return
	 */
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
	
}
