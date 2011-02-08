package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * The use of high-energy radiation from x-rays, gamma rays, neutrons, and other
 * sources to kill cancer cells and shrink tumors. Radiation may come from a
 * machine outside the body (external-beam radiation therapy) or from materials
 * called radioisotopes. Radioisotopes produce radiation and can be placed in or
 * near the tumor or in the area near cancer cells. This type of radiation
 * treatment is called internal radiation therapy, implant radiation, interstitial
 * radiation, or brachytherapy. Systemic radiation therapy uses a radioactive
 * substance, such as a radiolabeled monoclonal antibody, that circulates
 * throughout the body.
 * @version 1.0
 * @created 27-Sep-2006 9:54:15 AM
 */
@Entity
@DiscriminatorValue("RAD")
public class Radiation extends Procedure implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The value of dosing and administration of radiation therapy as part of a
	 * procedure.
	 */
	@Column private String dose;
	/**
	 * Unit of measure used for dosing and administration of radiation therapy. Values
	 * include: Gray, Centigray, Radiation Absorbed Dose.
	 */
	@Column private String doseUnitOfMeasure;
	/**
	 * The type of radiation therapy being used in a treatment regimen. Values include:
	 * Extensive Radiation, Limited Radiation, Radiation - Extent Unknown.
	 */
	@Column private String therapyType;

	public Radiation(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public String getDoseUnitOfMeasure() {
		return doseUnitOfMeasure;
	}

	public void setDoseUnitOfMeasure(String doseUnitOfMeasure) {
		this.doseUnitOfMeasure = doseUnitOfMeasure;
	}

	public String getTherapyType() {
		return therapyType;
	}

	public void setTherapyType(String therapyType) {
		this.therapyType = therapyType;
	}

}