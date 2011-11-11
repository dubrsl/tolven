package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_sidedruglink database table.
 * 
 */
@Entity
@Table(name="fdb_sidedruglink")
public class FdbSidedruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbSidedruglinkPK id;

	private Integer gcnseqno;

    public FdbSidedruglink() {
    }

	public FdbSidedruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbSidedruglinkPK id) {
		this.id = id;
	}
	
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}

}