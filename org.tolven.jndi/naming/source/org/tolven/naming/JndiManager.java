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
 * @version $Id: JndiManager.java 1779 2011-07-22 04:31:50Z joe.isaac $
 */
package org.tolven.naming;

import java.util.Properties;

public interface JndiManager {

    public static final String TOLVEN_ID_REF = "tolven.contextId";
    public static final String REALM_IDS_REF = "realm.ids";
    public static final String WEB_IDS_REF = "web.ids";
    public static final String QUEUE_IDS_REF = "queue.ids";

    public Properties getJndiProperties(Properties srcProperties);

}
