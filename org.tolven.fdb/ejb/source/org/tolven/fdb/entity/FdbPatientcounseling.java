package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_patientcounseling database table.
 * 
 */
@Entity
@Table(name="fdb_patientcounseling")
public class FdbPatientcounseling implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPatientcounselingPK id;

	private String patmessage1;

	private String patmessage2;

	private String profmessage1;

	private String profmessage2;

    public FdbPatientcounseling() {
    }

	public FdbPatientcounselingPK getId() {
		return this.id;
	}

	public void setId(FdbPatientcounselingPK id) {
		this.id = id;
	}
	
	public String getPatmessage1() {
		return this.patmessage1;
	}

	public void setPatmessage1(String patmessage1) {
		this.patmessage1 = patmessage1;
	}

	public String getPatmessage2() {
		return this.patmessage2;
	}

	public void setPatmessage2(String patmessage2) {
		this.patmessage2 = patmessage2;
	}

	public String getProfmessage1() {
		return this.profmessage1;
	}

	public void setProfmessage1(String profmessage1) {
		this.profmessage1 = profmessage1;
	}

	public String getProfmessage2() {
		return this.profmessage2;
	}

	public void setProfmessage2(String profmessage2) {
		this.profmessage2 = profmessage2;
	}

}