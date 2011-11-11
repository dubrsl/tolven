package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_allergenpicklstsrch database table.
 * 
 */
@Entity
@Table(name="fdb_custom_allergenpicklstsrch")
public class FdbCustomAllergenpicklstsrch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomAllergenpicklstsrchPK id;

	private Integer baselangid;

	private String descaltsearch;

	private String descsearch;

    public FdbCustomAllergenpicklstsrch() {
    }

	public FdbCustomAllergenpicklstsrchPK getId() {
		return this.id;
	}

	public void setId(FdbCustomAllergenpicklstsrchPK id) {
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