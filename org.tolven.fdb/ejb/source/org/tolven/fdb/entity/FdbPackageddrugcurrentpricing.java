package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_packageddrugcurrentpricing database table.
 * 
 */
@Entity
@Table(name="fdb_packageddrugcurrentpricing")
public class FdbPackageddrugcurrentpricing implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPackageddrugcurrentpricingPK id;

	private String currenteffectivedate;

	private BigDecimal price;

    public FdbPackageddrugcurrentpricing() {
    }

	public FdbPackageddrugcurrentpricingPK getId() {
		return this.id;
	}

	public void setId(FdbPackageddrugcurrentpricingPK id) {
		this.id = id;
	}
	
	public String getCurrenteffectivedate() {
		return this.currenteffectivedate;
	}

	public void setCurrenteffectivedate(String currenteffectivedate) {
		this.currenteffectivedate = currenteffectivedate;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}