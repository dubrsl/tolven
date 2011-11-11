package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_doseunitsconversion database table.
 * 
 */
@Embeddable
public class FdbDoseunitsconversionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String units1;

	private String units2;

    public FdbDoseunitsconversionPK() {
    }
	public String getUnits1() {
		return this.units1;
	}
	public void setUnits1(String units1) {
		this.units1 = units1;
	}
	public String getUnits2() {
		return this.units2;
	}
	public void setUnits2(String units2) {
		this.units2 = units2;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbDoseunitsconversionPK)) {
			return false;
		}
		FdbDoseunitsconversionPK castOther = (FdbDoseunitsconversionPK)other;
		return 
			this.units1.equals(castOther.units1)
			&& this.units2.equals(castOther.units2);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.units1.hashCode();
		hash = hash * prime + this.units2.hashCode();
		
		return hash;
    }
}