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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math.random.RandomData;

public class LabTestScenario extends Scenario {
	private String test;
	private String units;
	private String normalRange;
	private String pattern;
	private NumberFormat nf = NumberFormat.getInstance(Locale.US);
	private ValueGen valueGen;

	public LabTestScenario(String test, String units, String normalRange, int fractionDigits, ValueGen valueGen) {
		this.test = test;
		this.units = units;
		this.normalRange = normalRange;
		this.setFractionDigits(fractionDigits);
		this.valueGen = valueGen;
	}
	
	/**
	 * Actually create a lab test at the start time (we ignore endTime). Also add to new activity if newer than 30 days old.
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
		List<LabTest> tests = new ArrayList<LabTest>();
		tests.add(generateTest(startTime, rng ));
		patient.generateLabResult( startTime, test, tests );
		return true;
	}
	/**
	 * Create a representation of the test instance with the value we want to see.
	 * @param startTime
	 * @param rng
	 * @return
	 */
	public LabTest generateTest( Date startTime, RandomData rng ) {
		LabTest labTest = new LabTest();
		labTest.setValue( getValueGen().generate(rng) );
		labTest.setStringValue( nf.format(labTest.getValue()));
		labTest.setEffectiveTime(startTime);
		labTest.setTestName(test);
		labTest.setUnits(units);
		labTest.setSource("Medical Center");
		return labTest;
	}

	public ValueGen getValueGen() {
		return valueGen;
	}
	public void setValueGen(ValueGen valueGen) {
		this.valueGen = valueGen;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getNormalRange() {
		return normalRange;
	}
	public void setNormalRange(String normalRange) {
		this.normalRange = normalRange;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public int getFractionDigits() {
		return nf.getMaximumFractionDigits();
	}

	public void setFractionDigits(int fractionDigits) {
		this.nf.setMaximumFractionDigits(fractionDigits);
		this.nf.setMinimumFractionDigits(fractionDigits);
	}
	
}
