package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_etclassification_drugs database table.
 * 
 */
@Entity
@Table(name="fdb_etclassification_drugs")
public class FdbEtclassificationDrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbEtclassificationDrugPK id;

	private Integer commonuseind;

	private Integer defaultuseind;

    public FdbEtclassificationDrug() {
    }

	public FdbEtclassificationDrugPK getId() {
		return this.id;
	}

	public void setId(FdbEtclassificationDrugPK id) {
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