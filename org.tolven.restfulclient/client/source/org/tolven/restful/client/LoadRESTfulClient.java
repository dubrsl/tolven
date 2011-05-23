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
package org.tolven.restful.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

import javax.ws.rs.core.MediaType;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.tolven.logging.TolvenLogger;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class LoadRESTfulClient extends RESTfulClient {

    private Integer limit;
    protected static final String UPLOAD_LIMIT = "tolven.client.load.uploadLimit";

    protected int getIterationLimit() {
        if (limit == null) {
            try {
                limit = Integer.MAX_VALUE - 1;
                String limitString = System.getProperty(UPLOAD_LIMIT);
                if (limitString != null) {
                    limit = Integer.parseInt(limitString);
                }
            } catch (NumberFormatException e) {
                TolvenLogger.error("Error getting property: " + UPLOAD_LIMIT, LoadRESTfulClient.class);
                throw e;
            }
        }
        return limit;
    }

    /**
     * Simple method to extract only the name from a trim XML string.
     * @param trimXML
     * @return TrimName
     * @throws XMLStreamException 
     * @throws IOException 
     */
    protected String getTrimName(String trimXML) throws XMLStreamException, IOException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(trimXML));
        Stack<String> path = new Stack<String>();
        String trimName = null;
        while (reader.hasNext()) {
            reader.next();
            if (reader.getEventType() == XMLStreamReader.END_ELEMENT) {
                path.pop();
                continue;
            }
            if (reader.getEventType() != XMLStreamReader.START_ELEMENT)
                continue;
            String prefix;
            if (path.size() > 0) {
                prefix = path.peek() + ".";
            } else {
                prefix = "";
            }
            path.push(prefix + reader.getName().getLocalPart());
            if ("trim.name".equals(path.peek())) {
                trimName = reader.getElementText();
                path.pop();
                break;
                //              continue;
            }
        }
        reader.close();
        return trimName;
    }

    public void createTrimHeader(String xml) {
        WebResource webResource = getAppWebResource().path("loader/createTrimHeader");
        ClientResponse response = webResource.cookie(getTokenCookie()).type(MediaType.APPLICATION_XML).entity(xml).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
        }
    }

    /**
     * Now activate any new trims we've created.
     */
    public void activate() {
        try {
            WebResource webResource = getAppWebResource().path("loader/queueActivateNewTrimHeaders");
            ClientResponse response = webResource.cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
            if (response.getStatus() > 200) {
                throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed while activating trims", ex);
        }
    }

}
