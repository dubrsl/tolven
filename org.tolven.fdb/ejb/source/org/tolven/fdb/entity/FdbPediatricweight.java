package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_pediatricweight database table.
 * 
 */
@Entity
@Table(name="fdb_pediatricweight")
public class FdbPediatricweight implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPediatricweightPK id;

	private String agedesc;

	private BigDecimal female25thpctweight;

	private BigDecimal female50thpctweight;

	private BigDecimal female5thpctweight;

	private BigDecimal female75thpctweight;

	private BigDecimal female95thpctweight;

	private BigDecimal male25thpctweight;

	private BigDecimal male50thpctweight;

	private BigDecimal male5thpctweight;

	private BigDecimal male75thpctweight;

	private BigDecimal male95thpctweight;

    public FdbPediatricweight() {
    }

	public FdbPediatricweightPK getId() {
		return this.id;
	}

	public void setId(FdbPediatricweightPK id) {
		this.id = id;
	}
	
	public String getAgedesc() {
		return this.agedesc;
	}

	public void setAgedesc(String agedesc) {
		this.agedesc = agedesc;
	}

	public BigDecimal getFemale25thpctweight() {
		return this.female25thpctweight;
	}

	public void setFemale25thpctweight(BigDecimal female25thpctweight) {
		this.female25thpctweight = female25thpctweight;
	}

	public BigDecimal getFemale50thpctweight() {
		return this.female50thpctweight;
	}

	public void setFemale50thpctweight(BigDecimal female50thpctweight) {
		this.female50thpctweight = female50thpctweight;
	}

	public BigDecimal getFemale5thpctweight() {
		return this.female5thpctweight;
	}

	public void setFemale5thpctweight(BigDecimal female5thpctweight) {
		this.female5thpctweight = female5thpctweight;
	}

	public BigDecimal getFemale75thpctweight() {
		return this.female75thpctweight;
	}

	public void setFemale75thpctweight(BigDecimal female75thpctweight) {
		this.female75thpctweight = female75thpctweight;
	}

	public BigDecimal getFemale95thpctweight() {
		return this.female95thpctweight;
	}

	public void setFemale95thpctweight(BigDecimal female95thpctweight) {
		this.female95thpctweight = female95thpctweight;
	}

	public BigDecimal getMale25thpctweight() {
		return this.male25thpctweight;
	}

	public void setMale25thpctweight(BigDecimal male25thpctweight) {
		this.male25thpctweight = male25thpctweight;
	}

	public BigDecimal getMale50thpctweight() {
		return this.male50thpctweight;
	}

	public void setMale50thpctweight(BigDecimal male50thpctweight) {
		this.male50thpctweight = male50thpctweight;
	}

	public BigDecimal getMale5thpctweight() {
		return this.male5thpctweight;
	}

	public void setMale5thpctweight(BigDecimal male5thpctweight) {
		this.male5thpctweight = male5thpctweight;
	}

	public BigDecimal getMale75thpctweight() {
		return this.male75thpctweight;
	}

	public void setMale75thpctweight(BigDecimal male75thpctweight) {
		this.male75thpctweight = male75thpctweight;
	}

	public BigDecimal getMale95thpctweight() {
		return this.male95thpctweight;
	}

	public void setMale95thpctweight(BigDecimal male95thpctweight) {
		this.male95thpctweight = male95thpctweight;
	}

}