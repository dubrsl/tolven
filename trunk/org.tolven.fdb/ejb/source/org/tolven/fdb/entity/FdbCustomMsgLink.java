package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_msg_link database table.
 * 
 */
@Entity
@Table(name="fdb_custom_msg_link")
public class FdbCustomMsgLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomMsgLinkPK id;

	private Integer conceptidnumeric;

    public FdbCustomMsgLink() {
    }

	public FdbCustomMsgLinkPK getId() {
		return this.id;
	}

	public void setId(FdbCustomMsgLinkPK id) {
		this.id = id;
	}
	
	public Integer getConceptidnumeric() {
		return this.conceptidnumeric;
	}

	public void setConceptidnumeric(Integer conceptidnumeric) {
		this.conceptidnumeric = conceptidnumeric;
	}

}