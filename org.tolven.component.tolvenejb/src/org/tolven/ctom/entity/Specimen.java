package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;


/**
 * A part of a thing, or of several things, removed to demonstrate or to determine
 * the character of the whole, e.g. a substance, or portion of material obtained
 * for use in testing, examination, or study; particularly, a preparation of
 * tissue or bodily fluid taken for observation, examination or diagnosis.
 * @version 1.0
 * @created 27-Sep-2006 9:54:32 AM
 */
@Entity
@Table
public class Specimen implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * A unique sample, I.D. number  (Is this the unique specimen identifier - USI?)
	 */
	private long idNumber;
	/**
	 * Type of specimen/body tissue being sampled, using an enumerated set of values.
	 * Values include: V  Saliva, A  Aphaeresis Cells, B Whole Blood, C  CSF, D  CD33
	 * Myeloid Cells, L  CD3 Lymphoid Cells, M  PBMC Peripheral Blood Mononuclear
	 * Cells, O Bone Marrow,
	 * P  Plasma, S  Serum, T  Tumor Tissue, U  Urine, Y  CD14/CD15 Myeloid Cells.    
	 */
	@Column private int samplingType;
	@ManyToOne private SpecimenCollection specimenCollection;

	public Specimen(){

	}

	public void finalize() throws Throwable {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(long idNumber) {
		this.idNumber = idNumber;
	}

	public int getSamplingType() {
		return samplingType;
	}

	public void setSamplingType(int samplingType) {
		this.samplingType = samplingType;
	}

	public SpecimenCollection getSpecimenCollection() {
		return specimenCollection;
	}

	public void setSpecimenCollection(SpecimenCollection specimenCollection) {
		this.specimenCollection = specimenCollection;
	}

}