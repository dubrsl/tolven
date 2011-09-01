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
package org.tolven.session;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.StoppedSessionException;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.ValidatingSession;

public class DefaultTolvenSession implements TolvenSession, ValidatingSession, Serializable {

    protected static final long MILLIS_PER_SECOND = 1000;
    protected static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    protected static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    private String id;
    private Date startTimestamp;
    private Date lastAccessTime;
    private Date stopTimestamp;
    private long timeout;
    private String host;
    private boolean expired;
    private Map<Object, Object> attributes;

    public DefaultTolvenSession() {
        this.timeout = DefaultSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT; //TODO - remove concrete reference to DefaultSessionManager
        this.startTimestamp = new Date();
        this.lastAccessTime = this.startTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DefaultTolvenSession))
            return false;
        DefaultTolvenSession other = (DefaultTolvenSession) obj;
        return other.getId().equals(getId());
    }

    protected void expire() {
        stop();
        this.expired = true;
    }

    @Override
    public Object getAttribute(Object key) {
        Map<Object, Object> attributes = getAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.get(key);
    }

    @Override
    public Collection<Object> getAttributeKeys() {
        Map<Object, Object> attributes = getAttributes();
        if (attributes == null) {
            return Collections.emptySet();
        }
        return attributes.keySet();
    }

    public Map<Object, Object> getAttributes() {
        return attributes;
    }

    private Map<Object, Object> getAttributesLazy() {
        Map<Object, Object> attributes = getAttributes();
        if (attributes == null) {
            attributes = new HashMap<Object, Object>();
            setAttributes(attributes);
        }
        return attributes;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public Date getStopTimestamp() {
        return stopTimestamp;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    public boolean isExpired() {
        return expired;
    }

    protected boolean isStopped() {
        return getStopTimestamp() != null;
    }

    protected boolean isTimedOut() {

        if (isExpired()) {
            return true;
        }

        long timeout = getTimeout();

        if (timeout >= 0l) {

            Date lastAccessTime = getLastAccessTime();

            if (lastAccessTime == null) {
                String msg = "session.lastAccessTime for session with id [" + getId() + "] is null.  This value must be set at " + "least once, preferably at least upon instantiation.  Please check the " + getClass().getName() + " implementation and ensure " + "this value will be set (perhaps in the constructor?)";
                throw new IllegalStateException(msg);
            }
            long expireTimeMillis = System.currentTimeMillis() - timeout;
            Date expireTime = new Date(expireTimeMillis);
            return lastAccessTime.before(expireTime);
        }

        return false;
    }

    @Override
    public boolean isValid() {
        return !isStopped() && !isExpired();
    }

    @Override
    public void logout() {
        stop();
    }

    @Override
    public Object removeAttribute(Object name) {
        Map<Object, Object> attributes = getAttributes();
        if (attributes == null) {
            return null;
        } else {
            return attributes.remove(name);
        }
    }

    @Override
    public void setAttribute(Object name, Object value) {
        if (value == null) {
            removeAttribute(name);
        } else {
            getAttributesLazy().put(name, value);
        }
    }

    public void setAttributes(Map<Object, Object> attributes) {
        this.attributes = attributes;
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

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void stop() {
        if (this.stopTimestamp == null) {
            this.stopTimestamp = new Date();
        }
    }

    @Override
    public void touch() {
        this.lastAccessTime = new Date();
    }

    @Override
    public void validate() throws InvalidSessionException {
        //check for stopped:
        if (isStopped()) {
            //timestamp is set, so the session is considered stopped:
            String msg = "Session with id [" + getId() + "] has been " + "explicitly stopped.  No further interaction under this session is " + "allowed.";
            throw new StoppedSessionException(msg);
        }

        //check for expiration
        if (isTimedOut()) {
            expire();

            //throw an exception explaining details of why it expired:
            Date lastAccessTime = getLastAccessTime();
            long timeout = getTimeout();

            Serializable sessionId = getId();

            DateFormat df = DateFormat.getInstance();
            String msg = "Session with id [" + sessionId + "] has expired. " + "Last access time: " + df.format(lastAccessTime) + ".  Current time: " + df.format(new Date()) + ".  Session timeout is set to " + timeout / MILLIS_PER_SECOND + " seconds (" + timeout / MILLIS_PER_MINUTE + " minutes)";
            throw new ExpiredSessionException(msg);
        }
    }

}
