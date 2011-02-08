package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * Performing exams and tests to learn the extent of the cancer within the body,
 * especially whether the disease has spread from the original site to other parts
 * of the body.
 * @version 1.0
 * @created 27-Sep-2006 9:57:56 AM
 */
@Entity
@Table
public class CancerStaging implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * Numeric value used to express the extent of a cancer within the body,
	 * especially whether the disease has spread from the original site to other parts
	 * of the body. Values Include: I, IA, IB, II, III, IV, IIA, IIB, IIIA, IIIB, IIIC,
	 * IVA, IVB, A, B, C, D, D0, D1, D2, D3, 0.
	 */
	@Column private String numericStage;
	/**
	 * Diagnosis represented as a character short name using ICD-9 cancer types.
	 */
	@Column private String textStage;
	/**
	 * TNM value used to express the extent of a cancer within the body, citing
	 * disease extent in the primary tumor (T), lymph nodes (N), and metastatic sites
	 * (M).
	 */
	@Column private String tnmStage;
	@ManyToOne private Diagnosis diagnosis;

	public CancerStaging(){

	}

	public void finalize() throws Throwable {

	}

	public Diagnosis getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(Diagnosis diagnosis) {
		this.diagnosis = diagnosis;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumericStage() {
		return numericStage;
	}

	public void setNumericStage(String numericStage) {
		this.numericStage = numericStage;
	}

	public String getTextStage() {
		return textStage;
	}

	public void setTextStage(String textStage) {
		this.textStage = textStage;
	}

	public String getTnmStage() {
		return tnmStage;
	}

	public void setTnmStage(String tnmStage) {
		this.tnmStage = tnmStage;
	}

}