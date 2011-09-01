package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_poem database table.
 * 
 */
@Entity
@Table(name="fdb_poem")
public class FdbPoem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPoemPK id;

	private String dxid;

    public FdbPoem() {
    }

	public FdbPoemPK getId() {
		return this.id;
	}

	public void setId(FdbPoemPK id) {
		this.id = id;
	}
	
	public String getDxid() {
		return this.dxid;
	}

	public void setDxid(String dxid) {
		this.dxid = dxid;
	}

}