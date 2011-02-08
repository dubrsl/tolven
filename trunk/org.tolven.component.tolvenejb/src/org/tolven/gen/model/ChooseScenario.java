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
 * A scenario that chooses among the supplied ProbabilityScenarios).
 * When the apply method is called, a random number is selected between 0.0 and 1.0 and
 * the probabilities of each choice considered. The choices do not need to total 1.0. If they exceed
 * 1.0 any latter choices beyond the 1.0 limit would not be selected. For example: Say two choices were in the list:
 * Choice A = 0.25 and Choice B = 0.50 then 25% of the time Choice A would be selected and 25% of the time
 * neither choice A nor B would be selected. So as you can see, the probabilities are not normalized to 1.0.
 * @author John Churin
 *
 */
public class ChooseScenario extends Scenario {
	private List<ProbabilityScenario> scenarios;

	public ChooseScenario( ProbabilityScenario[] scenarios ) {
		for ( int x = 0; x < scenarios.length; x++ ) {
			getScenarios().add(scenarios[x]);
		}
	}

	/**
	 * 
	 * @param patient
	 * @param startTime
	 * @param endTime
	 * @param rng
	 * @return Return true if one of the child scenarios did something (returned true), otherwise false. 
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
		double choice = rng.nextUniform(0.0, 1.0);
		double base = 0.0;
		for ( ProbabilityScenario s : getScenarios()) {
			base += s.getProbability();
			if (base>choice) {
//				TolvenLogger.info( "Scenario Choice " + s.getTitle(), ChooseScenario.class);
				return s.apply( patient, startTime, endTime, rng);
			}
		}
		return false;
	}
	
	public List<ProbabilityScenario> getScenarios() {
		if (scenarios==null) scenarios = new ArrayList<ProbabilityScenario>( 10 );
		return scenarios;
	}

	public void setScenarios(List<ProbabilityScenario> scenarios) {
		this.scenarios = scenarios;
	}

	/**
	 * Add a Scenario
	 * @param scenario
	 */
	public void addScenario( ProbabilityScenario scenario) {
		getScenarios().add( scenario );
	}

}
