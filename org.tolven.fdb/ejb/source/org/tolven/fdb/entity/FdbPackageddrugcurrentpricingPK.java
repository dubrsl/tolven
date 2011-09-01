package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_packageddrugcurrentpricing database table.
 * 
 */
@Embeddable
public class FdbPackageddrugcurrentpricingPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String pmid;

	private String pricetypecode;

    public FdbPackageddrugcurrentpricingPK() {
    }
	public String getPmid() {
		return this.pmid;
	}
	public void setPmid(String pmid) {
		this.pmid = pmid;
	}
	public String getPricetypecode() {
		return this.pricetypecode;
	}
	public void setPricetypecode(String pricetypecode) {
		this.pricetypecode = pricetypecode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPackageddrugcurrentpricingPK)) {
			return false;
		}
		FdbPackageddrugcurrentpricingPK castOther = (FdbPackageddrugcurrentpricingPK)other;
		return 
			this.pmid.equals(castOther.pmid)
			&& this.pricetypecode.equals(castOther.pricetypecode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pmid.hashCode();
		hash = hash * prime + this.pricetypecode.hashCode();
		
		return hash;
    }
}