package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_medcondextvocablink database table.
 * 
 */
@Entity
@Table(name="fdb_medcondextvocablink")
public class FdbMedcondextvocablink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMedcondextvocablinkPK id;

	private String dxid;

	private String extvocabid;

    public FdbMedcondextvocablink() {
    }

	public FdbMedcondextvocablinkPK getId() {
		return this.id;
	}

	public void setId(FdbMedcondextvocablinkPK id) {
		this.id = id;
	}
	
	public String getDxid() {
		return this.dxid;
	}

	public void setDxid(String dxid) {
		this.dxid = dxid;
	}

	public String getExtvocabid() {
		return this.extvocabid;
	}

	public void setExtvocabid(String extvocabid) {
		this.extvocabid = extvocabid;
	}

}