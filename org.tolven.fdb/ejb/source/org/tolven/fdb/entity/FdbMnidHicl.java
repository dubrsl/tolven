package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_mnid_hicl database table.
 * 
 */
@Entity
@Table(name="fdb_mnid_hicl")
public class FdbMnidHicl implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMnidHiclPK id;

    public FdbMnidHicl() {
    }

	public FdbMnidHiclPK getId() {
		return this.id;
	}

	public void setId(FdbMnidHiclPK id) {
		this.id = id;
	}
	
}