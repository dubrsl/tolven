package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_extclassification_link database table.
 * 
 */
@Embeddable
public class FdbExtclassificationLinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String classtypecode;

	private String classid;

	private String linkedchild;

    public FdbExtclassificationLinkPK() {
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
	public String getLinkedchild() {
		return this.linkedchild;
	}
	public void setLinkedchild(String linkedchild) {
		this.linkedchild = linkedchild;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbExtclassificationLinkPK)) {
			return false;
		}
		FdbExtclassificationLinkPK castOther = (FdbExtclassificationLinkPK)other;
		return 
			this.classtypecode.equals(castOther.classtypecode)
			&& this.classid.equals(castOther.classid)
			&& this.linkedchild.equals(castOther.linkedchild);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.classtypecode.hashCode();
		hash = hash * prime + this.classid.hashCode();
		hash = hash * prime + this.linkedchild.hashCode();
		
		return hash;
    }
}