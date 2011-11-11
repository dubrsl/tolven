package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precpreg_druglink database table.
 * 
 */
@Entity
@Table(name="fdb_precpreg_druglink")
public class FdbPrecpregDruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecpregDruglinkPK id;

	private Integer conceptid;

	private Integer concepttype;

    public FdbPrecpregDruglink() {
    }

	public FdbPrecpregDruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbPrecpregDruglinkPK id) {
		this.id = id;
	}
	
	public Integer getConceptid() {
		return this.conceptid;
	}

	public void setConceptid(Integer conceptid) {
		this.conceptid = conceptid;
	}

	public Integer getConcepttype() {
		return this.concepttype;
	}

	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}

}