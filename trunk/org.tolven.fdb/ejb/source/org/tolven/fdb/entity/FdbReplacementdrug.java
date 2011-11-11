package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_replacementdrugs database table.
 * 
 */
@Entity
@Table(name="fdb_replacementdrugs")
public class FdbReplacementdrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbReplacementdrugPK id;

    public FdbReplacementdrug() {
    }

	public FdbReplacementdrugPK getId() {
		return this.id;
	}

	public void setId(FdbReplacementdrugPK id) {
		this.id = id;
	}
	
}