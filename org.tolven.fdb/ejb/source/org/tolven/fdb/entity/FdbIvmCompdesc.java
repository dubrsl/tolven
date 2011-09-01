package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ivm_compdesc database table.
 * 
 */
@Entity
@Table(name="fdb_ivm_compdesc")
public class FdbIvmCompdesc implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String compid;

	private String description;

	private Integer tpnind;

    public FdbIvmCompdesc() {
    }

	public String getCompid() {
		return this.compid;
	}

	public void setCompid(String compid) {
		this.compid = compid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getTpnind() {
		return this.tpnind;
	}

	public void setTpnind(Integer tpnind) {
		this.tpnind = tpnind;
	}

}