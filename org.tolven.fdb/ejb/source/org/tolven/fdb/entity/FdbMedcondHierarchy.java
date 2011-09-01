package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_medcond_hierarchy database table.
 * 
 */
@Entity
@Table(name="fdb_medcond_hierarchy")
public class FdbMedcondHierarchy implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMedcondHierarchyPK id;

    public FdbMedcondHierarchy() {
    }

	public FdbMedcondHierarchyPK getId() {
		return this.id;
	}

	public void setId(FdbMedcondHierarchyPK id) {
		this.id = id;
	}
	
}