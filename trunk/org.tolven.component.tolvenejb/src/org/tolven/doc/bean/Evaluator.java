/*
 *  Copyright (C) 2007 Tolven Inc 
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
package org.tolven.doc.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.interceptor.Interceptors;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.tolven.app.JMSMessageProcessorLocal;
import org.tolven.app.MessageProcessorLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.doc.TolvenMessageSchedulerLocal;
import org.tolven.session.QueueSessionInterceptor;
import org.tolven.util.ExceptionFormatter;
/**
 * Evaluate new documents that have arrived. We do this after a document is safely stored and in the
 * active state. In most cases, we simple create/update indexes be we can do other things like create new documents.
 * However, in most cases, we must limit what a document affects to the account in which the document lives.
 * @author John Churin
 */

@MessageDriven
@Interceptors({ QueueSessionInterceptor.class })
public class Evaluator implements MessageListener {
    
	protected @EJB TolvenPropertiesLocal propertyBean;
    protected @EJB TolvenMessageSchedulerLocal tmSchedulerBean;

	protected @Resource MessageDrivenContext ctx;
    
    private String messageProcessors[];
    
    public final static String PROCESSOR_NAME = "processorJNDI";
	private Logger logger = Logger.getLogger(this.getClass());

    /**
     * Find the list of message evaluators we are using.
     */
    public void initializeMessageProcessors() {
    	try {
			if (messageProcessors==null) {
				Properties properties = new Properties();
				String propertyFileName = this.getClass().getSimpleName()+".properties"; 
				properties.load(this.getClass().getResourceAsStream(propertyFileName));
				String processorNames = properties.getProperty(PROCESSOR_NAME);
				List<String> arr = new ArrayList<String>();
				for(String processorName : processorNames.split(",")) {
				    arr.add(processorName);
				}
				messageProcessors = new String[arr.size()];
				arr.toArray(messageProcessors);
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error initializing message processors", e);
		}
    }
    
    /**
     * Given a message, ask each message process requesting to process that message (there is usually just one) to process the message.
     * @param message
     */
    public void dispatchToMessageProcessors( ObjectMessage message ) throws Exception {
    	initializeMessageProcessors();
        if (messageProcessors.length==0) throw new RuntimeException( "No message processor found for " + message.getClass().getName());
    	Object payload = message.getObject();
    	TolvenMessage tm = tmSchedulerBean.unwrapTolvenMessage(message);
    	if (tm != null) {
            if (tm.getProcessDate() != null) {
                throw new RuntimeException("TolvenMessage " + tm.getId() + " already has a processDate:" + tm.getProcessDate());
            }
            payload = tm;
        }
		Date now = new Date();
        InitialContext ctx = new InitialContext();
    	for (String messageProcessor : messageProcessors ) {
    	    Object obj = ctx.lookup(messageProcessor);
    	    if(obj instanceof JMSMessageProcessorLocal) {
    	        JMSMessageProcessorLocal mpBean = (JMSMessageProcessorLocal) obj;
                mpBean.process( message, now );
    	    } else {
                MessageProcessorLocal mpBean = (MessageProcessorLocal) obj;
                mpBean.process( payload, now );
    	    }
    	}
    	if(tm != null) {
            tm.setProcessDate(now);
    	}
    }

    /**
     * Process one message from the Rule queue
     */
    public void onMessage(Message msg)  {
		String msgId = null;
		try {
	    	msgId = msg.getJMSMessageID();
    		logger.info("[Start] MsgID: " + msgId + " thread: " + Thread.currentThread().getId());
			if (msg instanceof ObjectMessage ) {
				dispatchToMessageProcessors( (ObjectMessage)msg );
			} else {
		    	throw new RuntimeException( "Message is not typed - rejected " + msg.getJMSMessageID());
			}
			
		} catch (Exception e) {
			ctx.setRollbackOnly();
			String errorString = ExceptionFormatter.toSimpleString(e, "\n");
			logger.error( "Message " + msgId + " failed:\n" + errorString + "\nMessage will be rolled back to the Queue and Read/Consumed again");
			throw new RuntimeException(e);
		}
		finally {
			logger.info( "[End] MsgID: " +  msgId + " thread: " + Thread.currentThread().getId());
		}
	}
}
