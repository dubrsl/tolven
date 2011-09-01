package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_drugname database table.
 * 
 */
@Entity
@Table(name="fdb_drugname")
public class FdbDrugname implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer mnid;

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

	private String nametypecode;

	private String obsoletedate;

	@Column(name="rdfmid_unique")
	private Integer rdfmidUnique;

	private String reffederallegendcode;

	private Integer replacedindicator;

	@Column(name="rmid_unique")
	private Integer rmidUnique;

	private Integer singleingredientind;

	private String statuscode;

	@Column(name="tm_confusiongroupid")
	private Integer tmConfusiongroupid;

	@Column(name="tm_descdisplay")
	private String tmDescdisplay;

	@Column(name="tm_groupdesc")
	private String tmGroupdesc;

	@Column(name="tm_sourcecode")
	private String tmSourcecode;

    public FdbDrugname() {
    }

	public Integer getMnid() {
		return this.mnid;
	}

	public void setMnid(Integer mnid) {
		this.mnid = mnid;
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

	public String getNametypecode() {
		return this.nametypecode;
	}

	public void setNametypecode(String nametypecode) {
		this.nametypecode = nametypecode;
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

	public Integer getRmidUnique() {
		return this.rmidUnique;
	}

	public void setRmidUnique(Integer rmidUnique) {
		this.rmidUnique = rmidUnique;
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