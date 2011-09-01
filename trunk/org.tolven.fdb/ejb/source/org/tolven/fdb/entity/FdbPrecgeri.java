package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precgeri database table.
 * 
 */
@Entity
@Table(name="fdb_precgeri")
public class FdbPrecgeri implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer gericode;

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

	private String severitylevelcode;

	private Integer syscardioind;

	private Integer sysendoind;

	private Integer syshepaticind;

	private Integer sysneurind;

	private Integer syspulmind;

	private Integer sysrenalind;

    public FdbPrecgeri() {
    }

	public Integer getGericode() {
		return this.gericode;
	}

	public void setGericode(Integer gericode) {
		this.gericode = gericode;
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

	public String getSeveritylevelcode() {
		return this.severitylevelcode;
	}

	public void setSeveritylevelcode(String severitylevelcode) {
		this.severitylevelcode = severitylevelcode;
	}

	public Integer getSyscardioind() {
		return this.syscardioind;
	}

	public void setSyscardioind(Integer syscardioind) {
		this.syscardioind = syscardioind;
	}

	public Integer getSysendoind() {
		return this.sysendoind;
	}

	public void setSysendoind(Integer sysendoind) {
		this.sysendoind = sysendoind;
	}

	public Integer getSyshepaticind() {
		return this.syshepaticind;
	}

	public void setSyshepaticind(Integer syshepaticind) {
		this.syshepaticind = syshepaticind;
	}

	public Integer getSysneurind() {
		return this.sysneurind;
	}

	public void setSysneurind(Integer sysneurind) {
		this.sysneurind = sysneurind;
	}

	public Integer getSyspulmind() {
		return this.syspulmind;
	}

	public void setSyspulmind(Integer syspulmind) {
		this.syspulmind = syspulmind;
	}

	public Integer getSysrenalind() {
		return this.sysrenalind;
	}

	public void setSysrenalind(Integer sysrenalind) {
		this.sysrenalind = sysrenalind;
	}

}