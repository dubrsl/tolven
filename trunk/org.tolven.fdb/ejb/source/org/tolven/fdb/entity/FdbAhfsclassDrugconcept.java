package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ahfsclass_drugconcept database table.
 * 
 */
@Entity
@Table(name="fdb_ahfsclass_drugconcept")
public class FdbAhfsclassDrugconcept implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbAhfsclassDrugconceptPK id;

    public FdbAhfsclassDrugconcept() {
    }

	public FdbAhfsclassDrugconceptPK getId() {
		return this.id;
	}

	public void setId(FdbAhfsclassDrugconceptPK id) {
		this.id = id;
	}
	
}