package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_drug_monograph_ext database table.
 * 
 */
@Entity
@Table(name="fdb_drug_monograph_ext")
public class FdbDrugMonographExt implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDrugMonographExtPK id;

    public FdbDrugMonographExt() {
    }

	public FdbDrugMonographExtPK getId() {
		return this.id;
	}

	public void setId(FdbDrugMonographExtPK id) {
		this.id = id;
	}
	
}