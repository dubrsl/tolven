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
import java.util.GregorianCalendar;

import org.apache.commons.math.random.RandomData;
/**
 * A diabetes disease generator. Our superclass handles the basics of getting the disease
 * added to the patient. We then can go on to apply additional data such as tests, doctor visits, 
 * and secondary diagnoses associated with diabetes.
 * @author John Churin
 */
public class DiabetesGen extends ChronicDiseaseGen {
	double demoSkew = 5.0;	// Increase the odds for demo purposes. Can be returned to 1.0 for more realism
	public DiabetesGen( RandomData rng ) {
		this.rng = rng;
		setDiseaseName( "Diabetes Mellitus" );
//		addMatcher( new DemogMatcher( 0, 20, "M", 0.04*demoSkew));
//		addMatcher( new DemogMatcher( 0, 20, "F", 0.06*demoSkew));
//		addMatcher( new DemogMatcher( 20, 40, "M", 0.012*demoSkew));
//		addMatcher( new DemogMatcher( 20, 40, "F", 0.018*demoSkew));
		addMatcher( new DemogMatcher( 40, 60, "M", 0.052*demoSkew));
		addMatcher( new DemogMatcher( 40, 60, "F", 0.078*demoSkew));
		addMatcher( new DemogMatcher( 60, 200, "M", 0.08*demoSkew));
		addMatcher( new DemogMatcher( 60, 200, "F", 0.12*demoSkew));
		// Some scenario components we'll use below
		Scenario podiatry = new RadiologyScenario( "Foot Exam", "No abnormality noted on either foot.", "Normal");
		Scenario eagerLab =	new GroupScenario("0.50 scenario in Diabetes", new Scenario[] {
			new LabTestScenario("hemoglobin A1C test", "%", "7 (or less)", 1, new GaussianValueGen( 6.0, rng.nextUniform(0.5, 1.3)) ),
			new LabTestScenario("Creatinine", "mg/dL", "0.9 to 1.4 mg/dL", 1, new GaussianValueGen( 1.2, rng.nextUniform(0.1, 0.3)) ),
			new BatteryScenario( "Lipid Panel", new Scenario[] {
				new LabTestScenario("low-density lipoprotein (LDL)", "mg/dL", ">100 mg/dL", 0, new GaussianValueGen( 85.0, rng.nextUniform(10.0, 20.0)) ),
				new LabTestScenario("triglycerides", "mg/dL", ">150 mg/dL", 0, new GaussianValueGen( 140.0, rng.nextUniform(5.0, 15.0)) )
			}),
			new LabTestScenario("urine albumin", "mcg", ">30 mcg", 0, new GaussianValueGen( 29.0, rng.nextUniform(4.0, 7.0)) )
			});
		Scenario lazyLab = new GroupScenario("0.25 scenario in Diabetes", new Scenario[] {
			new LabTestScenario("hemoglobin A1C test", "%", "7 (or less)", 1, new GaussianValueGen( 9.0, rng.nextUniform(1.0, 3.5)) ),
			new LabTestScenario("Creatinine", "mg/dL", "0.9 to 1.4 mg/dL", 1, new GaussianValueGen( 1.1, rng.nextUniform(0.3, 0.7)) ),
			new BatteryScenario( "Lipid Panel", new Scenario[] {
				new LabTestScenario("low-density lipoprotein (LDL)", "mg/dL", ">100 mg/dL", 0, new GaussianValueGen( 165.0, rng.nextUniform(10.0, 40.0)) ),
				new LabTestScenario("triglycerides", "mg/dL", ">150 mg/dL", 0, new GaussianValueGen( 180.0, rng.nextUniform(15.0, 28.0)) )
				}),
			new LabTestScenario("urine albumin", "mcg", ">30 mcg", 0, new GaussianValueGen( 35.0, rng.nextUniform(5.1, 11.2)) )
			});
		Scenario annualAppt = new AppointmentScenario( "12 Month diabetes followup", null, null, null);
//		Scenario semiAnnualAppt = new AppointmentScenario( "6 Month diabetes lab work", null, null, null);

		// Specify the scenario here.
		this.setScenario(new ChooseScenario( new ProbabilityScenario[] 
		 {
			// This says on 12 month cycle, do an appointment from which lab and podiatry exam events occur and then, repeat once at a 6 month interval a second lab scenario.
			// In addition, we'll schedule an annual appointment for one time in the future. (Ignore six-month appoitment) 
			new ProbabilityScenario( 0.50, new RepeatScenario( GregorianCalendar.MONTH, 12,1.5, false,  1, 0, 
					new GroupScenario( "12 month repeat", new Scenario[] {annualAppt, eagerLab, podiatry,
							new RepeatScenario(GregorianCalendar.MONTH, 6,1.5, false,  1, 1, eagerLab)}), annualAppt, 1)),
			new ProbabilityScenario( 0.25, new RepeatScenario( GregorianCalendar.MONTH, 8,2.1, true, 0, 0, 
					new GroupScenario( "12 month repeat", new Scenario[] {annualAppt, lazyLab, podiatry,
							new RepeatScenario(GregorianCalendar.MONTH, 6,1.5, false,  1, 1, lazyLab)}), annualAppt, 1
							)) 
		 }));
	}
	
	@Override
	protected void apply(GenMedical patient, Date eventTime) {
		patient.generateMedication(eventTime,
			"glucophage 500 mg tablets", 
			"120 tablets",
			"take two tablets twice a day for diabetes",
			"2");
		patient.generateMedication(eventTime,
				"tolbutamide 500 mg tablets", 
				"120 tablets",
				"take two tablets twice a day for diabetes",
				"2");
		super.apply(patient, eventTime);
	}
}
