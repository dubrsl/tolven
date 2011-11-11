package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_imprintcolors database table.
 * 
 */
@Entity
@Table(name="fdb_imprintcolors")
public class FdbImprintcolor implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer colorid;

	private String description;

	private Integer paletteid;

    public FdbImprintcolor() {
    }

	public Integer getColorid() {
		return this.colorid;
	}

	public void setColorid(Integer colorid) {
		this.colorid = colorid;
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