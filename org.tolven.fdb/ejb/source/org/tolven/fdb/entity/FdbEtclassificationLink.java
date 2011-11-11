package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_etclassification_link database table.
 * 
 */
@Entity
@Table(name="fdb_etclassification_link")
public class FdbEtclassificationLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbEtclassificationLinkPK id;

    public FdbEtclassificationLink() {
    }

	public FdbEtclassificationLinkPK getId() {
		return this.id;
	}

	public void setId(FdbEtclassificationLinkPK id) {
		this.id = id;
	}
	
}