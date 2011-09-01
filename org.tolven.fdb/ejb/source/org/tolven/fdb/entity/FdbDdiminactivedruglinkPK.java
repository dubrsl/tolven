package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_ddiminactivedruglink database table.
 * 
 */
@Embeddable
public class FdbDdiminactivedruglinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String pmid;

	private Integer interactionid;

    public FdbDdiminactivedruglinkPK() {
    }
	public String getPmid() {
		return this.pmid;
	}
	public void setPmid(String pmid) {
		this.pmid = pmid;
	}
	public Integer getInteractionid() {
		return this.interactionid;
	}
	public void setInteractionid(Integer interactionid) {
		this.interactionid = interactionid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDdiminactivedruglinkPK)) {
			return false;
		}
		FdbDdiminactivedruglinkPK castOther = (FdbDdiminactivedruglinkPK)other;
		return 
			this.pmid.equals(castOther.pmid)
			&& this.interactionid.equals(castOther.interactionid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pmid.hashCode();
		hash = hash * prime + this.interactionid.hashCode();
		
		return hash;
    }
}