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

public class HypertensionGen extends ChronicDiseaseGen {

	public HypertensionGen( RandomData rng ) {
		this.rng = rng;
		setDiseaseName( "Hypertension" );
		addMatcher( new DemogMatcher( 0, 20, "M", 0.12)); 
		addMatcher( new DemogMatcher( 0, 20, "F", 0.08));
		addMatcher( new DemogMatcher( 20, 40, "M", 0.06));
		addMatcher( new DemogMatcher( 20, 40, "F", 0.04));
		addMatcher( new DemogMatcher( 40, 60, "M", 0.228));
		addMatcher( new DemogMatcher( 40, 60, "F", 0.152));
		addMatcher( new DemogMatcher( 60, 200, "M", 0.426));
		addMatcher( new DemogMatcher( 60, 200, "F", 0.284));
		// Generate a Chest X-ray for hypertensive males over 30
		Scenario chestXRay = new CXRScenario( "Chest X-Ray", "Routine", "Normal");
		this.setScenario(chestXRay);
	}
	@Override
	protected void apply(GenMedical patient, Date eventTime) {
		patient.generateMedication(eventTime,
			"chlorothiazide 500 mg tablets", 
			"30 tablets",
			"take 1 tablet every day for high blood pressure",
			"2");
		patient.generateMedication(eventTime,
			"captopril 25 mg tablets", 
			"60 tablets",
			"take 1 tablet every day for high blood pressure",
			"2");
		super.apply(patient, eventTime);
	}

}
