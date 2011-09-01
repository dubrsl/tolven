package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_etclassification_link database table.
 * 
 */
@Embeddable
public class FdbEtclassificationLinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer classid;

	private Integer linkedchild;

    public FdbEtclassificationLinkPK() {
    }
	public Integer getClassid() {
		return this.classid;
	}
	public void setClassid(Integer classid) {
		this.classid = classid;
	}
	public Integer getLinkedchild() {
		return this.linkedchild;
	}
	public void setLinkedchild(Integer linkedchild) {
		this.linkedchild = linkedchild;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbEtclassificationLinkPK)) {
			return false;
		}
		FdbEtclassificationLinkPK castOther = (FdbEtclassificationLinkPK)other;
		return 
			this.classid.equals(castOther.classid)
			&& this.linkedchild.equals(castOther.linkedchild);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.classid.hashCode();
		hash = hash * prime + this.linkedchild.hashCode();
		
		return hash;
    }
}