package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_classification_ahfs database table.
 * 
 */
@Entity
@Table(name="fdb_classification_ahfs")
public class FdbClassificationAhf implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String classid;

	private String altcode;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private Integer directdruglinkind;

	private Integer formularylevelind;

	private Integer hierarchylevel;

	private String parentid;

	private Integer presentationseqno;

	private String retireddate;

	private Integer retiredind;

	private Integer sortnumber;

	private Integer ultimatechildind;

	private String ultiparentid;

    public FdbClassificationAhf() {
    }

	public String getClassid() {
		return this.classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getAltcode() {
		return this.altcode;
	}

	public void setAltcode(String altcode) {
		this.altcode = altcode;
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

	public Integer getDirectdruglinkind() {
		return this.directdruglinkind;
	}

	public void setDirectdruglinkind(Integer directdruglinkind) {
		this.directdruglinkind = directdruglinkind;
	}

	public Integer getFormularylevelind() {
		return this.formularylevelind;
	}

	public void setFormularylevelind(Integer formularylevelind) {
		this.formularylevelind = formularylevelind;
	}

	public Integer getHierarchylevel() {
		return this.hierarchylevel;
	}

	public void setHierarchylevel(Integer hierarchylevel) {
		this.hierarchylevel = hierarchylevel;
	}

	public String getParentid() {
		return this.parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public Integer getPresentationseqno() {
		return this.presentationseqno;
	}

	public void setPresentationseqno(Integer presentationseqno) {
		this.presentationseqno = presentationseqno;
	}

	public String getRetireddate() {
		return this.retireddate;
	}

	public void setRetireddate(String retireddate) {
		this.retireddate = retireddate;
	}

	public Integer getRetiredind() {
		return this.retiredind;
	}

	public void setRetiredind(Integer retiredind) {
		this.retiredind = retiredind;
	}

	public Integer getSortnumber() {
		return this.sortnumber;
	}

	public void setSortnumber(Integer sortnumber) {
		this.sortnumber = sortnumber;
	}

	public Integer getUltimatechildind() {
		return this.ultimatechildind;
	}

	public void setUltimatechildind(Integer ultimatechildind) {
		this.ultimatechildind = ultimatechildind;
	}

	public String getUltiparentid() {
		return this.ultiparentid;
	}

	public void setUltiparentid(String ultiparentid) {
		this.ultiparentid = ultiparentid;
	}

}