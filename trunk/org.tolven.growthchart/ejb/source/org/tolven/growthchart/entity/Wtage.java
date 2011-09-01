package org.tolven.growthchart.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Wtage
 * @author Suja
 * added on 01/19/2011
 */
@Entity
@Table(name = "wtage", schema="public")
public class Wtage implements Serializable {

	private static final long serialVersionUID = 4L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="GROWTHCHART_SEQ_GEN")
    private long id;

	@Column
    private int sex;

	@Column
    private double agemonth;

	@Column
    private double l;

	@Column
    private double m;

	@Column
    private double s;

	@Column
    private double p3;

	@Column
    private double p5;

	@Column
    private double p10;

	@Column
    private double p25;

	@Column
    private double p50;

	@Column
    private double p75;

	@Column
    private double p90;

	@Column
    private double p95;

	@Column
    private double p97;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public double getAgemonth() {
		return agemonth;
	}

	public void setAgemonth(double agemonth) {
		this.agemonth = agemonth;
	}

	public double getL() {
		return l;
	}

	public void setL(double l) {
		this.l = l;
	}

	public double getM() {
		return m;
	}

	public void setM(double m) {
		this.m = m;
	}

	public double getS() {
		return s;
	}

	public void setS(double s) {
		this.s = s;
	}

	public double getP3() {
		return p3;
	}

	public void setP3(double p3) {
		this.p3 = p3;
	}

	public double getP5() {
		return p5;
	}

	public void setP5(double p5) {
		this.p5 = p5;
	}

	public double getP10() {
		return p10;
	}

	public void setP10(double p10) {
		this.p10 = p10;
	}

	public double getP25() {
		return p25;
	}

	public void setP25(double p25) {
		this.p25 = p25;
	}

	public double getP50() {
		return p50;
	}

	public void setP50(double p50) {
		this.p50 = p50;
	}

	public double getP75() {
		return p75;
	}

	public void setP75(double p75) {
		this.p75 = p75;
	}

	public double getP90() {
		return p90;
	}

	public void setP90(double p90) {
		this.p90 = p90;
	}

	public double getP95() {
		return p95;
	}

	public void setP95(double p95) {
		this.p95 = p95;
	}

	public double getP97() {
		return p97;
	}

	public void setP97(double p97) {
		this.p97 = p97;
	}
}
