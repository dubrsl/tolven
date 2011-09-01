package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_user_ddiminteraction database table.
 * 
 */
@Entity
@Table(name="fdb_user_ddiminteraction")
public class FdbUserDdiminteraction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer interactionid;

	private String clinicaleffectcode1;

	private String clinicaleffectcode2;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private Integer monographid;

	private String severitylevelcode;

	private String uicategory1;

	private String uicategory2;

	private String uicategory3;

	private String uicategory4;

	private String uicategory5;

	private String uicategory6;

    public FdbUserDdiminteraction() {
    }

	public Integer getInteractionid() {
		return this.interactionid;
	}

	public void setInteractionid(Integer interactionid) {
		this.interactionid = interactionid;
	}

	public String getClinicaleffectcode1() {
		return this.clinicaleffectcode1;
	}

	public void setClinicaleffectcode1(String clinicaleffectcode1) {
		this.clinicaleffectcode1 = clinicaleffectcode1;
	}

	public String getClinicaleffectcode2() {
		return this.clinicaleffectcode2;
	}

	public void setClinicaleffectcode2(String clinicaleffectcode2) {
		this.clinicaleffectcode2 = clinicaleffectcode2;
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

	public Integer getMonographid() {
		return this.monographid;
	}

	public void setMonographid(Integer monographid) {
		this.monographid = monographid;
	}

	public String getSeveritylevelcode() {
		return this.severitylevelcode;
	}

	public void setSeveritylevelcode(String severitylevelcode) {
		this.severitylevelcode = severitylevelcode;
	}

	public String getUicategory1() {
		return this.uicategory1;
	}

	public void setUicategory1(String uicategory1) {
		this.uicategory1 = uicategory1;
	}

	public String getUicategory2() {
		return this.uicategory2;
	}

	public void setUicategory2(String uicategory2) {
		this.uicategory2 = uicategory2;
	}

	public String getUicategory3() {
		return this.uicategory3;
	}

	public void setUicategory3(String uicategory3) {
		this.uicategory3 = uicategory3;
	}

	public String getUicategory4() {
		return this.uicategory4;
	}

	public void setUicategory4(String uicategory4) {
		this.uicategory4 = uicategory4;
	}

	public String getUicategory5() {
		return this.uicategory5;
	}

	public void setUicategory5(String uicategory5) {
		this.uicategory5 = uicategory5;
	}

	public String getUicategory6() {
		return this.uicategory6;
	}

	public void setUicategory6(String uicategory6) {
		this.uicategory6 = uicategory6;
	}

}