package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_classification_extsearch database table.
 * 
 */
@Embeddable
public class FdbClassificationExtsearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer languageid;

	private String classtypecode;

	private String classid;

    public FdbClassificationExtsearchPK() {
    }
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public String getClasstypecode() {
		return this.classtypecode;
	}
	public void setClasstypecode(String classtypecode) {
		this.classtypecode = classtypecode;
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
		if (!(other instanceof FdbClassificationExtsearchPK)) {
			return false;
		}
		FdbClassificationExtsearchPK castOther = (FdbClassificationExtsearchPK)other;
		return 
			this.languageid.equals(castOther.languageid)
			&& this.classtypecode.equals(castOther.classtypecode)
			&& this.classid.equals(castOther.classid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.classtypecode.hashCode();
		hash = hash * prime + this.classid.hashCode();
		
		return hash;
    }
}