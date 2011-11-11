package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_manufdrugpropvalue database table.
 * 
 */
@Embeddable
public class FdbManufdrugpropvaluePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String imdid;

	private String propertyname;

	private Integer sequencenumber;

    public FdbManufdrugpropvaluePK() {
    }
	public String getImdid() {
		return this.imdid;
	}
	public void setImdid(String imdid) {
		this.imdid = imdid;
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
		if (!(other instanceof FdbManufdrugpropvaluePK)) {
			return false;
		}
		FdbManufdrugpropvaluePK castOther = (FdbManufdrugpropvaluePK)other;
		return 
			this.imdid.equals(castOther.imdid)
			&& this.propertyname.equals(castOther.propertyname)
			&& this.sequencenumber.equals(castOther.sequencenumber);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.imdid.hashCode();
		hash = hash * prime + this.propertyname.hashCode();
		hash = hash * prime + this.sequencenumber.hashCode();
		
		return hash;
    }
}