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
package org.tolven.naming;

import java.util.List;

/**
 * This class allows the parameters to be configured externally via JNDI
 * 
 * @author Joseph Isaac
 *
 */
public interface TolvenContext extends TolvenJndiContext {

    public Object getHtmlGatekeeperWebContext();

    public Object getQueueContext(String queueId);

    public List<String> getQueueIds();

    public Object getRealmContext(String realm);

    public List<String> getRealmIds();

    public Object getRsGatekeeperWebContext();

    public String getSsoCookieDomain();

    public String getSsoCookieMaxAge();

    public String getSsoCookieName();

    public String getSsoCookiePath();

    public String getSsoCookieSecure();

    public Object getWebContext(String webId);

    public List<String> getWebIds();

    public Object getWsGatekeeperWebContext();

}
