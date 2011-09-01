package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_ddimstrings database table.
 * 
 */
@Entity
@Table(name="fdb_custom_ddimstrings")
public class FdbCustomDdimstring implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomDdimstringPK id;

	private String customstring;

    public FdbCustomDdimstring() {
    }

	public FdbCustomDdimstringPK getId() {
		return this.id;
	}

	public void setId(FdbCustomDdimstringPK id) {
		this.id = id;
	}
	
	public String getCustomstring() {
		return this.customstring;
	}

	public void setCustomstring(String customstring) {
		this.customstring = customstring;
	}

}