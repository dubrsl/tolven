package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_class_drugconcept database table.
 * 
 */
@Entity
@Table(name="fdb_custom_class_drugconcept")
public class FdbCustomClassDrugconcept implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomClassDrugconceptPK id;

    public FdbCustomClassDrugconcept() {
    }

	public FdbCustomClassDrugconceptPK getId() {
		return this.id;
	}

	public void setId(FdbCustomClassDrugconceptPK id) {
		this.id = id;
	}
	
}