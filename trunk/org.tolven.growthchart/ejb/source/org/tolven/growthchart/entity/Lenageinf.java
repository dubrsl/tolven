package org.tolven.growthchart.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Lenageinf
 * @author Suja
 * added on 01/19/2011
 */
@Entity
@Table(name = "lenageinf", schema="public")
public class Lenageinf implements Serializable {

	private static final long serialVersionUID = 3L;

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

	@Column
    private double pub3;

	@Column
    private double pub5;

	@Column
    private double pub10;

	@Column
    private double pub25;

	@Column
    private double pub50;

	@Column
    private double pub75;

	@Column
    private double pub90;

	@Column
    private double pub95;

	@Column
    private double pub97;

	@Column
    private double diff3;

	@Column
    private double diff5;

	@Column
    private double diff10;

	@Column
    private double diff25;

	@Column
    private double diff50;

	@Column
    private double diff75;

	@Column
    private double diff90;

	@Column
    private double diff95;

	@Column
    private double diff97;

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

	public double getPub3() {
		return pub3;
	}

	public void setPub3(double pub3) {
		this.pub3 = pub3;
	}

	public double getPub5() {
		return pub5;
	}

	public void setPub5(double pub5) {
		this.pub5 = pub5;
	}

	public double getPub10() {
		return pub10;
	}

	public void setPub10(double pub10) {
		this.pub10 = pub10;
	}

	public double getPub25() {
		return pub25;
	}

	public void setPub25(double pub25) {
		this.pub25 = pub25;
	}

	public double getPub50() {
		return pub50;
	}

	public void setPub50(double pub50) {
		this.pub50 = pub50;
	}

	public double getPub75() {
		return pub75;
	}

	public void setPub75(double pub75) {
		this.pub75 = pub75;
	}

	public double getPub90() {
		return pub90;
	}

	public void setPub90(double pub90) {
		this.pub90 = pub90;
	}

	public double getPub95() {
		return pub95;
	}

	public void setPub95(double pub95) {
		this.pub95 = pub95;
	}

	public double getPub97() {
		return pub97;
	}

	public void setPub97(double pub97) {
		this.pub97 = pub97;
	}

	public double getDiff3() {
		return diff3;
	}

	public void setDiff3(double diff3) {
		this.diff3 = diff3;
	}

	public double getDiff5() {
		return diff5;
	}

	public void setDiff5(double diff5) {
		this.diff5 = diff5;
	}

	public double getDiff10() {
		return diff10;
	}

	public void setDiff10(double diff10) {
		this.diff10 = diff10;
	}

	public double getDiff25() {
		return diff25;
	}

	public void setDiff25(double diff25) {
		this.diff25 = diff25;
	}

	public double getDiff50() {
		return diff50;
	}

	public void setDiff50(double diff50) {
		this.diff50 = diff50;
	}

	public double getDiff75() {
		return diff75;
	}

	public void setDiff75(double diff75) {
		this.diff75 = diff75;
	}

	public double getDiff90() {
		return diff90;
	}

	public void setDiff90(double diff90) {
		this.diff90 = diff90;
	}

	public double getDiff95() {
		return diff95;
	}

	public void setDiff95(double diff95) {
		this.diff95 = diff95;
	}

	public double getDiff97() {
		return diff97;
	}

	public void setDiff97(double diff97) {
		this.diff97 = diff97;
	}
}
