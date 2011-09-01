package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_generic_dispensable_temp database table.
 * 
 */
@Entity
@Table(name="fdb_generic_dispensable_temp")
public class FdbGenericDispensableTemp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer tempid;

	private Integer gcnseqno;

    public FdbGenericDispensableTemp() {
    }

	public Integer getTempid() {
		return this.tempid;
	}

	public void setTempid(Integer tempid) {
		this.tempid = tempid;
	}

	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}

}