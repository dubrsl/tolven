package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_dfimdruglink database table.
 * 
 */
@Entity
@Table(name="fdb_dfimdruglink")
public class FdbDfimdruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDfimdruglinkPK id;

    public FdbDfimdruglink() {
    }

	public FdbDfimdruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbDfimdruglinkPK id) {
		this.id = id;
	}
	
}