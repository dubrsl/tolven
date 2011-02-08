package org.tolven.doc.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
/**
 * Contains a list of document attachments to a document.
 * @author John Churin
 *
 */
@Entity
@Table
public class DocAttachment implements Serializable {

	/**
	 * Version number for this entity object
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="DOC_SEQ_GEN")
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne
    private DocBase parentDocument;
    
    @ManyToOne
    private DocBase attachedDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountUser tolvenAuthor;

    @Column
    private String description;

    @Column
    private String mediaType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date uploadTime;

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AccountUser getTolvenAuthor() {
		return tolvenAuthor;
	}

	public void setTolvenAuthor(AccountUser tolvenAuthor) {
		this.tolvenAuthor = tolvenAuthor;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getMediaPrefix( ) {
		if (getMediaType()==null) return null;
		String parts[] = getMediaType().split("/");
		return parts[0];
	}

	public String getMediaSuffix( ) {
		if (getMediaType()==null) return null;
		String parts[] = getMediaType().split("/");
		return parts[1];
	}

	public DocBase getParentDocument() {
		return parentDocument;
	}

	public void setParentDocument(DocBase parentDocument) {
		this.parentDocument = parentDocument;
	}

	public DocBase getAttachedDocument() {
		return attachedDocument;
	}

	public void setAttachedDocument(DocBase attachedDocument) {
		this.attachedDocument = attachedDocument;
	}
}
