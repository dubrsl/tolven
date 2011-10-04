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
 * @version $Id: LdapManagerFactory.java 3245 2011-09-14 09:12:36Z joe.isaac $
 */
package org.tolven.ldap;

import java.util.Hashtable;
import java.util.Properties;

public class LdapManagerFactory {

    public static LdapManager getInstance(Hashtable<String, String> env) {
        DefaultLdapManager ldapManager = new DefaultLdapManager();
        ldapManager.setEnvironment(env);
        return ldapManager;
    }

    public static LdapManager getInstance(Properties props) {
        Hashtable<String, String> env = new Hashtable<String, String>();
        for (String key : props.stringPropertyNames()) {
            env.put(key, props.getProperty(key));
        }
        return getInstance(env);
    }

}
