package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_indication database table.
 * 
 */
@Entity
@Table(name="fdb_custom_indication")
public class FdbCustomIndication implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomIndicationPK id;

	private String comment1;

	private String comment2;

	private String comment3;

	private String comment4;

	private String comment5;

	private String comment6;

    public FdbCustomIndication() {
    }

	public FdbCustomIndicationPK getId() {
		return this.id;
	}

	public void setId(FdbCustomIndicationPK id) {
		this.id = id;
	}
	
	public String getComment1() {
		return this.comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	public String getComment2() {
		return this.comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	public String getComment3() {
		return this.comment3;
	}

	public void setComment3(String comment3) {
		this.comment3 = comment3;
	}

	public String getComment4() {
		return this.comment4;
	}

	public void setComment4(String comment4) {
		this.comment4 = comment4;
	}

	public String getComment5() {
		return this.comment5;
	}

	public void setComment5(String comment5) {
		this.comment5 = comment5;
	}

	public String getComment6() {
		return this.comment6;
	}

	public void setComment6(String comment6) {
		this.comment6 = comment6;
	}

}