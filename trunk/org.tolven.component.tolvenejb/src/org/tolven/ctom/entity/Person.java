package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * A human being.
 * @version 1.0
 * @upjava.util.Dated 27-Sep-2006 7:42:34 AM
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("PERSON")
public class Person implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The classification of the sex or gender role of the patient. Values include:
	 * Female, Male, Unknown.
	 */
	@Column private String administrativeGenderCode;
	/**
	 * Highest level of education completed. Values include: Less than High School
	 * Diploma; High School Diploma; Vocational School; Some College; College Degree;
	 * Some Graduate School; Graduate Degree; PostGraduate (Doctoral).
	 */
	@Column private String educationLevelCode;
	/**
	 * The current occupational status of the participant. Values include: Disabled,
	 * Employed, Homemaker, Retired, Student, Unemployed, Other.
	 */
	@Column private String employmentStatus;
	/**
	 * The patient's self declared ethnic origination, independent of racial
	 * origination, based on OMB approved categories. Values include: Hispanic Or
	 * Latino, Unknown, Not reported, Not Hispanic Or Latino.
	 */
	@Column private String ethnicGroupCode;
	/**
	 * A word or group of words indicating a person's first (personal or given) name;
	 * the name that precedes the surname. Synonym = Given Name. (DCP)
	 */
	@Column private String firstName;
	/**
	 * Category to designate the total income of a household. Values include: Less
	 * than $25,000; $25,000 to $50,000; $50,000 to $75,000; $75,000 to $100,000;
	 * Greater than $100,000.
	 */
	@Column private String householdIncomeCategory;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * A means of uniquely identifying an individual by using a word or group of words
	 * indicating a person's last (family) name. Last Name, Surname. (DCP)
	 */
	@Column private String lastName;
	/**
	 * Commonly used category name to describe the marital status of a person. Values
	 * include: Married, Widowed, Single, Separated, Divorced, I Prefer Not to Answer,
	 * Living As Married.
	 */
	@Column private String maritalStatusCode;
	/**
	 * The patient's self declared racial origination, independent of ethnic
	 * origination, using OMB approved categories. Values include: Not Reported,
	 * American Indian or Alaska Native, Native Hawaiian or other Pacific Islander,
	 * Unknown, Asian, White, Black or African American.
	 */
	@Column private String raceCode;

	public Person(){

	}

	public void finalize() throws Throwable {

	}

	public String getAdministrativeGenderCode() {
		return administrativeGenderCode;
	}

	public void setAdministrativeGenderCode(String administrativeGenderCode) {
		this.administrativeGenderCode = administrativeGenderCode;
	}

	public String getEducationLevelCode() {
		return educationLevelCode;
	}

	public void setEducationLevelCode(String educationLevelCode) {
		this.educationLevelCode = educationLevelCode;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	public String getEthnicGroupCode() {
		return ethnicGroupCode;
	}

	public void setEthnicGroupCode(String ethnicGroupCode) {
		this.ethnicGroupCode = ethnicGroupCode;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getHouseholdIncomeCategory() {
		return householdIncomeCategory;
	}

	public void setHouseholdIncomeCategory(String householdIncomeCategory) {
		this.householdIncomeCategory = householdIncomeCategory;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMaritalStatusCode() {
		return maritalStatusCode;
	}

	public void setMaritalStatusCode(String maritalStatusCode) {
		this.maritalStatusCode = maritalStatusCode;
	}

	public String getRaceCode() {
		return raceCode;
	}

	public void setRaceCode(String raceCode) {
		this.raceCode = raceCode;
	}

}





