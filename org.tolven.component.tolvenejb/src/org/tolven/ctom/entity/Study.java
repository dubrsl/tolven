package org.tolven.ctom.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * A type of research activity that tests how well new medical treatments or other
 * interventions work in subjects. Such plans test new methods of screening,
 * prevention, diagnosis or treatment of a disease. The specific plans are fully
 * defined in the protocol and may be carried out in a clinic or other medical
 * facility.
 * 
 * BRIDG: A systematic evaluation of an observation or an intervention (for
 * example, treatment, drug, device, procedure or system) in one or more subjects.
 * Frequently this is a test of a particular hypothesis about the treatment, drug,
 * device, procedure or system. [CDAM]  A study can be either primary or
 * correlative. A study is considered a primary study if it has one or more
 * correlative studies. A correlative study extends the objectives or observations
 * of a primary study, enrolling the same, or a subset of the same, subjects as
 * the primary study.
 * 
 * A Clinical Trial is a Study with type= "intervention" with subjects of
 * type="human".
 * @version 1.0
 * @upjava.util.Dated 27-Sep-2006 9:50:19 AM
 */
@Entity
@Table
public class Study implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Date of IRB approval of the initial protocol version; the java.util.Date the IRB Chair
	 * signs off on a protocol and patient enrollment can begin.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date activationDate;
	/**
	 * Indicator of Yes (Y) or No (N) to specify if a protocol is blinded.
	 */
	@Column private boolean blindedInd;
	/**
	 * Date of closure refers to the closing of a study to enrollment.  Patients
	 * enrolled on the study at the time of closure will continue their treatment plan.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date closureDate;
	/**
	 * The free text field of the type of study being conducted. (CTEP)
	 */
	@Column private String description;
	/**
	 * Code to represent at a  summary level the category of disease treated on a
	 * protocol (Cancer, AIDS, and Benign disease). Values Include: A: AIDS, B: Benign,
	 * C: Cancer.
	 */
	@Column private String diseaseCode;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * Codes to identify a type of protocol based upon its intent (genetic, diagnostic,
	 * preventive, etc.). Values include: D: Diagnostic Protocol, GN: Genetic Non-
	 * therapeutic Protocol, GT: Genetic Therapeutic Protocol, N: Therapeutic Protocol,
	 * P: Primary Treatment Protocol , S:  Supportive Protocol, T: Preventive Protocol.
	 */
	@Column private String intentCode;
	/**
	 * Descriptive text used to represent the long title or name of a protocol.
	 */
	@Column private String longTitle;
	/**
	 * Code to represent the monitor for a protocol. Values iclude: CTEP, CTEP - CTMS;
	 * CTEP - CDUS Complete; CTEP - CDUS Abbreviated; Pharmaceutical Company; Internal
	 * Monitor.
	 */
	@Column private String monitorCode;
	/**
	 * A Yes/No response to indicate if a protocol is being conducted at more than one
	 * site concurrently.
	 */
	@Column private boolean multiInstitutionInd;
	/**
	 * NCI Navy formatted protocol number.
	 * 
	 */
	@Column private String navyNCINumber;
	/**
	 * The numeric or alphanumeric identification assigned to the study by the NCI.
	 * Inter-Group protocols should use the lead Groups protocol number.
	 */
	@Column private String nciNumber;
	/**
	 * Coded designation of phase (I, II, III, or IV) for a clinical trial. Values
	 * include: I, I/II, II, III, NA.
	 */
	@Column private String phaseCode;
	/**
	 * A structured summary description of a protocol document.
	 */
	@Column private String precis;
	/**
	 * An indicator as to whether a study is randomized.  A randomized study is a
	 * study in which the participants are assigned by chance to separate groups that
	 * compare different treatments; neither the researchers nor the participants can
	 * choose which group. Using chance to assign people to groups means that the
	 * groups will be similar and that the treatments they receive can be compared
	 * objectively. At the time of the trial, it is not known which treatment is best.
	 * It is the patient's choice to be on a randomized study.
	 */
	@Column private boolean randomizedInd;
	/**
	 * Title of a protocol limited to 30 characters in length.
	 * BRIDG: A name or abbreviated title by which the document is known.
	 */
	@Column private String shortTitle;
	/**
	 * Code used to identify the sponsor (IND holder) for a clinical trial. Values
	 * include: AB  Abbott Labs; AL  Alkermes, Inc.; APH  Angiotech; AM  Amgen; BF
	 * Brian Fuller, MD; BI  Boehringer Ingelheim; BM Battelle Memorial, Inc.; BW
	 * Burroughs Wellcome; CG Celgene; CL  CanLab Pharm Research; CP  CellPro, Inc.;
	 * CT  CTEP  Cancer Therapy Evaluation Program, NCI; DHF  Daniel H. Fowler, MD; EL
	 * Eli Lilly; EV  Ellen Vitetta, MD; FJ  Fujisawa; GH  Genentech; GI  Gilead
	 * Sciences; GX  Glaxo; HLR  Hoffman LaRoche; IM  Immunogen; IRC  Immune Response
	 * Corp; JA  Janssen; KN  Knoll; LP  The Liposome Company; ME  Medarex, Inc.; MGI
	 * MGI Pharma, Inc.; MK  Merck and Co., Inc.; MT  Maria Turner, MD; NCI National
	 * Cancer Institute program; NI NIAID; PF  Pfizer; PG  Proctor & Gamble; RF
	 * Robert Fenton, MD; RI  RIBI Immunochem; SA  Sandoz; SG Sugen, Inc.; SP
	 * Schering-Plough; THN  Therion; TW  Thomas Waldmann, MD; TX  Texcellon; US  US
	 * Biosciences; VI	Vion Pharmaceuticals; WCE  W.C. Eckelman, MD; XE Xenova, Ltd.
	 */
	@Column private String sponsorCode;
	/**
	 * Codes to represent the status of a protocol in relation to the ability to
	 * enroll participants/patients. Values include: C: Closed, O: Open, S: Suspended,
	 * T: Terminated.
	 */
	@Column private String statusCode;
	/**
	 * Date of the status change of a protocol to 'suspended', requiring that patient
	 * accrual be halted until the protocol is restored to fully active status.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date suspensionDate;
	/**
	 * Total number of patients/subjects/participants needed for protocol enrollment
	 * (accrual). 
	 */
	@Column private int targetAccrualNumber;
	@ManyToOne private Study parentStudy;

	@OneToMany(mappedBy = "study", cascade=CascadeType.ALL) 
	private Set<StudySite> studySites;
	
	public Study(){

	}

	public void finalize() throws Throwable {

	}

	public java.util.Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(java.util.Date activationDate) {
		this.activationDate = activationDate;
	}

	public boolean isBlindedInd() {
		return blindedInd;
	}

	public void setBlindedInd(boolean blindedInd) {
		this.blindedInd = blindedInd;
	}

	public java.util.Date getClosureDate() {
		return closureDate;
	}

	public void setClosureDate(java.util.Date closureDate) {
		this.closureDate = closureDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDiseaseCode() {
		return diseaseCode;
	}

	public void setDiseaseCode(String diseaseCode) {
		this.diseaseCode = diseaseCode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIntentCode() {
		return intentCode;
	}

	public void setIntentCode(String intentCode) {
		this.intentCode = intentCode;
	}

	public String getLongTitle() {
		return longTitle;
	}

	public void setLongTitle(String longTitle) {
		this.longTitle = longTitle;
	}

	public String getMonitorCode() {
		return monitorCode;
	}

	public void setMonitorCode(String monitorCode) {
		this.monitorCode = monitorCode;
	}

	public boolean isMultiInstitutionInd() {
		return multiInstitutionInd;
	}

	public void setMultiInstitutionInd(boolean multiInstitutionInd) {
		this.multiInstitutionInd = multiInstitutionInd;
	}

	public String getNavyNCINumber() {
		return navyNCINumber;
	}

	public void setNavyNCINumber(String navyNCINumber) {
		this.navyNCINumber = navyNCINumber;
	}

	public String getNciNumber() {
		return nciNumber;
	}

	public void setNciNumber(String nciNumber) {
		this.nciNumber = nciNumber;
	}

	public Study getParentStudy() {
		return parentStudy;
	}

	public void setParentStudy(Study parentStudy) {
		this.parentStudy = parentStudy;
	}

	public String getPhaseCode() {
		return phaseCode;
	}

	public void setPhaseCode(String phaseCode) {
		this.phaseCode = phaseCode;
	}

	public String getPrecis() {
		return precis;
	}

	public void setPrecis(String precis) {
		this.precis = precis;
	}

	public boolean isRandomizedInd() {
		return randomizedInd;
	}

	public void setRandomizedInd(boolean randomizedInd) {
		this.randomizedInd = randomizedInd;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getSponsorCode() {
		return sponsorCode;
	}

	public void setSponsorCode(String sponsorCode) {
		this.sponsorCode = sponsorCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public java.util.Date getSuspensionDate() {
		return suspensionDate;
	}

	public void setSuspensionDate(java.util.Date suspensionDate) {
		this.suspensionDate = suspensionDate;
	}

	public int getTargetAccrualNumber() {
		return targetAccrualNumber;
	}

	public void setTargetAccrualNumber(int targetAccrualNumber) {
		this.targetAccrualNumber = targetAccrualNumber;
	}

	public Set<StudySite> getStudySites() {
		if (studySites==null) studySites = new HashSet<StudySite>();
		return studySites;
	}

	public void addStudySite( StudySite studySite ) {
		getStudySites().add(studySite);
		studySite.setStudy(this);
	}
	
	public void setStudySites(Set<StudySite> studySites) {
		this.studySites = studySites;
	}

}




