package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_hicl_hicseqno database table.
 * 
 */
@Entity
@Table(name="fdb_hicl_hicseqno")
public class FdbHiclHicseqno implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbHiclHicseqnoPK id;

    public FdbHiclHicseqno() {
    }

	public FdbHiclHicseqnoPK getId() {
		return this.id;
	}

	public void setId(FdbHiclHicseqnoPK id) {
		this.id = id;
	}
	
}