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
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLOutputFactory;

import org.tolven.app.DataExtractLocal;
import org.tolven.app.DataQueryResults;
import org.tolven.util.ExceptionFormatter;

@Provider
@Produces("application/xml")
@ManagedBean
public class ApplicationDataMessageWriter implements MessageBodyWriter<DataQueryResults> {
    
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    //private TrimExpressionEvaluator ee = new TrimExpressionEvaluator();

    @EJB
    private DataExtractLocal dataExtractBean;

    @Override
    public long getSize(DataQueryResults dataQueryResults, Class<?> genericType, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> genericType, Type type, Annotation[] annotations, MediaType mediaType) {
        return (genericType == DataQueryResults.class);
    }

	@Override
	public void writeTo(DataQueryResults dataQueryResults, Class<?> genericType, Type type, 
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        try {
            getDataExtractBean().streamResultsXML(new OutputStreamWriter(out, "UTF-8"), dataQueryResults);
        } catch (Exception e) {
            Response response = Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(ExceptionFormatter.toSimpleString(e, "\n")).build();
            throw new WebApplicationException(e, response);
        }
	}
	
	 protected DataExtractLocal getDataExtractBean() {
	        if (dataExtractBean == null) {
	            String jndiName = "java:app/tolvenEJB/DataExtractBean!org.tolven.app.DataExtractLocal";
	            try {
	                InitialContext ctx = new InitialContext();
	                dataExtractBean = (DataExtractLocal) ctx.lookup(jndiName);
	            } catch (Exception ex) {
	                throw new RuntimeException("Could not lookup " + jndiName);
	            }
	        }
	        return dataExtractBean;
	    }

}
