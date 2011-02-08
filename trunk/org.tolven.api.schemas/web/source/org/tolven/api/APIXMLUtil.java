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
package org.tolven.api;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.api.accountuser.XAccountUser;
import org.tolven.api.facade.accountuser.XFacadeAccountUsers;
import org.tolven.api.tolvenproperties.XProperties;
import org.tolven.api.tolvenproperties.XProperty;

public class APIXMLUtil {

    private static XMLOutputFactory xmlOutputFactory;
    private static JAXBContext accountUserJC;
    private static JAXBContext facadeAccountUsersJC;
    private static JAXBContext tolvenPropertiesJC;

    private static XMLOutputFactory getXmlOutputFactory() {
        if (xmlOutputFactory == null) {
            xmlOutputFactory = XMLOutputFactory.newInstance();
        }
        return xmlOutputFactory;
    }

    private static JAXBContext getAccountUserJC() {
        if(accountUserJC == null) {
            try {
                accountUserJC = JAXBContext.newInstance("org.tolven.api.accountuser", APIXMLUtil.class.getClassLoader());
            } catch (Exception ex) {
                throw new RuntimeException("Could not create JAXBContext for: org.tolven.api.accountuser", ex);
            }
        }
        return accountUserJC;
    }

    private static JAXBContext getFacadeAccountUsersJC() {
        if(facadeAccountUsersJC == null) {
            try {
                facadeAccountUsersJC = JAXBContext.newInstance("org.tolven.api.facade.accountuser", APIXMLUtil.class.getClassLoader());
            } catch (Exception ex) {
                throw new RuntimeException("Could not create JAXBContext for: org.tolven.api.facade.accountuser", ex);
            }
        }
        return facadeAccountUsersJC;
    }

    private static JAXBContext getTolvenPropertiesJC() {
        if(tolvenPropertiesJC == null) {
            try {
                tolvenPropertiesJC = JAXBContext.newInstance("org.tolven.api.tolvenproperties", APIXMLUtil.class.getClassLoader());
            } catch (Exception ex) {
                throw new RuntimeException("Could not create JAXBContext for: org.tolven.api.tolvenproperties", ex);
            }
        }
        return tolvenPropertiesJC;
    }

    public static String marshalXAccountUser(XAccountUser xAccountUser) {
        XMLStreamWriter writer = null;
        try {
            JAXBContext jc = getAccountUserJC();
            Marshaller marshaller = jc.createMarshaller();
            StringWriter xml = new StringWriter();
            writer = getXmlOutputFactory().createXMLStreamWriter(xml);
            marshaller.marshal(xAccountUser, writer);
            return xml.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Could not marshal XAccountUser: " + xAccountUser.getId(), ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (XMLStreamException ex) {
                throw new RuntimeException("Could not close XMLStreamWriter", ex);
            }
        }
    }

    public static XAccountUser unMarshalXAccountUser(String xAccountUserXML) {
        try {
            JAXBContext jc = getAccountUserJC();
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            ByteArrayInputStream bais = new ByteArrayInputStream(xAccountUserXML.getBytes("UTf-8"));
            return (XAccountUser) unmarshaller.unmarshal(bais);
        } catch (Exception ex) {
            throw new RuntimeException("Could not unmarshal XAccountUser", ex);
        }
    }

    public static String marshalXProperties(XProperties xProperties) {
        XMLStreamWriter writer = null;
        try {
            JAXBContext jc = getTolvenPropertiesJC();
            Marshaller marshaller = jc.createMarshaller();
            StringWriter xml = new StringWriter();
            writer = getXmlOutputFactory().createXMLStreamWriter(xml);
            marshaller.marshal(xProperties, writer);
            return xml.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Could not marshal XProperties", ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (XMLStreamException ex) {
                throw new RuntimeException("Could not close XMLStreamWriter", ex);
            }
        }
    }

    public static XProperties unMarshalXProperties(String xPropertiesXML) {
        try {
            JAXBContext jc = getTolvenPropertiesJC();
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            ByteArrayInputStream bais = new ByteArrayInputStream(xPropertiesXML.getBytes("UTf-8"));
            return (XProperties) unmarshaller.unmarshal(bais);
        } catch (Exception ex) {
            throw new RuntimeException("Could not unmarshal XProperties", ex);
        }
    }

    public static Properties asProperties(XProperties xProperties) {
        Properties properties = new Properties();
        for (XProperty xProperty : xProperties.getProperties()) {
            String key = xProperty.getName();
            String value = xProperty.getValue();
            properties.setProperty(key, value);
        }
        return properties;
    }

    public static String marshalXFacadeAccountUsers(XFacadeAccountUsers xFacadeAccountUsers) {
        XMLStreamWriter writer = null;
        try {
            JAXBContext jc = getFacadeAccountUsersJC();
            Marshaller marshaller = jc.createMarshaller();
            StringWriter xml = new StringWriter();
            writer = getXmlOutputFactory().createXMLStreamWriter(xml);
            marshaller.marshal(xFacadeAccountUsers, writer);
            return xml.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Could not marshal XFacadeAccountUsers: " + xFacadeAccountUsers.getUsername(), ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (XMLStreamException ex) {
                throw new RuntimeException("Could not close XMLStreamWriter", ex);
            }
        }
    }

    public static XFacadeAccountUsers unMarshalXFacadeAccountUsers(String xXFacadeAccountUsersXML) {
        try {
            JAXBContext jc = getFacadeAccountUsersJC();
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            ByteArrayInputStream bais = new ByteArrayInputStream(xXFacadeAccountUsersXML.getBytes("UTf-8"));
            return (XFacadeAccountUsers) unmarshaller.unmarshal(bais);
        } catch (Exception ex) {
            throw new RuntimeException("Could not unmarshal XAccountUser", ex);
        }
    }

}
