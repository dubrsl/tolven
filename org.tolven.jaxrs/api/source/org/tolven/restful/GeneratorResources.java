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

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.tolven.gen.GeneratorLocal;

@Path("generator")
@ManagedBean
public class GeneratorResources {
    
    @EJB
    private GeneratorLocal generatorBean;
    
	@Context
	UriInfo uriInfo;
	@Context
	HttpServletRequest request;
	
	/**
	 * Generate a patient including medical history
	 * @return response
	 */
	@Path("ccr")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response generateCCR(
			@DefaultValue("1998" ) @QueryParam("startYear") String startYear,
			@Context SecurityContext sc) throws Exception{
		byte[] ccr = generatorBean.generateCCRXML(Integer.parseInt(startYear));
		Response response = Response.ok().entity(ccr).build();
		return response;
	}
	

}
