package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_minmaxdosing database table.
 * 
 */
@Entity
@Table(name="fdb_minmaxdosing")
public class FdbMinmaxdosing implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMinmaxdosingPK id;

	private Integer bsarequiredind;

	private BigDecimal doseformhigh;

	private String doseformhighunits;

	private BigDecimal doseformlow;

	private String doseformlowunits;

	private BigDecimal doseformratehigh;

	private String doseformratehighunits;

	private BigDecimal doseformratelow;

	private String doseformratelowunits;

	private BigDecimal dosehigh;

	private String dosehighunits;

	private BigDecimal doselow;

	private String doselowunits;

	private BigDecimal doseratehigh;

	private String doseratehighunits;

	private BigDecimal doseratelow;

	private String doseratelowunits;

	private BigDecimal maxdailydose;

	private BigDecimal maxdailydoseform;

	private BigDecimal maxdailydoseformrate;

	private String maxdailydoseformrateunits;

	private String maxdailydoseformunits;

	private BigDecimal maxdailydoserate;

	private String maxdailydoserateunits;

	private String maxdailydoseunits;

	private Integer weightrequiredind;

    public FdbMinmaxdosing() {
    }

	public FdbMinmaxdosingPK getId() {
		return this.id;
	}

	public void setId(FdbMinmaxdosingPK id) {
		this.id = id;
	}
	
	public Integer getBsarequiredind() {
		return this.bsarequiredind;
	}

	public void setBsarequiredind(Integer bsarequiredind) {
		this.bsarequiredind = bsarequiredind;
	}

	public BigDecimal getDoseformhigh() {
		return this.doseformhigh;
	}

	public void setDoseformhigh(BigDecimal doseformhigh) {
		this.doseformhigh = doseformhigh;
	}

	public String getDoseformhighunits() {
		return this.doseformhighunits;
	}

	public void setDoseformhighunits(String doseformhighunits) {
		this.doseformhighunits = doseformhighunits;
	}

	public BigDecimal getDoseformlow() {
		return this.doseformlow;
	}

	public void setDoseformlow(BigDecimal doseformlow) {
		this.doseformlow = doseformlow;
	}

	public String getDoseformlowunits() {
		return this.doseformlowunits;
	}

	public void setDoseformlowunits(String doseformlowunits) {
		this.doseformlowunits = doseformlowunits;
	}

	public BigDecimal getDoseformratehigh() {
		return this.doseformratehigh;
	}

	public void setDoseformratehigh(BigDecimal doseformratehigh) {
		this.doseformratehigh = doseformratehigh;
	}

	public String getDoseformratehighunits() {
		return this.doseformratehighunits;
	}

	public void setDoseformratehighunits(String doseformratehighunits) {
		this.doseformratehighunits = doseformratehighunits;
	}

	public BigDecimal getDoseformratelow() {
		return this.doseformratelow;
	}

	public void setDoseformratelow(BigDecimal doseformratelow) {
		this.doseformratelow = doseformratelow;
	}

	public String getDoseformratelowunits() {
		return this.doseformratelowunits;
	}

	public void setDoseformratelowunits(String doseformratelowunits) {
		this.doseformratelowunits = doseformratelowunits;
	}

	public BigDecimal getDosehigh() {
		return this.dosehigh;
	}

	public void setDosehigh(BigDecimal dosehigh) {
		this.dosehigh = dosehigh;
	}

	public String getDosehighunits() {
		return this.dosehighunits;
	}

	public void setDosehighunits(String dosehighunits) {
		this.dosehighunits = dosehighunits;
	}

	public BigDecimal getDoselow() {
		return this.doselow;
	}

	public void setDoselow(BigDecimal doselow) {
		this.doselow = doselow;
	}

	public String getDoselowunits() {
		return this.doselowunits;
	}

	public void setDoselowunits(String doselowunits) {
		this.doselowunits = doselowunits;
	}

	public BigDecimal getDoseratehigh() {
		return this.doseratehigh;
	}

	public void setDoseratehigh(BigDecimal doseratehigh) {
		this.doseratehigh = doseratehigh;
	}

	public String getDoseratehighunits() {
		return this.doseratehighunits;
	}

	public void setDoseratehighunits(String doseratehighunits) {
		this.doseratehighunits = doseratehighunits;
	}

	public BigDecimal getDoseratelow() {
		return this.doseratelow;
	}

	public void setDoseratelow(BigDecimal doseratelow) {
		this.doseratelow = doseratelow;
	}

	public String getDoseratelowunits() {
		return this.doseratelowunits;
	}

	public void setDoseratelowunits(String doseratelowunits) {
		this.doseratelowunits = doseratelowunits;
	}

	public BigDecimal getMaxdailydose() {
		return this.maxdailydose;
	}

	public void setMaxdailydose(BigDecimal maxdailydose) {
		this.maxdailydose = maxdailydose;
	}

	public BigDecimal getMaxdailydoseform() {
		return this.maxdailydoseform;
	}

	public void setMaxdailydoseform(BigDecimal maxdailydoseform) {
		this.maxdailydoseform = maxdailydoseform;
	}

	public BigDecimal getMaxdailydoseformrate() {
		return this.maxdailydoseformrate;
	}

	public void setMaxdailydoseformrate(BigDecimal maxdailydoseformrate) {
		this.maxdailydoseformrate = maxdailydoseformrate;
	}

	public String getMaxdailydoseformrateunits() {
		return this.maxdailydoseformrateunits;
	}

	public void setMaxdailydoseformrateunits(String maxdailydoseformrateunits) {
		this.maxdailydoseformrateunits = maxdailydoseformrateunits;
	}

	public String getMaxdailydoseformunits() {
		return this.maxdailydoseformunits;
	}

	public void setMaxdailydoseformunits(String maxdailydoseformunits) {
		this.maxdailydoseformunits = maxdailydoseformunits;
	}

	public BigDecimal getMaxdailydoserate() {
		return this.maxdailydoserate;
	}

	public void setMaxdailydoserate(BigDecimal maxdailydoserate) {
		this.maxdailydoserate = maxdailydoserate;
	}

	public String getMaxdailydoserateunits() {
		return this.maxdailydoserateunits;
	}

	public void setMaxdailydoserateunits(String maxdailydoserateunits) {
		this.maxdailydoserateunits = maxdailydoserateunits;
	}

	public String getMaxdailydoseunits() {
		return this.maxdailydoseunits;
	}

	public void setMaxdailydoseunits(String maxdailydoseunits) {
		this.maxdailydoseunits = maxdailydoseunits;
	}

	public Integer getWeightrequiredind() {
		return this.weightrequiredind;
	}

	public void setWeightrequiredind(Integer weightrequiredind) {
		this.weightrequiredind = weightrequiredind;
	}

}