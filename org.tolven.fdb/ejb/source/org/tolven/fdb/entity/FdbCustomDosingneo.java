package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_custom_dosingneo database table.
 * 
 */
@Entity
@Table(name="fdb_custom_dosingneo")
public class FdbCustomDosingneo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomDosingneoPK id;

	private Integer bsarequiredind;

	private Integer crclthreshhold;

	private String crclthreshholdunits;

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

	private Integer durationhigh;

	private Integer durationlow;

	private String dxid;

	private BigDecimal frequencyhigh;

	private BigDecimal frequencylow;

	private Integer gabrequiredind;

	private String halflifeunits;

	private Integer hepaticimpairmentind;

	private BigDecimal higheliminationhalflife;

	private BigDecimal loweliminationhalflife;

	private BigDecimal maxdailydose;

	private BigDecimal maxdailydoseform;

	private BigDecimal maxdailydoseformrate;

	private String maxdailydoseformrateunits;

	private String maxdailydoseformunits;

	private BigDecimal maxdailydoserate;

	private String maxdailydoserateunits;

	private String maxdailydoseunits;

	private Integer maxduration;

	private BigDecimal maxlifetimedose;

	private BigDecimal maxlifetimedoseform;

	private String maxlifetimedoseformunits;

	private String maxlifetimedoseunits;

	private BigDecimal maxsingledose;

	private BigDecimal maxsingledoseform;

	private BigDecimal maxsingledoseformrate;

	private String maxsingledoseformrateunits;

	private String maxsingledoseformunits;

	private BigDecimal maxsingledoserate;

	private String maxsingledoserateunits;

	private String maxsingledoseunits;

	private Integer renalimpairmentind;

	private Integer weightrequiredind;

    public FdbCustomDosingneo() {
    }

	public FdbCustomDosingneoPK getId() {
		return this.id;
	}

	public void setId(FdbCustomDosingneoPK id) {
		this.id = id;
	}
	
	public Integer getBsarequiredind() {
		return this.bsarequiredind;
	}

	public void setBsarequiredind(Integer bsarequiredind) {
		this.bsarequiredind = bsarequiredind;
	}

	public Integer getCrclthreshhold() {
		return this.crclthreshhold;
	}

	public void setCrclthreshhold(Integer crclthreshhold) {
		this.crclthreshhold = crclthreshhold;
	}

	public String getCrclthreshholdunits() {
		return this.crclthreshholdunits;
	}

	public void setCrclthreshholdunits(String crclthreshholdunits) {
		this.crclthreshholdunits = crclthreshholdunits;
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

	public Integer getDurationhigh() {
		return this.durationhigh;
	}

	public void setDurationhigh(Integer durationhigh) {
		this.durationhigh = durationhigh;
	}

	public Integer getDurationlow() {
		return this.durationlow;
	}

	public void setDurationlow(Integer durationlow) {
		this.durationlow = durationlow;
	}

	public String getDxid() {
		return this.dxid;
	}

	public void setDxid(String dxid) {
		this.dxid = dxid;
	}

	public BigDecimal getFrequencyhigh() {
		return this.frequencyhigh;
	}

	public void setFrequencyhigh(BigDecimal frequencyhigh) {
		this.frequencyhigh = frequencyhigh;
	}

	public BigDecimal getFrequencylow() {
		return this.frequencylow;
	}

	public void setFrequencylow(BigDecimal frequencylow) {
		this.frequencylow = frequencylow;
	}

	public Integer getGabrequiredind() {
		return this.gabrequiredind;
	}

	public void setGabrequiredind(Integer gabrequiredind) {
		this.gabrequiredind = gabrequiredind;
	}

	public String getHalflifeunits() {
		return this.halflifeunits;
	}

	public void setHalflifeunits(String halflifeunits) {
		this.halflifeunits = halflifeunits;
	}

	public Integer getHepaticimpairmentind() {
		return this.hepaticimpairmentind;
	}

	public void setHepaticimpairmentind(Integer hepaticimpairmentind) {
		this.hepaticimpairmentind = hepaticimpairmentind;
	}

	public BigDecimal getHigheliminationhalflife() {
		return this.higheliminationhalflife;
	}

	public void setHigheliminationhalflife(BigDecimal higheliminationhalflife) {
		this.higheliminationhalflife = higheliminationhalflife;
	}

	public BigDecimal getLoweliminationhalflife() {
		return this.loweliminationhalflife;
	}

	public void setLoweliminationhalflife(BigDecimal loweliminationhalflife) {
		this.loweliminationhalflife = loweliminationhalflife;
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

	public Integer getMaxduration() {
		return this.maxduration;
	}

	public void setMaxduration(Integer maxduration) {
		this.maxduration = maxduration;
	}

	public BigDecimal getMaxlifetimedose() {
		return this.maxlifetimedose;
	}

	public void setMaxlifetimedose(BigDecimal maxlifetimedose) {
		this.maxlifetimedose = maxlifetimedose;
	}

	public BigDecimal getMaxlifetimedoseform() {
		return this.maxlifetimedoseform;
	}

	public void setMaxlifetimedoseform(BigDecimal maxlifetimedoseform) {
		this.maxlifetimedoseform = maxlifetimedoseform;
	}

	public String getMaxlifetimedoseformunits() {
		return this.maxlifetimedoseformunits;
	}

	public void setMaxlifetimedoseformunits(String maxlifetimedoseformunits) {
		this.maxlifetimedoseformunits = maxlifetimedoseformunits;
	}

	public String getMaxlifetimedoseunits() {
		return this.maxlifetimedoseunits;
	}

	public void setMaxlifetimedoseunits(String maxlifetimedoseunits) {
		this.maxlifetimedoseunits = maxlifetimedoseunits;
	}

	public BigDecimal getMaxsingledose() {
		return this.maxsingledose;
	}

	public void setMaxsingledose(BigDecimal maxsingledose) {
		this.maxsingledose = maxsingledose;
	}

	public BigDecimal getMaxsingledoseform() {
		return this.maxsingledoseform;
	}

	public void setMaxsingledoseform(BigDecimal maxsingledoseform) {
		this.maxsingledoseform = maxsingledoseform;
	}

	public BigDecimal getMaxsingledoseformrate() {
		return this.maxsingledoseformrate;
	}

	public void setMaxsingledoseformrate(BigDecimal maxsingledoseformrate) {
		this.maxsingledoseformrate = maxsingledoseformrate;
	}

	public String getMaxsingledoseformrateunits() {
		return this.maxsingledoseformrateunits;
	}

	public void setMaxsingledoseformrateunits(String maxsingledoseformrateunits) {
		this.maxsingledoseformrateunits = maxsingledoseformrateunits;
	}

	public String getMaxsingledoseformunits() {
		return this.maxsingledoseformunits;
	}

	public void setMaxsingledoseformunits(String maxsingledoseformunits) {
		this.maxsingledoseformunits = maxsingledoseformunits;
	}

	public BigDecimal getMaxsingledoserate() {
		return this.maxsingledoserate;
	}

	public void setMaxsingledoserate(BigDecimal maxsingledoserate) {
		this.maxsingledoserate = maxsingledoserate;
	}

	public String getMaxsingledoserateunits() {
		return this.maxsingledoserateunits;
	}

	public void setMaxsingledoserateunits(String maxsingledoserateunits) {
		this.maxsingledoserateunits = maxsingledoserateunits;
	}

	public String getMaxsingledoseunits() {
		return this.maxsingledoseunits;
	}

	public void setMaxsingledoseunits(String maxsingledoseunits) {
		this.maxsingledoseunits = maxsingledoseunits;
	}

	public Integer getRenalimpairmentind() {
		return this.renalimpairmentind;
	}

	public void setRenalimpairmentind(Integer renalimpairmentind) {
		this.renalimpairmentind = renalimpairmentind;
	}

	public Integer getWeightrequiredind() {
		return this.weightrequiredind;
	}

	public void setWeightrequiredind(Integer weightrequiredind) {
		this.weightrequiredind = weightrequiredind;
	}

}