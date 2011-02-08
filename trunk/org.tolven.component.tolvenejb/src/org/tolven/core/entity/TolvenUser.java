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
package org.tolven.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tolven.doc.entity.DocBase;

/**
 * <p>A TolvenUser is associated with one or more Tolven Accounts. This TolvenUser object is not used for authentication,
 * which is done by the application container using LDAP. Instead, a TolvenUser object is associated with an LDAP Distinguising Name.
 * What this TolvenUser object represents is the TolvenUser's participation in the Tolven database as the author of documents and participant
 * in one or more tolven accounts.</p>
 * <p>When a user logs in, the TolvenUser selects the desired Account and thus only ever works with one Tolven Account at a time.</p>
 * <p>If the user changes his or her username, such as when changing eMail accounts, a new TolvenUser record is created. Since Tolven can't always be certain
 * of the relationship between a set of credentials and a real human, a change to LDAP often results in a new
 * TolvenUser object being created. While this creates a bit more database clutter, it also ensures that potential identity ambiguities are accompainied by
 * unique user objects. In particular, it is quite possible for eMail addresses to be reused by a different person. Tolven accounts for this
 * as follows: Say a consumer establishes a Tolven account with userid <em>boney32@aol.com</em>. This consumer changes her eMail address and thus
 * her Tolvern user id to <em>Marge.Swanson@wilco.com</em>. Tolven drops the old address from LDAP. Later, someone else grabs the ever-popular <em>boney32@aol.com</em> email address.
 * And <b>that</b> person creates a Tolven account using the <em>aol</em> eMail address. To avoid an unintentional identity theft, the Id of the TolvenUser account is
 * is not reused. The result being that two different people presenting with the same eMail account (at different times) will have
 * separate Tolven TolvenUser objects. This technique also reduces the risk of intentional identity theft.</p>
 * <p>A TolvenUser may have access to more than one Tolven Account such as when a physician belongs to two practice groups (two unrelated provider accounts) and has a
 * third, personal account. A person may also have more than one active username such as a personal eMail address and an eMail address at work. This is
 * usually not needed since Tolven manages the difference between personal and work through account assignment. However, in cases where Tolven federates
 * identity with a large organization's own LDAP, multiple TolvenUser ids for Tolven may be unavoidable. </p>
 * <p>The net effect is that Tolven may, over time, have more than one TolvenUser object with the exact same DistinguishingName. Password and other TolvenUser demographic changes are handled by LDAP,
 * not the user object.</p>
 * 
 * @author John Churin
 * @see AccountUser
 */
@Entity
@Table
public class TolvenUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;
    
    @Column
    private String ldapUID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date lastLogin;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date creation;

    @Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sponsorship sponsorship;

    @Column
    private String timeZone;

    @Column
    private String locale;

    @Column
    private String emailFormat;

    @Column
    private boolean demoUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private DocBase likeness;
    
    transient private Date oldLastLogin;
    
    /**
     * Construct an empty TolvenUser object.
     */
    public TolvenUser() {
    }
    
    /**
     * The unique and meaningless internal Tolven Id for this user.
     * Leave the Id null if this is a new TolvenUser record: The EentityManager will assign a new, unique Id.
     */
    public long getId() {
        return id;
    }
    
    public void setId(long val) {
        this.id = val;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof TolvenUser)) return false;
        if (this.getId()==((TolvenUser)obj).getId()) return true;
        return false;
    }
    
    public String toString() {
        return "UserId: " + getId() + " UID: " + getLdapUID() + " status: " + getStatus();
    }
    
    public int hashCode() {
    	if (getId()==0) throw new IllegalStateException( "id not yet established in tolvenUser object");
        return Long.valueOf( getId()).hashCode();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLdapUID() {
        return ldapUID;
    }

    /**
     * Set the UID which must be stored as lower case to ensure matching when we do a lookup.
     */
    public void setLdapUID(String ldapUID) {
        if (ldapUID==null) this.ldapUID = null;
        else this.ldapUID = ldapUID.toLowerCase();
    }
    
    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }
    /**
     * A transient attribute to hold the previous setting of lastLogin for display. This attribute does not persist.
     */
    public Date getOldLastLogin() {
        return oldLastLogin;
    }

    public void setOldLastLogin(Date oldLastLogin) {
        this.oldLastLogin = oldLastLogin;
    }

	public DocBase getLikeness() {
		return likeness;
	}

	public void setLikeness(DocBase likeness) {
		this.likeness = likeness;
	}

	public boolean isDemoUser() {
		return demoUser;
	}

	public void setDemoUser(boolean demoUser) {
		this.demoUser = demoUser;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Sponsorship getSponsorship() {
		return sponsorship;
	}

	public void setSponsorship(Sponsorship sponsorship) {
		this.sponsorship = sponsorship;
	}
    
	public String getEmailFormat() {
		return emailFormat;
	}

	public void setEmailFormat(String emailFormat) {
		this.emailFormat = emailFormat;
	}

}
