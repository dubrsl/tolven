package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_compound_rtgen database table.
 * 
 */
@Entity
@Table(name="fdb_compound_rtgen")
public class FdbCompoundRtgen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCompoundRtgenPK id;

    public FdbCompoundRtgen() {
    }

	public FdbCompoundRtgenPK getId() {
		return this.id;
	}

	public void setId(FdbCompoundRtgenPK id) {
		this.id = id;
	}
	
}