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

import java.util.Date;

public interface TolvenSessionState {

    public String getEncryptedAttributes();

    public String getHost();

    public String getId();

    public Date getLastAccessTime();;

    public Date getStartTimestamp();

    public Date getStopTimestamp();

    public long getTimeout();

    public boolean isExpired();

    public void setEncryptedAttributes(String encryptedAttributes);

    public void setExpired(boolean expired);

    public void setHost(String host);

    public void setId(String id);

    public void setLastAccessTime(Date lastAccessTime);

    public void setStartTimestamp(Date startTimestamp);

    public void setStopTimestamp(Date stopTimestamp);

    public void setTimeout(long timeout);

}
