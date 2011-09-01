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
 */
package org.tolven.shiro.session.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tolven.session.TolvenSessionState;

@Entity
public class TolvenSimpleSession implements TolvenSessionState, Serializable {

    @Lob
    private String encryptedAttributes;

    private boolean expired;
    private String host;

    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAccessTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTimestamp;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopTimestamp;

    private long timeout;

    public TolvenSimpleSession() {
    }

    public String getEncryptedAttributes() {
        return encryptedAttributes;
    }

    public String getHost() {
        return host;
    }

    public String getId() {
        return id;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public Date getStopTimestamp() {
        return stopTimestamp;
    }

    public long getTimeout() {
        return timeout;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setEncryptedAttributes(String encryptedAttributes) {
        this.encryptedAttributes = encryptedAttributes;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setStopTimestamp(Date stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
