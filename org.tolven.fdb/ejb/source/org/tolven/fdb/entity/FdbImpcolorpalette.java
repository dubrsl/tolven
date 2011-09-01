package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_impcolorpalette database table.
 * 
 */
@Entity
@Table(name="fdb_impcolorpalette")
public class FdbImpcolorpalette implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer paletteid;

	private String description;

    public FdbImpcolorpalette() {
    }

	public Integer getPaletteid() {
		return this.paletteid;
	}

	public void setPaletteid(Integer paletteid) {
		this.paletteid = paletteid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}