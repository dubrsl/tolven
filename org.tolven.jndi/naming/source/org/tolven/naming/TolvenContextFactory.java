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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.log4j.Logger;

public class TolvenContextFactory implements ObjectFactory {

    private Logger logger = Logger.getLogger(TolvenContextFactory.class);

    public TolvenContextFactory() {
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        DefaultTolvenContext ctx = new DefaultTolvenContext();
        Reference ref = (Reference) obj;
        ctx.setMapping(getValue(ref));
        return ctx;
    }

    private Properties getValue(Reference ref) {
        Properties properties = new Properties();
        Enumeration<RefAddr> e = ref.getAll();
        while (e.hasMoreElements()) {
            RefAddr refAddr = e.nextElement();
            String key = refAddr.getType();
            String value = (String) refAddr.getContent();
            properties.setProperty(key, value);
            if (logger.isDebugEnabled()) {
                logger.debug(key + "=" + value);
            }
        }
        return properties;
    }

}
