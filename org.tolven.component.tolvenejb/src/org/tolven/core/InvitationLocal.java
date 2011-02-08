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
package org.tolven.core;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.JAXBException;

import org.tolven.admin.ActivateInvitation;
import org.tolven.admin.AdministrativeDetail;
import org.tolven.admin.InvitationDetail;
import org.tolven.core.bean.InvitationException;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.entity.Invitation;
import org.tolven.el.ExpressionEvaluator;

/**
 * Services used to create invitations and send email messages.
 */
public interface InvitationLocal {
	
    /**
     * Send a message to the specified addressee.
     * Variables extracted from the supplied EE: from, to, cc, bcc, subject, and body.
     * First, the name+"Property" is looked up. For example, if bodyProperty is supplied, then it 
     * is the name of an accountProperty containing the body, not the body itself.
     * In either case, the resulting variable is then evaluated using the same expression evaluator.
     * If the xxxProperty form is used, then an account or an accountUser variable must be present in the ee.
     * If the to (or toProperty) variable yields a null, then to is assumed to be the email address of the logged in user. 
     * If the from (or fromProperty) variable yields a null, then the from is taken from system properties <code>tolven.mail.fromName</code> and  <code>tolven.mail.from</code>
     * @param ExpressionEvaluator containing attributes needed to construct and send the message
     */
    public void sendMessage( ExpressionEvaluator ee );

	/**
	 * Send a test message to the specified address. This will use the "/invitation/testMessage.xhtml" template.
	 * Note that the template has no access to information about the logged in user. This is because
	 * an invitation's context comes from the invitation object itself, not the logged in user and since a test message
	 * has no invitation object, the template only has generic information avalable to it. 
	 * TODO: This method send the message in real-time whereas the real "sendInvitation" method is intended to be queued.
	 * @param addressee e-mail address of where to send test message.
	 * @throws Exception
	 */
	//  @WebMethod(operationName="sendTestMessage", action="urn:SendTestMessage")
	public abstract void sendTestMessage(String addressee,String emailFormat) throws Exception;

	/**
	 * Send an invitation to someone
	 * @param addressee
	 * @param id
	 * @param message
	 * @param contextPath
	 * @throws Exception
	 */
	public abstract void sendNotifyMessage(String subject,
			InternetAddress addressee, String message, String emailFormat) throws Exception;

	/**
	 * Send an invitation based on it's contents. The actual work will occur later
	 * in a messageDrivenBean. This is for two reasons: 1) it offloads non-critical
	 * work to the background, and 2) the invitation must be committed to the database
	 * before the mail message is created because it is done in a separate "callback" process
	 * (the backend needs to call the frontend to format the html message body).
	 * @param invitation
	 * @throws Exception
	 */
	public abstract void queueInvitation(Invitation invitation) throws Exception;

    public void invitationQueueTest( int count ) throws JMSException;
    
	/**
	 * Send an invitation based on it's contents. Don't call this directly. It won't work. Use queueInvitation instead.
	 * @see #queueInvitation
	 * @param invitation
	 * @throws Exception
	 */
	public abstract void sendInvitation(Invitation invitation) throws Exception;

    /**
	 * Get a Invitation by its internal ID
	 * @param invitationId the id of the Invitation
	 * @return the Invitation object
	 */
	public Invitation findInvitation( long invitationId );
	
	/**
	 * Extract the details as a list of object trees from the invitation XML
	 * @param invitation
	 * @return
	 * @throws JAXBException
	 */
	public List<AdministrativeDetail>extractDetails( Invitation invitation ) throws JAXBException;

	/**
	 * Given an invitation, return the activation details contained within, if any, otherwise
	 * return null. This method is pretty special purpose for the UI and is intended for display only,
	 * not for executing the invitation.
	 * @param invitation
	 * @return
	 * @throws JAXBException 
	 */
	public ActivateInvitation extractActivationDetails( Invitation invitation ) throws JAXBException;

	/**
     * Create a new invitation using the supplied details directed to the specified user
     * @throws IOException 
     * @throws JAXBException 
     * @deprecated
     */
    public Invitation createInvitation( String principal, InvitationDetail detail ) throws JAXBException, IOException;
 
    /**
     * Create a new invitation using the supplied details directed to the specified user
     * @throws IOException 
     * @throws JAXBException 
     */
    public Invitation createInvitation( String userName, String email, InvitationDetail detail ) throws JAXBException, IOException;
 
	/**
     * Find open invitations for this user, regardless of account. The invitation must be active and not expired.
     * @param user
     * @param now
     * @return A list of invitations
     */
	public List<Invitation> findOpenInvitations( TolvenUser user, Date now);

	/**
	 * Execute an invitation and if the invitation is chained, carry out all chained invitations as well.
	 * we stop either when we're done or if there's an error thrown.
	 * @throws GeneralSecurityException 
	 */
	public void executeInvitation( Invitation invitation, Date now, PublicKey userPublicKey ) throws InvitationException;

	/**
	 * Execute an invitation and if the invitation is chained, carry out all chained invitations as well.
	 * we stop either when we're done or if there's an error thrown.
	 * @throws GeneralSecurityException 
	 */
	public void executeInvitation( long invitationId, Date now, PublicKey userPublicKey ) throws InvitationException;

    /**
     * If the userID isn't registered within an hour, cancel the activation
     */ 
    public void followup( Invitation invitation );
}