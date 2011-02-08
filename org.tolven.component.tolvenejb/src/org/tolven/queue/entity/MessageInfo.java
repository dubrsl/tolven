
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
package org.tolven.queue.entity;


import java.io.Serializable;
import java.util.Enumeration;
/**
 * DTO for the Message oject .
 * This has got a  Properties from TolvenMessage and  javax.jms.Message 
 * 
 * @author Sabu Antony
 * 
 */
public class MessageInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long payloadSize;
	private String xmlNS;
	private String xmlName;
	private long accountId;
	private long fromAccountId;
	private String sender;
	private String recipient;
	private long authorId;
	private long documentId;
	private String correlationID;
	private int jmsDeliveryMode;
	
//	private Destination jmsDestination;
	private String  jmsMessageID ;
	
	private int 	jmsPriority; 
	private boolean 	jmsRedelivered;
//	private  Destination jmsReplyTo;
	private  long 	jmsTimestamp;
	private  String 	jmsType;
	private Enumeration propertyNames; 
	
	private boolean active; 
	
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getCorrelationID() {
		return correlationID;
	}
	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}
	public int getJmsDeliveryMode() {
		return jmsDeliveryMode;
	}
	public void setJmsDeliveryMode(int jmsDeliveryMode) {
		this.jmsDeliveryMode = jmsDeliveryMode;
	}
	public String getJmsMessageID() {
		return jmsMessageID;
	}
	public void setJmsMessageID(String jmsMessageID) {
		this.jmsMessageID = jmsMessageID;
	}
	public int getJmsPriority() {
		return jmsPriority;
	}
	public void setJmsPriority(int jmsPriority) {
		this.jmsPriority = jmsPriority;
	}
	public boolean isJmsRedelivered() {
		return jmsRedelivered;
	}
	public void setJmsRedelivered(boolean jmsRedelivered) {
		this.jmsRedelivered = jmsRedelivered;
	}
	public long getJmsTimestamp() {
		return jmsTimestamp;
	}
	public void setJmsTimestamp(long jmsTimestamp) {
		this.jmsTimestamp = jmsTimestamp;
	}
	public String getJmsType() {
		return jmsType;
	}
	public void setJmsType(String jmsType) {
		this.jmsType = jmsType;
	}
	public Enumeration getPropertyNames() {
		return propertyNames;
	}
	public void setPropertyNames(Enumeration propertyNames) {
		this.propertyNames = propertyNames;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getXmlName() {
		return xmlName;
	}
	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}
	public String getXmlNS() {
		return xmlNS;
	}
	public void setXmlNS(String xmlNS) {
		this.xmlNS = xmlNS;
	}
	public long getAuthorId() {
		return authorId;
	}
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	/**
	 * Specify that the target document, which should be in Active status and thus be immutable.
	 * the evaluator won't actually try to access the document contents directly since it will probably
	 * be encrypted under some else's id.
	 * If documentId is zero, then the evaluator will create a new document containing the payload of this
	 * message (creating a document is not subject to the same encryption restriction as reading it).
	 * @return
	 */
	public long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}
	public long getFromAccountId() {
		return fromAccountId;
	}
	public void setFromAccountId(long fromAccountId) {
		this.fromAccountId = fromAccountId;
	}
	public long getPayloadSize() {
		return payloadSize;
	}
	public void setPayloadSize(long payloadSize) {
		this.payloadSize = payloadSize;
	}
	
   

}
