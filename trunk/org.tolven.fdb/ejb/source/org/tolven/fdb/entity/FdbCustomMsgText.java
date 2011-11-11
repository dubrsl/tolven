package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_custom_msg_text database table.
 * 
 */
@Entity
@Table(name="fdb_custom_msg_text")
public class FdbCustomMsgText implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbCustomMsgTextPK id;

	private Integer beginparagraphind;

	private String linetext;

    public FdbCustomMsgText() {
    }

	public FdbCustomMsgTextPK getId() {
		return this.id;
	}

	public void setId(FdbCustomMsgTextPK id) {
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