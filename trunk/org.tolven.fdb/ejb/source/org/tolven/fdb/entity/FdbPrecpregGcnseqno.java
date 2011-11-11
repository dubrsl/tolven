package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precpreg_gcnseqno database table.
 * 
 */
@Entity
@Table(name="fdb_precpreg_gcnseqno")
public class FdbPrecpregGcnseqno implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecpregGcnseqnoPK id;

    public FdbPrecpregGcnseqno() {
    }

	public FdbPrecpregGcnseqnoPK getId() {
		return this.id;
	}

	public void setId(FdbPrecpregGcnseqnoPK id) {
		this.id = id;
	}
	
}