/*
 *  Copyright (C) 2006 Tolven Inc 
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
package org.tolven.doc.entity;

import java.io.Serializable;
import java.util.Date;

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

import org.tolven.core.entity.TolvenUser;
/**
 * 
 * @author Joseph Isaac
 * 
 * The class encapsulated a document signature.
 *
 */
@Entity
@Table
public class DocumentSignature implements Serializable {

    public static final String DOC_SIGNATURE_ALGORITHM_PROP = "tolven.security.doc.signatureAlgorithm";
    
    private static final long serialVersionUID = 2L;

    @ManyToOne(fetch=FetchType.LAZY)
    private DocBase docBase;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="DOC_SEQ_GEN")
    private long id;

    @Lob
    @Column
    private byte[] signature;

    @Column
    private String signatureAlgorithm;

    @Lob
    @Column
    private byte[] certificate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date timestamp;

    @ManyToOne(fetch=FetchType.LAZY)
    private TolvenUser user;

    public DocBase getDocBase() {
        return docBase;
    }

    public void setDocBase(DocBase docBase) {
        this.docBase = docBase;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimstamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public TolvenUser getUser() {
        return user;
    }

    public void setUser(TolvenUser user) {
        this.user = user;
    }

}
