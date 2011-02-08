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
import java.util.GregorianCalendar;


/**
 * Match on demographics alone. This matcher is "cumulative" such that a 40 year old
 * person could match criteria for a 0-20 year old. And if that happens, then the event
 * will be selected to be within that timeframe. Otherwise, the Even will occur somewhere between the
 * low age and the current date (now).
 * @author John Churin
 */
public class DemogMatcher extends Matcher {
	private int lowAge;
	private int highAge;
	private String gender;
	
	public DemogMatcher( int lowAge, int highAge, String gender, double probability ) {
		super( probability );
		setLowAge( lowAge );
		setHighAge( highAge);
		if (gender!=null) {
			setGender( gender.substring(0, 1).toLowerCase() );
		}
	}

	/**
	 * Try to match the patient to this criteria
	 * In this case, older patients match younger age ranges because the patient was that
	 * age at one time. The event time will be selected accordingly.
	 */
	@Override
	protected boolean match(GenMedical patient) {
		int pAge;
		pAge = patient.getAgeInYears();
//		TolvenLogger.info( "Matcher (" + getGender() + "): " + patient.getFamilyName() + " is: " + pAge + " Male: " + patient.isMale() +  " Female: " + patient.isFemale(), DemogMatcher.class );
		if (pAge < lowAge) { 
//			TolvenLogger.info( "Too young " + lowAge, DemogMatcher.class);
			return false;
		}
		// If we're looking for a male, see if this is not one
		if ("m".equals(getGender())) {
			if (!patient.isMale()) {
//				TolvenLogger.info( "Not male ", DemogMatcher.class);
				return false;
			}
		}
		// If we're looking for a female, see if this is not one
		if ("f".equals(getGender())) {
			if (!patient.isFemale()) {
//				TolvenLogger.info( "Not female ", DemogMatcher.class);
				return false;
			}
		}
		return true;
	}

	protected Date getEarliest( GenMedical patient ) {
		GregorianCalendar eventLow = new GregorianCalendar();
		eventLow.setTime(patient.getDOB());
		eventLow.add( GregorianCalendar.YEAR, getLowAge());
		return eventLow.getTime();
	}

	protected Date getLatest( GenMedical patient ) {
		GregorianCalendar eventHigh = new GregorianCalendar();
		eventHigh.setTime(patient.getDOB());
		eventHigh.add( GregorianCalendar.YEAR, getHighAge());
		return eventHigh.getTime();
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getHighAge() {
		return highAge;
	}

	public void setHighAge(int highAge) {
		this.highAge = highAge;
	}

	public int getLowAge() {
		return lowAge;
	}

	public void setLowAge(int lowAge) {
		this.lowAge = lowAge;
	}

}
