package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_preclact_gcnseqno database table.
 * 
 */
@Entity
@Table(name="fdb_preclact_gcnseqno")
public class FdbPreclactGcnseqno implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPreclactGcnseqnoPK id;

    public FdbPreclactGcnseqno() {
    }

	public FdbPreclactGcnseqnoPK getId() {
		return this.id;
	}

	public void setId(FdbPreclactGcnseqnoPK id) {
		this.id = id;
	}
	
}