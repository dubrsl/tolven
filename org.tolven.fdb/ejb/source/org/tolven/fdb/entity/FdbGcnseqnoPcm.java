package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_gcnseqno_pcm database table.
 * 
 */
@Entity
@Table(name="fdb_gcnseqno_pcm")
public class FdbGcnseqnoPcm implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbGcnseqnoPcmPK id;

	private Integer priority;

    public FdbGcnseqnoPcm() {
    }

	public FdbGcnseqnoPcmPK getId() {
		return this.id;
	}

	public void setId(FdbGcnseqnoPcmPK id) {
		this.id = id;
	}
	
	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}