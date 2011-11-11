package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_medcondxref database table.
 * 
 */
@Embeddable
public class FdbMedcondxrefPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String vocabtypecode;

	private String hexid;

	private String vocabtypecode2;

	private String hexid2;

    public FdbMedcondxrefPK() {
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
	public String getVocabtypecode2() {
		return this.vocabtypecode2;
	}
	public void setVocabtypecode2(String vocabtypecode2) {
		this.vocabtypecode2 = vocabtypecode2;
	}
	public String getHexid2() {
		return this.hexid2;
	}
	public void setHexid2(String hexid2) {
		this.hexid2 = hexid2;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbMedcondxrefPK)) {
			return false;
		}
		FdbMedcondxrefPK castOther = (FdbMedcondxrefPK)other;
		return 
			this.vocabtypecode.equals(castOther.vocabtypecode)
			&& this.hexid.equals(castOther.hexid)
			&& this.vocabtypecode2.equals(castOther.vocabtypecode2)
			&& this.hexid2.equals(castOther.hexid2);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.vocabtypecode.hashCode();
		hash = hash * prime + this.hexid.hashCode();
		hash = hash * prime + this.vocabtypecode2.hashCode();
		hash = hash * prime + this.hexid2.hashCode();
		
		return hash;
    }
}