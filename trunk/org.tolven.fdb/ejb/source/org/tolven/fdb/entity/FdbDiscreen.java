package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_discreen database table.
 * 
 */
@Entity
@Table(name="fdb_discreen")
public class FdbDiscreen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDiscreenPK id;

    public FdbDiscreen() {
    }

	public FdbDiscreenPK getId() {
		return this.id;
	}

	public void setId(FdbDiscreenPK id) {
		this.id = id;
	}
	
}