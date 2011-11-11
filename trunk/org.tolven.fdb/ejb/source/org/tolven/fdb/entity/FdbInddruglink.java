package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_inddruglink database table.
 * 
 */
@Entity
@Table(name="fdb_inddruglink")
public class FdbInddruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbInddruglinkPK id;

	private Integer gcnseqno;

	private Integer rtdfgenid;

	private Integer rtgenid;

    public FdbInddruglink() {
    }

	public FdbInddruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbInddruglinkPK id) {
		this.id = id;
	}
	
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}

	public Integer getRtdfgenid() {
		return this.rtdfgenid;
	}

	public void setRtdfgenid(Integer rtdfgenid) {
		this.rtdfgenid = rtdfgenid;
	}

	public Integer getRtgenid() {
		return this.rtgenid;
	}

	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}

}