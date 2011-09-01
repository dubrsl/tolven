package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_allergens_ii database table.
 * 
 */
@Entity
@Table(name="fdb_allergens_ii")
public class FdbAllergensIi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String allergenid;

	private Integer clinicallinkind;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private Integer gcnseqno;

	private Integer hicl;

	private String statuscode;

	@Column(name="tm_descdisplay")
	private String tmDescdisplay;

    public FdbAllergensIi() {
    }

	public String getAllergenid() {
		return this.allergenid;
	}

	public void setAllergenid(String allergenid) {
		this.allergenid = allergenid;
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

	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}

	public Integer getHicl() {
		return this.hicl;
	}

	public void setHicl(Integer hicl) {
		this.hicl = hicl;
	}

	public String getStatuscode() {
		return this.statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

	public String getTmDescdisplay() {
		return this.tmDescdisplay;
	}

	public void setTmDescdisplay(String tmDescdisplay) {
		this.tmDescdisplay = tmDescdisplay;
	}

}