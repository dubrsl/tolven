package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * The results of a medical test that help determine a diagnosis, plan treatment,
 * check to see if treatment is working, or monitor the disease over time.  This
 * includes the results of lab tests, physical examination, other sample analysis
 * results.
 * @version 1.0
 * @created 27-Sep-2006 9:55:51 AM
 */
@Entity
@DiscriminatorValue("QUANT")
public class QuantitativeResult extends Observation implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Name of the method of measuring the pharmacologic (PK) effects of a
	 * biologically active substance using an intermediate in vivo or in vitro tissue
	 * or cell model under controlled conditions. Values include: ER Estrogen Receptor
	 * Assay; PR Progesterone Receptor Assay; p53  p53 Assay; HER-2 (IHC)  Her-2,
	 * Immunohistochemistry Assay; HER-2 (ISH)  Her-2, In Situ Hybridization Assay. 
	 */
	@Column private String assayMethod;
	/**
	 * Determines if result is considered a study biomarker. 
	 */
	@Column private boolean biomarkerInd;
	/**
	 * The arrangement or location of a patient's body using an enumerated set of
	 * values. Values include: Unknown, Semiprone,Prone, Sitting, Standing, Upright,
	 * Supine.
	 */
	@Column private String bodyPosition;
	/**
	 * Character result of a laboratory analysis expressed as text.
	 */
	@Column private String characterValue;
	/**
	 * Indicator that a lab test result is normal, high or low in reference to a
	 * reference range of values for this test. Values include: Lower than reference
	 * range; High- greater than normal in degree or intensity or amount; NORMAL;
	 * Nonnumeric test results; No range exists for lab test results.
	 */
	@Column private String labReferenceRange;
	/**
	 * Techniques used in laboratory for research purposes. NCI Metathesaurus code:
	 * CL026563. Values include: IHC  Immunohistochemistry; RTPCR
	 * reversetranscriptase- polymerase chain reaction; PCR  Polymerase Chain Reaction;
	 * TUNEL Assay  In Situ Nick-End Labeling; qRTPCR: quantitative Reverse
	 * Transcriptase-Polymerase Chain Reaction; Automatic Differentiation; Manual
	 * Differentiation.
	 */
	@Column private String labTechnique;
	/**
	 * The way in which person's vital status was obtained. Values include: Unknown,
	 * Conference notes, Hospital Information System (electronic transfer), Hard copy,
	 * Patient supplied verbally, Patient supplied in written form.
	 */
	@Column private String meansVitalStatusObtained;
	/**
	 * Numeric result of the laboratory analysis expressed as a number of 12 digits
	 * total, with 5 after the decimal.
	 */
	@Column private int numericValue;
	/**
	 * Indicator placed on a test result, reflecting the decision to report a specific
	 * piece of data to a sponsor or monitor.  Response is represented as Y (Yes) or N
	 * (No).
	 */
	@Column private boolean significanceInd;
	/**
	 * Identifier name given to a medical test.
	 */
	@Column private String testName;
	/**
	 * Name given to a logical grouping of lab test values. 
	 */
	@Column private String testPanelName;
	/**
	 * The units of the test result. In case of  laboratory test results, this element
	 * maps to CDE Lab Unit Of Measure Name; publicID 2003753; version 3.0.
	 */
	@Column private String unitOfMeasure;

	public QuantitativeResult(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getAssayMethod() {
		return assayMethod;
	}

	public void setAssayMethod(String assayMethod) {
		this.assayMethod = assayMethod;
	}

	public boolean isBiomarkerInd() {
		return biomarkerInd;
	}

	public void setBiomarkerInd(boolean biomarkerInd) {
		this.biomarkerInd = biomarkerInd;
	}

	public String getBodyPosition() {
		return bodyPosition;
	}

	public void setBodyPosition(String bodyPosition) {
		this.bodyPosition = bodyPosition;
	}

	public String getCharacterValue() {
		return characterValue;
	}

	public void setCharacterValue(String characterValue) {
		this.characterValue = characterValue;
	}

	public String getLabReferenceRange() {
		return labReferenceRange;
	}

	public void setLabReferenceRange(String labReferenceRange) {
		this.labReferenceRange = labReferenceRange;
	}

	public String getLabTechnique() {
		return labTechnique;
	}

	public void setLabTechnique(String labTechnique) {
		this.labTechnique = labTechnique;
	}

	public String getMeansVitalStatusObtained() {
		return meansVitalStatusObtained;
	}

	public void setMeansVitalStatusObtained(String meansVitalStatusObtained) {
		this.meansVitalStatusObtained = meansVitalStatusObtained;
	}

	public int getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(int numericValue) {
		this.numericValue = numericValue;
	}

	public boolean isSignificanceInd() {
		return significanceInd;
	}

	public void setSignificanceInd(boolean significanceInd) {
		this.significanceInd = significanceInd;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestPanelName() {
		return testPanelName;
	}

	public void setTestPanelName(String testPanelName) {
		this.testPanelName = testPanelName;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

}