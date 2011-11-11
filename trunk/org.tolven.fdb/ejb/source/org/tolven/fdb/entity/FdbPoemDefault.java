package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_poem_default database table.
 * 
 */
@Entity
@Table(name="fdb_poem_default")
public class FdbPoemDefault implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer gcnseqno;

	private Integer orderstringid;

    public FdbPoemDefault() {
    }

	public Integer getGcnseqno() {
		return this.gcnseqno;
	}

	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}

	public Integer getOrderstringid() {
		return this.orderstringid;
	}

	public void setOrderstringid(Integer orderstringid) {
		this.orderstringid = orderstringid;
	}

}