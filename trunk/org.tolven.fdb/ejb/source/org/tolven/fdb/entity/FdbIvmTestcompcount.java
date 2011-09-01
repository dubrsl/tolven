package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ivm_testcompcount database table.
 * 
 */
@Entity
@Table(name="fdb_ivm_testcompcount")
public class FdbIvmTestcompcount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String grouptestid;

	private Integer compidcount;

    public FdbIvmTestcompcount() {
    }

	public String getGrouptestid() {
		return this.grouptestid;
	}

	public void setGrouptestid(String grouptestid) {
		this.grouptestid = grouptestid;
	}

	public Integer getCompidcount() {
		return this.compidcount;
	}

	public void setCompidcount(Integer compidcount) {
		this.compidcount = compidcount;
	}

}