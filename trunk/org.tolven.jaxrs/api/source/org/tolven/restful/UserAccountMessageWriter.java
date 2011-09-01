/*
 * Copyright (C) 2010 Tolven Inc

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
 * @author <your name>
 * @version $Id$
 */  

package org.tolven.restful;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.ManagedBean;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.xml.userAccounts.UserAccount;
import org.tolven.xml.userAccounts.UserAccounts;
// *** NOTE: This is disabled ***
@Provider
@Produces("application/xml")
@ManagedBean
public class UserAccountMessageWriter implements MessageBodyWriter<UserAccounts> {
    
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    @Override
    public long getSize(UserAccounts userAccounts, Class<?> genericType, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> genericType, Type type, Annotation[] annotations, MediaType mediaType) {
        return false; //	DISABLED
        //	DISABLED	return (genericType==UserAccounts.class);
    }

	@Override
	public void writeTo(UserAccounts userAccounts, Class<?> genericType, Type type, 
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        XMLStreamWriter xmlStreamWriter = null;
        try {
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            xmlStreamWriter = factory.createXMLStreamWriter(osw);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("userAccounts");
            xmlStreamWriter.writeAttribute("username", userAccounts.getUsername());
            xmlStreamWriter.writeAttribute("timestamp", userAccounts.getTimestamp().toXMLFormat());
            for (UserAccount ua : userAccounts.getUserAccounts()) {
                xmlStreamWriter.writeStartElement("userAccount");
                xmlStreamWriter.writeAttribute("accountUserId", Long.toString(ua.getAccountUserId()));
                xmlStreamWriter.writeAttribute("accountId", Long.toString(ua.getAccountId()));
                xmlStreamWriter.writeAttribute("title", ua.getTitle());
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();

        } catch (Exception e) {
            throw new RuntimeException("Exception writing user account list", e);
        } finally {
            if (xmlStreamWriter != null) {
                try {
                    xmlStreamWriter.close();
                } catch (XMLStreamException e) {
                    throw new RuntimeException("Error closing XML Stream writing UserAccounts", e);
                }
            }
        }
    }
}
