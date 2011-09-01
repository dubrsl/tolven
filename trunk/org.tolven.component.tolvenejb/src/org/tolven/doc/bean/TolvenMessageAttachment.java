/*
 *  Copyright (C) 2008 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.doc.bean;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table
public class TolvenMessageAttachment implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="DOC_SEQ_GEN")
    private long id;

    @Version
    @Column
    private Integer version;
    
    @Lob
    @Basic
	private byte[] payload;

    @Column
    private String description;

    @Column
    private String mediaType;

    @Column
	private String xmlNS;

    @Column
	private long documentId;

    @ManyToOne
    private TolvenMessageWithAttachments parent;

	public long getId() {
        return id;
    }
	
    public void setId(long id) {
        this.id = id;
    }
    
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getXmlNS() {
		return xmlNS;
	}
	public void setXmlNS(String xmlNS) {
		this.xmlNS = xmlNS;
	}

	/**
	 * A non-zero documentId in an attachment means the document already exists in the database.
	 * As such, the document payload can be omitted. However, this means that the processing rules
	 * will not be able to look inside the attachment but can only refer to the attachment as a whole.
	 * @return
	 */
	public long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

    public TolvenMessageWithAttachments getParent() {
        return parent;
    }

    public void setParent(TolvenMessageWithAttachments parent) {
        this.parent = parent;
    }

}
