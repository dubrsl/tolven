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
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tolven.core.entity.Account;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.key.AccountPrivateKey;

/**
 * An invitation is a workflow payload for Tolven actions that must be sent via eMail or otherwise
 *  stored or referenced outside of Tolven while still preserving the confidentiality of the information
 *  and subject.
 * Invitations usually have an expiration date and are always for a specific purpose.
 * Invitations can be chained such that one invitation immediately triggers a second invitation upon completion of
 * the previous invitation.
 * In most cases, the details of an invitation are contained in a separate XML.
 * Account and user links are optional and depend on the specific invitation type.
 * @author John Churin
 */
@Entity
@Table
public class Invitation implements Serializable {

    /**
	 * V1 of an invitation
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="DOC_SEQ_GEN")
    private Long id;
    
    @ManyToOne
    private Account account;
    
    @ManyToOne
    private TolvenUser author;
    
    @ManyToOne
    private TolvenUser targetUser;
    
    @Column
    private String targetUserName;

	@ManyToOne
    private Invitation chainedInvitation;

    @Column
    private String template;
    
    @Column
    private String title;

    @Column
    private String brand;
    
	@Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date expiration;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date created;

   @Column
    private String dispatchAction;

   @Column
    private String status;
    
   @Column
   private String targetEmail;

   @Column
   public String emailFormat;
   
   @Lob
   @Column
   private byte[] detailContent;

   @Embedded
   private AccountPrivateKey accountPrivateKey;
   
    /** Creates a new instance of Invitation */
    public Invitation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

/** At this time, chaining used for one purpose: A non-Tolven user gets an invitation such as a referral but first needs to
 * create an account. A sponsored invitation may need the same feature. The activation process in this case has to remember the
 * original invitation (eg the referral) so that when the activation process completes, the
 * original invitation can be acted upon.
 */
     public Invitation getChainedInvitation() {
        return chainedInvitation;
    }

    public void setChainedInvitation(Invitation chainedInvitation) {
        this.chainedInvitation = chainedInvitation;
    }

    /**
     * If non-null, then the invitation is no longer valid.
     */
    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getDispatchAction() {
        return dispatchAction;
    }

    public void setDispatchAction(String dispatchAction) {
        this.dispatchAction = dispatchAction;
    }

    /**
     * An invitation can only be used once. The Status field is used to indicate that it has been USED
     * (or has EXPIRED). Otherwise, it's ACTIVE.
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * The user who is the author of this invitation. 
     * @return
     */
	public TolvenUser getAuthor() {
		return author;
	}

	public void setAuthor(TolvenUser author) {
		this.author = author;
	}
	/**
	 * The Tolven User who is to receive this invitation. Not all invitations can be associated with a specific user 
	 * in which case targetEmail should be specified.  
	 * @return A TolvenUser or null.
	 */
	public TolvenUser getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(TolvenUser targetUser) {
		this.targetUser = targetUser;
	}

	/**
	 * Specify this when the target TolvenUser attribute is unknown. This essentially creates a "bearer invitation" in that
	 * the recipient can log into Tolven under any user account.
	 * @return A String containing an email address, eg first.last@mydomain.com
	 */
	public String getTargetEmail() {
		return targetEmail;
	}

	public void setTargetEmail(String targetEmail) {
		this.targetEmail = targetEmail;
	}
	
	public String getEmailFormat()
		{
			return emailFormat;
		}
	
		public void setEmailFormat(String emailFormat)
		{
			this.emailFormat = emailFormat;
	    }

	/**
	 * The date and time when the invitation was created.
	 * @return
	 */
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	/**
	 * The template used for this invitation. This should contain a context-relative URL such as /invitation/activate.jsf.
	 * TODO: All the path to specify a Tolven document rather than flat file.
	 * @return The template path
	 */
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * Invitations often have details beyond just the type of invitation. Those details are contained in the
	 * here, if any. For example, a referral will have details about the patient and what is being requested.
	 * All details are in XML form.
	 * @return If non-null, a document containing the invitation details
	 */
	public byte[] getDetailContent() {
		return detailContent;
	}

	public void setDetailContent(byte[] detailContent) {
		this.detailContent = detailContent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    public AccountPrivateKey getAccountPrivateKey() {
        return accountPrivateKey;
    }

    public void setAccountPrivateKey(AccountPrivateKey anAccountPrivateKey) {
        accountPrivateKey = anAccountPrivateKey;
    }
    public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
	
    public String getTargetUserName() {
		return targetUserName;
	}

	public void setTargetUserName(String targetUserName) {
		this.targetUserName = targetUserName;
	}


}
