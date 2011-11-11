package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_ivm database table.
 * 
 */
@Entity
@Table(name="fdb_ivm")
public class FdbIvm implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbIvmPK id;

	private String mfgid;

	private String resultcode;

	private BigDecimal strength;

	private String struom;

	private String testtypecode;

	private BigDecimal volume;

	private String voluom;

    public FdbIvm() {
    }

	public FdbIvmPK getId() {
		return this.id;
	}

	public void setId(FdbIvmPK id) {
		this.id = id;
	}
	
	public String getMfgid() {
		return this.mfgid;
	}

	public void setMfgid(String mfgid) {
		this.mfgid = mfgid;
	}

	public String getResultcode() {
		return this.resultcode;
	}

	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}

	public BigDecimal getStrength() {
		return this.strength;
	}

	public void setStrength(BigDecimal strength) {
		this.strength = strength;
	}

	public String getStruom() {
		return this.struom;
	}

	public void setStruom(String struom) {
		this.struom = struom;
	}

	public String getTesttypecode() {
		return this.testtypecode;
	}

	public void setTesttypecode(String testtypecode) {
		this.testtypecode = testtypecode;
	}

	public BigDecimal getVolume() {
		return this.volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public String getVoluom() {
		return this.voluom;
	}

	public void setVoluom(String voluom) {
		this.voluom = voluom;
	}

}