package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_poem_doseformunits database table.
 * 
 */
@Entity
@Table(name="fdb_poem_doseformunits")
public class FdbPoemDoseformunit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer unitsid;

	private String descriptiona1;

	private String descriptiona2;

	private String descriptiona3;

	private String descriptiona4;

	private String descriptiona5;

	private String descriptiona6;

	private String descriptionb1;

	private String descriptionb2;

	private String descriptionb3;

	private String descriptionb4;

	private String descriptionb5;

	private String descriptionb6;

	private Integer dispquantityind;

	private Integer mlconversion;

	private BigDecimal perdayconversion;

    public FdbPoemDoseformunit() {
    }

	public Integer getUnitsid() {
		return this.unitsid;
	}

	public void setUnitsid(Integer unitsid) {
		this.unitsid = unitsid;
	}

	public String getDescriptiona1() {
		return this.descriptiona1;
	}

	public void setDescriptiona1(String descriptiona1) {
		this.descriptiona1 = descriptiona1;
	}

	public String getDescriptiona2() {
		return this.descriptiona2;
	}

	public void setDescriptiona2(String descriptiona2) {
		this.descriptiona2 = descriptiona2;
	}

	public String getDescriptiona3() {
		return this.descriptiona3;
	}

	public void setDescriptiona3(String descriptiona3) {
		this.descriptiona3 = descriptiona3;
	}

	public String getDescriptiona4() {
		return this.descriptiona4;
	}

	public void setDescriptiona4(String descriptiona4) {
		this.descriptiona4 = descriptiona4;
	}

	public String getDescriptiona5() {
		return this.descriptiona5;
	}

	public void setDescriptiona5(String descriptiona5) {
		this.descriptiona5 = descriptiona5;
	}

	public String getDescriptiona6() {
		return this.descriptiona6;
	}

	public void setDescriptiona6(String descriptiona6) {
		this.descriptiona6 = descriptiona6;
	}

	public String getDescriptionb1() {
		return this.descriptionb1;
	}

	public void setDescriptionb1(String descriptionb1) {
		this.descriptionb1 = descriptionb1;
	}

	public String getDescriptionb2() {
		return this.descriptionb2;
	}

	public void setDescriptionb2(String descriptionb2) {
		this.descriptionb2 = descriptionb2;
	}

	public String getDescriptionb3() {
		return this.descriptionb3;
	}

	public void setDescriptionb3(String descriptionb3) {
		this.descriptionb3 = descriptionb3;
	}

	public String getDescriptionb4() {
		return this.descriptionb4;
	}

	public void setDescriptionb4(String descriptionb4) {
		this.descriptionb4 = descriptionb4;
	}

	public String getDescriptionb5() {
		return this.descriptionb5;
	}

	public void setDescriptionb5(String descriptionb5) {
		this.descriptionb5 = descriptionb5;
	}

	public String getDescriptionb6() {
		return this.descriptionb6;
	}

	public void setDescriptionb6(String descriptionb6) {
		this.descriptionb6 = descriptionb6;
	}

	public Integer getDispquantityind() {
		return this.dispquantityind;
	}

	public void setDispquantityind(Integer dispquantityind) {
		this.dispquantityind = dispquantityind;
	}

	public Integer getMlconversion() {
		return this.mlconversion;
	}

	public void setMlconversion(Integer mlconversion) {
		this.mlconversion = mlconversion;
	}

	public BigDecimal getPerdayconversion() {
		return this.perdayconversion;
	}

	public void setPerdayconversion(BigDecimal perdayconversion) {
		this.perdayconversion = perdayconversion;
	}

}