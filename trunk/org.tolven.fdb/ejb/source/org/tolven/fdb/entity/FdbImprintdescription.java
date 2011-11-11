package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_imprintdescription database table.
 * 
 */
@Entity
@Table(name="fdb_imprintdescription")
public class FdbImprintdescription implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbImprintdescriptionPK id;

    public FdbImprintdescription() {
    }

	public FdbImprintdescriptionPK getId() {
		return this.id;
	}

	public void setId(FdbImprintdescriptionPK id) {
		this.id = id;
	}
	
}