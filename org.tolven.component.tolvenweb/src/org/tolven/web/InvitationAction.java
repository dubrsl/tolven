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
package org.tolven.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.tolven.core.bean.InvitationException;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.entity.Invitation;

public class InvitationAction extends TolvenAction{
	private List<Invitation> invitations = null;
	private long invitationId;
	private Invitation invitation;
	
	public InvitationAction() {
        super();
        invitationId = 0;
    }

	public long getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(long invitationId) {
		this.invitationId = invitationId;
	}

	public Invitation getInvitation() throws NamingException {
		if (invitation==null || invitationId !=0) {
			this.invitation = getInvitationLocal().findInvitation( invitationId );
		}
		return invitation;
	}

	public void setInvitation(Invitation invitation) {
		this.invitation = invitation;
	}
	
	/**
	 * Return a list of open invitations for the current user
	 * @return Zero or more invitations
	 */
	public List<Invitation> getOpenInvitations() throws NamingException {
        TolvenUser user = getActivationBean().findTolvenUser(getSessionTolvenUserId());
		if (invitations==null) invitations = getInvitationLocal().findOpenInvitations( user, getNow() );
		return invitations;
	}

	/**
	 * Return a list of open invitations for the current user
	 * @return Zero or more invitations
	 */
	public int getOpenInvitationCount() throws NamingException {
		return getOpenInvitations().size();
	}

	/**
	 * User selected this invitation for execution so go ahead and do it.
	 * @return
	 * @throws InvitationException 
	 * @throws IOException 
	 * @throws JAXBException 
	 * @throws NamingException 
	 * @throws GeneralSecurityException 
	 */
	public String executeInvitation( ) throws InvitationException, JAXBException, IOException, NamingException, GeneralSecurityException {
		long invitationId = getRequestParameterAsLong( "invitationId" );
		getInvitationLocal().executeInvitation( invitationId, getNow(), getUserPublicKey());
		invitations = null;	// reset invitations list
		return "inviteSuccess";
	}
	
	public String testInvitationQueue() throws JMSException, NamingException {
	    getInvitationLocal().invitationQueueTest( 10 );
		return "success";
	}
}
