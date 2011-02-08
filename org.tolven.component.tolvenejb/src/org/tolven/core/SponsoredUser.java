package org.tolven.core;

import java.util.Date;
/**
 * A simple bean that carried the restricted results of a SponsoredUser query.
 * this special class prevents any further navigation because this query is gathering information
 * from outside of the requesting user's account.
 * 
 * @author John Churin
 *
 */
public class SponsoredUser {
	private String referenceCode;
	String uid;
	private Date created;
	private Date lastLogin;
	
	public SponsoredUser( String referenceCode, String uid, Date created, Date lastLogin ) {
		this.referenceCode = referenceCode;
		this.uid = uid;
		this.created = created;
		this.lastLogin = lastLogin;
	}

	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
}
