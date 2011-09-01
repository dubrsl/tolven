package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_poem_adminrates database table.
 * 
 */
@Entity
@Table(name="fdb_poem_adminrates")
public class FdbPoemAdminrate implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPoemAdminratePK id;

	private BigDecimal adminrate;

	private Integer adminrateunitsid;

    public FdbPoemAdminrate() {
    }

	public FdbPoemAdminratePK getId() {
		return this.id;
	}

	public void setId(FdbPoemAdminratePK id) {
		this.id = id;
	}
	
	public BigDecimal getAdminrate() {
		return this.adminrate;
	}

	public void setAdminrate(BigDecimal adminrate) {
		this.adminrate = adminrate;
	}

	public Integer getAdminrateunitsid() {
		return this.adminrateunitsid;
	}

	public void setAdminrateunitsid(Integer adminrateunitsid) {
		this.adminrateunitsid = adminrateunitsid;
	}

}