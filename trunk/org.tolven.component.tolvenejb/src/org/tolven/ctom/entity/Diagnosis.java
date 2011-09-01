package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The process of identifying a disease or illness by examining the signs and
 * symptoms. In cancer diagnosis, this process is usually followed by cancer
 * staging in order to ascertain the extent of the cancer in the body.
 * @version 1.0
 * @created 27-Sep-2006 9:57:27 AM
 */
@Entity
@DiscriminatorValue("DIAG")
public class Diagnosis extends Assessment implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Age at which condition or disease was diagnosed.
	 */
	@Column private int age;
	/**
	 * The patient's primary cancer diagnosis.  Use CTEP Terms and MedDRA Codes. (CDUS)
	 */
	@Column private String code;
	/**
	 * The coding scheme used to define the diagnosis.
	 */
	@Column private String codingScheme;
	/**
	 * The java.util.Date when a pathologic diagnosis is verified and confirmed. 
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date confirmationDate;
	/**
	 * The extent of invasion of the disease. (CTEP)
	 * 
	 * Definition of the extent of the disease in the body (e.g., in-situ, localized,
	 * invasion, regional, etc.)
	 */
	@Column private String diseaseExtent;
	/**
	 * Summary status of the amount of disease present at a specified timepoint.
	 * Values include: metastatic, disease free.
	 * 
	 * Current status of the disease in the body (e.g., disease-free, metastatic).
	 */
	@Column private String diseaseStatus;
	/**
	 * Name to represent a subject's cancer diagnosis, using CTEP terms and MedDRA
	 * disease terms.
	 */
	@Column private String name;
	/**
	 * Indication of whether this is a new or recurring disease
	 */
	@Column private String occurrenceType;
	/**
	 * Name of the primary anatomic site of disease using a set of values in
	 * compliance with the Adverse Event Expedited Reporting System (AdEERS). Values
	 * include: Appendix; Bladder; Cervix; Colon; Esophagus; Liver; Lung; Mediastinum;
	 * Ovary; Pericardium; Pleura; Prostate; Rectum; Ureter; Uterus; Vagina; Breast;
	 * Kidney; Chest; Cerebellum; Brainstem; Spleen; Thyroid; Stomach; mouth; Skull;
	 * Mandible; Sternum; Ribs; Humerus; Radius; Ulna; Femur; Tibia; Fibula; Skin;
	 * Sigmoid Colon; Anus; Jejunum; Ileum; Pelvis; Pancreas; Bone Marrow; Back;
	 * Testicle; Duodenum; Penis; Pharynx; Nasopharynx; Peripheral Blood;
	 * Cerebrospinal Fluid; Cerebrum; Bile Duct; Fallopian Tube; Ear, External; Ear,
	 * Inner; Ear, Mid; Eye, Globe; Eye, Orbit; Nose; Sinuses; Throat; Lymph Node,
	 * Axilla; Lymph Node, Brachial; Lymph Node, Cervical; Lymph Node, Epitrochlear;
	 * Lymph Node, Femoral; Lymph Node, Hilar; Lymph Node, Iliac; Lymph Node, Inguinal;
	 * Lymph Node, Internal Mammary; Lymph Node; Lymph Node, Mediastinal; Lymph Node,
	 * Mesenteric; Lymph Node, Mid-Pelvis; Lymph Node, Neck; Lymph Node, Occipital;
	 * Lymph Node, Paraaortic; Lymph Node, Pelvis; Lymph Node, Periauricular; Lymph
	 * Node, Popliteal; Lymph Node, Retroperitoneal; Lymph Node(s), Subclavian; Lymph
	 * Node, Submental; Lymph Node, Supraclavicular; Feet; Hands; Iliac Bone; Muscle,
	 * Upper Extremity Distal; Muscle, Upper Extremity Proximal; Muscle, Lower
	 * Extremity Distal; Muscle, Lower Extremity Proximal; Spine, Cervical; Spine,
	 * Lumbar; Spine, Sacral; Spine, Thoracic; Adrenal Gland; Other Site; Hilum,
	 * Thoracic, Lung, Anterior Left Lower Lobe; Lung, Anterior Left Upper Lobe; Lung,
	 * Anterior Right Lower Lobe; Lung, Anterior RIght Middle Lobe; Lung, Anterior
	 * Right Upper Lobe; Lung, Posterior Left Lower Lobe; Lung, Posterior Left Upper
	 * Lobe; Lung, Posterior Right Lower Lobe; Lung, Posterior Right Middle Lobe; Lung,
	 * Posterior Right Upper Lobe.
	 */
	@Column private String primarySite;
	/**
	 * For a tumor in a paired organ, a designation for the side of the body on which
	 * the tumor or cancer first developed. Values include: Bilateral; Both; Left;
	 * Right; Unilateral; Side Not Specified; Not a Paired Site; Unknown. (CTEP)
	 */
	@Column private String primarySiteLaterality;
	/**
	 * Source or origin of information for a cancer diagnosis, using a list of values
	 * (Path report, family members, others). Values Include: Other, Pathology Report,
	 * Report, Self-Report, Family Members, MD (Physician) Report.
	 * 
	 * Identification of the source of origin of the diagnosis; this can be self
	 * reported, family, physician, etc.
	 */
	@Column private String source;

	public Diagnosis(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodingScheme() {
		return codingScheme;
	}

	public void setCodingScheme(String codingScheme) {
		this.codingScheme = codingScheme;
	}

	public java.util.Date getConfirmationDate() {
		return confirmationDate;
	}

	public void setConfirmationDate(java.util.Date confirmationDate) {
		this.confirmationDate = confirmationDate;
	}

	public String getDiseaseExtent() {
		return diseaseExtent;
	}

	public void setDiseaseExtent(String diseaseExtent) {
		this.diseaseExtent = diseaseExtent;
	}

	public String getDiseaseStatus() {
		return diseaseStatus;
	}

	public void setDiseaseStatus(String diseaseStatus) {
		this.diseaseStatus = diseaseStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOccurrenceType() {
		return occurrenceType;
	}

	public void setOccurrenceType(String occurrenceType) {
		this.occurrenceType = occurrenceType;
	}

	public String getPrimarySite() {
		return primarySite;
	}

	public void setPrimarySite(String primarySite) {
		this.primarySite = primarySite;
	}

	public String getPrimarySiteLaterality() {
		return primarySiteLaterality;
	}

	public void setPrimarySiteLaterality(String primarySiteLaterality) {
		this.primarySiteLaterality = primarySiteLaterality;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}