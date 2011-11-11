package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_codedefinition database table.
 * 
 */
@Entity
@Table(name="fdb_codedefinition")
public class FdbCodedefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCodedefinitionPK id;

	private String codedescription1;

	private String codedescription2;

	private String codedescription3;

	private String codedescription4;

	private String codedescription5;

	private String codedescription6;

    public FdbCodedefinition() {
    }

	public FdbCodedefinitionPK getId() {
		return this.id;
	}

	public void setId(FdbCodedefinitionPK id) {
		this.id = id;
	}
	
	public String getCodedescription1() {
		return this.codedescription1;
	}

	public void setCodedescription1(String codedescription1) {
		this.codedescription1 = codedescription1;
	}

	public String getCodedescription2() {
		return this.codedescription2;
	}

	public void setCodedescription2(String codedescription2) {
		this.codedescription2 = codedescription2;
	}

	public String getCodedescription3() {
		return this.codedescription3;
	}

	public void setCodedescription3(String codedescription3) {
		this.codedescription3 = codedescription3;
	}

	public String getCodedescription4() {
		return this.codedescription4;
	}

	public void setCodedescription4(String codedescription4) {
		this.codedescription4 = codedescription4;
	}

	public String getCodedescription5() {
		return this.codedescription5;
	}

	public void setCodedescription5(String codedescription5) {
		this.codedescription5 = codedescription5;
	}

	public String getCodedescription6() {
		return this.codedescription6;
	}

	public void setCodedescription6(String codedescription6) {
		this.codedescription6 = codedescription6;
	}

}