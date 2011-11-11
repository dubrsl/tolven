package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_medcondext_hierarchy database table.
 * 
 */
@Entity
@Table(name="fdb_medcondext_hierarchy")
public class FdbMedcondextHierarchy implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMedcondextHierarchyPK id;

	private String broaderextvocabid;

	private String extvocabid;

    public FdbMedcondextHierarchy() {
    }

	public FdbMedcondextHierarchyPK getId() {
		return this.id;
	}

	public void setId(FdbMedcondextHierarchyPK id) {
		this.id = id;
	}
	
	public String getBroaderextvocabid() {
		return this.broaderextvocabid;
	}

	public void setBroaderextvocabid(String broaderextvocabid) {
		this.broaderextvocabid = broaderextvocabid;
	}

	public String getExtvocabid() {
		return this.extvocabid;
	}

	public void setExtvocabid(String extvocabid) {
		this.extvocabid = extvocabid;
	}

}