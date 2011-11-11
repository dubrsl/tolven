package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_rtgenid_genericrmid database table.
 * 
 */
@Entity
@Table(name="fdb_rtgenid_genericrmid")
public class FdbRtgenidGenericrmid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbRtgenidGenericrmidPK id;

    public FdbRtgenidGenericrmid() {
    }

	public FdbRtgenidGenericrmidPK getId() {
		return this.id;
	}

	public void setId(FdbRtgenidGenericrmidPK id) {
		this.id = id;
	}
	
}