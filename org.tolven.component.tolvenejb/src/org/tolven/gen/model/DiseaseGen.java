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



public class DiseaseGen extends GenSource {
	private String diseaseName;

	public DiseaseGen() {
	}

	public DiseaseGen(String diseaseName) {
		setDiseaseName( diseaseName );
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}
	@Override
	protected void apply(GenMedical patient, Date eventTime) {
//		TolvenLogger.info( "Patient " + patient.getFamilyName() + " has " + this.getDiseaseName(), DiseaseGen.class);
		super.apply(patient, eventTime);
	}
	
}
