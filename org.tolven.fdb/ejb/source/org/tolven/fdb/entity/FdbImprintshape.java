package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_imprintshape database table.
 * 
 */
@Entity
@Table(name="fdb_imprintshape")
public class FdbImprintshape implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer shapeid;

	private String description;

	private Integer paletteid;

    public FdbImprintshape() {
    }

	public Integer getShapeid() {
		return this.shapeid;
	}

	public void setShapeid(Integer shapeid) {
		this.shapeid = shapeid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPaletteid() {
		return this.paletteid;
	}

	public void setPaletteid(Integer paletteid) {
		this.paletteid = paletteid;
	}

}