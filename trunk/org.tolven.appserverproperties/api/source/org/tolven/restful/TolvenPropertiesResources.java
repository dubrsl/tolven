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
 * @author John Churin
 * @version $Id$
 */

package org.tolven.restful;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.util.ExceptionFormatter;

import com.sun.jersey.core.util.MultivaluedMapImpl;

@Path("tolvenproperties")
@ManagedBean
public class TolvenPropertiesResources {

    @EJB
    private TolvenPropertiesLocal tolvenPropertiesBean;

    public TolvenPropertiesResources() {
    }

    protected TolvenPropertiesLocal getTolvenPropertiesBean() {
        if (tolvenPropertiesBean == null) {
            String jndiName = "java:app/tolvenEJB/TolvenProperties!org.tolven.core.TolvenPropertiesLocal";
            try {
                InitialContext ctx = new InitialContext();
                tolvenPropertiesBean = (TolvenPropertiesLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return tolvenPropertiesBean;
    }

    /**
     * Return tolven persistent properties (from the database, not the running system). If any of the properties
     * do not exist, a 404 results
     * @return
     */
    @Path("find")
    @GET
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response findProperties(@QueryParam("propertyNames") List<String> propertyNames) {
        try {
            Properties foundProperties = null;
            if (propertyNames == null || propertyNames.isEmpty()) {
                foundProperties = getTolvenPropertiesBean().findProperties();
            } else {
                foundProperties = getTolvenPropertiesBean().findProperties(propertyNames);
            }
            MultivaluedMap<String, String> mvMap = new MultivaluedMapImpl();
            for (Object obj : foundProperties.keySet()) {
                String name = (String) obj;
                mvMap.putSingle(name, foundProperties.getProperty(name));
            }
            Set<String> inputPropertyNames = new HashSet<String>(propertyNames);
            if (!inputPropertyNames.isEmpty() && !inputPropertyNames.equals(foundProperties.keySet())) {
                inputPropertyNames.removeAll(foundProperties.keySet());
                StringBuffer buff = new StringBuffer();
                Iterator<String> it = inputPropertyNames.iterator();
                while (it.hasNext()) {
                    buff.append(it.next());
                    if (it.hasNext()) {
                        buff.append(",");
                    }
                }
                return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity(buff.toString()).build();
            }
            return Response.ok(mvMap).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    @Path("set")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setProperties(MultivaluedMap<String, String> mvMap) {
        try {
            if (mvMap == null || mvMap.isEmpty()) {
                return Response.status(400).type(MediaType.TEXT_PLAIN).entity("No properties supplied").build();
            }
            Properties properties = new Properties();
            for (String name : mvMap.keySet()) {
                properties.setProperty(name, mvMap.getFirst(name));
            }
            getTolvenPropertiesBean().setProperties(properties);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    @Path("set")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response setPropertiesXML(String propertiesXML) {
        try {
            if (propertiesXML == null || propertiesXML.length() == 0) {
                return Response.status(400).type(MediaType.TEXT_PLAIN).entity("No propertiesXML supplied").build();
            }
            Properties properties = new Properties();
            properties.loadFromXML(new ByteArrayInputStream(propertiesXML.getBytes("UTF-8")));
            getTolvenPropertiesBean().setProperties(properties);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Remove properties
     * @param propertyNames
     * @return
     */
    @Path("remove")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response removeProperties(MultivaluedMap<String, String> map) {
        try {
            List<String> propertyNames = map.get("propertyNames");
            if (propertyNames != null) {
                boolean silentIfMissing = "true".equals(map.get("silentIfMissing"));
                if (!silentIfMissing) {
                    Properties foundProperties = getTolvenPropertiesBean().findProperties(propertyNames);
                    if (!propertyNames.containsAll(foundProperties.keySet())) {
                        StringBuffer buff = new StringBuffer();
                        propertyNames.removeAll(foundProperties.keySet());
                        Iterator<String> it = propertyNames.iterator();
                        while (it.hasNext()) {
                            buff.append(it.next());
                            if (it.hasNext()) {
                                buff.append(",");
                            }
                        }
                        return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity(buff.toString()).build();
                    }
                }
                getTolvenPropertiesBean().removeProperties(propertyNames);
            }
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Reset the runtime properties
     */
    @Path("reset")
    @POST
    public Response resetAllProperties() {
        try {
            getTolvenPropertiesBean().resetAllProperties();
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

}
