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
import java.util.List;

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

import org.tolven.doc.DocumentLocal;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.doc.entity.DocBase;
// *** NOTE: This is disabled ***
@Provider
@Produces("application/xml")
@ManagedBean
public class DocumentHeaderMessageWriter implements MessageBodyWriter<DocBase> {

    @EJB
    private DocumentLocal docBean;

    @Override
    public long getSize(DocBase doc, Class<?> genericType, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> genericType, Type type, Annotation[] annotations, MediaType mediaType) {
        return (DocBase.class.isAssignableFrom(genericType));
    }

	@Override
	public void writeTo(DocBase doc, Class<?> genericType, Type type, 
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        XMLStreamWriter xmlStreamWriter = null;
        try {
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            xmlStreamWriter = factory.createXMLStreamWriter(osw);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("document");
            xmlStreamWriter.writeAttribute("id", Long.toString(doc.getId()));
            xmlStreamWriter.writeAttribute("mediaType", doc.getMediaType());
            if (doc.getSchemaURI() != null) {
                xmlStreamWriter.writeAttribute("schema", doc.getSchemaURI());
            }
            if (doc.getStatus() != null) {
                xmlStreamWriter.writeAttribute("status", doc.getStatus());
            }
            xmlStreamWriter.writeAttribute("authorId", Long.toString(doc.getAuthor().getId()));
            xmlStreamWriter.writeAttribute("signatureRequired", doc.isSignatureRequired() ? "true" : "false");
            xmlStreamWriter.writeStartElement("attachments");
            List<DocAttachment> attachments = docBean.findAttachments(doc);
            for (DocAttachment attachment : attachments) {
                xmlStreamWriter.writeStartElement("attachment");
                xmlStreamWriter.writeAttribute("id", Long.toString(attachment.getId()));
                xmlStreamWriter.writeAttribute("description", attachment.getDescription());
                xmlStreamWriter.writeAttribute("documentId", Long.toString(attachment.getAttachedDocument().getId()));
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();
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
