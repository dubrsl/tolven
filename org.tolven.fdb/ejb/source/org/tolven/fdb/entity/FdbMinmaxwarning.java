package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_minmaxwarnings database table.
 * 
 */
@Entity
@Table(name="fdb_minmaxwarnings")
public class FdbMinmaxwarning implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMinmaxwarningPK id;

	private String warningcode;

    public FdbMinmaxwarning() {
    }

	public FdbMinmaxwarningPK getId() {
		return this.id;
	}

	public void setId(FdbMinmaxwarningPK id) {
		this.id = id;
	}
	
	public String getWarningcode() {
		return this.warningcode;
	}

	public void setWarningcode(String warningcode) {
		this.warningcode = warningcode;
	}

}