package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_regpackaged database table.
 * 
 */
@Entity
@Table(name="fdb_regpackaged")
public class FdbRegpackaged implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String ipdid;

	private String adddate;

	private String addldescriptor1;

	private String addldescriptor2;

	private String addldescriptor3;

	private String addldescriptor4;

	private String addldescriptor5;

	private String addldescriptor6;

	private String currentimagefilename;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private Integer drcsingledoserouteind;

	private Integer gcnseqno;

	private Integer gendfid;

	private Integer hicl;

	private Integer hospitalind;

	private String imdid;

	private Integer inactiveingredientreviewind;

	private String labelerid;

	private Integer medicaldeviceind;

	private Integer medid;

	private BigDecimal needlegauge;

	private BigDecimal needlelength;

	private Integer neosingledoserouteind;

	private BigDecimal numstrength;

	private String numstrengthuom;

	private BigDecimal numvolume;

	private String numvolumeuom;

	private String obsoletedate;

	private String packagedescription;

	private BigDecimal packagesize;

	private String packagesizeunitscode;

	private Integer patientpkginsertind;

	private String previousipdid;

	private Integer privlabeledprodind;

	private Integer repackagerind;

	private Integer rtdfgenid;

	private Integer rtgenid;

	private Integer rtid;

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

	private String statuscode;

	private String strength1;

	private String strength2;

	private String strength3;

	private String strength4;

	private String strength5;

	private String strength6;

	private BigDecimal syringecapacity;

	private String updatedate;

    public FdbRegpackaged() {
    }

	public String getIpdid() {
		return this.ipdid;
	}

	public void setIpdid(String ipdid) {
		this.ipdid = ipdid;
	}

	public String getAdddate() {
		return this.adddate;
	}

	public void setAdddate(String adddate) {
		this.adddate = adddate;
	}

	public String getAddldescriptor1() {
		return this.addldescriptor1;
	}

	public void setAddldescriptor1(String addldescriptor1) {
		this.addldescriptor1 = addldescriptor1;
	}

	public String getAddldescriptor2() {
		return this.addldescriptor2;
	}

	public void setAddldescriptor2(String addldescriptor2) {
		this.addldescriptor2 = addldescriptor2;
	}

	public String getAddldescriptor3() {
		return this.addldescriptor3;
	}

	public void setAddldescriptor3(String addldescriptor3) {
		this.addldescriptor3 = addldescriptor3;
	}

	public String getAddldescriptor4() {
		return this.addldescriptor4;
	}

	public void setAddldescriptor4(String addldescriptor4) {
		this.addldescriptor4 = addldescriptor4;
	}

	public String getAddldescriptor5() {
		return this.addldescriptor5;
	}

	public void setAddldescriptor5(String addldescriptor5) {
		this.addldescriptor5 = addldescriptor5;
	}

	public String getAddldescriptor6() {
		return this.addldescriptor6;
	}

	public void setAddldescriptor6(String addldescriptor6) {
		this.addldescriptor6 = addldescriptor6;
	}

	public String getCurrentimagefilename() {
		return this.currentimagefilename;
	}

	public void setCurrentimagefilename(String currentimagefilename) {
		this.currentimagefilename = currentimagefilename;
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

	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}

	public Integer getGendfid() {
		return this.gendfid;
	}

	public void setGendfid(Integer gendfid) {
		this.gendfid = gendfid;
	}

	public Integer getHicl() {
		return this.hicl;
	}

	public void setHicl(Integer hicl) {
		this.hicl = hicl;
	}

	public Integer getHospitalind() {
		return this.hospitalind;
	}

	public void setHospitalind(Integer hospitalind) {
		this.hospitalind = hospitalind;
	}

	public String getImdid() {
		return this.imdid;
	}

	public void setImdid(String imdid) {
		this.imdid = imdid;
	}

	public Integer getInactiveingredientreviewind() {
		return this.inactiveingredientreviewind;
	}

	public void setInactiveingredientreviewind(Integer inactiveingredientreviewind) {
		this.inactiveingredientreviewind = inactiveingredientreviewind;
	}

	public String getLabelerid() {
		return this.labelerid;
	}

	public void setLabelerid(String labelerid) {
		this.labelerid = labelerid;
	}

	public Integer getMedicaldeviceind() {
		return this.medicaldeviceind;
	}

	public void setMedicaldeviceind(Integer medicaldeviceind) {
		this.medicaldeviceind = medicaldeviceind;
	}

	public Integer getMedid() {
		return this.medid;
	}

	public void setMedid(Integer medid) {
		this.medid = medid;
	}

	public BigDecimal getNeedlegauge() {
		return this.needlegauge;
	}

	public void setNeedlegauge(BigDecimal needlegauge) {
		this.needlegauge = needlegauge;
	}

	public BigDecimal getNeedlelength() {
		return this.needlelength;
	}

	public void setNeedlelength(BigDecimal needlelength) {
		this.needlelength = needlelength;
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

	public String getObsoletedate() {
		return this.obsoletedate;
	}

	public void setObsoletedate(String obsoletedate) {
		this.obsoletedate = obsoletedate;
	}

	public String getPackagedescription() {
		return this.packagedescription;
	}

	public void setPackagedescription(String packagedescription) {
		this.packagedescription = packagedescription;
	}

	public BigDecimal getPackagesize() {
		return this.packagesize;
	}

	public void setPackagesize(BigDecimal packagesize) {
		this.packagesize = packagesize;
	}

	public String getPackagesizeunitscode() {
		return this.packagesizeunitscode;
	}

	public void setPackagesizeunitscode(String packagesizeunitscode) {
		this.packagesizeunitscode = packagesizeunitscode;
	}

	public Integer getPatientpkginsertind() {
		return this.patientpkginsertind;
	}

	public void setPatientpkginsertind(Integer patientpkginsertind) {
		this.patientpkginsertind = patientpkginsertind;
	}

	public String getPreviousipdid() {
		return this.previousipdid;
	}

	public void setPreviousipdid(String previousipdid) {
		this.previousipdid = previousipdid;
	}

	public Integer getPrivlabeledprodind() {
		return this.privlabeledprodind;
	}

	public void setPrivlabeledprodind(Integer privlabeledprodind) {
		this.privlabeledprodind = privlabeledprodind;
	}

	public Integer getRepackagerind() {
		return this.repackagerind;
	}

	public void setRepackagerind(Integer repackagerind) {
		this.repackagerind = repackagerind;
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

	public String getStatuscode() {
		return this.statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
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

	public BigDecimal getSyringecapacity() {
		return this.syringecapacity;
	}

	public void setSyringecapacity(BigDecimal syringecapacity) {
		this.syringecapacity = syringecapacity;
	}

	public String getUpdatedate() {
		return this.updatedate;
	}

	public void setUpdatedate(String updatedate) {
		this.updatedate = updatedate;
	}

}