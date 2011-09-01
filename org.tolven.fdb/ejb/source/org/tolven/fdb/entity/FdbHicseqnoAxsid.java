package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_hicseqno_axsid database table.
 * 
 */
@Entity
@Table(name="fdb_hicseqno_axsid")
public class FdbHicseqnoAxsid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbHicseqnoAxsidPK id;

    public FdbHicseqnoAxsid() {
    }

	public FdbHicseqnoAxsidPK getId() {
		return this.id;
	}

	public void setId(FdbHicseqnoAxsidPK id) {
		this.id = id;
	}
	
}