package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_hicseqno_baseingredient database table.
 * 
 */
@Entity
@Table(name="fdb_hicseqno_baseingredient")
public class FdbHicseqnoBaseingredient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer hicseqno;

	private Integer baseingredientid;

    public FdbHicseqnoBaseingredient() {
    }

	public Integer getHicseqno() {
		return this.hicseqno;
	}

	public void setHicseqno(Integer hicseqno) {
		this.hicseqno = hicseqno;
	}

	public Integer getBaseingredientid() {
		return this.baseingredientid;
	}

	public void setBaseingredientid(Integer baseingredientid) {
		this.baseingredientid = baseingredientid;
	}

}