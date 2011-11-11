package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ddimdruglink database table.
 * 
 */
@Entity
@Table(name="fdb_ddimdruglink")
public class FdbDdimdruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDdimdruglinkPK id;

    public FdbDdimdruglink() {
    }

	public FdbDdimdruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbDdimdruglinkPK id) {
		this.id = id;
	}
	
}