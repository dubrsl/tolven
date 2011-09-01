package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_extvocab_relateddxid database table.
 * 
 */
@Embeddable
public class FdbExtvocabRelateddxidPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String vocabtypecode;

	private String hexid;

	private String relateddxid;

	private String clinicalcode;

    public FdbExtvocabRelateddxidPK() {
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
	public String getRelateddxid() {
		return this.relateddxid;
	}
	public void setRelateddxid(String relateddxid) {
		this.relateddxid = relateddxid;
	}
	public String getClinicalcode() {
		return this.clinicalcode;
	}
	public void setClinicalcode(String clinicalcode) {
		this.clinicalcode = clinicalcode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbExtvocabRelateddxidPK)) {
			return false;
		}
		FdbExtvocabRelateddxidPK castOther = (FdbExtvocabRelateddxidPK)other;
		return 
			this.vocabtypecode.equals(castOther.vocabtypecode)
			&& this.hexid.equals(castOther.hexid)
			&& this.relateddxid.equals(castOther.relateddxid)
			&& this.clinicalcode.equals(castOther.clinicalcode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.vocabtypecode.hashCode();
		hash = hash * prime + this.hexid.hashCode();
		hash = hash * prime + this.relateddxid.hashCode();
		hash = hash * prime + this.clinicalcode.hashCode();
		
		return hash;
    }
}