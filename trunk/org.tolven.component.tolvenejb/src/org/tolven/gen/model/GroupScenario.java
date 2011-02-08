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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math.random.RandomData;

/**
 * Apply all scenarios in the group
 * @author John Churin
 *
 */
public class GroupScenario extends Scenario {
	private List<Scenario> scenarios;

	public GroupScenario( String title, Scenario[] scenarios ) {
		super( title );
		for ( int x = 0; x < scenarios.length; x++ ) {
			getScenarios().add(scenarios[x]);
		}
	}

	/**
	 * Apply a group of scenarios unconditionally.
	 * @param patient
	 * @param startTime
	 * @param endTime
	 * @param rng
	 * @return Return true if at least one of the child scenarios did something (returned true), otherwise false. 
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
		boolean rslt = false;
		for ( Scenario s : getScenarios()) {
				if (s.apply( patient, startTime, endTime, rng)) rslt = true;
		}
		return rslt;
	}
	
	public List<Scenario> getScenarios() {
		if (scenarios==null) scenarios = new ArrayList<Scenario>( 10 );
		return scenarios;
	}

	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios = scenarios;
	}

	/**
	 * Add a Scenario
	 * @param scenario
	 */
	public void addScenario( Scenario scenario) {
		getScenarios().add( scenario );
	}


}
