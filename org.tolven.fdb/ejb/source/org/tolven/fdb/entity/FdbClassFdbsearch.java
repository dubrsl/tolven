package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_class_fdbsearch database table.
 * 
 */
@Entity
@Table(name="fdb_class_fdbsearch")
public class FdbClassFdbsearch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbClassFdbsearchPK id;

	private Integer baselangid;

	private String descaltsearch;

	private String descsearch;

    public FdbClassFdbsearch() {
    }

	public FdbClassFdbsearchPK getId() {
		return this.id;
	}

	public void setId(FdbClassFdbsearchPK id) {
		this.id = id;
	}
	
	public Integer getBaselangid() {
		return this.baselangid;
	}

	public void setBaselangid(Integer baselangid) {
		this.baselangid = baselangid;
	}

	public String getDescaltsearch() {
		return this.descaltsearch;
	}

	public void setDescaltsearch(String descaltsearch) {
		this.descaltsearch = descaltsearch;
	}

	public String getDescsearch() {
		return this.descsearch;
	}

	public void setDescsearch(String descsearch) {
		this.descsearch = descsearch;
	}

}