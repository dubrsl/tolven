package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_replacementdxids database table.
 * 
 */
@Entity
@Table(name="fdb_replacementdxids")
public class FdbReplacementdxid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbReplacementdxidPK id;

    public FdbReplacementdxid() {
    }

	public FdbReplacementdxidPK getId() {
		return this.id;
	}

	public void setId(FdbReplacementdxidPK id) {
		this.id = id;
	}
	
}