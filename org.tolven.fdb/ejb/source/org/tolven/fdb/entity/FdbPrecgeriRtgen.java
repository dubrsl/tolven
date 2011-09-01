package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precgeri_rtgen database table.
 * 
 */
@Entity
@Table(name="fdb_precgeri_rtgen")
public class FdbPrecgeriRtgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecgeriRtgenPK id;

    public FdbPrecgeriRtgen() {
    }

	public FdbPrecgeriRtgenPK getId() {
		return this.id;
	}

	public void setId(FdbPrecgeriRtgenPK id) {
		this.id = id;
	}
	
}