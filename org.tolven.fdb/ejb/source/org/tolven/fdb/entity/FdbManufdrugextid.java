package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_manufdrugextid database table.
 * 
 */
@Entity
@Table(name="fdb_manufdrugextid")
public class FdbManufdrugextid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbManufdrugextidPK id;

	private String extid;

	private String imdid;

    public FdbManufdrugextid() {
    }

	public FdbManufdrugextidPK getId() {
		return this.id;
	}

	public void setId(FdbManufdrugextidPK id) {
		this.id = id;
	}
	
	public String getExtid() {
		return this.extid;
	}

	public void setExtid(String extid) {
		this.extid = extid;
	}

	public String getImdid() {
		return this.imdid;
	}

	public void setImdid(String imdid) {
		this.imdid = imdid;
	}

}