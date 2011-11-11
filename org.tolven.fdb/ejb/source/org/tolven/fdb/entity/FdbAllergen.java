package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_allergens database table.
 * 
 */
@Entity
@Table(name="fdb_allergens")
public class FdbAllergen implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String allergenid;

	private Integer clinicallinkind;

	private Integer gcnseqno;

	private Integer hicl;

	private String statuscode;

    public FdbAllergen() {
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

}