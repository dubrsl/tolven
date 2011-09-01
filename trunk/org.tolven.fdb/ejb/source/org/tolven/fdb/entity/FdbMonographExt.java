package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_monograph_ext database table.
 * 
 */
@Entity
@Table(name="fdb_monograph_ext")
public class FdbMonographExt implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMonographExtPK id;

	private String formatcode;

	private String linetext;

	private String sectioncode;

    public FdbMonographExt() {
    }

	public FdbMonographExtPK getId() {
		return this.id;
	}

	public void setId(FdbMonographExtPK id) {
		this.id = id;
	}
	
	public String getFormatcode() {
		return this.formatcode;
	}

	public void setFormatcode(String formatcode) {
		this.formatcode = formatcode;
	}

	public String getLinetext() {
		return this.linetext;
	}

	public void setLinetext(String linetext) {
		this.linetext = linetext;
	}

	public String getSectioncode() {
		return this.sectioncode;
	}

	public void setSectioncode(String sectioncode) {
		this.sectioncode = sectioncode;
	}

}