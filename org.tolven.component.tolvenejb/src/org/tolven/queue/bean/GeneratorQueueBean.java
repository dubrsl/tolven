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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.tolven.gen.GeneratorQueueLocal;

@Stateless
@Local(GeneratorQueueLocal.class)
public class GeneratorQueueBean implements GeneratorQueueLocal {

    @Resource(name = "jms/JmsXA")
    private ConnectionFactory connectionFactory;

    @Resource(name = "queue/generator")
    private Queue generatorQueue;

    private String queueName;

    @Override
    public String getQueueName() {
        if (queueName == null) {
            try {
                queueName = generatorQueue.getQueueName();
            } catch (JMSException ex) {
                throw new RuntimeException("Failed to get queue name", ex);
            }
        }
        return queueName;
    }

    /**
     * Queue a payload.
     * @param payload
     */
    @Override
    public void send(Serializable payload) {
        List<Serializable> payloads = new ArrayList<Serializable>(1);
        payloads.add(payload);
        send(payloads);
    }

    /**
     * Queue a list of payloads.
     * One connection is used to send the List of payloads
     * @param payloads
     */
    @Override
    public void send(List<Serializable> payloads) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(generatorQueue);
            ObjectMessage message = null;
            for (Serializable payload : payloads) {
                message = session.createObjectMessage(payload);
                messageProducer.send(message);
            }
        } catch (JMSException ex) {
            throw new RuntimeException("Failed to send " + payloads.size() + " payload(s) to queue: " + getQueueName(), ex);
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

}
