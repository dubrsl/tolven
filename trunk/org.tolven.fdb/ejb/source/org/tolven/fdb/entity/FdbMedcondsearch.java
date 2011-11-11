package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_medcondsearch database table.
 * 
 */
@Entity
@Table(name="fdb_medcondsearch")
public class FdbMedcondsearch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMedcondsearchPK id;

	private Integer baselangid;

	private String descaltsearch;

	private String descsearch;

    public FdbMedcondsearch() {
    }

	public FdbMedcondsearchPK getId() {
		return this.id;
	}

	public void setId(FdbMedcondsearchPK id) {
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