package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_custom_class database table.
 * 
 */
@Embeddable
public class FdbCustomClassPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String category;

	private String classid;

    public FdbCustomClassPK() {
    }
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
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
		if (!(other instanceof FdbCustomClassPK)) {
			return false;
		}
		FdbCustomClassPK castOther = (FdbCustomClassPK)other;
		return 
			this.category.equals(castOther.category)
			&& this.classid.equals(castOther.classid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.category.hashCode();
		hash = hash * prime + this.classid.hashCode();
		
		return hash;
    }
}