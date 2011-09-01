package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_generic_drugnamesearch database table.
 * 
 */
@Entity
@Table(name="fdb_generic_drugnamesearch")
public class FdbGenericDrugnamesearch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbGenericDrugnamesearchPK id;

	private Integer baselangid;

	private String descaltsearch;

	private String descdisplay;

	private String descsearch;

    public FdbGenericDrugnamesearch() {
    }

	public FdbGenericDrugnamesearchPK getId() {
		return this.id;
	}

	public void setId(FdbGenericDrugnamesearchPK id) {
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