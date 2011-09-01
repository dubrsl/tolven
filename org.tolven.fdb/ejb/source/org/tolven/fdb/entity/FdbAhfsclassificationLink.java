package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ahfsclassification_link database table.
 * 
 */
@Entity
@Table(name="fdb_ahfsclassification_link")
public class FdbAhfsclassificationLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbAhfsclassificationLinkPK id;

    public FdbAhfsclassificationLink() {
    }

	public FdbAhfsclassificationLinkPK getId() {
		return this.id;
	}

	public void setId(FdbAhfsclassificationLinkPK id) {
		this.id = id;
	}
	
}