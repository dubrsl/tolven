package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_doseform database table.
 * 
 */
@Entity
@Table(name="fdb_doseform")
public class FdbDoseform implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer dfid;

	private String abbrev;

	private String description;

    public FdbDoseform() {
    }

	public Integer getDfid() {
		return this.dfid;
	}

	public void setDfid(Integer dfid) {
		this.dfid = dfid;
	}

	public String getAbbrev() {
		return this.abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}