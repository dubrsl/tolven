package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * The microscopic study of characteristic tissue abnormalities by employing
 * various cytochemical and immunocytochemical stains.
 * @version 1.0
 * @created 27-Sep-2006 9:55:23 AM
 */
@Entity
@DiscriminatorValue("HISTO")
public class Histopathology extends Observation implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Results of a pathological review of a specimen or sample of tissue. Values
	 * include: Positive, Negative, Indeterminate, Not Done.
	 */
	@Column private String grossExamResult;
	/**
	 * The yes/no indicator to ask if the margins of surgical resection are involved
	 * or infiltrated by the tumor. (CTEP)
	 */
	@Column private boolean involvedSurgicalMargin;
	/**
	 * Text that provides a description of a pathology report.
	 */
	@Column private String reportDescriptiveText;

	public Histopathology(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getGrossExamResult() {
		return grossExamResult;
	}

	public void setGrossExamResult(String grossExamResult) {
		this.grossExamResult = grossExamResult;
	}

	public boolean isInvolvedSurgicalMargin() {
		return involvedSurgicalMargin;
	}

	public void setInvolvedSurgicalMargin(boolean involvedSurgicalMargin) {
		this.involvedSurgicalMargin = involvedSurgicalMargin;
	}

	public String getReportDescriptiveText() {
		return reportDescriptiveText;
	}

	public void setReportDescriptiveText(String reportDescriptiveText) {
		this.reportDescriptiveText = reportDescriptiveText;
	}

}