package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_generic_dispsearch database table.
 * 
 */
@Entity
@Table(name="fdb_generic_dispsearch")
public class FdbGenericDispsearch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbGenericDispsearchPK id;

	private Integer baselangid;

	private String descaltsearch;

	private String descdisplay;

	private String descsearch;

    public FdbGenericDispsearch() {
    }

	public FdbGenericDispsearchPK getId() {
		return this.id;
	}

	public void setId(FdbGenericDispsearchPK id) {
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

	public String getDescdisplay() {
		return this.descdisplay;
	}

	public void setDescdisplay(String descdisplay) {
		this.descdisplay = descdisplay;
	}

	public String getDescsearch() {
		return this.descsearch;
	}

	public void setDescsearch(String descsearch) {
		this.descsearch = descsearch;
	}

}