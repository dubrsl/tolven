package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_ivm_tpn_ingred database table.
 * 
 */
@Entity
@Table(name="fdb_ivm_tpn_ingred")
public class FdbIvmTpnIngred implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbIvmTpnIngredPK id;

	private String descaltsearch;

	private String descdisplay;

	private String descsearch;

	private String mfgid;

	private BigDecimal strength;

	private String struom;

	private BigDecimal volume;

	private String voluom;

    public FdbIvmTpnIngred() {
    }

	public FdbIvmTpnIngredPK getId() {
		return this.id;
	}

	public void setId(FdbIvmTpnIngredPK id) {
		this.id = id;
	}
	
	public String getDescaltsearch() {
		return this.descaltsearch;
	}

	public void setDescaltsearch(String descaltsearch) {
		this.descaltsearch = descaltsearch;
	}

	public String getDescdisplay() {
		return this.descdisplay;
	}

	public void setDescdisplay(String descdisplay) {
		this.descdisplay = descdisplay;
	}

	public String getDescsearch() {
		return this.descsearch;
	}

	public void setDescsearch(String descsearch) {
		this.descsearch = descsearch;
	}

	public String getMfgid() {
		return this.mfgid;
	}

	public void setMfgid(String mfgid) {
		this.mfgid = mfgid;
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