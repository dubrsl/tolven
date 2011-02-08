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
import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import javax.ws.rs.core.Response.Status;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.api.security.GeneralSecurityFilter;
import org.tolven.app.DataExtractLocal;
import org.tolven.app.DataQueryResults;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.core.ActivationLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.util.ExceptionFormatter;

@Path("application")
@ManagedBean
public class ApplicationResources {

    @EJB
    private ActivationLocal activationBean;
    
    @EJB
    private DataExtractLocal dataExtractBean;
    
    @EJB
    private MenuLocal menuBean;
    
    @Context
    private HttpServletRequest request;
		
	/**
	 * Perform a general application query. 
	 * @param path Specify the path to query, includes placeholder ids where appropriate.
	 * @param fields A list of fields to include in the query or null to include all fields
	 * @param order A list of fields to sort on. Maybe include " asc" or " desc" to specify direction
	 * @param totalCount If true, (default is false), also return the total count of items in the list
	 * @param offset Starting row to return, defaults to zero (first row)
	 * @param limit With a (default) limit of 0, no query is performed, only metadata is returned. A limit of -1 returns all rows.
	 * @return
	 */
    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_XML)
	public Response getApplicationList (
			@QueryParam("path") String path, 
			@QueryParam("fields") String fields,
			@QueryParam("order") String order,
			@DefaultValue("false" ) @QueryParam("totalCount") String totalCount,
			@DefaultValue("false" ) @QueryParam("filterCount") String filterCount,
			@DefaultValue("0" ) @QueryParam("offset") String offset,
			@DefaultValue("0" ) @QueryParam("limit") String limit
			) {
        Long accountUserId = (Long) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID);
        if (accountUserId == null) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Account required").build();
        }
        AccountUser accountUser = activationBean.findAccountUser(accountUserId);
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        //			MenuPath menuPath = new MenuPath( path );
        //			long accountId = accountUser.getAccount().getId();
        Date now = (Date) request.getAttribute("tolvenNow");
        DataQueryResults dataQueryResults = dataExtractBean.setupQuery(path, accountUser);
        dataQueryResults.setNow(now);
        if (order != null) {
            dataQueryResults.setOrder(order.split(","));
        }
        dataQueryResults.setOffset(Integer.parseInt(offset));
        dataQueryResults.setLimit(Integer.parseInt(limit));
        if (fields != null && fields.length() > 0) {
            dataQueryResults.disableAllFields();
            // Setup fields
            String fieldArray[] = fields.split(",");
            for (String external : fieldArray) {
                dataQueryResults.findExternalField(external).setEnabled(true);
            }
        } else {
            dataQueryResults.enableAllFields();
        }
        dataQueryResults.setReturnTotalCount("true".equals(totalCount));
        dataQueryResults.setReturnFilterCount("true".equals(filterCount));
        Response response = Response.ok().entity(dataQueryResults).build();
        return response;
    }
		
	/**
	 * Perform a general application query. 
	 * @param path Specify the path to query, includes placeholder ids where appropriate.
	 * @param fields A list of fields to include in the query or null to include all fields
	 * @param order A list of fields to sort on. Maybe include " asc" or " desc" to specify direction
	 * @param offset Starting row to return, defaults to zero (first row)
	 * @param limit With a (default) limit of 0, no query is performed, only metadata is returned. A limit of -1 returns all rows.
	 * @return
	 */
    public Response getApplicationItem (
            @QueryParam("path") String path, 
            @QueryParam("fields") String fields
            ) throws Exception  {
        Long accountUserId = (Long) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID);
        if (accountUserId == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Account required").build();
        }
        AccountUser accountUser = activationBean.findAccountUser(accountUserId);
        if (accountUser == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        Date now = (Date) request.getAttribute("tolvenNow");
        DataQueryResults dataQueryResults = dataExtractBean.setupQuery(path, accountUser);
        dataQueryResults.setNow(now);
        if (fields != null && fields.length() > 0) {
            dataQueryResults.disableAllFields();
            // Setup fields
            String fieldArray[] = fields.split(",");
            for (String external : fieldArray) {
                dataQueryResults.findExternalField(external).setEnabled(true);
            }
        } else {
            dataQueryResults.enableAllFields();
        }
        dataQueryResults.setItemQuery(true);
        dataQueryResults.setLimit(1);
        Response response = Response.ok().entity(dataQueryResults).build();
        return response;
    }

	/**
	 * Get column metadata for a specific metadata item. 
	 * @param path Specify the path. Placeholder ids, of present, will be ignored.
	 * @return
	 */
    @Path("describe")
    @GET
    @Produces(MediaType.APPLICATION_XML)
	public Response getDescribeColumns (
			@QueryParam("path") String path
			) {
        Long accountUserId = (Long) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID);
        if (accountUserId == null) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Account required").build();
        }
        AccountUser accountUser = activationBean.findAccountUser(accountUserId);
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        MenuPath menuPath = new MenuPath(path);
        //			long accountId = accountUser.getAccount().getId();
        //			Date now = (Date) request.getAttribute("tolvenNow");
        AccountMenuStructure ams;
        try {
            ams = dataExtractBean.findAccountMenuStructure(accountUser.getAccount(), menuPath);
        } catch (Exception e1) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("Metadata not found: " + menuPath).build();
        }
        Response response = Response.ok().entity(ams).build();
        return response;
    }
		
    /**
     * Get all metadata  
     * @param path Specify the path. Placeholder ids, of present, will be ignored.
     * @return
     */
    @Path("metadata")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getMetadata() {
        Long accountUserId = (Long) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID);
        if (accountUserId == null) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Account required").build();
        }
        AccountUser accountUser = activationBean.findAccountUser(accountUserId);
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        List<AccountMenuStructure> amss;
        amss = menuBean.findFullAccountMenuStructure(accountUser.getAccount().getId());
        StringWriter sw = new StringWriter();
        XMLStreamWriter xmlStreamWriter = null;
        try {
            GregorianCalendar now = new GregorianCalendar();
            now.setTime((Date) request.getAttribute("tolvenNow"));
            DatatypeFactory xmlFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar ts = xmlFactory.newXMLGregorianCalendar(now);
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            xmlStreamWriter = factory.createXMLStreamWriter(sw);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("user");
            xmlStreamWriter.writeAttribute("accountId", Long.toString(accountUser.getAccount().getId()));
            xmlStreamWriter.writeAttribute("timestamp", ts.toXMLFormat());
            for (AccountMenuStructure ams : amss) {
                String role = ams.getRole();
                xmlStreamWriter.writeStartElement(role);
                xmlStreamWriter.writeAttribute("name", ams.getNode());
                xmlStreamWriter.writeAttribute("path", ams.getPath());
                if (ams.getText() != null) {
                    xmlStreamWriter.writeStartElement("text");
                    xmlStreamWriter.writeCharacters(ams.getText());
                    xmlStreamWriter.writeEndElement();
                }
                if (ams.getAllowRoles() != null) {
                    xmlStreamWriter.writeStartElement("rolesAllowed");
                    xmlStreamWriter.writeCharacters(ams.getAllowRoles());
                    xmlStreamWriter.writeEndElement();
                }
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(ExceptionFormatter.toSimpleString(e, "\n")).build();
        } finally {
            if (xmlStreamWriter != null) {
                try {
                    xmlStreamWriter.close();
                } catch (XMLStreamException e) {
                }
            }
        }
        Response response = Response.ok().entity(sw.toString()).build();
        return response;
    }

}
