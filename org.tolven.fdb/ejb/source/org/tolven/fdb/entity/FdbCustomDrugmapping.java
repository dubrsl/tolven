package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_drugmapping database table.
 * 
 */
@Entity
@Table(name="fdb_custom_drugmapping")
public class FdbCustomDrugmapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomDrugmappingPK id;

	private Integer conceptidnumeric;

	private String conceptidstring;

	private Integer concepttype;

    public FdbCustomDrugmapping() {
    }

	public FdbCustomDrugmappingPK getId() {
		return this.id;
	}

	public void setId(FdbCustomDrugmappingPK id) {
		this.id = id;
	}
	
	public Integer getConceptidnumeric() {
		return this.conceptidnumeric;
	}

	public void setConceptidnumeric(Integer conceptidnumeric) {
		this.conceptidnumeric = conceptidnumeric;
	}

	public String getConceptidstring() {
		return this.conceptidstring;
	}

	public void setConceptidstring(String conceptidstring) {
		this.conceptidstring = conceptidstring;
	}

	public Integer getConcepttype() {
		return this.concepttype;
	}

	public void setConcepttype(Integer concepttype) {
		this.concepttype = concepttype;
	}

}