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

public class RadiologyScenario extends Scenario {
	private String test;
	private String report;
	private String interpretation;

	public String getInterpretation() {
		return interpretation;
	}

	public void setInterpretation(String interpretation) {
		this.interpretation = interpretation;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public RadiologyScenario(String test, String report, String interpretation) {
		this.test = test;
		this.report = report;
		this.interpretation = interpretation;
	}
	
	/**
	 * Actually create a radiology report at the start time (we ignore endTime).
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
	   	patient.generateRadResult( startTime, test, report, interpretation );
		return true;
	}
	

}
