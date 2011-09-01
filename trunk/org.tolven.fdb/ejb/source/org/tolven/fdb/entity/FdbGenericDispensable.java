package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_generic_dispensable database table.
 * 
 */
@Entity
@Table(name="fdb_generic_dispensable")
public class FdbGenericDispensable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer gcnseqno;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private Integer drcsingledoserouteind;

	private String genderspecificdrugcode;

	private Integer gendfid;

	private Integer genericmedid;

	private Integer hasgestagedosingind;

	private Integer haspackageddrugsind;

	private Integer hicl;

	private Integer inactiveingredientreviewind;

	private Integer medicaldeviceind;

	private Integer neosingledoserouteind;

	private BigDecimal numstrength;

	private String numstrengthuom;

	private BigDecimal numvolume;

	private String numvolumeuom;

	private Integer regionalind;

	private Integer rtdfgenid;

	private Integer rtgenid;

	private Integer rtid;

	private String rxotccode;

	private String safenumstrengthuom;

	private String safenumvolumeuom;

	private String safestrength1;

	private String safestrength2;

	private String safestrength3;

	private String safestrength4;

	private String safestrength5;

	private String safestrength6;

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

	private String strength1;

	private String strength2;

	private String strength3;

	private String strength4;

	private String strength5;

	private String strength6;

    public FdbGenericDispensable() {
    }

	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
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

	public Integer getDrcsingledoserouteind() {
		return this.drcsingledoserouteind;
	}

	public void setDrcsingledoserouteind(Integer drcsingledoserouteind) {
		this.drcsingledoserouteind = drcsingledoserouteind;
	}

	public String getGenderspecificdrugcode() {
		return this.genderspecificdrugcode;
	}

	public void setGenderspecificdrugcode(String genderspecificdrugcode) {
		this.genderspecificdrugcode = genderspecificdrugcode;
	}

	public Integer getGendfid() {
		return this.gendfid;
	}

	public void setGendfid(Integer gendfid) {
		this.gendfid = gendfid;
	}

	public Integer getGenericmedid() {
		return this.genericmedid;
	}

	public void setGenericmedid(Integer genericmedid) {
		this.genericmedid = genericmedid;
	}

	public Integer getHasgestagedosingind() {
		return this.hasgestagedosingind;
	}

	public void setHasgestagedosingind(Integer hasgestagedosingind) {
		this.hasgestagedosingind = hasgestagedosingind;
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

	public Integer getInactiveingredientreviewind() {
		return this.inactiveingredientreviewind;
	}

	public void setInactiveingredientreviewind(Integer inactiveingredientreviewind) {
		this.inactiveingredientreviewind = inactiveingredientreviewind;
	}

	public Integer getMedicaldeviceind() {
		return this.medicaldeviceind;
	}

	public void setMedicaldeviceind(Integer medicaldeviceind) {
		this.medicaldeviceind = medicaldeviceind;
	}

	public Integer getNeosingledoserouteind() {
		return this.neosingledoserouteind;
	}

	public void setNeosingledoserouteind(Integer neosingledoserouteind) {
		this.neosingledoserouteind = neosingledoserouteind;
	}

	public BigDecimal getNumstrength() {
		return this.numstrength;
	}

	public void setNumstrength(BigDecimal numstrength) {
		this.numstrength = numstrength;
	}

	public String getNumstrengthuom() {
		return this.numstrengthuom;
	}

	public void setNumstrengthuom(String numstrengthuom) {
		this.numstrengthuom = numstrengthuom;
	}

	public BigDecimal getNumvolume() {
		return this.numvolume;
	}

	public void setNumvolume(BigDecimal numvolume) {
		this.numvolume = numvolume;
	}

	public String getNumvolumeuom() {
		return this.numvolumeuom;
	}

	public void setNumvolumeuom(String numvolumeuom) {
		this.numvolumeuom = numvolumeuom;
	}

	public Integer getRegionalind() {
		return this.regionalind;
	}

	public void setRegionalind(Integer regionalind) {
		this.regionalind = regionalind;
	}

	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}

	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
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

	public String getSafenumstrengthuom() {
		return this.safenumstrengthuom;
	}

	public void setSafenumstrengthuom(String safenumstrengthuom) {
		this.safenumstrengthuom = safenumstrengthuom;
	}

	public String getSafenumvolumeuom() {
		return this.safenumvolumeuom;
	}

	public void setSafenumvolumeuom(String safenumvolumeuom) {
		this.safenumvolumeuom = safenumvolumeuom;
	}

	public String getSafestrength1() {
		return this.safestrength1;
	}

	public void setSafestrength1(String safestrength1) {
		this.safestrength1 = safestrength1;
	}

	public String getSafestrength2() {
		return this.safestrength2;
	}

	public void setSafestrength2(String safestrength2) {
		this.safestrength2 = safestrength2;
	}

	public String getSafestrength3() {
		return this.safestrength3;
	}

	public void setSafestrength3(String safestrength3) {
		this.safestrength3 = safestrength3;
	}

	public String getSafestrength4() {
		return this.safestrength4;
	}

	public void setSafestrength4(String safestrength4) {
		this.safestrength4 = safestrength4;
	}

	public String getSafestrength5() {
		return this.safestrength5;
	}

	public void setSafestrength5(String safestrength5) {
		this.safestrength5 = safestrength5;
	}

	public String getSafestrength6() {
		return this.safestrength6;
	}

	public void setSafestrength6(String safestrength6) {
		this.safestrength6 = safestrength6;
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

	public String getStrength1() {
		return this.strength1;
	}

	public void setStrength1(String strength1) {
		this.strength1 = strength1;
	}

	public String getStrength2() {
		return this.strength2;
	}

	public void setStrength2(String strength2) {
		this.strength2 = strength2;
	}

	public String getStrength3() {
		return this.strength3;
	}

	public void setStrength3(String strength3) {
		this.strength3 = strength3;
	}

	public String getStrength4() {
		return this.strength4;
	}

	public void setStrength4(String strength4) {
		this.strength4 = strength4;
	}

	public String getStrength5() {
		return this.strength5;
	}

	public void setStrength5(String strength5) {
		this.strength5 = strength5;
	}

	public String getStrength6() {
		return this.strength6;
	}

	public void setStrength6(String strength6) {
		this.strength6 = strength6;
	}

}