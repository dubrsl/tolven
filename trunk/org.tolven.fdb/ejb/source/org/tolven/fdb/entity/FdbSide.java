package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_side database table.
 * 
 */
@Entity
@Table(name="fdb_side")
public class FdbSide implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbSidePK id;

	private String descdrug1;

	private String descdrug2;

	private String descdrug3;

	private String descdrug4;

	private String descdrug5;

	private String descdrug6;

	private String desceffect1;

	private String desceffect2;

	private String desceffect3;

	private String desceffect4;

	private String desceffect5;

	private String desceffect6;

	private String dxid;

	private String fdbdx;

	private String freqcode;

	private String hypercode;

	private String labtestcode;

	private String physcode;

	private String sevcode;

	private String viscode;

    public FdbSide() {
    }

	public FdbSidePK getId() {
		return this.id;
	}

	public void setId(FdbSidePK id) {
		this.id = id;
	}
	
	public String getDescdrug1() {
		return this.descdrug1;
	}

	public void setDescdrug1(String descdrug1) {
		this.descdrug1 = descdrug1;
	}

	public String getDescdrug2() {
		return this.descdrug2;
	}

	public void setDescdrug2(String descdrug2) {
		this.descdrug2 = descdrug2;
	}

	public String getDescdrug3() {
		return this.descdrug3;
	}

	public void setDescdrug3(String descdrug3) {
		this.descdrug3 = descdrug3;
	}

	public String getDescdrug4() {
		return this.descdrug4;
	}

	public void setDescdrug4(String descdrug4) {
		this.descdrug4 = descdrug4;
	}

	public String getDescdrug5() {
		return this.descdrug5;
	}

	public void setDescdrug5(String descdrug5) {
		this.descdrug5 = descdrug5;
	}

	public String getDescdrug6() {
		return this.descdrug6;
	}

	public void setDescdrug6(String descdrug6) {
		this.descdrug6 = descdrug6;
	}

	public String getDesceffect1() {
		return this.desceffect1;
	}

	public void setDesceffect1(String desceffect1) {
		this.desceffect1 = desceffect1;
	}

	public String getDesceffect2() {
		return this.desceffect2;
	}

	public void setDesceffect2(String desceffect2) {
		this.desceffect2 = desceffect2;
	}

	public String getDesceffect3() {
		return this.desceffect3;
	}

	public void setDesceffect3(String desceffect3) {
		this.desceffect3 = desceffect3;
	}

	public String getDesceffect4() {
		return this.desceffect4;
	}

	public void setDesceffect4(String desceffect4) {
		this.desceffect4 = desceffect4;
	}

	public String getDesceffect5() {
		return this.desceffect5;
	}

	public void setDesceffect5(String desceffect5) {
		this.desceffect5 = desceffect5;
	}

	public String getDesceffect6() {
		return this.desceffect6;
	}

	public void setDesceffect6(String desceffect6) {
		this.desceffect6 = desceffect6;
	}

	public String getDxid() {
		return this.dxid;
	}

	public void setDxid(String dxid) {
		this.dxid = dxid;
	}

	public String getFdbdx() {
		return this.fdbdx;
	}

	public void setFdbdx(String fdbdx) {
		this.fdbdx = fdbdx;
	}

	public String getFreqcode() {
		return this.freqcode;
	}

	public void setFreqcode(String freqcode) {
		this.freqcode = freqcode;
	}

	public String getHypercode() {
		return this.hypercode;
	}

	public void setHypercode(String hypercode) {
		this.hypercode = hypercode;
	}

	public String getLabtestcode() {
		return this.labtestcode;
	}

	public void setLabtestcode(String labtestcode) {
		this.labtestcode = labtestcode;
	}

	public String getPhyscode() {
		return this.physcode;
	}

	public void setPhyscode(String physcode) {
		this.physcode = physcode;
	}

	public String getSevcode() {
		return this.sevcode;
	}

	public void setSevcode(String sevcode) {
		this.sevcode = sevcode;
	}

	public String getViscode() {
		return this.viscode;
	}

	public void setViscode(String viscode) {
		this.viscode = viscode;
	}

}