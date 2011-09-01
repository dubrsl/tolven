package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_attributenames database table.
 * 
 */
@Entity
@Table(name="fdb_custom_attributenames")
public class FdbCustomAttributename implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String attributeid;

	private String description;

    public FdbCustomAttributename() {
    }

	public String getAttributeid() {
		return this.attributeid;
	}

	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}