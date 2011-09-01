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
package org.tolven.queue;

import java.util.List;
import org.tolven.queue.entity.MessageInfo;
import org.tolven.queue.entity.QueueInfo;
/**
 * The interface for the session bean for keeping track of performance measures
 * for all request calls.
 * 
 * @author Sabu Antony
 */
public interface JMSRemote {
	
	public List<MessageInfo> getMessages(String queueName) throws Exception;
    public List<QueueInfo> getActiveQueueNames();
}
