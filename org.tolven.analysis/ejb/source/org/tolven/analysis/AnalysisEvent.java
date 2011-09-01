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
 * @version $Id: AnalysisEvent.java 486 2011-03-29 00:51:12Z kanag.kuttiannan $
 */
package org.tolven.analysis;

import javax.jms.JMSException;
import javax.jms.Message;

public class AnalysisEvent {

    private Message message;
    private String command;
    private String cohortType;
    private String snapshotType;

    public AnalysisEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getCommand() {
        try {
            if (command == null) {
                command = getMessage().getStringProperty("command");
            }
            return command;
        } catch (JMSException ex) {
            throw new RuntimeException("Could not obtain the command string", ex);
        }
    }

    public String getCohortType() {
        try {
            if (cohortType == null) {
                cohortType = getMessage().getStringProperty("cohortType");
            }
            return cohortType;
        } catch (JMSException ex) {
            throw new RuntimeException("Could not obtain the cohortType string", ex);
        }
    }

    public void setCohortType(String cohortType) {
        this.cohortType = cohortType;
    }

    public String getSnapshotType() {
        try {
            if (snapshotType == null) {
                snapshotType = getMessage().getStringProperty("snapshotType");
            }
            return snapshotType;
        } catch (JMSException ex) {
            throw new RuntimeException("Could not obtain the snapshotType string", ex);
        }
    }

}
