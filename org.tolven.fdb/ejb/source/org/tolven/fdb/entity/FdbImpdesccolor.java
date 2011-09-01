package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_impdesccolors database table.
 * 
 */
@Entity
@Table(name="fdb_impdesccolors")
public class FdbImpdesccolor implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbImpdesccolorPK id;

    public FdbImpdesccolor() {
    }

	public FdbImpdesccolorPK getId() {
		return this.id;
	}

	public void setId(FdbImpdesccolorPK id) {
		this.id = id;
	}
	
}