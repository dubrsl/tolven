package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;


/**
 * The process used to determine the extent of any localized or abnormal change in
 * the structure of part of an organ or tissue.
 * @version 1.0
 * @created 27-Sep-2006 9:56:05 AM
 */
@Entity
@DiscriminatorValue("LESION")
public class LesionEvaluation extends Observation implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Description of the anatomic location of a specific lesion or site of disease.
	 */
	@Column private String anatomicSiteDescription;
	/**
	 * Text description of the appearance of a superficial lesion. Values include:
	 * Flat Lesion, Nodular Lesion. 
	 */
	@Column private String appearanceType;
	/**
	 * Name of the organ site (single) within the body in contact with a site of tumor.
	 * 
	 */
	@Column private String bodySiteContactName;
	/**
	 * Sequential number corresponding to an evaluation timepoint.  (O for the
	 * baseline evaluation, 1 for the first evaluation, 2 for the second evaluation,
	 * etc.)
	 */
	@Column private int evaluationNumber;
	/**
	 * The unique lesion number. Once a lesion number is designated for a specific
	 * lesion, that number may not change or re-used to denote a different lesion.
	 */
	@Column private String lesionNumber;
	/**
	 * A method of assessment by which a cancer lesion is examined and or measured; by
	 * RECIST guidelines, the same method must be used to evaluate a lesion at each
	 * assessment from baseline to follow-up."  (CTEP) Values include: PET scan;
	 * Gallium scan; Not done; Differential; Visual assessment; Bone scan; CT scan;
	 * Chest x-ray; Clinical examination; Cytology; Fine needle aspiration biopsy;
	 * Fine needle aspiration biopsy, with wire localization; Histology; Incisional
	 * biopsy; Incisional biopsy, with wire localization; MRI; Mastectomy; Needle
	 * biopsy (tru-cut or core); Needle biopsy (tru-cut or core), with wire
	 * localization); Not evaluated; Other; Resection/re-excision of lumpectomy
	 * margins; Spiral CT scan; Tumor markers; Ultrasound; Autopsy; Axillary
	 * dissection; Ductal lavage; Ductoscopic biopsy; Excisional biopsy or lumpectomy;
	 * Excisional biopsy or lumpectomy, with wire localization; Fluid cytology,
	 * ascites; Fluid cytology, cerebrospinal fluid; Fluid cytology, pleural; Nipple
	 * fluid; Other, specify; Prophalactic mastectomy; Sentinel node biopsy;
	 * Laparotomy; Laparoscopy; Biopsy, other; Preoperative CT scan; EKG; Biopsy; Not
	 * applicable; Pathologic; Physical exam; Radiograph; Bilateral mammogram;
	 * Bimanual examination; Direct inspection; Intraoperative ultrasound; Primary
	 * surgery; X-ray; Surgery; unknown; clinical; Palpation; Visualization;
	 * Colposcopy; Endoscopy; MRI scan; Plain film/X-ray without contrast; Plain
	 * film/X-ray with contrast; Radioisotope scan; Histologic confirmation; Cytologic
	 * confirmation; cytoscopy; cystoscopy; Histologic; Radiographic; Tumor Marker.
	 */
	@Column private String method;
	/**
	 * Yes/No/Unknown response to signify that a lesion or site of disease has
	 * previously been irradiated.
	 */
	@Column private String previouslyIrradiatedSite;
	/**
	 * The field to specify the number of lesions.
	 */
	@Column private int quantityNumber;
	/**
	 * Text to represent that a lesion is classified as target or nontarget using the
	 * Recist Criteria. Values include: Target Lesion, Nontarget Lesion.
	 */
	@Column private String targetNonTargetCode;

	public LesionEvaluation(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getAnatomicSiteDescription() {
		return anatomicSiteDescription;
	}

	public void setAnatomicSiteDescription(String anatomicSiteDescription) {
		this.anatomicSiteDescription = anatomicSiteDescription;
	}

	public String getAppearanceType() {
		return appearanceType;
	}

	public void setAppearanceType(String appearanceType) {
		this.appearanceType = appearanceType;
	}

	public String getBodySiteContactName() {
		return bodySiteContactName;
	}

	public void setBodySiteContactName(String bodySiteContactName) {
		this.bodySiteContactName = bodySiteContactName;
	}

	public int getEvaluationNumber() {
		return evaluationNumber;
	}

	public void setEvaluationNumber(int evaluationNumber) {
		this.evaluationNumber = evaluationNumber;
	}

	public String getLesionNumber() {
		return lesionNumber;
	}

	public void setLesionNumber(String lesionNumber) {
		this.lesionNumber = lesionNumber;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPreviouslyIrradiatedSite() {
		return previouslyIrradiatedSite;
	}

	public void setPreviouslyIrradiatedSite(String previouslyIrradiatedSite) {
		this.previouslyIrradiatedSite = previouslyIrradiatedSite;
	}

	public int getQuantityNumber() {
		return quantityNumber;
	}

	public void setQuantityNumber(int quantityNumber) {
		this.quantityNumber = quantityNumber;
	}

	public String getTargetNonTargetCode() {
		return targetNonTargetCode;
	}

	public void setTargetNonTargetCode(String targetNonTargetCode) {
		this.targetNonTargetCode = targetNonTargetCode;
	}

}