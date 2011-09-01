package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_gcnseqno_plblw database table.
 * 
 */
@Entity
@Table(name="fdb_gcnseqno_plblw")
public class FdbGcnseqnoPlblw implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbGcnseqnoPlblwPK id;

	private Integer priority;

    public FdbGcnseqnoPlblw() {
    }

	public FdbGcnseqnoPlblwPK getId() {
		return this.id;
	}

	public void setId(FdbGcnseqnoPlblwPK id) {
		this.id = id;
	}
	
	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}