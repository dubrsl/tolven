package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_fdbclass_drugconcept database table.
 * 
 */
@Entity
@Table(name="fdb_fdbclass_drugconcept")
public class FdbFdbclassDrugconcept implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbFdbclassDrugconceptPK id;

    public FdbFdbclassDrugconcept() {
    }

	public FdbFdbclassDrugconceptPK getId() {
		return this.id;
	}

	public void setId(FdbFdbclassDrugconceptPK id) {
		this.id = id;
	}
	
}