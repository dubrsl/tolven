package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_drug_plblw database table.
 * 
 */
@Entity
@Table(name="fdb_drug_plblw")
public class FdbDrugPlblw implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDrugPlblwPK id;

	private Integer priority;

    public FdbDrugPlblw() {
    }

	public FdbDrugPlblwPK getId() {
		return this.id;
	}

	public void setId(FdbDrugPlblwPK id) {
		this.id = id;
	}
	
	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}