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
package org.tolven.user;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class DefaultUserLoader extends RESTfulClient implements UserLoader {

    public DefaultUserLoader(String userId, char[] password) {
        init(userId, password);
    }

    @Override
    public void createUser(String uid, Properties properties, String userId) {
        try {
            MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
            for (String key : properties.stringPropertyNames()) {
                formData.putSingle(key, properties.getProperty(key));
            }
            WebResource webResource = getAuthWebResource().path("user/" + userId + "/user/" + uid);
            ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Error: " + response.getStatus() + " POST " + getUserId() + " " + webResource.getURI() + " " + response.getEntity(String.class));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not create user : " + uid, ex);
        } finally {
            logout();
        }
    }

}
