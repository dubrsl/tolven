package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_fdbclassification_link database table.
 * 
 */
@Entity
@Table(name="fdb_fdbclassification_link")
public class FdbFdbclassificationLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbFdbclassificationLinkPK id;

    public FdbFdbclassificationLink() {
    }

	public FdbFdbclassificationLinkPK getId() {
		return this.id;
	}

	public void setId(FdbFdbclassificationLinkPK id) {
		this.id = id;
	}
	
}