package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precpedi_gcnseqno database table.
 * 
 */
@Entity
@Table(name="fdb_precpedi_gcnseqno")
public class FdbPrecpediGcnseqno implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecpediGcnseqnoPK id;

    public FdbPrecpediGcnseqno() {
    }

	public FdbPrecpediGcnseqnoPK getId() {
		return this.id;
	}

	public void setId(FdbPrecpediGcnseqnoPK id) {
		this.id = id;
	}
	
}