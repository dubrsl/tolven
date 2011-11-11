package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_extclassification_link database table.
 * 
 */
@Entity
@Table(name="fdb_extclassification_link")
public class FdbExtclassificationLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbExtclassificationLinkPK id;

    public FdbExtclassificationLink() {
    }

	public FdbExtclassificationLinkPK getId() {
		return this.id;
	}

	public void setId(FdbExtclassificationLinkPK id) {
		this.id = id;
	}
	
}