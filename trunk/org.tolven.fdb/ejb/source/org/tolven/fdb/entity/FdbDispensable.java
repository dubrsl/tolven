package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_dispensable database table.
 * 
 */
@Entity
@Table(name="fdb_dispensable")
public class FdbDispensable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer medid;

	private String descaltsearch;

	private String descdisplay;

	private String descsearch;

	private Integer dfid;

	private Integer drcsingledoserouteind;

	private Integer gcnseqno;

	private String genderspecificdrugcode;

	private Integer genericlinkindicator;

	private Integer genericmedid;

	private Integer genericmnid;

	private Integer hasequivpackageddrugsind;

	private Integer hasimagesind;

	private Integer haspackageddrugsind;

	private Integer hicl;

	private Integer inactiveingredientreviewind;

	private Integer medicaldeviceind;

	private Integer mnid;

	private String namesourcecode;

	private String nametypecode;

	private Integer neosingledoserouteind;

	private BigDecimal numstrength;

	private String numstrengthuom;

	private BigDecimal numvolume;

	private String numvolumeuom;

	private String obsoletedate;

	private Integer rdfmid;

	private String refdesi2code;

	private String refdesicode;

	private String reffederaldeaclasscode;

	private String reffederallegendcode;

	private String refgenericcomppricecode;

	private String refgenericdrugnamecode;

	private String refgenericpricespreadcode;

	private String refgenerictheraequivcode;

	private String refinnovatorcode;

	private String refmultisourcecode;

	private String refprivlabeledprodcode;

	private String refrepackagercode;

	private Integer replacedindicator;

	private Integer rmid;

	private Integer rtgenid;

	private Integer rtid;

	private String safenumstrengthuom;

	private String safenumvolumeuom;

	private Integer singledoserouteind;

	private Integer singleingredientind;

	private String statuscode;

	private String strength;

	private String strengthunits;

	@Column(name="tm_confusiongroupid")
	private Integer tmConfusiongroupid;

	@Column(name="tm_descdisplay")
	private String tmDescdisplay;

	@Column(name="tm_groupdesc")
	private String tmGroupdesc;

	@Column(name="tm_sourcecode")
	private String tmSourcecode;

    public FdbDispensable() {
    }

	public Integer getMedid() {
		return this.medid;
	}

	public void setMedid(Integer medid) {
		this.medid = medid;
	}

	public String getDescaltsearch() {
		return this.descaltsearch;
	}

	public void setDescaltsearch(String descaltsearch) {
		this.descaltsearch = descaltsearch;
	}

	public String getDescdisplay() {
		return this.descdisplay;
	}

	public void setDescdisplay(String descdisplay) {
		this.descdisplay = descdisplay;
	}

	public String getDescsearch() {
		return this.descsearch;
	}

	public void setDescsearch(String descsearch) {
		this.descsearch = descsearch;
	}

	public Integer getDfid() {
		return this.dfid;
	}

	public void setDfid(Integer dfid) {
		this.dfid = dfid;
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

	public String getGenderspecificdrugcode() {
		return this.genderspecificdrugcode;
	}

	public void setGenderspecificdrugcode(String genderspecificdrugcode) {
		this.genderspecificdrugcode = genderspecificdrugcode;
	}

	public Integer getGenericlinkindicator() {
		return this.genericlinkindicator;
	}

	public void setGenericlinkindicator(Integer genericlinkindicator) {
		this.genericlinkindicator = genericlinkindicator;
	}

	public Integer getGenericmedid() {
		return this.genericmedid;
	}

	public void setGenericmedid(Integer genericmedid) {
		this.genericmedid = genericmedid;
	}

	public Integer getGenericmnid() {
		return this.genericmnid;
	}

	public void setGenericmnid(Integer genericmnid) {
		this.genericmnid = genericmnid;
	}

	public Integer getHasequivpackageddrugsind() {
		return this.hasequivpackageddrugsind;
	}

	public void setHasequivpackageddrugsind(Integer hasequivpackageddrugsind) {
		this.hasequivpackageddrugsind = hasequivpackageddrugsind;
	}

	public Integer getHasimagesind() {
		return this.hasimagesind;
	}

	public void setHasimagesind(Integer hasimagesind) {
		this.hasimagesind = hasimagesind;
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

	public Integer getMnid() {
		return this.mnid;
	}

	public void setMnid(Integer mnid) {
		this.mnid = mnid;
	}

	public String getNamesourcecode() {
		return this.namesourcecode;
	}

	public void setNamesourcecode(String namesourcecode) {
		this.namesourcecode = namesourcecode;
	}

	public String getNametypecode() {
		return this.nametypecode;
	}

	public void setNametypecode(String nametypecode) {
		this.nametypecode = nametypecode;
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

	public Integer getRdfmid() {
		return this.rdfmid;
	}

	public void setRdfmid(Integer rdfmid) {
		this.rdfmid = rdfmid;
	}

	public String getRefdesi2code() {
		return this.refdesi2code;
	}

	public void setRefdesi2code(String refdesi2code) {
		this.refdesi2code = refdesi2code;
	}

	public String getRefdesicode() {
		return this.refdesicode;
	}

	public void setRefdesicode(String refdesicode) {
		this.refdesicode = refdesicode;
	}

	public String getReffederaldeaclasscode() {
		return this.reffederaldeaclasscode;
	}

	public void setReffederaldeaclasscode(String reffederaldeaclasscode) {
		this.reffederaldeaclasscode = reffederaldeaclasscode;
	}

	public String getReffederallegendcode() {
		return this.reffederallegendcode;
	}

	public void setReffederallegendcode(String reffederallegendcode) {
		this.reffederallegendcode = reffederallegendcode;
	}

	public String getRefgenericcomppricecode() {
		return this.refgenericcomppricecode;
	}

	public void setRefgenericcomppricecode(String refgenericcomppricecode) {
		this.refgenericcomppricecode = refgenericcomppricecode;
	}

	public String getRefgenericdrugnamecode() {
		return this.refgenericdrugnamecode;
	}

	public void setRefgenericdrugnamecode(String refgenericdrugnamecode) {
		this.refgenericdrugnamecode = refgenericdrugnamecode;
	}

	public String getRefgenericpricespreadcode() {
		return this.refgenericpricespreadcode;
	}

	public void setRefgenericpricespreadcode(String refgenericpricespreadcode) {
		this.refgenericpricespreadcode = refgenericpricespreadcode;
	}

	public String getRefgenerictheraequivcode() {
		return this.refgenerictheraequivcode;
	}

	public void setRefgenerictheraequivcode(String refgenerictheraequivcode) {
		this.refgenerictheraequivcode = refgenerictheraequivcode;
	}

	public String getRefinnovatorcode() {
		return this.refinnovatorcode;
	}

	public void setRefinnovatorcode(String refinnovatorcode) {
		this.refinnovatorcode = refinnovatorcode;
	}

	public String getRefmultisourcecode() {
		return this.refmultisourcecode;
	}

	public void setRefmultisourcecode(String refmultisourcecode) {
		this.refmultisourcecode = refmultisourcecode;
	}

	public String getRefprivlabeledprodcode() {
		return this.refprivlabeledprodcode;
	}

	public void setRefprivlabeledprodcode(String refprivlabeledprodcode) {
		this.refprivlabeledprodcode = refprivlabeledprodcode;
	}

	public String getRefrepackagercode() {
		return this.refrepackagercode;
	}

	public void setRefrepackagercode(String refrepackagercode) {
		this.refrepackagercode = refrepackagercode;
	}

	public Integer getReplacedindicator() {
		return this.replacedindicator;
	}

	public void setReplacedindicator(Integer replacedindicator) {
		this.replacedindicator = replacedindicator;
	}

	public Integer getRmid() {
		return this.rmid;
	}

	public void setRmid(Integer rmid) {
		this.rmid = rmid;
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

	public String getStrength() {
		return this.strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	public String getStrengthunits() {
		return this.strengthunits;
	}

	public void setStrengthunits(String strengthunits) {
		this.strengthunits = strengthunits;
	}

	public Integer getTmConfusiongroupid() {
		return this.tmConfusiongroupid;
	}

	public void setTmConfusiongroupid(Integer tmConfusiongroupid) {
		this.tmConfusiongroupid = tmConfusiongroupid;
	}

	public String getTmDescdisplay() {
		return this.tmDescdisplay;
	}

	public void setTmDescdisplay(String tmDescdisplay) {
		this.tmDescdisplay = tmDescdisplay;
	}

	public String getTmGroupdesc() {
		return this.tmGroupdesc;
	}

	public void setTmGroupdesc(String tmGroupdesc) {
		this.tmGroupdesc = tmGroupdesc;
	}

	public String getTmSourcecode() {
		return this.tmSourcecode;
	}

	public void setTmSourcecode(String tmSourcecode) {
		this.tmSourcecode = tmSourcecode;
	}

}