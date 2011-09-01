package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_medcondextvocab database table.
 * 
 */
@Embeddable
public class FdbMedcondextvocabPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String vocabtypecode;

	private String hexid;

    public FdbMedcondextvocabPK() {
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbMedcondextvocabPK)) {
			return false;
		}
		FdbMedcondextvocabPK castOther = (FdbMedcondextvocabPK)other;
		return 
			this.vocabtypecode.equals(castOther.vocabtypecode)
			&& this.hexid.equals(castOther.hexid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.vocabtypecode.hashCode();
		hash = hash * prime + this.hexid.hashCode();
		
		return hash;
    }
}