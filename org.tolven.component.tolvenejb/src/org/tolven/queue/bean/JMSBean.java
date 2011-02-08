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
package org.tolven.queue.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.management.MalformedObjectNameException;
import javax.naming.InitialContext;

import javax.jms.Connection;
import javax.jms.ObjectMessage;
import javax.jms.Message;

import org.tolven.doc.bean.TolvenMessage;
import org.tolven.logging.TolvenLogger;
import org.tolven.queue.JMSRemote;
import org.tolven.queue.entity.MessageInfo;
import org.tolven.queue.entity.QueueInfo;

/**
 * The session bean for getting  JMS Queue Statistics.
 * 
 * @author Sabu Antony
 */
@Stateless
@Remote(JMSRemote.class)
public class JMSBean implements JMSRemote, Serializable {

    String[] currentQueueNames = {
            "invitation",
            "invitationDLQ",
            "generator",
            "generatorDLQ",
            "rule",
            "ruleDLQ",
            "adminApp",
            "adminAppDLQ" };

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    private static final long serialVersionUID = 1L;

    /*
     * Return a list of all Messages inside a queue given search time. return
     * List return only TolvenMessage Object.
     * 
     * @see org.tolven.queue.bean.JMSLocal#getAllMesages(String queueName)
     */
    public List<TolvenMessage> getAllTolvenMessages(String queueJNDIName) throws Exception {
        List<TolvenMessage> msgList = new ArrayList<TolvenMessage>();
        try {
            Connection connFactory = connectionFactory.createConnection();
            javax.jms.Session queueSession = connFactory.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            // create a queue browser
            InitialContext ctx = new InitialContext();
            Queue queueObj = (Queue) ctx.lookup(queueJNDIName);

            QueueBrowser queueBrowser = queueSession.createBrowser(queueObj);
            // browse the messages
            Enumeration<Message> e = queueBrowser.getEnumeration();
            int numMsgs = 0;
            // count number of messages
            while (e.hasMoreElements()) {
                Message ms = (Message) e.nextElement();
                TolvenMessage ts = (TolvenMessage) ((ObjectMessage) ms).getObject();
                msgList.add(ts);
                numMsgs++;
            }
        } catch (Exception e) {
            TolvenLogger.info(" JMSBean getAllTolvenMessages  failed with error: " + e.getMessage(), JMSBean.class);
            //					e.printStackTrace();
        }
        return msgList;

    }

    /**
     * 
     * @param queueName
     * @return
     * @throws Exception
     */
    public List<MessageInfo> getMessages(String queueJNDIName) throws Exception {
        List<MessageInfo> msgList = new ArrayList<MessageInfo>();
        Connection connFactory = connectionFactory.createConnection();
        javax.jms.Session queueSession = connFactory.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
        QueueBrowser queueBrowser = null;
        try {
            InitialContext ctx = new InitialContext();
            Queue queueObj = (Queue) ctx.lookup(queueJNDIName);
            // create a queue browser
            queueBrowser = queueSession.createBrowser(queueObj);
            // browse the messages
            Enumeration<Message> e = queueBrowser.getEnumeration();
            while (e.hasMoreElements()) {
                Message jmsMessageObj = (Message) e.nextElement();
                MessageInfo msgInfo = new MessageInfo();
                msgInfo.setCorrelationID(jmsMessageObj.getJMSCorrelationID());
                msgInfo.setJmsType(jmsMessageObj.getJMSType());
                msgInfo.setJmsDeliveryMode(jmsMessageObj.getJMSDeliveryMode());
                msgInfo.setJmsPriority(jmsMessageObj.getJMSPriority());
                msgInfo.setJmsMessageID(jmsMessageObj.getJMSMessageID());
                msgInfo.setJmsRedelivered(jmsMessageObj.getJMSRedelivered());
                msgInfo.setJmsTimestamp(jmsMessageObj.getJMSTimestamp());
                TolvenMessage ts = (TolvenMessage) ((ObjectMessage) jmsMessageObj).getObject();
                msgInfo = (MessageInfo) BeanPropertyLoader.populate(msgInfo, ts);
                msgInfo.setPayloadSize(ts.getPayload().length);
                msgList.add(msgInfo);
            }
        } finally {
            if (queueBrowser != null)
                queueBrowser.close();
            queueSession.close();
            connFactory.close();
        }
        return msgList;
    }

    /**
     * 
     * @return
     * @throws NullPointerException 
     * @throws MalformedObjectNameException 
     * @throws Exception
     */
    public List<QueueInfo> getActiveQueueNames() {
        List<QueueInfo> queueStatalist = new ArrayList<QueueInfo>();
        for (String queueName : currentQueueNames) {
            QueueInfo queueInfo = new QueueInfo();
            queueInfo.setName(queueName);
            queueStatalist.add(queueInfo);
        }
        return queueStatalist;
    }

}
