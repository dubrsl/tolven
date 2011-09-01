package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_extclassification_idrugs database table.
 * 
 */
@Entity
@Table(name="fdb_extclassification_idrugs")
public class FdbExtclassificationIdrug implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbExtclassificationIdrugPK id;

	private Integer commonuseind;

	private Integer defaultuseind;

    public FdbExtclassificationIdrug() {
    }

	public FdbExtclassificationIdrugPK getId() {
		return this.id;
	}

	public void setId(FdbExtclassificationIdrugPK id) {
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