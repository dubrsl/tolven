package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ahfsclassification_drugs database table.
 * 
 */
@Entity
@Table(name="fdb_ahfsclassification_drugs")
public class FdbAhfsclassificationDrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbAhfsclassificationDrugPK id;

	private Integer commonuseind;

	private Integer defaultuseind;

    public FdbAhfsclassificationDrug() {
    }

	public FdbAhfsclassificationDrugPK getId() {
		return this.id;
	}

	public void setId(FdbAhfsclassificationDrugPK id) {
		this.id = id;
	}
	
	public Integer getCommonuseind() {
		return this.commonuseind;
	}

	public void setCommonuseind(Integer commonuseind) {
		this.commonuseind = commonuseind;
	}

	public Integer getDefaultuseind() {
		return this.defaultuseind;
	}

	public void setDefaultuseind(Integer defaultuseind) {
		this.defaultuseind = defaultuseind;
	}

}