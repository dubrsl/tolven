package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_allergen_hicseqno database table.
 * 
 */
@Entity
@Table(name="fdb_allergen_hicseqno")
public class FdbAllergenHicseqno implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbAllergenHicseqnoPK id;

    public FdbAllergenHicseqno() {
    }

	public FdbAllergenHicseqnoPK getId() {
		return this.id;
	}

	public void setId(FdbAllergenHicseqnoPK id) {
		this.id = id;
	}
	
}