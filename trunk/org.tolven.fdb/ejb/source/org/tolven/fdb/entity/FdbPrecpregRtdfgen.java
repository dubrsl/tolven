package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precpreg_rtdfgen database table.
 * 
 */
@Entity
@Table(name="fdb_precpreg_rtdfgen")
public class FdbPrecpregRtdfgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecpregRtdfgenPK id;

    public FdbPrecpregRtdfgen() {
    }

	public FdbPrecpregRtdfgenPK getId() {
		return this.id;
	}

	public void setId(FdbPrecpregRtdfgenPK id) {
		this.id = id;
	}
	
}