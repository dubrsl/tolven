package org.tolven.gen.model;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.math.random.RandomData;

public class WellBabyGen extends GenSource {
	public WellBabyGen( RandomData rng ) {
		this.rng = rng;
		addMatcher( new DemogMatcher( 0, 6, "M", 0.99));
		addMatcher( new DemogMatcher( 0, 6, "F", 0.99));
		Scenario annualAppt = new AppointmentScenario( "Routine well-baby checkup", null, null, null);
		this.setScenario(new RepeatScenario(GregorianCalendar.MONTH, 12,1.5, false,  0, 6, annualAppt));
	}

	@Override
	protected void apply(GenMedical patient, Date eventTime) {
		if (rng.nextUniform(0.0, 1.0) < 0.8) {
			patient.generateProblem(eventTime, "Hay Fever", "Active");
			patient.generateAllergy(eventTime, "Ragweed", "Active");
			patient.generateAllergy(eventTime, "Cedar Pollen", "Active");
		}
		if (rng.nextUniform(0.0, 1.0) < 0.5) {
			patient.generateAllergy(eventTime, "Leaf Mold", "Active");
		}
		super.apply(patient, eventTime);
	}

}
