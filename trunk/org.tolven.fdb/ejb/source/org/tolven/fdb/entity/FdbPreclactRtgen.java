package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_preclact_rtgen database table.
 * 
 */
@Entity
@Table(name="fdb_preclact_rtgen")
public class FdbPreclactRtgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPreclactRtgenPK id;

    public FdbPreclactRtgen() {
    }

	public FdbPreclactRtgenPK getId() {
		return this.id;
	}

	public void setId(FdbPreclactRtgenPK id) {
		this.id = id;
	}
	
}