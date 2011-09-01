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
import org.tolven.logging.TolvenLogger;

public class PregnantGen extends DiseaseGen {
	public static long MONTHS = 30L*24L*60L*60L*1000L;
	
	@Override
	protected void apply(GenMedical patient, Date eventTime) {
		super.apply(patient, eventTime);
		Date birthDate = new Date(eventTime.getTime() + getMinimumSpacing());
		TolvenLogger.info( "**********Pregnancy: " + eventTime + " DOB: " + birthDate + " Spacing: " + getMinimumSpacing(), PregnantGen.class);
		if (patient.getNow().after(birthDate)) {
			patient.generateProblem(eventTime, getDiseaseName(), "Inactive");
			// Usually single, sometimes twins, rarely triples
			int allBirths = rng.nextInt(0, 4089000);
			int birthCount = 1;
			if ( allBirths < 7110 ) birthCount = 3;
			else if (allBirths < 128000) birthCount = 2;
			patient.birth(birthCount, birthDate);
		} else {
			// Still pregnant to this day
			patient.generateProblem(eventTime, getDiseaseName(), "Active");
		}
	}

	public PregnantGen( RandomData rng ) {
		this.rng = rng;
		setDiseaseName( "Pregnant" );
		addMatcher( new DemogMatcher( 0, 15, "M", 0.0));
		addMatcher( new DemogMatcher( 0, 15, "F", 0.0));
		addMatcher( new DemogMatcher( 15, 20, "M", 0.0));
		addMatcher( new DemogMatcher( 15, 20, "F", 0.01));
		addMatcher( new DemogMatcher( 20, 40, "M", 0.0));
		addMatcher( new DemogMatcher( 20, 40, "F", 0.03));
		addMatcher( new DemogMatcher( 40, 60, "M", 0.0));
		addMatcher( new DemogMatcher( 40, 60, "F", 0.02));
		addMatcher( new DemogMatcher( 60, 65, "M", 0.0));
		addMatcher( new DemogMatcher( 60, 65, "F", 0.01));
		this.setMinimumSpacing(9*MONTHS);
		this.addRepeatProbability(0.5);
		this.addRepeatProbability(0.25);
		this.addRepeatProbability(0.25);
	}

}
