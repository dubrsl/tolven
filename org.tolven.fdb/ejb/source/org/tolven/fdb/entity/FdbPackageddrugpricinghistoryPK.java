package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_packageddrugpricinghistory database table.
 * 
 */
@Embeddable
public class FdbPackageddrugpricinghistoryPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String pmid;

	private String pricetypecode;

	private String effectivedate;

    public FdbPackageddrugpricinghistoryPK() {
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
	public String getEffectivedate() {
		return this.effectivedate;
	}
	public void setEffectivedate(String effectivedate) {
		this.effectivedate = effectivedate;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPackageddrugpricinghistoryPK)) {
			return false;
		}
		FdbPackageddrugpricinghistoryPK castOther = (FdbPackageddrugpricinghistoryPK)other;
		return 
			this.pmid.equals(castOther.pmid)
			&& this.pricetypecode.equals(castOther.pricetypecode)
			&& this.effectivedate.equals(castOther.effectivedate);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pmid.hashCode();
		hash = hash * prime + this.pricetypecode.hashCode();
		hash = hash * prime + this.effectivedate.hashCode();
		
		return hash;
    }
}