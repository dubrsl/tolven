package org.tolven.gen.model;

import java.util.Date;

import org.apache.commons.math.random.RandomData;

public class CXRScenario extends RadiologyScenario {

	public CXRScenario(String test, String report, String interpretation) {
		super(test, report, interpretation);
	}

	/**
	 * Actually create an X-Ray report at the start time (we ignore endTime).
	 * But only for males over 30.
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
		if (patient.isMale() && patient.getAgeInYears() > 30) {
			super.apply(patient, startTime, endTime, rng);
		}
		return true;
	}

}
