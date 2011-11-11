package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_subset_drugvalues database table.
 * 
 */
@Entity
@Table(name="fdb_subset_drugvalues")
public class FdbSubsetDrugvalue implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbSubsetDrugvaluePK id;

	private Integer conceptidnumeric;

	private String customvalue1;

	private String customvalue2;

	private String customvalue3;

	private String customvalue4;

    public FdbSubsetDrugvalue() {
    }

	public FdbSubsetDrugvaluePK getId() {
		return this.id;
	}

	public void setId(FdbSubsetDrugvaluePK id) {
		this.id = id;
	}
	
	public Integer getConceptidnumeric() {
		return this.conceptidnumeric;
	}

	public void setConceptidnumeric(Integer conceptidnumeric) {
		this.conceptidnumeric = conceptidnumeric;
	}

	public String getCustomvalue1() {
		return this.customvalue1;
	}

	public void setCustomvalue1(String customvalue1) {
		this.customvalue1 = customvalue1;
	}

	public String getCustomvalue2() {
		return this.customvalue2;
	}

	public void setCustomvalue2(String customvalue2) {
		this.customvalue2 = customvalue2;
	}

	public String getCustomvalue3() {
		return this.customvalue3;
	}

	public void setCustomvalue3(String customvalue3) {
		this.customvalue3 = customvalue3;
	}

	public String getCustomvalue4() {
		return this.customvalue4;
	}

	public void setCustomvalue4(String customvalue4) {
		this.customvalue4 = customvalue4;
	}

}