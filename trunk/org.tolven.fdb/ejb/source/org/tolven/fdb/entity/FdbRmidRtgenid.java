package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_rmid_rtgenid database table.
 * 
 */
@Entity
@Table(name="fdb_rmid_rtgenid")
public class FdbRmidRtgenid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbRmidRtgenidPK id;

    public FdbRmidRtgenid() {
    }

	public FdbRmidRtgenidPK getId() {
		return this.id;
	}

	public void setId(FdbRmidRtgenidPK id) {
		this.id = id;
	}
	
}