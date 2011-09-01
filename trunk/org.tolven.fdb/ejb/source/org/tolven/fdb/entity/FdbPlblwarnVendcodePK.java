package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_plblwarn_vendcode database table.
 * 
 */
@Embeddable
public class FdbPlblwarnVendcodePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String lwid;

	private String vendversioncode;

    public FdbPlblwarnVendcodePK() {
    }
	public String getLwid() {
		return this.lwid;
	}
	public void setLwid(String lwid) {
		this.lwid = lwid;
	}
	public String getVendversioncode() {
		return this.vendversioncode;
	}
	public void setVendversioncode(String vendversioncode) {
		this.vendversioncode = vendversioncode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbPlblwarnVendcodePK)) {
			return false;
		}
		FdbPlblwarnVendcodePK castOther = (FdbPlblwarnVendcodePK)other;
		return 
			this.lwid.equals(castOther.lwid)
			&& this.vendversioncode.equals(castOther.vendversioncode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.lwid.hashCode();
		hash = hash * prime + this.vendversioncode.hashCode();
		
		return hash;
    }
}