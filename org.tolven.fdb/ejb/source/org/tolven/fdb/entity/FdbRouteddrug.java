package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_routeddrug database table.
 * 
 */
@Entity
@Table(name="fdb_routeddrug")
public class FdbRouteddrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer rmid;

	private Integer canbedosedind;

	private Integer clinicallinkind;

	private String descaltsearch;

	private String descdisplay;

	private String descsearch;

	private Integer genericmnid;

	private Integer hasequivpackageddrugsind;

	private Integer haspackageddrugsind;

	@Column(name="hicl_unique")
	private Integer hiclUnique;

	private Integer medicaldeviceind;

	@Column(name="medid_unique")
	private Integer medidUnique;

	private Integer mnid;

	private String nametypecode;

	private Integer neocanbedosedind;

	private Integer neosingledoserouteind;

	private String obsoletedate;

	@Column(name="rdfmid_unique")
	private Integer rdfmidUnique;

	private String reffederallegendcode;

	private Integer replacedindicator;

	@Column(name="rtgenid_unique")
	private Integer rtgenidUnique;

	private Integer rtid;

	private Integer singledoserouteind;

	private Integer singleingredientind;

	private String statuscode;

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

	@Column(name="tm_confusiongroupid")
	private Integer tmConfusiongroupid;

	@Column(name="tm_descdisplay")
	private String tmDescdisplay;

	@Column(name="tm_groupdesc")
	private String tmGroupdesc;

	@Column(name="tm_sourcecode")
	private String tmSourcecode;

    public FdbRouteddrug() {
    }

	public Integer getRmid() {
		return this.rmid;
	}

	public void setRmid(Integer rmid) {
		this.rmid = rmid;
	}

	public Integer getCanbedosedind() {
		return this.canbedosedind;
	}

	public void setCanbedosedind(Integer canbedosedind) {
		this.canbedosedind = canbedosedind;
	}

	public Integer getClinicallinkind() {
		return this.clinicallinkind;
	}

	public void setClinicallinkind(Integer clinicallinkind) {
		this.clinicallinkind = clinicallinkind;
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

	public Integer getHaspackageddrugsind() {
		return this.haspackageddrugsind;
	}

	public void setHaspackageddrugsind(Integer haspackageddrugsind) {
		this.haspackageddrugsind = haspackageddrugsind;
	}

	public Integer getHiclUnique() {
		return this.hiclUnique;
	}

	public void setHiclUnique(Integer hiclUnique) {
		this.hiclUnique = hiclUnique;
	}

	public Integer getMedicaldeviceind() {
		return this.medicaldeviceind;
	}

	public void setMedicaldeviceind(Integer medicaldeviceind) {
		this.medicaldeviceind = medicaldeviceind;
	}

	public Integer getMedidUnique() {
		return this.medidUnique;
	}

	public void setMedidUnique(Integer medidUnique) {
		this.medidUnique = medidUnique;
	}

	public Integer getMnid() {
		return this.mnid;
	}

	public void setMnid(Integer mnid) {
		this.mnid = mnid;
	}

	public String getNametypecode() {
		return this.nametypecode;
	}

	public void setNametypecode(String nametypecode) {
		this.nametypecode = nametypecode;
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

	public String getObsoletedate() {
		return this.obsoletedate;
	}

	public void setObsoletedate(String obsoletedate) {
		this.obsoletedate = obsoletedate;
	}

	public Integer getRdfmidUnique() {
		return this.rdfmidUnique;
	}

	public void setRdfmidUnique(Integer rdfmidUnique) {
		this.rdfmidUnique = rdfmidUnique;
	}

	public String getReffederallegendcode() {
		return this.reffederallegendcode;
	}

	public void setReffederallegendcode(String reffederallegendcode) {
		this.reffederallegendcode = reffederallegendcode;
	}

	public Integer getReplacedindicator() {
		return this.replacedindicator;
	}

	public void setReplacedindicator(Integer replacedindicator) {
		this.replacedindicator = replacedindicator;
	}

	public Integer getRtgenidUnique() {
		return this.rtgenidUnique;
	}

	public void setRtgenidUnique(Integer rtgenidUnique) {
		this.rtgenidUnique = rtgenidUnique;
	}

	public Integer getRtid() {
		return this.rtid;
	}

	public void setRtid(Integer rtid) {
		this.rtid = rtid;
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