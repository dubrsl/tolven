package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the fdb_poem_orderstring_addltext database table.
 * 
 */
@Entity
@Table(name="fdb_poem_orderstring_addltext")
public class FdbPoemOrderstringAddltext implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbPoemOrderstringAddltextPK id;

	private BigDecimal poemtextid;

    public FdbPoemOrderstringAddltext() {
    }

	public FdbPoemOrderstringAddltextPK getId() {
		return this.id;
	}

	public void setId(FdbPoemOrderstringAddltextPK id) {
		this.id = id;
	}
	
	public BigDecimal getPoemtextid() {
		return this.poemtextid;
	}

	public void setPoemtextid(BigDecimal poemtextid) {
		this.poemtextid = poemtextid;
	}

}