package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_packageddrug database table.
 * 
 */
@Entity
@Table(name="fdb_packageddrug")
public class FdbPackageddrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String pmid;

	private String adddate;

	private String adddescriptor;

	private String bbawpchangedate;

	private String currentimagefilename;

	private String descaltsearch;

	private String descdisplay;

	private String descsearch;

	private String desi2code;

	private String desi2effectivedate;

	private String desicode;

	private String desieffectivedate;

	private Integer dfid;

	private Integer drcsingledoserouteind;

	private String federaldeaclasscode;

	private String federallegendcode;

	private String formatcode;

	private Integer gcnseqno;

	private String gcrtsystemic;

	private String gencomppricechangedate;

	private String gencomppricecode;

	private Integer genericmnid;

	private String genmfgcode;

	private String gennameddrugcode;

	private String genpricespreadcode;

	private String gentherequivcode;

	private String hcfacommonproccode;

	private String hcfadrugcategorycode;

	private String hcfadrugtypecode;

	private String hcfafdaapprovaldate;

	private String hcfafdatheraequivcode;

	private String hcfamarketentrydate;

	private String hcfaterminationdate;

	private BigDecimal hcfaunitsperpackage;

	private String hcfaunittypecode;

	private Integer hicl;

	private Integer homehealthind;

	private Integer hospitalind;

	private Integer inactiveingredientreviewind;

	private Integer innerpackind;

	private Integer innovatorind;

	private Integer institutionalprodind;

	private String labelerid;

	private String labelname25;

	private String labelname25gennamecode;

	private Integer maintenancedrugind;

	private Integer medicaldeviceind;

	private Integer medid;

	private Integer miniind;

	private Integer mnid;

	private String multisourcecode;

	private String nametypecode;

	private String ndc;

	private String ndcformatted;

	private BigDecimal needlegauge;

	private BigDecimal needlelength;

	private Integer neosingledoserouteind;

	private BigDecimal numstrength;

	private String numstrengthuom;

	private BigDecimal numvolume;

	private String numvolumeuom;

	private String obsoletedate;

	private String orangebookcode;

	private Integer outerpackind;

	private String packagedescription;

	private Integer packagequantity;

	private BigDecimal packagesize;

	private BigDecimal packagesizeequiv;

	private String packagesizeunitscode;

	private Integer patientpkginsertind;

	private String previousndc;

	private Integer privlabeledprodind;

	private Integer rdfmid;

	private Integer repackagerind;

	private String replacementndc;

	private Integer rmid;

	private Integer rtgenid;

	private Integer rtid;

	private String safedescaltsearch;

	private String safedescdisplay;

	private String safedescsearch;

	private String safenumstrengthuom;

	private String safenumvolumeuom;

	private Integer shelfpack;

	private Integer shipperpack;

	private Integer singleingredientind;

	private Integer standardpackind;

	private String strength;

	private String strengthunits;

	private BigDecimal syringecapacity;

	private String thirdpartyrestrictioncode;

	private String top200rank;

	private String top50genrank;

	private Integer unitdosepackagingind;

	private Integer unitofusepackagingind;

	private String updatedate;

    public FdbPackageddrug() {
    }

	public String getPmid() {
		return this.pmid;
	}

	public void setPmid(String pmid) {
		this.pmid = pmid;
	}

	public String getAdddate() {
		return this.adddate;
	}

	public void setAdddate(String adddate) {
		this.adddate = adddate;
	}

	public String getAdddescriptor() {
		return this.adddescriptor;
	}

	public void setAdddescriptor(String adddescriptor) {
		this.adddescriptor = adddescriptor;
	}

	public String getBbawpchangedate() {
		return this.bbawpchangedate;
	}

	public void setBbawpchangedate(String bbawpchangedate) {
		this.bbawpchangedate = bbawpchangedate;
	}

	public String getCurrentimagefilename() {
		return this.currentimagefilename;
	}

	public void setCurrentimagefilename(String currentimagefilename) {
		this.currentimagefilename = currentimagefilename;
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

	public String getDesi2code() {
		return this.desi2code;
	}

	public void setDesi2code(String desi2code) {
		this.desi2code = desi2code;
	}

	public String getDesi2effectivedate() {
		return this.desi2effectivedate;
	}

	public void setDesi2effectivedate(String desi2effectivedate) {
		this.desi2effectivedate = desi2effectivedate;
	}

	public String getDesicode() {
		return this.desicode;
	}

	public void setDesicode(String desicode) {
		this.desicode = desicode;
	}

	public String getDesieffectivedate() {
		return this.desieffectivedate;
	}

	public void setDesieffectivedate(String desieffectivedate) {
		this.desieffectivedate = desieffectivedate;
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

	public String getFederaldeaclasscode() {
		return this.federaldeaclasscode;
	}

	public void setFederaldeaclasscode(String federaldeaclasscode) {
		this.federaldeaclasscode = federaldeaclasscode;
	}

	public String getFederallegendcode() {
		return this.federallegendcode;
	}

	public void setFederallegendcode(String federallegendcode) {
		this.federallegendcode = federallegendcode;
	}

	public String getFormatcode() {
		return this.formatcode;
	}

	public void setFormatcode(String formatcode) {
		this.formatcode = formatcode;
	}

	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}

	public String getGcrtsystemic() {
		return this.gcrtsystemic;
	}

	public void setGcrtsystemic(String gcrtsystemic) {
		this.gcrtsystemic = gcrtsystemic;
	}

	public String getGencomppricechangedate() {
		return this.gencomppricechangedate;
	}

	public void setGencomppricechangedate(String gencomppricechangedate) {
		this.gencomppricechangedate = gencomppricechangedate;
	}

	public String getGencomppricecode() {
		return this.gencomppricecode;
	}

	public void setGencomppricecode(String gencomppricecode) {
		this.gencomppricecode = gencomppricecode;
	}

	public Integer getGenericmnid() {
		return this.genericmnid;
	}

	public void setGenericmnid(Integer genericmnid) {
		this.genericmnid = genericmnid;
	}

	public String getGenmfgcode() {
		return this.genmfgcode;
	}

	public void setGenmfgcode(String genmfgcode) {
		this.genmfgcode = genmfgcode;
	}

	public String getGennameddrugcode() {
		return this.gennameddrugcode;
	}

	public void setGennameddrugcode(String gennameddrugcode) {
		this.gennameddrugcode = gennameddrugcode;
	}

	public String getGenpricespreadcode() {
		return this.genpricespreadcode;
	}

	public void setGenpricespreadcode(String genpricespreadcode) {
		this.genpricespreadcode = genpricespreadcode;
	}

	public String getGentherequivcode() {
		return this.gentherequivcode;
	}

	public void setGentherequivcode(String gentherequivcode) {
		this.gentherequivcode = gentherequivcode;
	}

	public String getHcfacommonproccode() {
		return this.hcfacommonproccode;
	}

	public void setHcfacommonproccode(String hcfacommonproccode) {
		this.hcfacommonproccode = hcfacommonproccode;
	}

	public String getHcfadrugcategorycode() {
		return this.hcfadrugcategorycode;
	}

	public void setHcfadrugcategorycode(String hcfadrugcategorycode) {
		this.hcfadrugcategorycode = hcfadrugcategorycode;
	}

	public String getHcfadrugtypecode() {
		return this.hcfadrugtypecode;
	}

	public void setHcfadrugtypecode(String hcfadrugtypecode) {
		this.hcfadrugtypecode = hcfadrugtypecode;
	}

	public String getHcfafdaapprovaldate() {
		return this.hcfafdaapprovaldate;
	}

	public void setHcfafdaapprovaldate(String hcfafdaapprovaldate) {
		this.hcfafdaapprovaldate = hcfafdaapprovaldate;
	}

	public String getHcfafdatheraequivcode() {
		return this.hcfafdatheraequivcode;
	}

	public void setHcfafdatheraequivcode(String hcfafdatheraequivcode) {
		this.hcfafdatheraequivcode = hcfafdatheraequivcode;
	}

	public String getHcfamarketentrydate() {
		return this.hcfamarketentrydate;
	}

	public void setHcfamarketentrydate(String hcfamarketentrydate) {
		this.hcfamarketentrydate = hcfamarketentrydate;
	}

	public String getHcfaterminationdate() {
		return this.hcfaterminationdate;
	}

	public void setHcfaterminationdate(String hcfaterminationdate) {
		this.hcfaterminationdate = hcfaterminationdate;
	}

	public BigDecimal getHcfaunitsperpackage() {
		return this.hcfaunitsperpackage;
	}

	public void setHcfaunitsperpackage(BigDecimal hcfaunitsperpackage) {
		this.hcfaunitsperpackage = hcfaunitsperpackage;
	}

	public String getHcfaunittypecode() {
		return this.hcfaunittypecode;
	}

	public void setHcfaunittypecode(String hcfaunittypecode) {
		this.hcfaunittypecode = hcfaunittypecode;
	}

	public Integer getHicl() {
		return this.hicl;
	}

	public void setHicl(Integer hicl) {
		this.hicl = hicl;
	}

	public Integer getHomehealthind() {
		return this.homehealthind;
	}

	public void setHomehealthind(Integer homehealthind) {
		this.homehealthind = homehealthind;
	}

	public Integer getHospitalind() {
		return this.hospitalind;
	}

	public void setHospitalind(Integer hospitalind) {
		this.hospitalind = hospitalind;
	}

	public Integer getInactiveingredientreviewind() {
		return this.inactiveingredientreviewind;
	}

	public void setInactiveingredientreviewind(Integer inactiveingredientreviewind) {
		this.inactiveingredientreviewind = inactiveingredientreviewind;
	}

	public Integer getInnerpackind() {
		return this.innerpackind;
	}

	public void setInnerpackind(Integer innerpackind) {
		this.innerpackind = innerpackind;
	}

	public Integer getInnovatorind() {
		return this.innovatorind;
	}

	public void setInnovatorind(Integer innovatorind) {
		this.innovatorind = innovatorind;
	}

	public Integer getInstitutionalprodind() {
		return this.institutionalprodind;
	}

	public void setInstitutionalprodind(Integer institutionalprodind) {
		this.institutionalprodind = institutionalprodind;
	}

	public String getLabelerid() {
		return this.labelerid;
	}

	public void setLabelerid(String labelerid) {
		this.labelerid = labelerid;
	}

	public String getLabelname25() {
		return this.labelname25;
	}

	public void setLabelname25(String labelname25) {
		this.labelname25 = labelname25;
	}

	public String getLabelname25gennamecode() {
		return this.labelname25gennamecode;
	}

	public void setLabelname25gennamecode(String labelname25gennamecode) {
		this.labelname25gennamecode = labelname25gennamecode;
	}

	public Integer getMaintenancedrugind() {
		return this.maintenancedrugind;
	}

	public void setMaintenancedrugind(Integer maintenancedrugind) {
		this.maintenancedrugind = maintenancedrugind;
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

	public Integer getMiniind() {
		return this.miniind;
	}

	public void setMiniind(Integer miniind) {
		this.miniind = miniind;
	}

	public Integer getMnid() {
		return this.mnid;
	}

	public void setMnid(Integer mnid) {
		this.mnid = mnid;
	}

	public String getMultisourcecode() {
		return this.multisourcecode;
	}

	public void setMultisourcecode(String multisourcecode) {
		this.multisourcecode = multisourcecode;
	}

	public String getNametypecode() {
		return this.nametypecode;
	}

	public void setNametypecode(String nametypecode) {
		this.nametypecode = nametypecode;
	}

	public String getNdc() {
		return this.ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	public String getNdcformatted() {
		return this.ndcformatted;
	}

	public void setNdcformatted(String ndcformatted) {
		this.ndcformatted = ndcformatted;
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

	public String getOrangebookcode() {
		return this.orangebookcode;
	}

	public void setOrangebookcode(String orangebookcode) {
		this.orangebookcode = orangebookcode;
	}

	public Integer getOuterpackind() {
		return this.outerpackind;
	}

	public void setOuterpackind(Integer outerpackind) {
		this.outerpackind = outerpackind;
	}

	public String getPackagedescription() {
		return this.packagedescription;
	}

	public void setPackagedescription(String packagedescription) {
		this.packagedescription = packagedescription;
	}

	public Integer getPackagequantity() {
		return this.packagequantity;
	}

	public void setPackagequantity(Integer packagequantity) {
		this.packagequantity = packagequantity;
	}

	public BigDecimal getPackagesize() {
		return this.packagesize;
	}

	public void setPackagesize(BigDecimal packagesize) {
		this.packagesize = packagesize;
	}

	public BigDecimal getPackagesizeequiv() {
		return this.packagesizeequiv;
	}

	public void setPackagesizeequiv(BigDecimal packagesizeequiv) {
		this.packagesizeequiv = packagesizeequiv;
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

	public String getPreviousndc() {
		return this.previousndc;
	}

	public void setPreviousndc(String previousndc) {
		this.previousndc = previousndc;
	}

	public Integer getPrivlabeledprodind() {
		return this.privlabeledprodind;
	}

	public void setPrivlabeledprodind(Integer privlabeledprodind) {
		this.privlabeledprodind = privlabeledprodind;
	}

	public Integer getRdfmid() {
		return this.rdfmid;
	}

	public void setRdfmid(Integer rdfmid) {
		this.rdfmid = rdfmid;
	}

	public Integer getRepackagerind() {
		return this.repackagerind;
	}

	public void setRepackagerind(Integer repackagerind) {
		this.repackagerind = repackagerind;
	}

	public String getReplacementndc() {
		return this.replacementndc;
	}

	public void setReplacementndc(String replacementndc) {
		this.replacementndc = replacementndc;
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

	public String getSafedescaltsearch() {
		return this.safedescaltsearch;
	}

	public void setSafedescaltsearch(String safedescaltsearch) {
		this.safedescaltsearch = safedescaltsearch;
	}

	public String getSafedescdisplay() {
		return this.safedescdisplay;
	}

	public void setSafedescdisplay(String safedescdisplay) {
		this.safedescdisplay = safedescdisplay;
	}

	public String getSafedescsearch() {
		return this.safedescsearch;
	}

	public void setSafedescsearch(String safedescsearch) {
		this.safedescsearch = safedescsearch;
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

	public Integer getShelfpack() {
		return this.shelfpack;
	}

	public void setShelfpack(Integer shelfpack) {
		this.shelfpack = shelfpack;
	}

	public Integer getShipperpack() {
		return this.shipperpack;
	}

	public void setShipperpack(Integer shipperpack) {
		this.shipperpack = shipperpack;
	}

	public Integer getSingleingredientind() {
		return this.singleingredientind;
	}

	public void setSingleingredientind(Integer singleingredientind) {
		this.singleingredientind = singleingredientind;
	}

	public Integer getStandardpackind() {
		return this.standardpackind;
	}

	public void setStandardpackind(Integer standardpackind) {
		this.standardpackind = standardpackind;
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

	public BigDecimal getSyringecapacity() {
		return this.syringecapacity;
	}

	public void setSyringecapacity(BigDecimal syringecapacity) {
		this.syringecapacity = syringecapacity;
	}

	public String getThirdpartyrestrictioncode() {
		return this.thirdpartyrestrictioncode;
	}

	public void setThirdpartyrestrictioncode(String thirdpartyrestrictioncode) {
		this.thirdpartyrestrictioncode = thirdpartyrestrictioncode;
	}

	public String getTop200rank() {
		return this.top200rank;
	}

	public void setTop200rank(String top200rank) {
		this.top200rank = top200rank;
	}

	public String getTop50genrank() {
		return this.top50genrank;
	}

	public void setTop50genrank(String top50genrank) {
		this.top50genrank = top50genrank;
	}

	public Integer getUnitdosepackagingind() {
		return this.unitdosepackagingind;
	}

	public void setUnitdosepackagingind(Integer unitdosepackagingind) {
		this.unitdosepackagingind = unitdosepackagingind;
	}

	public Integer getUnitofusepackagingind() {
		return this.unitofusepackagingind;
	}

	public void setUnitofusepackagingind(Integer unitofusepackagingind) {
		this.unitofusepackagingind = unitofusepackagingind;
	}

	public String getUpdatedate() {
		return this.updatedate;
	}

	public void setUpdatedate(String updatedate) {
		this.updatedate = updatedate;
	}

}