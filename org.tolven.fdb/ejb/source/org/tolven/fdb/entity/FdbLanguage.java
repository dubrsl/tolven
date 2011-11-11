package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_language database table.
 * 
 */
@Entity
@Table(name="fdb_language")
public class FdbLanguage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer languageid;

	private String abbrev;

	private Integer altsearchalgorithm;

	private Integer baselangid;

	private Integer columnindex;

	private String description;

	private String regionaldescription;

    public FdbLanguage() {
    }

	public Integer getLanguageid() {
		return this.languageid;
	}

	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}

	public String getAbbrev() {
		return this.abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public Integer getAltsearchalgorithm() {
		return this.altsearchalgorithm;
	}

	public void setAltsearchalgorithm(Integer altsearchalgorithm) {
		this.altsearchalgorithm = altsearchalgorithm;
	}

	public Integer getBaselangid() {
		return this.baselangid;
	}

	public void setBaselangid(Integer baselangid) {
		this.baselangid = baselangid;
	}

	public Integer getColumnindex() {
		return this.columnindex;
	}

	public void setColumnindex(Integer columnindex) {
		this.columnindex = columnindex;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRegionaldescription() {
		return this.regionaldescription;
	}

	public void setRegionaldescription(String regionaldescription) {
		this.regionaldescription = regionaldescription;
	}

}