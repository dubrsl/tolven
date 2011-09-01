package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_monograph_dfim database table.
 * 
 */
@Entity
@Table(name="fdb_monograph_dfim")
public class FdbMonographDfim implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMonographDfimPK id;

	private String formatcode;

	private String linetext;

	private String sectioncode;

    public FdbMonographDfim() {
    }

	public FdbMonographDfimPK getId() {
		return this.id;
	}

	public void setId(FdbMonographDfimPK id) {
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