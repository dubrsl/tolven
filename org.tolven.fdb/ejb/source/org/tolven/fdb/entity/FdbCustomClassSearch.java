package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_class_search database table.
 * 
 */
@Entity
@Table(name="fdb_custom_class_search")
public class FdbCustomClassSearch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomClassSearchPK id;

	private Integer baselangid;

	private String descaltsearch;

	private String descsearch;

    public FdbCustomClassSearch() {
    }

	public FdbCustomClassSearchPK getId() {
		return this.id;
	}

	public void setId(FdbCustomClassSearchPK id) {
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