package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_imprintdesctext database table.
 * 
 */
@Entity
@Table(name="fdb_imprintdesctext")
public class FdbImprintdesctext implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbImprintdesctextPK id;

	private String linetext;

    public FdbImprintdesctext() {
    }

	public FdbImprintdesctextPK getId() {
		return this.id;
	}

	public void setId(FdbImprintdesctextPK id) {
		this.id = id;
	}
	
	public String getLinetext() {
		return this.linetext;
	}

	public void setLinetext(String linetext) {
		this.linetext = linetext;
	}

}