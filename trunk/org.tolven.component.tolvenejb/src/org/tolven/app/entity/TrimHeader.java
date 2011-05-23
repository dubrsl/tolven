package org.tolven.app.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table
public class TrimHeader implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;
    
    @Column
    private String name;
 
    @Column
    private String status;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column
    private byte[] trim;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private java.util.Date lastUpdated;
    
    @Column
    private Boolean autogenerated;

    @Column
    private String comment;
    
    @Column
    private Integer version;
    
    public TrimHeader() {
    	
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TrimHeader)) return false;
        if (this.getId()==((TrimHeader)obj).getId()) return true;
        return false;
    }

    public int hashCode() {
    	if (getId()==0) throw new IllegalStateException( "id not yet established");
        return new Long( getId()).hashCode();
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getTrim() {
		return trim;
	}

	public void setTrim(byte[] trim) {
		this.trim = trim;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public java.util.Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(java.util.Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Boolean getAutogenerated() {
		return autogenerated;
	}

	public void setAutogenerated(Boolean autogenerated) {
		this.autogenerated = autogenerated;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}