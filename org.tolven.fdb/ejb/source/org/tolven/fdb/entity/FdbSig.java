package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_sig database table.
 * 
 */
@Entity
@Table(name="fdb_sig")
public class FdbSig implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbSigPK id;

    public FdbSig() {
    }

	public FdbSigPK getId() {
		return this.id;
	}

	public void setId(FdbSigPK id) {
		this.id = id;
	}
	
}