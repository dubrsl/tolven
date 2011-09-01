package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precgeri_rtdfgen database table.
 * 
 */
@Entity
@Table(name="fdb_precgeri_rtdfgen")
public class FdbPrecgeriRtdfgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecgeriRtdfgenPK id;

    public FdbPrecgeriRtdfgen() {
    }

	public FdbPrecgeriRtdfgenPK getId() {
		return this.id;
	}

	public void setId(FdbPrecgeriRtdfgenPK id) {
		this.id = id;
	}
	
}