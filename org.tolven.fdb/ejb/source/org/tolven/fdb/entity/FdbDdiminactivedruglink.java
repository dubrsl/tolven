package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ddiminactivedruglink database table.
 * 
 */
@Entity
@Table(name="fdb_ddiminactivedruglink")
public class FdbDdiminactivedruglink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDdiminactivedruglinkPK id;

	private Integer interactionidrev;

    public FdbDdiminactivedruglink() {
    }

	public FdbDdiminactivedruglinkPK getId() {
		return this.id;
	}

	public void setId(FdbDdiminactivedruglinkPK id) {
		this.id = id;
	}
	
	public Integer getInteractionidrev() {
		return this.interactionidrev;
	}

	public void setInteractionidrev(Integer interactionidrev) {
		this.interactionidrev = interactionidrev;
	}

}