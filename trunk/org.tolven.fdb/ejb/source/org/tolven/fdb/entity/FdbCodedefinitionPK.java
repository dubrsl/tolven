package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_codedefinition database table.
 * 
 */
@Embeddable
public class FdbCodedefinitionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer codetype;

	private String codevalue;

    public FdbCodedefinitionPK() {
    }
	public Integer getCodetype() {
		return this.codetype;
	}
	public void setCodetype(Integer codetype) {
		this.codetype = codetype;
	}
	public String getCodevalue() {
		return this.codevalue;
	}
	public void setCodevalue(String codevalue) {
		this.codevalue = codevalue;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbCodedefinitionPK)) {
			return false;
		}
		FdbCodedefinitionPK castOther = (FdbCodedefinitionPK)other;
		return 
			this.codetype.equals(castOther.codetype)
			&& this.codevalue.equals(castOther.codevalue);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.codetype.hashCode();
		hash = hash * prime + this.codevalue.hashCode();
		
		return hash;
    }
}