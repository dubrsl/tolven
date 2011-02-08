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
package org.tolven.api.tolvenproperties;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * A class to assist with populating an AccountUser object
 * 
 * @author Joseph Isaac
 *
 */

public class XPropertiesFactory {

    private static ObjectFactory objectFactory;

    private static ObjectFactory getObjectFactory() {
        if (objectFactory == null) {
            objectFactory = new ObjectFactory();
        }
        return objectFactory;
    }

    public static XProperties createXProperties(Properties properties, Date now) {
        XProperties xProperties = getObjectFactory().createXProperties();
        for (Object obj : properties.keySet()) {
            String name = (String) obj;
            String value = properties.getProperty(name);
            XProperty xProperty = getObjectFactory().createXProperty();
            xProperty.setName(name);
            xProperty.setValue(value);
            xProperties.getProperties().add(xProperty);
        }
        xProperties.setTimestamp(now);
        return xProperties;
    }

    public static Properties asProperties(List<XProperty> xPropertys) {
        Properties properties = new Properties();
        for (XProperty xProperty : xPropertys) {
            properties.setProperty(xProperty.getName(), xProperty.getValue());
        }
        return properties;
    }

}
