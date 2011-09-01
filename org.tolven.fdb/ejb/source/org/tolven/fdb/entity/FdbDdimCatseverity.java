package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ddim_catseverities database table.
 * 
 */
@Entity
@Table(name="fdb_ddim_catseverities")
public class FdbDdimCatseverity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDdimCatseverityPK id;

	private String severitycode;

    public FdbDdimCatseverity() {
    }

	public FdbDdimCatseverityPK getId() {
		return this.id;
	}

	public void setId(FdbDdimCatseverityPK id) {
		this.id = id;
	}
	
	public String getSeveritycode() {
		return this.severitycode;
	}

	public void setSeveritycode(String severitycode) {
		this.severitycode = severitycode;
	}

}