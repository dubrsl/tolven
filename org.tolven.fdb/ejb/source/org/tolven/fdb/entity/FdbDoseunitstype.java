package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_doseunitstype database table.
 * 
 */
@Entity
@Table(name="fdb_doseunitstype")
public class FdbDoseunitstype implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String units;

	private Integer unitstype;

    public FdbDoseunitstype() {
    }

	public String getUnits() {
		return this.units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Integer getUnitstype() {
		return this.unitstype;
	}

	public void setUnitstype(Integer unitstype) {
		this.unitstype = unitstype;
	}

}