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

public class Scenario {
	private String title;
	
	public Scenario( ) {
		
	}
	public Scenario( String title ) {
		this.title = title;
	}
	
	/**
	 * 
	 * @param patient
	 * @param startTime
	 * @param endTime
	 * @param rng
	 * @return Return true if this scenario did anything or not. For example, the select-one scenario
	 * means the first (child) scenario to return true finishes the lot. 
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng) {
//		TolvenLogger.info( "Generic Scenario " + getTitle() 
//			+ " for " + patient.getVp().getFirst() + " " + patient.getVp().getLast() 
//			+ " starting " + startTime + " ending " + endTime, Scenario.class);
		return false;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
}
