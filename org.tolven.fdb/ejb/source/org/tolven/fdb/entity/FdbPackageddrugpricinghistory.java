package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_packageddrugpricinghistory database table.
 * 
 */
@Entity
@Table(name="fdb_packageddrugpricinghistory")
public class FdbPackageddrugpricinghistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPackageddrugpricinghistoryPK id;

	private BigDecimal price;

    public FdbPackageddrugpricinghistory() {
    }

	public FdbPackageddrugpricinghistoryPK getId() {
		return this.id;
	}

	public void setId(FdbPackageddrugpricinghistoryPK id) {
		this.id = id;
	}
	
	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}