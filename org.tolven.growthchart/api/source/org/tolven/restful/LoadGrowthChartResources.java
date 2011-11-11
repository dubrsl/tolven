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
 * @author Kanagaraj Kuttiannan
 * @version $Id: LoadGrowthChartResources.java 1089 2011-05-24 03:19:23Z srini.kandula $
 * 
 * The class serves as a Restful Service to load Growth Charts data.
 */

package org.tolven.restful;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.tolven.growthchart.GrowthChartLocal;
import org.tolven.util.ExceptionFormatter;


@Path("growthChartLoader")
@ManagedBean
public class LoadGrowthChartResources {

    public static final String APPLICATION_EXTENSION = ".application.xml";
    
    @Context
    private HttpServletRequest request;

    @EJB
    private GrowthChartLocal growthChartLocal;
    
    protected GrowthChartLocal getGrowthChartBean() {
        if (growthChartLocal == null) {
            String jndiName = "java:app/tolvenEJB/GrowthChartBean!org.tolven.growthchart.GrowthChartLocal";
            try {
                InitialContext ctx = new InitialContext();
                growthChartLocal = (GrowthChartLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return growthChartLocal;
    }

    @Path("createGrowthChart")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    /**
     * Create a GrowthChart Entry.
     * 
     * @param xml The XML representation Growth Chart Data
     */
    public Response createGrowthChart(String xml) {
    	try {
    		getGrowthChartBean().loadGrowthChart(xml);
	    	return Response.ok().build();
	    } catch (Exception ex) {
	        return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
	    }
    }

}
