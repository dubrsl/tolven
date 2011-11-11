package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_damscreen database table.
 * 
 */
@Entity
@Table(name="fdb_damscreen")
public class FdbDamscreen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDamscreenPK id;

	private String inactivehicls;

    public FdbDamscreen() {
    }

	public FdbDamscreenPK getId() {
		return this.id;
	}

	public void setId(FdbDamscreenPK id) {
		this.id = id;
	}
	
	public String getInactivehicls() {
		return this.inactivehicls;
	}

	public void setInactivehicls(String inactivehicls) {
		this.inactivehicls = inactivehicls;
	}

}