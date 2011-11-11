package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_precpreg_rtgen database table.
 * 
 */
@Entity
@Table(name="fdb_precpreg_rtgen")
public class FdbPrecpregRtgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPrecpregRtgenPK id;

    public FdbPrecpregRtgen() {
    }

	public FdbPrecpregRtgenPK getId() {
		return this.id;
	}

	public void setId(FdbPrecpregRtgenPK id) {
		this.id = id;
	}
	
}