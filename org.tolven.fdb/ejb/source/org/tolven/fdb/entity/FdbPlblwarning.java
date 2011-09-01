package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_plblwarnings database table.
 * 
 */
@Entity
@Table(name="fdb_plblwarnings")
public class FdbPlblwarning implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPlblwarningPK id;

	private String agecode;

	private String gendercode;

	private String informationalcode;

	private Integer pregnancyind;

	private String warningtext;

    public FdbPlblwarning() {
    }

	public FdbPlblwarningPK getId() {
		return this.id;
	}

	public void setId(FdbPlblwarningPK id) {
		this.id = id;
	}
	
	public String getAgecode() {
		return this.agecode;
	}

	public void setAgecode(String agecode) {
		this.agecode = agecode;
	}

	public String getGendercode() {
		return this.gendercode;
	}

	public void setGendercode(String gendercode) {
		this.gendercode = gendercode;
	}

	public String getInformationalcode() {
		return this.informationalcode;
	}

	public void setInformationalcode(String informationalcode) {
		this.informationalcode = informationalcode;
	}

	public Integer getPregnancyind() {
		return this.pregnancyind;
	}

	public void setPregnancyind(Integer pregnancyind) {
		this.pregnancyind = pregnancyind;
	}

	public String getWarningtext() {
		return this.warningtext;
	}

	public void setWarningtext(String warningtext) {
		this.warningtext = warningtext;
	}

}