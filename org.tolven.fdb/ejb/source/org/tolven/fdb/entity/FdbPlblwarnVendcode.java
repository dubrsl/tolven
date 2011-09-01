package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_plblwarn_vendcode database table.
 * 
 */
@Entity
@Table(name="fdb_plblwarn_vendcode")
public class FdbPlblwarnVendcode implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPlblwarnVendcodePK id;

	private String vendorcode;

    public FdbPlblwarnVendcode() {
    }

	public FdbPlblwarnVendcodePK getId() {
		return this.id;
	}

	public void setId(FdbPlblwarnVendcodePK id) {
		this.id = id;
	}
	
	public String getVendorcode() {
		return this.vendorcode;
	}

	public void setVendorcode(String vendorcode) {
		this.vendorcode = vendorcode;
	}

}