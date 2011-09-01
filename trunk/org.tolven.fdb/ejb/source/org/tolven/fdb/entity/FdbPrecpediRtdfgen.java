package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precpedi_rtdfgen database table.
 * 
 */
@Entity
@Table(name="fdb_precpedi_rtdfgen")
public class FdbPrecpediRtdfgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecpediRtdfgenPK id;

    public FdbPrecpediRtdfgen() {
    }

	public FdbPrecpediRtdfgenPK getId() {
		return this.id;
	}

	public void setId(FdbPrecpediRtdfgenPK id) {
		this.id = id;
	}
	
}