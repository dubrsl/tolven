package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_generic_drugname database table.
 * 
 */
@Entity
@Table(name="fdb_generic_drugname")
public class FdbGenericDrugname implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer hicl;

	private String compid;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	@Column(name="gcnseqno_unique")
	private Integer gcnseqnoUnique;

	private Integer haspackageddrugsind;

	private Integer medicaldeviceind;

	private Integer regionalind;

	@Column(name="rtdfgenid_unique")
	private Integer rtdfgenidUnique;

	@Column(name="rtgenid_unique")
	private Integer rtgenidUnique;

	private String rxotccode;

	private Integer singleingredientind;

    public FdbGenericDrugname() {
    }

	public Integer getHicl() {
		return this.hicl;
	}

	public void setHicl(Integer hicl) {
		this.hicl = hicl;
	}

	public String getCompid() {
		return this.compid;
	}

	public void setCompid(String compid) {
		this.compid = compid;
	}

	public String getDescription1() {
		return this.description1;
	}

	public void setDescription1(String description1) {
		this.description1 = description1;
	}

	public String getDescription2() {
		return this.description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
	}

	public String getDescription3() {
		return this.description3;
	}

	public void setDescription3(String description3) {
		this.description3 = description3;
	}

	public String getDescription4() {
		return this.description4;
	}

	public void setDescription4(String description4) {
		this.description4 = description4;
	}

	public String getDescription5() {
		return this.description5;
	}

	public void setDescription5(String description5) {
		this.description5 = description5;
	}

	public String getDescription6() {
		return this.description6;
	}

	public void setDescription6(String description6) {
		this.description6 = description6;
	}

	public Integer getGcnseqnoUnique() {
		return this.gcnseqnoUnique;
	}

	public void setGcnseqnoUnique(Integer gcnseqnoUnique) {
		this.gcnseqnoUnique = gcnseqnoUnique;
	}

	public Integer getHaspackageddrugsind() {
		return this.haspackageddrugsind;
	}

	public void setHaspackageddrugsind(Integer haspackageddrugsind) {
		this.haspackageddrugsind = haspackageddrugsind;
	}

	public Integer getMedicaldeviceind() {
		return this.medicaldeviceind;
	}

	public void setMedicaldeviceind(Integer medicaldeviceind) {
		this.medicaldeviceind = medicaldeviceind;
	}

	public Integer getRegionalind() {
		return this.regionalind;
	}

	public void setRegionalind(Integer regionalind) {
		this.regionalind = regionalind;
	}

	public Integer getRtdfgenidUnique() {
		return this.rtdfgenidUnique;
	}

	public void setRtdfgenidUnique(Integer rtdfgenidUnique) {
		this.rtdfgenidUnique = rtdfgenidUnique;
	}

	public Integer getRtgenidUnique() {
		return this.rtgenidUnique;
	}

	public void setRtgenidUnique(Integer rtgenidUnique) {
		this.rtgenidUnique = rtgenidUnique;
	}

	public String getRxotccode() {
		return this.rxotccode;
	}

	public void setRxotccode(String rxotccode) {
		this.rxotccode = rxotccode;
	}

	public Integer getSingleingredientind() {
		return this.singleingredientind;
	}

	public void setSingleingredientind(Integer singleingredientind) {
		this.singleingredientind = singleingredientind;
	}

}