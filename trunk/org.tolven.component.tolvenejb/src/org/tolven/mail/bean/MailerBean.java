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
 * @version $Id: MailerBean.java,v 1.2 2009/01/05 23:18:02 jchurin Exp $
 */  
 package org.tolven.mail.bean;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.bean.TextConverter;
import org.tolven.mail.MailerLocal;
import org.tolven.mail.MailerRemote;
/*
 * This is a utility package performing only mail related functions.
 * 1) Send/receive email to/from a mail account(Configured in tolven properties)
 * 2) Set properties to send or receive(specific to the mail server) in tolven properties.
 * 
 */
@Stateless
@Local(MailerLocal.class)
@Remote(MailerRemote.class)
public class MailerBean implements MailerLocal, MailerRemote{

	@EJB private TolvenPropertiesLocal propertiesBean;

    /* 
     * (non-Javadoc)
     * @see org.tolven.mail.MailerRemote#sendMail(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void sendMail(String recipient, String subject, String body, String emailFormat) throws Exception{
			InternetAddress[] recipients = new InternetAddress[]{ new InternetAddress( recipient)};
			sendMail(recipients, subject, body, emailFormat);
    }

    /* (non-Javadoc)
     * @see org.tolven.mail.MailerRemote#sendMail(javax.mail.internet.InternetAddress[], java.lang.String, java.lang.String, java.lang.String)
     */
    public void sendMail(InternetAddress[] recipients, String subject, String body, String emailFormat) throws Exception{
        String protocol = propertiesBean.getProperty("mail.transport.protocol");
        String host = propertiesBean.getProperty("mail." + protocol + ".host");
        int port = Integer.parseInt( propertiesBean.getProperty("mail." + protocol + ".port"));
        String user = propertiesBean.getProperty("tolven.mail.user");
        String pwd = propertiesBean.getProperty("tolven.mail.password");

/*        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.port", SMTP_HOST_PORT);
        props.put("mail.smtps.auth", "true");
 */       // props.put("mail.smtps.quitwait", "false");
        
        Session session = Session.getDefaultInstance(System.getProperties());

        MimeMessage msg = new MimeMessage(session);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setFrom(new InternetAddress(propertiesBean.getProperty("tolven.mail.from"), 
        		propertiesBean.getProperty("tolven.mail.fromName")));
        msg.setRecipients(Message.RecipientType.TO, recipients);

        if (emailFormat.equals("html")) {
			msg.setContent(body, "text/html");
		}else {
			msg.setContent(new TextConverter().html2text(body), "text/plain");
		}
        
        session.setDebug(true);
        
        Transport transport = session.getTransport();
        transport.connect(host, port, user, pwd);
        transport.sendMessage(msg,
            msg.getRecipients(Message.RecipientType.TO));
        transport.close();
    }
    
    public void receiveMail() throws Exception{
    	
    }
    
}
