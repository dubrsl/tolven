package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_drugvalidation database table.
 * 
 */
@Entity
@Table(name="fdb_drugvalidation")
public class FdbDrugvalidation implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDrugvalidationPK id;

	private Integer clinicallinkind;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private Integer drcsingledoserouteind;

	private Integer gcnseqno;

	private Integer hasgestagedosingind;

	private Integer hicl;

	private Integer neosingledoserouteind;

	private BigDecimal percentsurveyed;

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

	@Column(name="tm_descdisplay")
	private String tmDescdisplay;

    public FdbDrugvalidation() {
    }

	public FdbDrugvalidationPK getId() {
		return this.id;
	}

	public void setId(FdbDrugvalidationPK id) {
		this.id = id;
	}
	
	public Integer getClinicallinkind() {
		return this.clinicallinkind;
	}

	public void setClinicallinkind(Integer clinicallinkind) {
		this.clinicallinkind = clinicallinkind;
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

	public Integer getHasgestagedosingind() {
		return this.hasgestagedosingind;
	}

	public void setHasgestagedosingind(Integer hasgestagedosingind) {
		this.hasgestagedosingind = hasgestagedosingind;
	}

	public Integer getHicl() {
		return this.hicl;
	}

	public void setHicl(Integer hicl) {
		this.hicl = hicl;
	}

	public Integer getNeosingledoserouteind() {
		return this.neosingledoserouteind;
	}

	public void setNeosingledoserouteind(Integer neosingledoserouteind) {
		this.neosingledoserouteind = neosingledoserouteind;
	}

	public BigDecimal getPercentsurveyed() {
		return this.percentsurveyed;
	}

	public void setPercentsurveyed(BigDecimal percentsurveyed) {
		this.percentsurveyed = percentsurveyed;
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

	public String getTmDescdisplay() {
		return this.tmDescdisplay;
	}

	public void setTmDescdisplay(String tmDescdisplay) {
		this.tmDescdisplay = tmDescdisplay;
	}

}