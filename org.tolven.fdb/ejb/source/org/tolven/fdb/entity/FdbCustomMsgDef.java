package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_msg_def database table.
 * 
 */
@Entity
@Table(name="fdb_custom_msg_def")
public class FdbCustomMsgDef implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer messageid;

	private String categoryid;

    public FdbCustomMsgDef() {
    }

	public Integer getMessageid() {
		return this.messageid;
	}

	public void setMessageid(Integer messageid) {
		this.messageid = messageid;
	}

	public String getCategoryid() {
		return this.categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}

}