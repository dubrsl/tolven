package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_dispensable_labeler database table.
 * 
 */
@Entity
@Table(name="fdb_dispensable_labeler")
public class FdbDispensableLabeler implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDispensableLabelerPK id;

	private String refrepackagercode;

    public FdbDispensableLabeler() {
    }

	public FdbDispensableLabelerPK getId() {
		return this.id;
	}

	public void setId(FdbDispensableLabelerPK id) {
		this.id = id;
	}
	
	public String getRefrepackagercode() {
		return this.refrepackagercode;
	}

	public void setRefrepackagercode(String refrepackagercode) {
		this.refrepackagercode = refrepackagercode;
	}

}