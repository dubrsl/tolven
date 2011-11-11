package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_drug_rtgenid database table.
 * 
 */
@Entity
@Table(name="fdb_drug_rtgenid")
public class FdbDrugRtgenid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDrugRtgenidPK id;

    public FdbDrugRtgenid() {
    }

	public FdbDrugRtgenidPK getId() {
		return this.id;
	}

	public void setId(FdbDrugRtgenidPK id) {
		this.id = id;
	}
	
}