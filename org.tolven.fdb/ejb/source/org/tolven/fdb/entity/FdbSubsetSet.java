package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_subset_set database table.
 * 
 */
@Entity
@Table(name="fdb_subset_set")
public class FdbSubsetSet implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbSubsetSetPK id;

	private String description;

	private String owner;

	private String setcomment;

    public FdbSubsetSet() {
    }

	public FdbSubsetSetPK getId() {
		return this.id;
	}

	public void setId(FdbSubsetSetPK id) {
		this.id = id;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSetcomment() {
		return this.setcomment;
	}

	public void setSetcomment(String setcomment) {
		this.setcomment = setcomment;
	}

}