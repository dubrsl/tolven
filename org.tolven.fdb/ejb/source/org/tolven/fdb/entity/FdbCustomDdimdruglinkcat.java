package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_ddimdruglinkcat database table.
 * 
 */
@Entity
@Table(name="fdb_custom_ddimdruglinkcat")
public class FdbCustomDdimdruglinkcat implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomDdimdruglinkcatPK id;

	private String uicategory1;

	private String uicategory2;

	private String uicategory3;

	private String uicategory4;

	private String uicategory5;

	private String uicategory6;

    public FdbCustomDdimdruglinkcat() {
    }

	public FdbCustomDdimdruglinkcatPK getId() {
		return this.id;
	}

	public void setId(FdbCustomDdimdruglinkcatPK id) {
		this.id = id;
	}
	
	public String getUicategory1() {
		return this.uicategory1;
	}

	public void setUicategory1(String uicategory1) {
		this.uicategory1 = uicategory1;
	}

	public String getUicategory2() {
		return this.uicategory2;
	}

	public void setUicategory2(String uicategory2) {
		this.uicategory2 = uicategory2;
	}

	public String getUicategory3() {
		return this.uicategory3;
	}

	public void setUicategory3(String uicategory3) {
		this.uicategory3 = uicategory3;
	}

	public String getUicategory4() {
		return this.uicategory4;
	}

	public void setUicategory4(String uicategory4) {
		this.uicategory4 = uicategory4;
	}

	public String getUicategory5() {
		return this.uicategory5;
	}

	public void setUicategory5(String uicategory5) {
		this.uicategory5 = uicategory5;
	}

	public String getUicategory6() {
		return this.uicategory6;
	}

	public void setUicategory6(String uicategory6) {
		this.uicategory6 = uicategory6;
	}

}