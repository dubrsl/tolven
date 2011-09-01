package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ivm_mfgdesc database table.
 * 
 */
@Entity
@Table(name="fdb_ivm_mfgdesc")
public class FdbIvmMfgdesc implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String mfgid;

	private String description;

    public FdbIvmMfgdesc() {
    }

	public String getMfgid() {
		return this.mfgid;
	}

	public void setMfgid(String mfgid) {
		this.mfgid = mfgid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}