package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_doseunitsconversion database table.
 * 
 */
@Entity
@Table(name="fdb_doseunitsconversion")
public class FdbDoseunitsconversion implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDoseunitsconversionPK id;

	private BigDecimal conversionfactor;

    public FdbDoseunitsconversion() {
    }

	public FdbDoseunitsconversionPK getId() {
		return this.id;
	}

	public void setId(FdbDoseunitsconversionPK id) {
		this.id = id;
	}
	
	public BigDecimal getConversionfactor() {
		return this.conversionfactor;
	}

	public void setConversionfactor(BigDecimal conversionfactor) {
		this.conversionfactor = conversionfactor;
	}

}