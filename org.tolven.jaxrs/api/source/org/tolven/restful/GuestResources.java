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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("guest")
@ManagedBean
public class GuestResources {
	
	@Context
	private HttpServletRequest request;
	
	/**
	 * Return the time on the server.
	 * Example "/time?pattern=MMM dd yyyy"
	 * 
	 * @param pattern
	 * @return
	 */
	@Path("time")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTime(@QueryParam("pattern") String pattern) {
		HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute("tolvenLocale");
        if(locale == null) {
            locale = Locale.getDefault();
        }
        DateFormat df;
        if (pattern!=null) {
    		df = new SimpleDateFormat(pattern, locale);
        } else {
    		df = DateFormat.getDateTimeInstance (DateFormat.SHORT, DateFormat.SHORT, locale);
        }
        Date now = (Date) request.getAttribute("tolvenNow");
        if (now==null) {
			return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Server Now not available").build();
        }
        // Return the formatted date
		return Response.ok().entity(df.format(now)).build();
	}
	
	/**
	 * Return and then clear the cumulative request time in milliseconds
	 * Example "/milliseconds"
	 * 
	 * @return
	 */
	@Path("milliseconds")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getCumulativeTime() {
		HttpSession session = request.getSession();
		Double elapsed = (Double) session.getAttribute("elapsedMilli");
		session.removeAttribute("elapsedMilli");
		if (elapsed==null) {
			return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("No elapsed time accumulator available").build();
		} else {
			return Response.ok().entity(Double.toString(elapsed)).build();
		}
	}
	
	/**
	 * Return the HTTP headers for this request (not very useful)
	 * @param pattern
	 * @return
	 */
	@Path("headers")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String listHeaderNames(@Context HttpHeaders headers) {
		StringBuilder buf = new StringBuilder();
		for (String header : headers.getRequestHeaders().keySet()) {
			buf.append(header);
			buf.append("=");
			buf.append(headers.getRequestHeaders().get(header));
			buf.append("\n");
		}
		return buf.toString();
	}

}
