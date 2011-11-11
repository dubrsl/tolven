package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_replacementingredients database table.
 * 
 */
@Entity
@Table(name="fdb_replacementingredients")
public class FdbReplacementingredient implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbReplacementingredientPK id;

    public FdbReplacementingredient() {
    }

	public FdbReplacementingredientPK getId() {
		return this.id;
	}

	public void setId(FdbReplacementingredientPK id) {
		this.id = id;
	}
	
}