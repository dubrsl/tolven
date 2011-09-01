package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ivm_remarklink database table.
 * 
 */
@Entity
@Table(name="fdb_ivm_remarklink")
public class FdbIvmRemarklink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbIvmRemarklinkPK id;

	private String remarkid;

    public FdbIvmRemarklink() {
    }

	public FdbIvmRemarklinkPK getId() {
		return this.id;
	}

	public void setId(FdbIvmRemarklinkPK id) {
		this.id = id;
	}
	
	public String getRemarkid() {
		return this.remarkid;
	}

	public void setRemarkid(String remarkid) {
		this.remarkid = remarkid;
	}

}