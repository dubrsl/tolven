package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_allergengroupsearch_ii database table.
 * 
 */
@Entity
@Table(name="fdb_allergengroupsearch_ii")
public class FdbAllergengroupsearchIi implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbAllergengroupsearchIiPK id;

	private Integer baselangid;

	private String descaltsearch;

	private String descsearch;

    public FdbAllergengroupsearchIi() {
    }

	public FdbAllergengroupsearchIiPK getId() {
		return this.id;
	}

	public void setId(FdbAllergengroupsearchIiPK id) {
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