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

public class ProbabilityScenario extends Scenario {
	private Scenario scenarioToApply;
	private double probability;

	public ProbabilityScenario( double probability, Scenario scenarioToApply ) {
		this.probability = probability;
		this.scenarioToApply = scenarioToApply;
		setTitle( "Probability " + getProbability() + " " + getScenarioToApply().getTitle());
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
		return getScenarioToApply().apply(patient, startTime, endTime, rng );
	}

	public Scenario getScenarioToApply() {
		return scenarioToApply;
	}

	public void setScenarioToApply(Scenario scenarioToApply) {
		this.scenarioToApply = scenarioToApply;
	}
}
