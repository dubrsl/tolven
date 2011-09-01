/* Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Arun
 * @version $Id: MailerRemote.java,v 1.3 2010/03/17 20:10:24 jchurin Exp $
 */
/**
 * 
 */
package org.tolven.mail;

import javax.mail.internet.InternetAddress;

/**
 * Remote service to compose and send mail messages
 * @author Arun
 *
 */
public interface MailerRemote {
	
	/**
	 * Use this method to send email to multiple recipients. 
	 * 
	 * @param recipients 	: A list of email addresses. Each member of recipient_list will be in the "To:" field of the e-mail message.
	 * @param subject   	: The subject line of the e-mail.
	 * @param body   		: The body text. This can be a plain text or html message.
	 * @param emailFormat 	: Content type of the message. By default, this is "plain".
	 * @throws Exception
	 */
	public void sendMail( InternetAddress[] recipients, String subject, String body, String emailFormat) throws Exception;

	/**
	 * Use this method to send email to a single recipient. 
	 * 
	 * @param recipient 	: Email address as string.
	 * @param subject   	: The subject line of the e-mail.
	 * @param body   		: The body text. This can be a plain text or html message.
	 * @param emailFormat 	: Content type of the message. By default, this is "plain".
	 * @throws Exception
	 */
	public void sendMail( String recipient, String subject, String body, String emailFormat) throws Exception;

}
