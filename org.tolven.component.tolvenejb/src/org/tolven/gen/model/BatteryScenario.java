package org.tolven.gen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math.random.RandomData;

	/**
	 * A battery is almost the same as a group scenario except that a battery creates a
	 * manifestation of the group in the output which a group is only a group relative to
	 * the input side.
	 * Functionally, a battery is a group of tests ordered together, usually taken from a single 
	 * specimen collection event. 
	 * @author John Churin
	 *
	 */
	public class BatteryScenario extends GroupScenario {
		public BatteryScenario( String title, Scenario[] scenarios ) {
			super( title, scenarios );
		}
		
	/**
	 * Apply a battery of tests. Each test is asked to return it's result to us rather than
	 * generating a result independently. we then pass the whole collection to the data generator. 
	 * @param patient
	 * @param startTime
	 * @param endTime
	 * @param rng
	 * @return Return true if at least one of the child scenarios did something (returned true), otherwise false. 
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
		boolean rslt = false;
		List<LabTest> tests = new ArrayList<LabTest>( getScenarios().size());
		for ( Scenario s : getScenarios()) {
			tests.add(((LabTestScenario)s).generateTest(startTime, rng ));
		}
		patient.generateLabResult(startTime, getTitle(), tests);
		return rslt;
	}
	
}
