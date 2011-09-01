package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_generic_routeddfdrug database table.
 * 
 */
@Entity
@Table(name="fdb_generic_routeddfdrug")
public class FdbGenericRouteddfdrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer rtdfgenid;

	private Integer canbedosedind;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	@Column(name="gcnseqno_unique")
	private Integer gcnseqnoUnique;

	private Integer gendfid;

	private Integer haspackageddrugsind;

	private Integer hicl;

	private Integer medicaldeviceind;

	private Integer neocanbedosedind;

	private Integer neosingledoserouteind;

	private Integer regionalind;

	private Integer rtgenid;

	private Integer rtid;

	private String rxotccode;

	private Integer singledoserouteind;

	private Integer singleingredientind;

	private Integer statusddcm;

	private Integer statusddim;

	private Integer statusdfim;

	private Integer statusdrc;

	private Integer statusdt;

	private Integer statusprecgeri;

	private Integer statuspreclact;

	private Integer statusprecpedi;

	private Integer statusprecpreg;

	private Integer statusside;

    public FdbGenericRouteddfdrug() {
    }

	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}

	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
	}

	public Integer getCanbedosedind() {
		return this.canbedosedind;
	}

	public void setCanbedosedind(Integer canbedosedind) {
		this.canbedosedind = canbedosedind;
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

	public Integer getGendfid() {
		return this.gendfid;
	}

	public void setGendfid(Integer gendfid) {
		this.gendfid = gendfid;
	}

	public Integer getHaspackageddrugsind() {
		return this.haspackageddrugsind;
	}

	public void setHaspackageddrugsind(Integer haspackageddrugsind) {
		this.haspackageddrugsind = haspackageddrugsind;
	}

	public Integer getHicl() {
		return this.hicl;
	}

	public void setHicl(Integer hicl) {
		this.hicl = hicl;
	}

	public Integer getMedicaldeviceind() {
		return this.medicaldeviceind;
	}

	public void setMedicaldeviceind(Integer medicaldeviceind) {
		this.medicaldeviceind = medicaldeviceind;
	}

	public Integer getNeocanbedosedind() {
		return this.neocanbedosedind;
	}

	public void setNeocanbedosedind(Integer neocanbedosedind) {
		this.neocanbedosedind = neocanbedosedind;
	}

	public Integer getNeosingledoserouteind() {
		return this.neosingledoserouteind;
	}

	public void setNeosingledoserouteind(Integer neosingledoserouteind) {
		this.neosingledoserouteind = neosingledoserouteind;
	}

	public Integer getRegionalind() {
		return this.regionalind;
	}

	public void setRegionalind(Integer regionalind) {
		this.regionalind = regionalind;
	}

	public Integer getRtgenid() {
		return this.rtgenid;
	}

	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}

	public Integer getRtid() {
		return this.rtid;
	}

	public void setRtid(Integer rtid) {
		this.rtid = rtid;
	}

	public String getRxotccode() {
		return this.rxotccode;
	}

	public void setRxotccode(String rxotccode) {
		this.rxotccode = rxotccode;
	}

	public Integer getSingledoserouteind() {
		return this.singledoserouteind;
	}

	public void setSingledoserouteind(Integer singledoserouteind) {
		this.singledoserouteind = singledoserouteind;
	}

	public Integer getSingleingredientind() {
		return this.singleingredientind;
	}

	public void setSingleingredientind(Integer singleingredientind) {
		this.singleingredientind = singleingredientind;
	}

	public Integer getStatusddcm() {
		return this.statusddcm;
	}

	public void setStatusddcm(Integer statusddcm) {
		this.statusddcm = statusddcm;
	}

	public Integer getStatusddim() {
		return this.statusddim;
	}

	public void setStatusddim(Integer statusddim) {
		this.statusddim = statusddim;
	}

	public Integer getStatusdfim() {
		return this.statusdfim;
	}

	public void setStatusdfim(Integer statusdfim) {
		this.statusdfim = statusdfim;
	}

	public Integer getStatusdrc() {
		return this.statusdrc;
	}

	public void setStatusdrc(Integer statusdrc) {
		this.statusdrc = statusdrc;
	}

	public Integer getStatusdt() {
		return this.statusdt;
	}

	public void setStatusdt(Integer statusdt) {
		this.statusdt = statusdt;
	}

	public Integer getStatusprecgeri() {
		return this.statusprecgeri;
	}

	public void setStatusprecgeri(Integer statusprecgeri) {
		this.statusprecgeri = statusprecgeri;
	}

	public Integer getStatuspreclact() {
		return this.statuspreclact;
	}

	public void setStatuspreclact(Integer statuspreclact) {
		this.statuspreclact = statuspreclact;
	}

	public Integer getStatusprecpedi() {
		return this.statusprecpedi;
	}

	public void setStatusprecpedi(Integer statusprecpedi) {
		this.statusprecpedi = statusprecpedi;
	}

	public Integer getStatusprecpreg() {
		return this.statusprecpreg;
	}

	public void setStatusprecpreg(Integer statusprecpreg) {
		this.statusprecpreg = statusprecpreg;
	}

	public Integer getStatusside() {
		return this.statusside;
	}

	public void setStatusside(Integer statusside) {
		this.statusside = statusside;
	}

}