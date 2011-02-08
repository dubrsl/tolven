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
package org.tolven.doc.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.tolven.doc.RuleQueueLocal;
import org.tolven.doc.TolvenMessageSchedulerLocal;

/**
 * This class allows TolvenMessages to be scheduled and/or queued for processing.
 * 
 * @author Joseph Isaac
 *
 */
@Stateless
@Local(TolvenMessageSchedulerLocal.class)
public class TolvenMessageScheduler implements TolvenMessageSchedulerLocal {

    public static final int QUEUE_PROCESS_LIMIT = 100;

    @Resource
    private EJBContext ejbContext;

    @PersistenceContext
    private EntityManager em;

    @EJB
    private RuleQueueLocal ruleQueueBean;

    private Logger logger = Logger.getLogger(TolvenMessageScheduler.class);

    @Override
    public void setScheduler(long interval) {
        stopScheduler();
        sleep(interval);
        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.SECOND, 20);
        TolvenMessage tm = new TolvenMessage();
        tm.setMediaType("text/xml");
        tm.setXmlNS("none");
        schedule(tm, cal.getTime());
    }

    @Override
    public void stopScheduler() {
        for (Object obj : ejbContext.getTimerService().getTimers()) {
            Timer timer = (Timer) obj;
            timer.cancel();
        }
    }

    @Override
    public Date getNextTimeout() {
        Timer timer = null;
        for (Object obj : ejbContext.getTimerService().getTimers()) {
            timer = (Timer) obj;
            break;
        }
        if (timer == null) {
            return null;
        } else {
            return timer.getNextTimeout();
        }
    }

    /**
     * Find a TolvenMessage by id
     * 
     * @param id
     * @return
     */
    @Override
    public TolvenMessage findTolvenMessage(long id) {
        return em.find(TolvenMessage.class, id);
    }

    /**
     * Queue a TolvenMessage.
     * @param tm
     */
    @Override
    public void queue(TolvenMessage tm) {
        schedule(tm, new HashMap<String, Object>(0), null);
    }

    /**
     * Queue a list of TolvenMessages.
     * One connection is used to send the List of TolvenMessages
     * @param tms
     */
    @Override
    public void queue(List<TolvenMessage> tms) {
        schedule(tms, new ArrayList<Map<String, Object>>(0), null);
    }

    /**
     * Queue a TolvenMessage along with its JMS properties
     * @param tm
     * @param properties
     */
    @Override
    public void queue(TolvenMessage tm, Map<String, Object> properties) {
        schedule(tm, properties, null);
    }

    /**
     * Queue a list of TolvenMessages along with their JMS properties. Each TolvenMessage must have a corresponding
     * properties map, even if that map is empty
     * A separate connection is used for each payload
     * @param tms
     * @param listOfProperties
     */
    @Override
    public void queue(List<TolvenMessage> tms, List<Map<String, Object>> listOfProperties) {
        schedule(tms, listOfProperties, null);
    }

    /**
     * Schedule a TolvenMessage to be queued on queueOnDate
     * @param tm
     * @param queueOnDate
     */
    @Override
    public void schedule(TolvenMessage tm, Date queueOnDate) {
        schedule(tm, new HashMap<String, Object>(0), queueOnDate);
    }

    /**
     * Schedule a message to a queued on queueOnDate, with properties for the JMS message
     * If the queueOnDate is null, then the message is queued immediately
     * @param tm
     * @param properties
     * @param queueOnDate
     */
    @Override
    public void schedule(TolvenMessage tm, Map<String, Object> properties, Date queueOnDate) {
        List<TolvenMessage> tms = new ArrayList<TolvenMessage>(1);
        tms.add(tm);
        List<Map<String, Object>> listOfProperties = new ArrayList<Map<String, Object>>(1);
        listOfProperties.add(properties);
        schedule(tms, listOfProperties, queueOnDate);
    }

    /**
     * Schedule a list of TolvenMessages to be queued on queueOnDate, along with their JMS properties.
     * If the queueOnDate is null, then the message is queued immediately
     * @param tms
     * @param listOfProperties
     * @param queueOnDate
     */
    public void schedule(List<TolvenMessage> tms, List<Map<String, Object>> listOfProperties, Date queueOnDate) {
        //timeNow should be passed to the EJB tier
        if (listOfProperties != null && !listOfProperties.isEmpty() && listOfProperties.size() != tms.size()) {
            throw new RuntimeException("The listOfProperties length does not match the number of TolvenMessages");
        }
        Date now = new Date();
        for (int i = 0; i < tms.size(); i++) {
            TolvenMessage tm = tms.get(i);
            if (listOfProperties != null && !listOfProperties.isEmpty()) {
                Map<String, Object> properties = listOfProperties.get(i);
                for (String key : properties.keySet()) {
                    TolvenMessageProperty tmp = new TolvenMessageProperty(key);
                    tmp.setObject(properties.get(key));
                    tm.getProperties().add(tmp);
                }
            }
            tm.setScheduleDate(now);
            em.persist(tm);
            if (queueOnDate != null) {
                tm.setQueueOnDate(queueOnDate);
                logger.info("TolvenMessage " + tm.getId() + " scheduled for " + ruleQueueBean.getQueueName() + " at " + queueOnDate);
            }
        }
        if (queueOnDate == null) {
            queueMessages(tms, now);
        }
    }

    /**
     * 
     * @param timer
     * @throws Exception
     */
    @Timeout
    public void timeout(Timer timer) {
        //logger.info("TIMEOUT-TIMEOUT-TIMEOUT-TIMEOUT-TIMEOUT-TIMEOUT-TIMEOUT-TIMEOUT-TIMEOUT-TIMEOUT");
        boolean moreMessagesToQueue = queueMessages();
        if (moreMessagesToQueue) {
            sleep(0);
        } else {
            sleep((Long) timer.getInfo());
        }
    }

    private void sleep(long interval) {
        ejbContext.getTimerService().createTimer(interval, new Long(interval));
    }

    private boolean queueMessages() {
        Date now = new Date();
        Query query = em.createQuery("SELECT tm FROM TolvenMessage tm WHERE " + "tm.scheduleDate is NOT NULL AND " + "(tm.queueOnDate is NULL or tm.queueOnDate <= :now) AND " + "tm.queueDate is NULL AND " + "tm.processDate is NULL AND " + "(tm.deleted IS NULL OR tm.deleted = false)");
        query.setParameter("now", now);
        query.setMaxResults(QUEUE_PROCESS_LIMIT);
        List<TolvenMessage> tms = query.getResultList();
        return queueMessages(tms, now);
    }

    private boolean queueMessages(List<TolvenMessage> tms, Date now) {
        if (!tms.isEmpty()) {
            //logger.info("SCHEDULE_MESSAGES-SCHEDULE_MESSAGES-SCHEDULE_MESSAGES-SCHEDULE_MESSAGES-SCHEDULE_MESSAGES");
            for (TolvenMessage tm : tms) {
                Map<String, Object> properties = new HashMap<String, Object>();
                for (TolvenMessageProperty tmp : tm.getProperties()) {
                    properties.put(tmp.getPropertyName(), tmp.getObject());
                }
                ruleQueueBean.send(tm, properties);
                tm.setQueueDate(now);
                logger.info("TolvenMessage " + tm.getId() + " queued to " + ruleQueueBean.getQueueName() + " at " + now);
            }
        }
        if (tms.size() >= QUEUE_PROCESS_LIMIT) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Unwrap TolvenMessage from an objectMessage
     * @param objectMessage
     */
    @Override
    public TolvenMessage unwrapTolvenMessage(ObjectMessage objectMessage) {
        TolvenMessage tm = null;
        Object payload;
        try {
            payload = objectMessage.getObject();
        } catch (JMSException ex) {
            throw new RuntimeException("Could not obtain object from ObjectMessage", ex);
        }
        if (payload instanceof TolvenMessageInfo) {
            TolvenMessageInfo info = (TolvenMessageInfo) payload;
            tm = findTolvenMessage(info.getId());
            if (tm == null) {
                throw new RuntimeException("TolvenMessage sent with property " + TolvenMessage.TOLVENMESSAGE_ID + "=" + info.getId() + ", but not in TolvenMessage table");
            }
        } else if (payload instanceof TolvenMessage) {
            TolvenMessage rawTM = (TolvenMessage) payload;
            tm = findTolvenMessage(rawTM.getId());
            if (tm == null) {
                if (rawTM.getId() != 0) {
                    throw new RuntimeException("TolvenMessage has id: " + rawTM.getId() + ", but could not be found in TolvenMessage table");
                }
            }
            tm = rawTM;
            em.persist(tm);
        }
        return tm;
    }

}
