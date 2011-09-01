package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * A human being who may be assigned to a study.  The study specifies how the
 * subject's illness will be treated and/or monitored overtime.
 * @version 1.0
 * @created 27-Sep-2006 9:52:14 AM
 */
@Entity
@DiscriminatorValue("SUBJ")
public class Subject extends Person implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * A yes/no indicator that determines whether the patient, or the patient's
	 * legally acceptable representative, provided written authorization to allow the
	 * use and disclosure of their protected health information.
	 */
	@Column private String confidentialityCode;
	/**
	 * The initial letters of the first, middle, and last names of the patient
	 * registered on the clinical trial.
	 * Note:  this should be considered as identifying information and should not be
	 * part of research database --(Still TBD).
	 */
	@Column private String initials;
	/**
	 * The unique numeric or alphanumeric designation used by the facility to identify
	 * the patient.
	 * NOTE:  Note:  this should be considered as identifying information and should
	 * not be part of research database --(Still TBD).
	 */
	@Column private String medicalRecordNumber;
	/**
	 * Primary Payer/insurance carrier information at the time of treatment on a
	 * protocol. Values include:
	 * 1	Private Insurance
	 * 2	Medicare
	 * 3	Medicare And Private Insurance
	 * 4	Medicaid
	 * 5	Medicaid and Medicare
	 * 6	Military Or Veterans Sponsored NOS
	 * 6b	Veterans Sponsored
	 * 7	Self Pay (No Insurance)
	 * 8	No Means Of Payment (No Insurance)
	 * 98	Other
	 * 99	Unknown
	 * 6a	Military Sponsored (Including Champus & Tricare).
	 */
	@Column private String paymentMethod;
	/**
	 * The number representing a postal area for a patient (in the USA this is a five
	 * or nine digit number).
	 */
	@Column private String zipCode;

	public Subject(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getConfidentialityCode() {
		return confidentialityCode;
	}

	public void setConfidentialityCode(String confidentialityCode) {
		this.confidentialityCode = confidentialityCode;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public String getMedicalRecordNumber() {
		return medicalRecordNumber;
	}

	public void setMedicalRecordNumber(String medicalRecordNumber) {
		this.medicalRecordNumber = medicalRecordNumber;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}