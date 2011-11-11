package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_replacementallergengroups database table.
 * 
 */
@Entity
@Table(name="fdb_replacementallergengroups")
public class FdbReplacementallergengroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbReplacementallergengroupPK id;

    public FdbReplacementallergengroup() {
    }

	public FdbReplacementallergengroupPK getId() {
		return this.id;
	}

	public void setId(FdbReplacementallergengroupPK id) {
		this.id = id;
	}
	
}