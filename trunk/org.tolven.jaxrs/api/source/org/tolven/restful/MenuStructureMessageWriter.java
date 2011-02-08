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
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSColumn;
import org.tolven.core.TolvenPropertiesLocal;
/**
 * Send Metadata for a specific MenuStructure to client
 * @author John Churin
 *
 */
@Provider
@Produces("application/xml")
@ManagedBean
public class MenuStructureMessageWriter implements MessageBodyWriter<AccountMenuStructure> {

    @EJB
    private TolvenPropertiesLocal propertiesBean;

    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    //private TrimExpressionEvaluator ee = new TrimExpressionEvaluator();

    @Override
    public long getSize(AccountMenuStructure ams, Class<?> genericType, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> genericType, Type type, Annotation[] annotations, MediaType mediaType) {
        return (genericType == AccountMenuStructure.class);
    }

	@Override
	public void writeTo(AccountMenuStructure ams, Class<?> genericType, Type type, 
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        XMLStreamWriter xmlStreamWriter = null;
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            xmlStreamWriter = factory.createXMLStreamWriter(out, "UTF-8");
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("metadata");
            xmlStreamWriter.writeAttribute("path", ams.getPath());
            xmlStreamWriter.writeAttribute("account", String.valueOf(ams.getAccount().getId()));
            xmlStreamWriter.writeAttribute("database", propertiesBean.getProperty("tolven.repository.oid"));
            for (MSColumn col : ams.getColumns()) {
                xmlStreamWriter.writeStartElement("column");
                xmlStreamWriter.writeAttribute("name", col.getHeading());
                if (col.getDatatype() != null) {
                    xmlStreamWriter.writeAttribute("datatype", col.getDatatype());
                }
                xmlStreamWriter.writeAttribute("text", col.getText());
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
        } catch (Exception e) {
            throw new RuntimeException("Exception writing metadata", e);
        } finally {
            if (xmlStreamWriter != null) {
                try {
                    xmlStreamWriter.close();
                } catch (XMLStreamException e) {
                }
            }
        }
    }
}
