package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_msg_catdef database table.
 * 
 */
@Entity
@Table(name="fdb_custom_msg_catdef")
public class FdbCustomMsgCatdef implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String categoryid;

	private String description;

    public FdbCustomMsgCatdef() {
    }

	public String getCategoryid() {
		return this.categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}