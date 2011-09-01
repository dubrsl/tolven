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
package org.tolven.client.load;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.tolven.queue.entity.QueueInfo;
import org.tolven.restful.client.RESTfulClient;

/**
 * The Cient for JMSBean Interface..
 * 
 * @author Sabu Antony
 */
public class LoadQueueStatistics extends RESTfulClient {

    //private RMIAdaptor rmiAdaptor;

    public LoadQueueStatistics() {
    }
/*
    private RMIAdaptor getRMIAdaptor() throws NamingException {
        if (rmiAdaptor == null) {
            InitialContext ctx = getCtx();
            rmiAdaptor = (RMIAdaptor) ctx.lookup("jmx/rmi/RMIAdaptor");
        }
        return rmiAdaptor;
    }
*/
    /*
    public List<QueueInfo> findQueueStatistics() throws Exception {
        return getStatistics();
    }
*/
    /*
     * Return a List of QueueInfo Object. Each QueueInfo object represents a
     * Queue in the server
     * 
     * @see org.tolven.queue.bean.JMSLocal#getStatistics( )
     */
    /*
    public List<QueueInfo> getStatistics() throws Exception {
        List<QueueInfo> queueStatalist = getJmsBean().getActiveQueueNames();

        for (QueueInfo queueInf : queueStatalist) {
            queueInf.setMessageCount(findPendingCount(queueInf.getName()));
            queueInf.setJndiName(findJndiName(queueInf.getName()));
        }
        return queueStatalist;
    }
    */

    /*
     * To get the queue depth /message count
     * 
     * @see org.tolven.queue.bean.JMSLocal#findPendingCount(String queueName)
     */
    /*
    public String findJndiName(String queueName) throws Exception {
        ObjectName objectName = null;
        objectName = new ObjectName("jboss.messaging.destination:name=" + queueName + ",service=Queue");
        String jndiName = ((getRMIAdaptor().getAttribute(objectName, "JNDIName")).toString());
        return jndiName;
    }
*/
    /*
     * To get the queue depth /message count
     * 
     * @see org.tolven.queue.bean.JMSLocal#findPendingCount(String queueName)
     */
    /*
    public int findPendingCount(String queueName) throws Exception {
        ObjectName objectName = null;
        objectName = new ObjectName("jboss.messaging.destination:name=" + queueName + ",service=Queue");
        int count = Integer.parseInt((getRMIAdaptor().getAttribute(objectName, "MessageCount")).toString());
        return count;
    }
*/
    /**
     * 
     * @param queueName
     * @return
     * @throws Exception
     */
    /*
    public List<MessageInfo> getMessages(String queueName) throws Exception {
        return getJmsBean().getMessages(findJndiName(queueName));
    }
*/
    public List<QueueInfo> getAllQueueNames() throws Exception {
        //does nothing for now...not sure this class is even used anymore. Otherwise it needs to conform to the RESTful API
        //return getJmsBean().getActiveQueueNames();
        return new ArrayList<QueueInfo>();
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    /*
    public Queue getQueue(String queueName) throws Exception {
        Queue queue = null;
        ObjectName objectName = null;
        objectName = new ObjectName("jboss.messaging:service=ServerPeer");
        java.util.Set<Object> queueSet = (java.util.Set<Object>) (getRMIAdaptor().getAttribute(objectName, "Destinations"));
        if (queueSet != null) {
            for (Object dest : queueSet) {
                if (dest.getClass().getSimpleName().equals("JBossQueue") && ((Queue) dest).getQueueName().equals(queueName)) {
                    return queue;

                }
            }
        }
        return queue;
    }
*/
    /**
     * @param args
     * @throws NamingException
     * @throws SystemException
     * @throws NotSupportedException
     */
    public static void main(String[] args) throws Exception {
        //		LoadQueueStatistics lp = new LoadQueueStatistics();
        //		lp.getMessages("rule");
        //		for(QueueInfo queueInf:quelist){
        //			
        //			TolvenLogger.info(queueInf.getName()+"   jndi name  "+queueInf.getJndiName()+ "couny  :"+queueInf.getMessageCount(), LoadQueueStatistics.class);
        //			
        //		}

    }

}
