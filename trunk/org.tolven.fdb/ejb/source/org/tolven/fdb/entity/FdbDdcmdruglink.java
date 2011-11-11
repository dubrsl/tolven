package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ddcmdruglink database table.
 * 
 */
@Entity
@Table(name="fdb_ddcmdruglink")
public class FdbDdcmdruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDdcmdruglinkPK id;

	private Integer gcnseqno;

    public FdbDdcmdruglink() {
    }

	public FdbDdcmdruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbDdcmdruglinkPK id) {
		this.id = id;
	}
	
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}

}