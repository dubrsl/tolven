package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_poem_orderstring database table.
 * 
 */
@Entity
@Table(name="fdb_poem_orderstring")
public class FdbPoemOrderstring implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer orderstringid;

	private Integer calculationind;

	private BigDecimal doseformhigh;

	private Integer doseformhighunitsid;

	private BigDecimal doseformlow;

	private Integer doseformlowunitsid;

	private BigDecimal dosehigh;

	private Integer dosehighunitsid;

	private BigDecimal doselow;

	private Integer doselowunitsid;

	private String doserouteid;

	private String dosetypeid;

	private Integer durationhigh;

	private Integer durationhighunitsid;

	private Integer durationlow;

	private Integer durationlowunitsid;

	private Integer frequency2high;

	private Integer frequency2low;

	private Integer frequencyhigh;

	private Integer frequencylow;

	private Integer interval2high;

	private Integer interval2highunitsid;

	private Integer interval2low;

	private Integer interval2lowunitsid;

	private Integer intervalhigh;

	private Integer intervalhighunitsid;

	private Integer intervallow;

	private Integer intervallowunitsid;

	private BigDecimal ordertextid;

	private String routeinstruction1;

	private String routeinstruction2;

	private String routeinstruction3;

	private String routeinstruction4;

	private String routeinstruction5;

	private String routeinstruction6;

    public FdbPoemOrderstring() {
    }

	public Integer getOrderstringid() {
		return this.orderstringid;
	}

	public void setOrderstringid(Integer orderstringid) {
		this.orderstringid = orderstringid;
	}

	public Integer getCalculationind() {
		return this.calculationind;
	}

	public void setCalculationind(Integer calculationind) {
		this.calculationind = calculationind;
	}

	public BigDecimal getDoseformhigh() {
		return this.doseformhigh;
	}

	public void setDoseformhigh(BigDecimal doseformhigh) {
		this.doseformhigh = doseformhigh;
	}

	public Integer getDoseformhighunitsid() {
		return this.doseformhighunitsid;
	}

	public void setDoseformhighunitsid(Integer doseformhighunitsid) {
		this.doseformhighunitsid = doseformhighunitsid;
	}

	public BigDecimal getDoseformlow() {
		return this.doseformlow;
	}

	public void setDoseformlow(BigDecimal doseformlow) {
		this.doseformlow = doseformlow;
	}

	public Integer getDoseformlowunitsid() {
		return this.doseformlowunitsid;
	}

	public void setDoseformlowunitsid(Integer doseformlowunitsid) {
		this.doseformlowunitsid = doseformlowunitsid;
	}

	public BigDecimal getDosehigh() {
		return this.dosehigh;
	}

	public void setDosehigh(BigDecimal dosehigh) {
		this.dosehigh = dosehigh;
	}

	public Integer getDosehighunitsid() {
		return this.dosehighunitsid;
	}

	public void setDosehighunitsid(Integer dosehighunitsid) {
		this.dosehighunitsid = dosehighunitsid;
	}

	public BigDecimal getDoselow() {
		return this.doselow;
	}

	public void setDoselow(BigDecimal doselow) {
		this.doselow = doselow;
	}

	public Integer getDoselowunitsid() {
		return this.doselowunitsid;
	}

	public void setDoselowunitsid(Integer doselowunitsid) {
		this.doselowunitsid = doselowunitsid;
	}

	public String getDoserouteid() {
		return this.doserouteid;
	}

	public void setDoserouteid(String doserouteid) {
		this.doserouteid = doserouteid;
	}

	public String getDosetypeid() {
		return this.dosetypeid;
	}

	public void setDosetypeid(String dosetypeid) {
		this.dosetypeid = dosetypeid;
	}

	public Integer getDurationhigh() {
		return this.durationhigh;
	}

	public void setDurationhigh(Integer durationhigh) {
		this.durationhigh = durationhigh;
	}

	public Integer getDurationhighunitsid() {
		return this.durationhighunitsid;
	}

	public void setDurationhighunitsid(Integer durationhighunitsid) {
		this.durationhighunitsid = durationhighunitsid;
	}

	public Integer getDurationlow() {
		return this.durationlow;
	}

	public void setDurationlow(Integer durationlow) {
		this.durationlow = durationlow;
	}

	public Integer getDurationlowunitsid() {
		return this.durationlowunitsid;
	}

	public void setDurationlowunitsid(Integer durationlowunitsid) {
		this.durationlowunitsid = durationlowunitsid;
	}

	public Integer getFrequency2high() {
		return this.frequency2high;
	}

	public void setFrequency2high(Integer frequency2high) {
		this.frequency2high = frequency2high;
	}

	public Integer getFrequency2low() {
		return this.frequency2low;
	}

	public void setFrequency2low(Integer frequency2low) {
		this.frequency2low = frequency2low;
	}

	public Integer getFrequencyhigh() {
		return this.frequencyhigh;
	}

	public void setFrequencyhigh(Integer frequencyhigh) {
		this.frequencyhigh = frequencyhigh;
	}

	public Integer getFrequencylow() {
		return this.frequencylow;
	}

	public void setFrequencylow(Integer frequencylow) {
		this.frequencylow = frequencylow;
	}

	public Integer getInterval2high() {
		return this.interval2high;
	}

	public void setInterval2high(Integer interval2high) {
		this.interval2high = interval2high;
	}

	public Integer getInterval2highunitsid() {
		return this.interval2highunitsid;
	}

	public void setInterval2highunitsid(Integer interval2highunitsid) {
		this.interval2highunitsid = interval2highunitsid;
	}

	public Integer getInterval2low() {
		return this.interval2low;
	}

	public void setInterval2low(Integer interval2low) {
		this.interval2low = interval2low;
	}

	public Integer getInterval2lowunitsid() {
		return this.interval2lowunitsid;
	}

	public void setInterval2lowunitsid(Integer interval2lowunitsid) {
		this.interval2lowunitsid = interval2lowunitsid;
	}

	public Integer getIntervalhigh() {
		return this.intervalhigh;
	}

	public void setIntervalhigh(Integer intervalhigh) {
		this.intervalhigh = intervalhigh;
	}

	public Integer getIntervalhighunitsid() {
		return this.intervalhighunitsid;
	}

	public void setIntervalhighunitsid(Integer intervalhighunitsid) {
		this.intervalhighunitsid = intervalhighunitsid;
	}

	public Integer getIntervallow() {
		return this.intervallow;
	}

	public void setIntervallow(Integer intervallow) {
		this.intervallow = intervallow;
	}

	public Integer getIntervallowunitsid() {
		return this.intervallowunitsid;
	}

	public void setIntervallowunitsid(Integer intervallowunitsid) {
		this.intervallowunitsid = intervallowunitsid;
	}

	public BigDecimal getOrdertextid() {
		return this.ordertextid;
	}

	public void setOrdertextid(BigDecimal ordertextid) {
		this.ordertextid = ordertextid;
	}

	public String getRouteinstruction1() {
		return this.routeinstruction1;
	}

	public void setRouteinstruction1(String routeinstruction1) {
		this.routeinstruction1 = routeinstruction1;
	}

	public String getRouteinstruction2() {
		return this.routeinstruction2;
	}

	public void setRouteinstruction2(String routeinstruction2) {
		this.routeinstruction2 = routeinstruction2;
	}

	public String getRouteinstruction3() {
		return this.routeinstruction3;
	}

	public void setRouteinstruction3(String routeinstruction3) {
		this.routeinstruction3 = routeinstruction3;
	}

	public String getRouteinstruction4() {
		return this.routeinstruction4;
	}

	public void setRouteinstruction4(String routeinstruction4) {
		this.routeinstruction4 = routeinstruction4;
	}

	public String getRouteinstruction5() {
		return this.routeinstruction5;
	}

	public void setRouteinstruction5(String routeinstruction5) {
		this.routeinstruction5 = routeinstruction5;
	}

	public String getRouteinstruction6() {
		return this.routeinstruction6;
	}

	public void setRouteinstruction6(String routeinstruction6) {
		this.routeinstruction6 = routeinstruction6;
	}

}