package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_medcondxref database table.
 * 
 */
@Entity
@Table(name="fdb_medcondxref")
public class FdbMedcondxref implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMedcondxrefPK id;

	private String vocabid;

	private String vocabid2;

    public FdbMedcondxref() {
    }

	public FdbMedcondxrefPK getId() {
		return this.id;
	}

	public void setId(FdbMedcondxrefPK id) {
		this.id = id;
	}
	
	public String getVocabid() {
		return this.vocabid;
	}

	public void setVocabid(String vocabid) {
		this.vocabid = vocabid;
	}

	public String getVocabid2() {
		return this.vocabid2;
	}

	public void setVocabid2(String vocabid2) {
		this.vocabid2 = vocabid2;
	}

}