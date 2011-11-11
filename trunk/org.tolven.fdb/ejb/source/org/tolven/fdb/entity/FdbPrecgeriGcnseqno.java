package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precgeri_gcnseqno database table.
 * 
 */
@Entity
@Table(name="fdb_precgeri_gcnseqno")
public class FdbPrecgeriGcnseqno implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecgeriGcnseqnoPK id;

    public FdbPrecgeriGcnseqno() {
    }

	public FdbPrecgeriGcnseqnoPK getId() {
		return this.id;
	}

	public void setId(FdbPrecgeriGcnseqnoPK id) {
		this.id = id;
	}
	
}