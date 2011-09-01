package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_hicseqno_agid database table.
 * 
 */
@Entity
@Table(name="fdb_hicseqno_agid")
public class FdbHicseqnoAgid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbHicseqnoAgidPK id;

    public FdbHicseqnoAgid() {
    }

	public FdbHicseqnoAgidPK getId() {
		return this.id;
	}

	public void setId(FdbHicseqnoAgidPK id) {
		this.id = id;
	}
	
}