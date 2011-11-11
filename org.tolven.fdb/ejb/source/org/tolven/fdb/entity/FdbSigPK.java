package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_sig database table.
 * 
 */
@Embeddable
public class FdbSigPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String sigcode;

	private String sigdesc;

    public FdbSigPK() {
    }
	public String getSigcode() {
		return this.sigcode;
	}
	public void setSigcode(String sigcode) {
		this.sigcode = sigcode;
	}
	public String getSigdesc() {
		return this.sigdesc;
	}
	public void setSigdesc(String sigdesc) {
		this.sigdesc = sigdesc;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbSigPK)) {
			return false;
		}
		FdbSigPK castOther = (FdbSigPK)other;
		return 
			this.sigcode.equals(castOther.sigcode)
			&& this.sigdesc.equals(castOther.sigdesc);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.sigcode.hashCode();
		hash = hash * prime + this.sigdesc.hashCode();
		
		return hash;
    }
}