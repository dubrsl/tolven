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


public class CancerGen extends DiseaseGen {
	@Override
	protected void apply(GenMedical patient, Date eventTime) {
		Date threeYearsAgo = patient.monthsAgo( 36 );
		String status;
		if (eventTime.before(threeYearsAgo)) status = "Active";
		else status = "Inactive";
		patient.generateProblem(eventTime, getDiseaseName(), status);
		super.apply(patient, eventTime);
	}

}
