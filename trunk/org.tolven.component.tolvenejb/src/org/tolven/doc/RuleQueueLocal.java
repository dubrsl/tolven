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
package org.tolven.doc;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface RuleQueueLocal {
    
    public String getQueueName();

    /**
     * Queue a payload
     * @param payload
     */
    public void send(Serializable payload);

    /**
     * Queue payloads
     * One connection is used to send the List of payloads
     * @param payloads
     */
    public void send(List<Serializable> payloads);

    /**
     * Queue a payload along with its JMS properties
     * A separate connection is used for each payload
     * @param payload
     * @param properties
     */
    public void send(Serializable payload, Map<String, Object> properties);

    /**
     * Queue a list of payloads along with their JMS properties. Each payload must have a corresponding
     * properties map, even if that map is empty
     * One connection is used to send the List of payloads
     * @param payloads
     * @param listOfProperties
     */
    public void send(List<Serializable> payloads, List<Map<String, Object>> listOfProperties);
    
}
