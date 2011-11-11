package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precpedi_druglink database table.
 * 
 */
@Entity
@Table(name="fdb_precpedi_druglink")
public class FdbPrecpediDruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecpediDruglinkPK id;

	private Integer conceptid;

	private Integer concepttype;

    public FdbPrecpediDruglink() {
    }

	public FdbPrecpediDruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbPrecpediDruglinkPK id) {
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