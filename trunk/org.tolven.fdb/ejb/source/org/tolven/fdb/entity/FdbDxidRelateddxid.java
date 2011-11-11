package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_dxid_relateddxid database table.
 * 
 */
@Entity
@Table(name="fdb_dxid_relateddxid")
public class FdbDxidRelateddxid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDxidRelateddxidPK id;

	private String fdbdx;

	private String navcode;

    public FdbDxidRelateddxid() {
    }

	public FdbDxidRelateddxidPK getId() {
		return this.id;
	}

	public void setId(FdbDxidRelateddxidPK id) {
		this.id = id;
	}
	
	public String getFdbdx() {
		return this.fdbdx;
	}

	public void setFdbdx(String fdbdx) {
		this.fdbdx = fdbdx;
	}

	public String getNavcode() {
		return this.navcode;
	}

	public void setNavcode(String navcode) {
		this.navcode = navcode;
	}

}