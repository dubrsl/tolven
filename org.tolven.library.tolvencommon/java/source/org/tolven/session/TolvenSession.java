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
import java.util.Collection;
import java.util.Date;

/**
 * This class provides a consistent interface to access session related information
 * 
 * @author Joseph Isaac
 *
 */
public interface TolvenSession {

    public Object getAttribute(Object name);

    public Collection<Object> getAttributeKeys();

    public String getHost();

    public Serializable getId();

    public Date getLastAccessTime();

    public Date getStartTimestamp();

    public long getTimeout();

    public Object removeAttribute(Object name);

    public void setAttribute(Object name, Object value);

    public void setTimeout(long timeout);

    public void stop();

    public void touch();

    public void logout();

}
