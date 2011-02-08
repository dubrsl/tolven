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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * A TolvenMessage is used as the payload of a JMS message. It primary contains or refers to a document
 * that will be evaluated but rules.
 * @author John Churin
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TM")
public class TolvenMessage implements Serializable {

    public static final String TOLVENMESSAGE_ID = "TolvenMessageId";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "DOC_SEQ_GEN")
    private long id;

    @Version
    @Column
    private Integer version;

    @Lob
    @Basic
    private byte[] payload;

    @Column
    private String xmlNS;

    @Column
    private String xmlName;

    @Column
    private String mediaType;

    @Column
    private long accountId;

    @Column
    private long fromAccountId;

    @Column
    private String sender;

    @Column
    private String recipient;

    @Column
    private long authorId;

    @Column
    private long documentId;

    private transient boolean decrypted = false;

    @Lob
    @Basic
    private byte[] encryptedKey;

    @Column
    private String encryptedKeyAlgorithm;

    @Lob
    @Basic
    private byte[] encryptionX509Certificate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date scheduleDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date queueOnDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date queueDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date processDate;

    @Column
    private Boolean deleted;

    @OneToMany(mappedBy = "tolvenMessage", cascade = CascadeType.ALL)
    private Set<TolvenMessageProperty> properties;

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

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public byte[] getPayload() {
        return payload;
    }

    /**
     * Specify the document payload, eg a CCR or TRIM XML document. Upon receipt, the tolven evaluator
     * will create a document containing this payload if the documentID is 0, otherwise, it will
     * just verify that the document exists.
     * @return
     */
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String getXmlName() {
        return xmlName;
    }

    public void setXmlName(String xmlName) {
        this.xmlName = xmlName;
    }

    public String getXmlNS() {
        return xmlNS;
    }

    public void setXmlNS(String xmlNS) {
        this.xmlNS = xmlNS;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Specify that the target document, which should be in Active status and thus be immutable.
     * the evaluator won't actually try to access the document contents directly since it will probably
     * be encrypted under some else's id.
     * If documentId is zero, then the evaluator will create a new document containing the payload of this
     * message (creating a document is not subject to the same encryption restriction as reading it).
     * @return
     */
    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getEncryptionKeyAlgorithm() {
        return encryptedKeyAlgorithm;
    }

    public void setEncryptedKeyAlgorithm(String encryptedKeyAlgorithm) {
        this.encryptedKeyAlgorithm = encryptedKeyAlgorithm;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isDecrypted() {
        return decrypted;
    }

    public void setDecrypted(boolean decrypted) {
        this.decrypted = decrypted;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Date getQueueOnDate() {
        return queueOnDate;
    }

    public void setQueueOnDate(Date queueOnDate) {
        this.queueOnDate = queueOnDate;
    }

    public Date getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(Date queueDate) {
        this.queueDate = queueDate;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Set<TolvenMessageProperty> getProperties() {
        if(properties == null) {
            properties = new HashSet<TolvenMessageProperty>();
        }
        return properties;
    }

    public void setProperties(Set<TolvenMessageProperty> properties) {
        this.properties = properties;
    }

    public byte[] getEncryptionX509Certificate() {
        return encryptionX509Certificate;
    }

    public void setEncryptionX509Certificate(byte[] encryptionX509Certificate) {
        this.encryptionX509Certificate = encryptionX509Certificate;
    }

    @PrePersist
    @PreUpdate
    protected void prePersist() {
        for (TolvenMessageProperty property : getProperties()) {
            if (property.getTolvenMessage() != this) {
                property.setTolvenMessage(this);
            }
        }
    }
}
