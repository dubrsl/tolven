package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the fdb_gcnseqno_pcm database table.
 * 
 */
@Embeddable
public class FdbGcnseqnoPcmPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Integer gcnseqno;

	private Integer languageid;

	private Integer pcmid;

    public FdbGcnseqnoPcmPK() {
    }
	public Integer getGcnseqno() {
		return this.gcnseqno;
	}
	public void setGcnseqno(Integer gcnseqno) {
		this.gcnseqno = gcnseqno;
	}
	public Integer getLanguageid() {
		return this.languageid;
	}
	public void setLanguageid(Integer languageid) {
		this.languageid = languageid;
	}
	public Integer getPcmid() {
		return this.pcmid;
	}
	public void setPcmid(Integer pcmid) {
		this.pcmid = pcmid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FdbGcnseqnoPcmPK)) {
			return false;
		}
		FdbGcnseqnoPcmPK castOther = (FdbGcnseqnoPcmPK)other;
		return 
			this.gcnseqno.equals(castOther.gcnseqno)
			&& this.languageid.equals(castOther.languageid)
			&& this.pcmid.equals(castOther.pcmid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gcnseqno.hashCode();
		hash = hash * prime + this.languageid.hashCode();
		hash = hash * prime + this.pcmid.hashCode();
		
		return hash;
    }
}