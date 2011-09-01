package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_refitem database table.
 * 
 */
@Entity
@Table(name="fdb_refitem")
public class FdbRefitem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer refitemid;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private String refitemcomment1;

	private String refitemcomment2;

	private String refitemcomment3;

	private String refitemcomment4;

	private String refitemcomment5;

	private String refitemcomment6;

    public FdbRefitem() {
    }

	public Integer getRefitemid() {
		return this.refitemid;
	}

	public void setRefitemid(Integer refitemid) {
		this.refitemid = refitemid;
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

	public String getRefitemcomment1() {
		return this.refitemcomment1;
	}

	public void setRefitemcomment1(String refitemcomment1) {
		this.refitemcomment1 = refitemcomment1;
	}

	public String getRefitemcomment2() {
		return this.refitemcomment2;
	}

	public void setRefitemcomment2(String refitemcomment2) {
		this.refitemcomment2 = refitemcomment2;
	}

	public String getRefitemcomment3() {
		return this.refitemcomment3;
	}

	public void setRefitemcomment3(String refitemcomment3) {
		this.refitemcomment3 = refitemcomment3;
	}

	public String getRefitemcomment4() {
		return this.refitemcomment4;
	}

	public void setRefitemcomment4(String refitemcomment4) {
		this.refitemcomment4 = refitemcomment4;
	}

	public String getRefitemcomment5() {
		return this.refitemcomment5;
	}

	public void setRefitemcomment5(String refitemcomment5) {
		this.refitemcomment5 = refitemcomment5;
	}

	public String getRefitemcomment6() {
		return this.refitemcomment6;
	}

	public void setRefitemcomment6(String refitemcomment6) {
		this.refitemcomment6 = refitemcomment6;
	}

}