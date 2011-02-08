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
 * @version $Id$
 */
package org.tolven.naming;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import com.sun.jndi.url.ldap.ldapURLContextFactory;

public class TolvenDirContextFactory implements ObjectFactory {

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Hashtable<String, String> env = new Hashtable<String, String>();
        String url = null;
        Reference ref = (Reference) obj;
        Enumeration<RefAddr> addrs = ref.getAll();
        while (addrs.hasMoreElements()) {
            RefAddr addr = (RefAddr) addrs.nextElement();
            String n = addr.getType();
            String value = (String) addr.getContent();
            if (n.equals(Context.PROVIDER_URL)) {
                url = value;
            } else {
                env.put(n, value);
            }
        }
        if (url == null) {
            throw new RuntimeException("The property '" + Context.PROVIDER_URL + "' for LDAP has not been supplied");
        }
        ObjectFactory factory = new ldapURLContextFactory();
        String[] urls = url.split(",");
        return factory.getObjectInstance(urls, name, nameCtx, env);
    }

}
