package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ivm_remarks database table.
 * 
 */
@Entity
@Table(name="fdb_ivm_remarks")
public class FdbIvmRemark implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String remarkid;

	private String remark;

    public FdbIvmRemark() {
    }

	public String getRemarkid() {
		return this.remarkid;
	}

	public void setRemarkid(String remarkid) {
		this.remarkid = remarkid;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}