package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_refitem_msg_link database table.
 * 
 */
@Entity
@Table(name="fdb_refitem_msg_link")
public class FdbRefitemMsgLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbRefitemMsgLinkPK id;

    public FdbRefitemMsgLink() {
    }

	public FdbRefitemMsgLinkPK getId() {
		return this.id;
	}

	public void setId(FdbRefitemMsgLinkPK id) {
		this.id = id;
	}
	
}