package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precpedi_rtgen database table.
 * 
 */
@Entity
@Table(name="fdb_precpedi_rtgen")
public class FdbPrecpediRtgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecpediRtgenPK id;

    public FdbPrecpediRtgen() {
    }

	public FdbPrecpediRtgenPK getId() {
		return this.id;
	}

	public void setId(FdbPrecpediRtgenPK id) {
		this.id = id;
	}
	
}