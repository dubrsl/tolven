package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_extvocab_relateddxid database table.
 * 
 */
@Entity
@Table(name="fdb_extvocab_relateddxid")
public class FdbExtvocabRelateddxid implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbExtvocabRelateddxidPK id;

	private String navcode;

	private String prefhexid;

    public FdbExtvocabRelateddxid() {
    }

	public FdbExtvocabRelateddxidPK getId() {
		return this.id;
	}

	public void setId(FdbExtvocabRelateddxidPK id) {
		this.id = id;
	}
	
	public String getNavcode() {
		return this.navcode;
	}

	public void setNavcode(String navcode) {
		this.navcode = navcode;
	}

	public String getPrefhexid() {
		return this.prefhexid;
	}

	public void setPrefhexid(String prefhexid) {
		this.prefhexid = prefhexid;
	}

}