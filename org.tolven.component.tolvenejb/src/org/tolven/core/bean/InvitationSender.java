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
package org.tolven.core.bean;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.interceptor.Interceptors;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.tolven.core.InvitationLocal;
import org.tolven.core.entity.Status;
import org.tolven.doc.entity.Invitation;
import org.tolven.session.QueueSessionInterceptor;

@MessageDriven
@Interceptors({ QueueSessionInterceptor.class })
public class InvitationSender implements MessageListener {
	@EJB private InvitationLocal invitationBean;

	@Resource private MessageDrivenContext ctx;
	private Logger logger = Logger.getLogger(this.getClass());

	public void onMessage(Message msg)  {
		try {
			Invitation invitation = (Invitation) ((ObjectMessage)msg).getObject();
			// The invitation may require followup if the user doesn't activate in time
			invitationBean.followup(invitation);
			
	        if ("test".equals(invitation.getDispatchAction())) {
	        	logger.info("[InvitationSender:onMessage] TEST MSG: " + invitation.getTitle());
				String completedStatus = Status.COMPLETED.value();	  
				invitation.setStatus(completedStatus);
				return;
	        }
            invitationBean.sendInvitation(invitation);
            //logger.info("[InvitationSender:onMessage] Invitation id " + textMsg.getText() + " complete");
		} catch (JMSException e) {
            ctx.setRollbackOnly();
			e.printStackTrace();
            logger.error(e.getMessage());
            throw new RuntimeException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
            ctx.setRollbackOnly();
			e.printStackTrace();
            logger.error(e.getMessage());
            throw new RuntimeException(e);
		}
	}

}
