package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_preclact_rtdfgen database table.
 * 
 */
@Entity
@Table(name="fdb_preclact_rtdfgen")
public class FdbPreclactRtdfgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPreclactRtdfgenPK id;

    public FdbPreclactRtdfgen() {
    }

	public FdbPreclactRtdfgenPK getId() {
		return this.id;
	}

	public void setId(FdbPreclactRtdfgenPK id) {
		this.id = id;
	}
	
}