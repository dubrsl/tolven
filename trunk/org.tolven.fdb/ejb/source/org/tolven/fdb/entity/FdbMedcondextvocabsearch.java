package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_medcondextvocabsearch database table.
 * 
 */
@Entity
@Table(name="fdb_medcondextvocabsearch")
public class FdbMedcondextvocabsearch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMedcondextvocabsearchPK id;

	private Integer baselangid;

	private String descaltsearch;

	private String descsearch;

	private String extvocabid;

    public FdbMedcondextvocabsearch() {
    }

	public FdbMedcondextvocabsearchPK getId() {
		return this.id;
	}

	public void setId(FdbMedcondextvocabsearchPK id) {
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

	public String getExtvocabid() {
		return this.extvocabid;
	}

	public void setExtvocabid(String extvocabid) {
		this.extvocabid = extvocabid;
	}

}