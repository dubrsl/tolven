package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_custom_packageddrugpricing database table.
 * 
 */
@Entity
@Table(name="fdb_custom_packageddrugpricing")
public class FdbCustomPackageddrugpricing implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomPackageddrugpricingPK id;

	private Integer currentpriceind;

	private BigDecimal price;

    public FdbCustomPackageddrugpricing() {
    }

	public FdbCustomPackageddrugpricingPK getId() {
		return this.id;
	}

	public void setId(FdbCustomPackageddrugpricingPK id) {
		this.id = id;
	}
	
	public Integer getCurrentpriceind() {
		return this.currentpriceind;
	}

	public void setCurrentpriceind(Integer currentpriceind) {
		this.currentpriceind = currentpriceind;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}