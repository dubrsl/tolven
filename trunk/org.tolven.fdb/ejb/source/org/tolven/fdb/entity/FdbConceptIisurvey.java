package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_concept_iisurvey database table.
 * 
 */
@Entity
@Table(name="fdb_concept_iisurvey")
public class FdbConceptIisurvey implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbConceptIisurveyPK id;

	private BigDecimal percentsurveyed;

    public FdbConceptIisurvey() {
    }

	public FdbConceptIisurveyPK getId() {
		return this.id;
	}

	public void setId(FdbConceptIisurveyPK id) {
		this.id = id;
	}
	
	public BigDecimal getPercentsurveyed() {
		return this.percentsurveyed;
	}

	public void setPercentsurveyed(BigDecimal percentsurveyed) {
		this.percentsurveyed = percentsurveyed;
	}

}