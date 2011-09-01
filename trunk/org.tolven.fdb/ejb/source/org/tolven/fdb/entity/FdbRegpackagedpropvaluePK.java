package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_regpackagedpropvalue database table.
 * 
 */
@Embeddable
public class FdbRegpackagedpropvaluePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String ipdid;

	private String propertyname;

	private Integer sequencenumber;

    public FdbRegpackagedpropvaluePK() {
    }
	public String getIpdid() {
		return this.ipdid;
	}
	public void setIpdid(String ipdid) {
		this.ipdid = ipdid;
	}
	public String getPropertyname() {
		return this.propertyname;
	}
	public void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}
	public Integer getSequencenumber() {
		return this.sequencenumber;
	}
	public void setSequencenumber(Integer sequencenumber) {
		this.sequencenumber = sequencenumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbRegpackagedpropvaluePK)) {
			return false;
		}
		FdbRegpackagedpropvaluePK castOther = (FdbRegpackagedpropvaluePK)other;
		return 
			this.ipdid.equals(castOther.ipdid)
			&& this.propertyname.equals(castOther.propertyname)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.ipdid.hashCode();
		hash = hash * prime + this.propertyname.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}