package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_medcondextvocablink database table.
 * 
 */
@Embeddable
public class FdbMedcondextvocablinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String vocabtypecode;

	private String hexid;

	private String fdbdx;

    public FdbMedcondextvocablinkPK() {
    }
	public String getVocabtypecode() {
		return this.vocabtypecode;
	}
	public void setVocabtypecode(String vocabtypecode) {
		this.vocabtypecode = vocabtypecode;
	}
	public String getHexid() {
		return this.hexid;
	}
	public void setHexid(String hexid) {
		this.hexid = hexid;
	}
	public String getFdbdx() {
		return this.fdbdx;
	}
	public void setFdbdx(String fdbdx) {
		this.fdbdx = fdbdx;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbMedcondextvocablinkPK)) {
			return false;
		}
		FdbMedcondextvocablinkPK castOther = (FdbMedcondextvocablinkPK)other;
		return 
			this.vocabtypecode.equals(castOther.vocabtypecode)
			&& this.hexid.equals(castOther.hexid)
			&& this.fdbdx.equals(castOther.fdbdx);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.vocabtypecode.hashCode();
		hash = hash * prime + this.hexid.hashCode();
		hash = hash * prime + this.fdbdx.hashCode();
		
		return hash;
    }
}