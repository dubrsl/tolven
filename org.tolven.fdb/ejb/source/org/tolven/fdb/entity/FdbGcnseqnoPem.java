package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_gcnseqno_pem database table.
 * 
 */
@Entity
@Table(name="fdb_gcnseqno_pem")
public class FdbGcnseqnoPem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbGcnseqnoPemPK id;

    public FdbGcnseqnoPem() {
    }

	public FdbGcnseqnoPemPK getId() {
		return this.id;
	}

	public void setId(FdbGcnseqnoPemPK id) {
		this.id = id;
	}
	
}