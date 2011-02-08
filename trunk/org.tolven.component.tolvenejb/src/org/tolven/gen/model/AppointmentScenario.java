package org.tolven.gen.model;

import java.util.Date;

import org.apache.commons.math.random.RandomData;

public class AppointmentScenario extends Scenario {
	private String purpose;
	private String location;
	private String practitioner;
	private String instructions;
	
	/**
	 * Generate an appoint for the specified purpose
	 * @param purpose
	 */
	public AppointmentScenario( String purpose, String location, String practitioner, String instructions) {
		setTitle( purpose + ", " + location + ", " + practitioner + ", " + instructions);
		this.purpose = purpose;
		this.location = location;
		this.practitioner = practitioner;
		this.instructions = instructions;
	}

	/**
	 * Actually create the appointment.
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
		patient.generateAppointment( startTime, getPurpose(), getLocation(), getPractitioner(), getInstructions() );
		return true;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPractitioner() {
		return practitioner;
	}

	public void setPractitioner(String practitioner) {
		this.practitioner = practitioner;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

}
