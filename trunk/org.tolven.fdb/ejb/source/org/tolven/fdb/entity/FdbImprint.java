package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_imprint database table.
 * 
 */
@Entity
@Table(name="fdb_imprint")
public class FdbImprint implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbImprintPK id;

	private String claritycode;

	private String coatingcode;

	private String colorpaletteidlist;

	private Integer currentimprintind;

	private String flavorcode;

	private String formcode;

	private String imprint1;

	private String imprint2;

	private String scoringcode;

	private String searchdescription;

	private Integer shapeid;

    public FdbImprint() {
    }

	public FdbImprintPK getId() {
		return this.id;
	}

	public void setId(FdbImprintPK id) {
		this.id = id;
	}
	
	public String getClaritycode() {
		return this.claritycode;
	}

	public void setClaritycode(String claritycode) {
		this.claritycode = claritycode;
	}

	public String getCoatingcode() {
		return this.coatingcode;
	}

	public void setCoatingcode(String coatingcode) {
		this.coatingcode = coatingcode;
	}

	public String getColorpaletteidlist() {
		return this.colorpaletteidlist;
	}

	public void setColorpaletteidlist(String colorpaletteidlist) {
		this.colorpaletteidlist = colorpaletteidlist;
	}

	public Integer getCurrentimprintind() {
		return this.currentimprintind;
	}

	public void setCurrentimprintind(Integer currentimprintind) {
		this.currentimprintind = currentimprintind;
	}

	public String getFlavorcode() {
		return this.flavorcode;
	}

	public void setFlavorcode(String flavorcode) {
		this.flavorcode = flavorcode;
	}

	public String getFormcode() {
		return this.formcode;
	}

	public void setFormcode(String formcode) {
		this.formcode = formcode;
	}

	public String getImprint1() {
		return this.imprint1;
	}

	public void setImprint1(String imprint1) {
		this.imprint1 = imprint1;
	}

	public String getImprint2() {
		return this.imprint2;
	}

	public void setImprint2(String imprint2) {
		this.imprint2 = imprint2;
	}

	public String getScoringcode() {
		return this.scoringcode;
	}

	public void setScoringcode(String scoringcode) {
		this.scoringcode = scoringcode;
	}

	public String getSearchdescription() {
		return this.searchdescription;
	}

	public void setSearchdescription(String searchdescription) {
		this.searchdescription = searchdescription;
	}

	public Integer getShapeid() {
		return this.shapeid;
	}

	public void setShapeid(Integer shapeid) {
		this.shapeid = shapeid;
	}

}