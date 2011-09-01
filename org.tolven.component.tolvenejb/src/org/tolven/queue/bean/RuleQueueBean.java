/*
 * Copyright (C) 2009 Tolven Inc

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
 * @author Joseph Isaac
 * @version $Id$
 */
package org.tolven.queue.bean;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.tolven.doc.RuleQueueLocal;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageInfo;
import org.tolven.key.QueueKeyLocal;

@Stateless
@Local(RuleQueueLocal.class)
public class RuleQueueBean implements RuleQueueLocal {

    @Resource(name = "jms/JmsXA")
    private ConnectionFactory connectionFactory;

    @EJB
    private QueueKeyLocal queueKeyLocal;

    private String queueName;
    
    @Resource(name = "queue/rule")
    private Queue ruleQueue;

    @Override
    public String getQueueName() {
        if (queueName == null) {
            try {
                queueName = ruleQueue.getQueueName();
            } catch (JMSException ex) {
                throw new RuntimeException("Failed to get queue name", ex);
            }
        }
        return queueName;
    }

    @Override
    public X509Certificate getQueueOwnerX509Certificate() {
        return queueKeyLocal.getUserX509Certificate(getQueueName());
    }

    /**
     * Queue payloads
     * One connection is used to send the List of payloads
     * @param payloads
     */
    @Override
    public void send(List<Serializable> payloads) {
        send(payloads, null);
    }

    /**
     * Queue a list of payloads along with their JMS properties. Each payload must have a corresponding
     * properties map, even if that map is empty
     * One connection is used to send the List of payloads
     * @param payloads
     * @param listOfProperties
     */
    @Override
    public void send(List<Serializable> payloads, List<Map<String, Object>> listOfProperties) {
        Connection connection = null;
        try {
            if (listOfProperties != null && !listOfProperties.isEmpty() && listOfProperties.size() != payloads.size()) {
                throw new RuntimeException("The listOfProperties length does not match the number of payloads");
            }
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(ruleQueue);
            for (int i = 0; i < payloads.size(); i++) {
                Serializable payload = payloads.get(i);
                boolean isTolvenMessage = payload instanceof TolvenMessage;
                ObjectMessage message = null;
                if (isTolvenMessage) {
                    TolvenMessage tm = (TolvenMessage) payload;
                    TolvenMessageInfo info = new TolvenMessageInfo(tm.getId());
                    message = session.createObjectMessage(info);
                } else {
                    message = session.createObjectMessage(payload);
                }
                if (listOfProperties != null && !listOfProperties.isEmpty()) {
                    Map<String, Object> properties = listOfProperties.get(i);
                    for (String key : properties.keySet()) {
                        message.setObjectProperty(key, properties.get(key));
                    }
                }
                messageProducer.send(message);
            }
        } catch (JMSException ex) {
            throw new RuntimeException("Failed to send payload to queue: " + getQueueName(), ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                throw new RuntimeException("Failed to close connection to queue" + getQueueName(), e);
            }
        }
    }

    /**
     * Queue a payload
     * @param payload
     */
    @Override
    public void send(Serializable payload) {
        List<Serializable> payloads = new ArrayList<Serializable>(1);
        payloads.add(payload);
        send(payloads, null);
    }

    /**
     * Queue a payload along with its JMS properties
     * @param payload
     * @param properties
     */
    @Override
    public void send(Serializable payload, Map<String, Object> properties) {
        List<Serializable> payloads = new ArrayList<Serializable>(1);
        payloads.add(payload);
        List<Map<String, Object>> listOfProperties = new ArrayList<Map<String, Object>>(1);
        listOfProperties.add(properties);
        send(payloads, listOfProperties);
    }

}
