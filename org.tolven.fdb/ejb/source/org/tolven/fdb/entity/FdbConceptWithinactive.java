package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_concept_withinactives database table.
 * 
 */
@Entity
@Table(name="fdb_concept_withinactives")
public class FdbConceptWithinactive implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbConceptWithinactivePK id;

	private Integer alwayspresentind;

    public FdbConceptWithinactive() {
    }

	public FdbConceptWithinactivePK getId() {
		return this.id;
	}

	public void setId(FdbConceptWithinactivePK id) {
		this.id = id;
	}
	
	public Integer getAlwayspresentind() {
		return this.alwayspresentind;
	}

	public void setAlwayspresentind(Integer alwayspresentind) {
		this.alwayspresentind = alwayspresentind;
	}

}