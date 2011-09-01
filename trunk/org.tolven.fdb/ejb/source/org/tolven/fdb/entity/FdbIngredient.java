package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ingredient database table.
 * 
 */
@Entity
@Table(name="fdb_ingredient")
public class FdbIngredient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer hicseqno;

	private Integer allergengroupind;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private Integer potentiallyinactiveind;

	private String statuscode;

    public FdbIngredient() {
    }

	public Integer getHicseqno() {
		return this.hicseqno;
	}

	public void setHicseqno(Integer hicseqno) {
		this.hicseqno = hicseqno;
	}

	public Integer getAllergengroupind() {
		return this.allergengroupind;
	}

	public void setAllergengroupind(Integer allergengroupind) {
		this.allergengroupind = allergengroupind;
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

	public Integer getPotentiallyinactiveind() {
		return this.potentiallyinactiveind;
	}

	public void setPotentiallyinactiveind(Integer potentiallyinactiveind) {
		this.potentiallyinactiveind = potentiallyinactiveind;
	}

	public String getStatuscode() {
		return this.statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

}