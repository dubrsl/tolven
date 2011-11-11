package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_dtdruglink database table.
 * 
 */
@Entity
@Table(name="fdb_dtdruglink")
public class FdbDtdruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDtdruglinkPK id;

	private Integer rtgenid;

    public FdbDtdruglink() {
    }

	public FdbDtdruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbDtdruglinkPK id) {
		this.id = id;
	}
	
	public Integer getRtgenid() {
		return this.rtgenid;
	}

	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}

}