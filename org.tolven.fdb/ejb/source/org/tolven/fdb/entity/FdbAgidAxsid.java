package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_agid_axsid database table.
 * 
 */
@Entity
@Table(name="fdb_agid_axsid")
public class FdbAgidAxsid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbAgidAxsidPK id;

    public FdbAgidAxsid() {
    }

	public FdbAgidAxsidPK getId() {
		return this.id;
	}

	public void setId(FdbAgidAxsidPK id) {
		this.id = id;
	}
	
}