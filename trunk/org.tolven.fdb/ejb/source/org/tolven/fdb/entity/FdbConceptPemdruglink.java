package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_concept_pemdruglink database table.
 * 
 */
@Entity
@Table(name="fdb_concept_pemdruglink")
public class FdbConceptPemdruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbConceptPemdruglinkPK id;

    public FdbConceptPemdruglink() {
    }

	public FdbConceptPemdruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbConceptPemdruglinkPK id) {
		this.id = id;
	}
	
}