package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_impdesccolors database table.
 * 
 */
@Embeddable
public class FdbImpdesccolorPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String pmid;

	private String startdate;

	private Integer colorid;

    public FdbImpdesccolorPK() {
    }
	public String getPmid() {
		return this.pmid;
	}
	public void setPmid(String pmid) {
		this.pmid = pmid;
	}
	public String getStartdate() {
		return this.startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public Integer getColorid() {
		return this.colorid;
	}
	public void setColorid(Integer colorid) {
		this.colorid = colorid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbImpdesccolorPK)) {
			return false;
		}
		FdbImpdesccolorPK castOther = (FdbImpdesccolorPK)other;
		return 
			this.pmid.equals(castOther.pmid)
			&& this.startdate.equals(castOther.startdate)
			&& this.colorid.equals(castOther.colorid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pmid.hashCode();
		hash = hash * prime + this.startdate.hashCode();
		hash = hash * prime + this.colorid.hashCode();
		
		return hash;
    }
}