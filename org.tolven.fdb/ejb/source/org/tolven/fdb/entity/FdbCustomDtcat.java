package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_dtcat database table.
 * 
 */
@Entity
@Table(name="fdb_custom_dtcat")
public class FdbCustomDtcat implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomDtcatPK id;

	private Integer customdupallowance;

	private Integer customdupallowind;

	private String customstring;

    public FdbCustomDtcat() {
    }

	public FdbCustomDtcatPK getId() {
		return this.id;
	}

	public void setId(FdbCustomDtcatPK id) {
		this.id = id;
	}
	
	public Integer getCustomdupallowance() {
		return this.customdupallowance;
	}

	public void setCustomdupallowance(Integer customdupallowance) {
		this.customdupallowance = customdupallowance;
	}

	public Integer getCustomdupallowind() {
		return this.customdupallowind;
	}

	public void setCustomdupallowind(Integer customdupallowind) {
		this.customdupallowind = customdupallowind;
	}

	public String getCustomstring() {
		return this.customstring;
	}

	public void setCustomstring(String customstring) {
		this.customstring = customstring;
	}

}