package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_rdfmid_rtdfgenid database table.
 * 
 */
@Entity
@Table(name="fdb_rdfmid_rtdfgenid")
public class FdbRdfmidRtdfgenid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbRdfmidRtdfgenidPK id;

    public FdbRdfmidRtdfgenid() {
    }

	public FdbRdfmidRtdfgenidPK getId() {
		return this.id;
	}

	public void setId(FdbRdfmidRtdfgenidPK id) {
		this.id = id;
	}
	
}