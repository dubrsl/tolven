package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_preclact database table.
 * 
 */
@Entity
@Table(name="fdb_preclact")
public class FdbPreclact implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer lactcode;

	private String addcomment1;

	private String addcomment2;

	private String addcomment3;

	private String addcomment4;

	private String addcomment5;

	private String addcomment6;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private String excreffectcode;

	private String lacteffectcode;

	private String severitylevelcode;

    public FdbPreclact() {
    }

	public Integer getLactcode() {
		return this.lactcode;
	}

	public void setLactcode(Integer lactcode) {
		this.lactcode = lactcode;
	}

	public String getAddcomment1() {
		return this.addcomment1;
	}

	public void setAddcomment1(String addcomment1) {
		this.addcomment1 = addcomment1;
	}

	public String getAddcomment2() {
		return this.addcomment2;
	}

	public void setAddcomment2(String addcomment2) {
		this.addcomment2 = addcomment2;
	}

	public String getAddcomment3() {
		return this.addcomment3;
	}

	public void setAddcomment3(String addcomment3) {
		this.addcomment3 = addcomment3;
	}

	public String getAddcomment4() {
		return this.addcomment4;
	}

	public void setAddcomment4(String addcomment4) {
		this.addcomment4 = addcomment4;
	}

	public String getAddcomment5() {
		return this.addcomment5;
	}

	public void setAddcomment5(String addcomment5) {
		this.addcomment5 = addcomment5;
	}

	public String getAddcomment6() {
		return this.addcomment6;
	}

	public void setAddcomment6(String addcomment6) {
		this.addcomment6 = addcomment6;
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

	public String getExcreffectcode() {
		return this.excreffectcode;
	}

	public void setExcreffectcode(String excreffectcode) {
		this.excreffectcode = excreffectcode;
	}

	public String getLacteffectcode() {
		return this.lacteffectcode;
	}

	public void setLacteffectcode(String lacteffectcode) {
		this.lacteffectcode = lacteffectcode;
	}

	public String getSeveritylevelcode() {
		return this.severitylevelcode;
	}

	public void setSeveritylevelcode(String severitylevelcode) {
		this.severitylevelcode = severitylevelcode;
	}

}