package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_refitem_msg_text database table.
 * 
 */
@Entity
@Table(name="fdb_refitem_msg_text")
public class FdbRefitemMsgText implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbRefitemMsgTextPK id;

	private Integer beginparagraphind;

	private String linetext;

    public FdbRefitemMsgText() {
    }

	public FdbRefitemMsgTextPK getId() {
		return this.id;
	}

	public void setId(FdbRefitemMsgTextPK id) {
		this.id = id;
	}
	
	public Integer getBeginparagraphind() {
		return this.beginparagraphind;
	}

	public void setBeginparagraphind(Integer beginparagraphind) {
		this.beginparagraphind = beginparagraphind;
	}

	public String getLinetext() {
		return this.linetext;
	}

	public void setLinetext(String linetext) {
		this.linetext = linetext;
	}

}