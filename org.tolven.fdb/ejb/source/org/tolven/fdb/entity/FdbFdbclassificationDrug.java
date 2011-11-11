package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_fdbclassification_drugs database table.
 * 
 */
@Entity
@Table(name="fdb_fdbclassification_drugs")
public class FdbFdbclassificationDrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbFdbclassificationDrugPK id;

	private Integer commonuseind;

	private Integer defaultuseind;

    public FdbFdbclassificationDrug() {
    }

	public FdbFdbclassificationDrugPK getId() {
		return this.id;
	}

	public void setId(FdbFdbclassificationDrugPK id) {
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