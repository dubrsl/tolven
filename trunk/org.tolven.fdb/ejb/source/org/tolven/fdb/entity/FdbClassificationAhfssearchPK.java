package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_classification_ahfssearch database table.
 * 
 */
@Embeddable
public class FdbClassificationAhfssearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String classid;

    public FdbClassificationAhfssearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getClassid() {
		return this.classid;
	}
	public void setClassid(String classid) {
		this.classid = classid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbClassificationAhfssearchPK)) {
			return false;
		}
		FdbClassificationAhfssearchPK castOther = (FdbClassificationAhfssearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.classid.equals(castOther.classid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.classid.hashCode();
		
		return hash;
    }
}