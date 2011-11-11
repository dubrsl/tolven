package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_regpackagedextid database table.
 * 
 */
@Entity
@Table(name="fdb_regpackagedextid")
public class FdbRegpackagedextid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbRegpackagedextidPK id;

	private String extid;

	private String ipdid;

    public FdbRegpackagedextid() {
    }

	public FdbRegpackagedextidPK getId() {
		return this.id;
	}

	public void setId(FdbRegpackagedextidPK id) {
		this.id = id;
	}
	
	public String getExtid() {
		return this.extid;
	}

	public void setExtid(String extid) {
		this.extid = extid;
	}

	public String getIpdid() {
		return this.ipdid;
	}

	public void setIpdid(String ipdid) {
		this.ipdid = ipdid;
	}

}