package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_damscreeninfo_ii database table.
 * 
 */
@Entity
@Table(name="fdb_damscreeninfo_ii")
public class FdbDamscreeninfoIi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String hitval;

	private String hitdescription1;

	private String hitdescription2;

	private String hitdescription3;

	private String hitdescription4;

	private String hitdescription5;

	private String hitdescription6;

	private Integer hitid;

	private Integer hittype;

    public FdbDamscreeninfoIi() {
    }

	public String getHitval() {
		return this.hitval;
	}

	public void setHitval(String hitval) {
		this.hitval = hitval;
	}

	public String getHitdescription1() {
		return this.hitdescription1;
	}

	public void setHitdescription1(String hitdescription1) {
		this.hitdescription1 = hitdescription1;
	}

	public String getHitdescription2() {
		return this.hitdescription2;
	}

	public void setHitdescription2(String hitdescription2) {
		this.hitdescription2 = hitdescription2;
	}

	public String getHitdescription3() {
		return this.hitdescription3;
	}

	public void setHitdescription3(String hitdescription3) {
		this.hitdescription3 = hitdescription3;
	}

	public String getHitdescription4() {
		return this.hitdescription4;
	}

	public void setHitdescription4(String hitdescription4) {
		this.hitdescription4 = hitdescription4;
	}

	public String getHitdescription5() {
		return this.hitdescription5;
	}

	public void setHitdescription5(String hitdescription5) {
		this.hitdescription5 = hitdescription5;
	}

	public String getHitdescription6() {
		return this.hitdescription6;
	}

	public void setHitdescription6(String hitdescription6) {
		this.hitdescription6 = hitdescription6;
	}

	public Integer getHitid() {
		return this.hitid;
	}

	public void setHitid(Integer hitid) {
		this.hitid = hitid;
	}

	public Integer getHittype() {
		return this.hittype;
	}

	public void setHittype(Integer hittype) {
		this.hittype = hittype;
	}

}