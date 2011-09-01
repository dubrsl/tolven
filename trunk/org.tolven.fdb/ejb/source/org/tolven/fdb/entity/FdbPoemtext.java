package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_poemtext database table.
 * 
 */
@Entity
@Table(name="fdb_poemtext")
public class FdbPoemtext implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPoemtextPK id;

	private String linetext;

    public FdbPoemtext() {
    }

	public FdbPoemtextPK getId() {
		return this.id;
	}

	public void setId(FdbPoemtextPK id) {
		this.id = id;
	}
	
	public String getLinetext() {
		return this.linetext;
	}

	public void setLinetext(String linetext) {
		this.linetext = linetext;
	}

}