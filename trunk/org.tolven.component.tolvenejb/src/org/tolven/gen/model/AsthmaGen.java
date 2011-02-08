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

public class AsthmaGen extends ChronicDiseaseGen {

	public AsthmaGen( RandomData rng ) {
		this.rng = rng;
		setDiseaseName( "Asthma" );
		addMatcher( new DemogMatcher( 0, 20, "M", 0.035));
		addMatcher( new DemogMatcher( 0, 20, "F", 0.035));
		addMatcher( new DemogMatcher( 20, 40, "M", 0.045));
		addMatcher( new DemogMatcher( 20, 40, "F", 0.045));
		addMatcher( new DemogMatcher( 40, 60, "M", 0.035));
		addMatcher( new DemogMatcher( 40, 60, "F", 0.035));
		addMatcher( new DemogMatcher( 60, 200, "M", 0.03));
		addMatcher( new DemogMatcher( 60, 200, "F", 0.03));
		
	}
	@Override
	protected void apply(GenMedical patient, Date eventTime) {
		patient.generateMedication(eventTime,
			"salmeterol xinafoate inhalation powder", 
			"1 diskus",
			"one inhalation  twice daily for asthma",
			"2");
		super.apply(patient, eventTime);
	}

}
