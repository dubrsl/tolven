package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_drug_pcm database table.
 * 
 */
@Entity
@Table(name="fdb_drug_pcm")
public class FdbDrugPcm implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDrugPcmPK id;

	private Integer priority;

    public FdbDrugPcm() {
    }

	public FdbDrugPcmPK getId() {
		return this.id;
	}

	public void setId(FdbDrugPcmPK id) {
		this.id = id;
	}
	
	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}