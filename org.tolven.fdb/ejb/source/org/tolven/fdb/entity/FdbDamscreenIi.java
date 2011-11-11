package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_damscreen_ii database table.
 * 
 */
@Entity
@Table(name="fdb_damscreen_ii")
public class FdbDamscreenIi implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDamscreenIiPK id;

	private String inactivehicls;

	private Integer inactiveingredienthitind;

    public FdbDamscreenIi() {
    }

	public FdbDamscreenIiPK getId() {
		return this.id;
	}

	public void setId(FdbDamscreenIiPK id) {
		this.id = id;
	}
	
	public String getInactivehicls() {
		return this.inactivehicls;
	}

	public void setInactivehicls(String inactivehicls) {
		this.inactivehicls = inactivehicls;
	}

	public Integer getInactiveingredienthitind() {
		return this.inactiveingredienthitind;
	}

	public void setInactiveingredienthitind(Integer inactiveingredienthitind) {
		this.inactiveingredienthitind = inactiveingredienthitind;
	}

}