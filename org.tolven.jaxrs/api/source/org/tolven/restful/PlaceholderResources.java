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
package org.tolven.restful;

import java.net.URI;
import java.net.URLEncoder;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.tolven.app.ApplicationMetadataLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.util.ExceptionFormatter;

/**
 * A class to support RESTful API to manage Placeholders
 * 
 * @author Joseph Isaac
 *
 */
@Path("placeholders")
@ManagedBean
public class PlaceholderResources {

    @EJB
    private AccountDAOLocal accountBean;

    @EJB
    private ApplicationMetadataLocal appMetadata;

  
	@EJB
    private MenuLocal menuBean;
    
    @Context
    private HttpServletRequest request;
    
    @Resource
    private ApplicationResources applicationResources;

    /**
         * Create a Placeholder and return HTTP status code 201, if the placeholder is created.
         * If name is not supplied, return HTTP status code 400.
         * If accountId is not supplied, return HTTP status code 400.
         * Exceptions return HTTP status code 500.
     * @param name
     * @param parent
     * @param accountId
     * @param page
     * @param sequence
     * @param title
     * @param visible
     * @param eventInstance
     * @param heading
     * @param initialSort
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createPlaceholder(
            @FormParam("name") String name,
            @FormParam("parent") String parent,
            @FormParam("accountId") String accountId,
            @FormParam("page") String page,
            @FormParam("sequence") String sequence,
            @FormParam("title") String title,
            @FormParam("visible") String visible,
            @FormParam("eventInstance") String eventInstance,
            @FormParam("heading") String heading,
            @FormParam("initialSort") String initialSort) {
        if (name == null || name.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("name is missing").build();
        }
        if (accountId == null || accountId.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("accountId is missing").build();
        }
        Account account = null;
        try {
            long id = Long.parseLong(accountId);
            if(getAccountBean().isTemplateAccount(id)) {
                account = getAccountBean().findTemplateAccount(Long.parseLong(accountId));
            } else {
                AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
                if(accountUser == null) {
                    return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Not logged into: " + accountId).build();
                }
                account = accountUser.getAccount();
            }
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Invalid accountId: " + accountId).build();
        }
        if (account == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("Template account not found: " + accountId).build();
        }
        try {
            AccountMenuStructure ms = getAppMetadata().createPlaceholder(name, parent, account);
            updatePlaceholder(ms, page, sequence, title, visible, eventInstance, heading, initialSort);
            URI uri = new URI(URLEncoder.encode(ms.getPath(), "UTF-8"));
            return Response.created(uri).entity(ms.getPath()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Update a and return HTTP status code 204, if updated.
     * If accountId is not supplied, return HTTP status code 400.
     * If the placeholder does not exist returns HTTP status code 501 (use POST)
     * Exceptions return HTTP status code 500.
     * @param path
     * @param accountId
     * @param page
     * @param sequence
     * @param title
     * @param visible
     * @param eventInstance
     * @param heading
     * @param initialSort
     * @return
     */
    @Path("{path}")
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response updatePlaceholder(
            @PathParam("path") String path,
            @FormParam("accountId") String accountId,
            @FormParam("page") String page,
            @FormParam("sequence") String sequence,
            @FormParam("title") String title,
            @FormParam("visible") String visible,
            @FormParam("eventInstance") String eventInstance,
            @FormParam("heading") String heading,
            @FormParam("initialSort") String initialSort) {
        if (accountId == null || accountId.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("accountId is missing").build();
        }
        try {
            long accountIdLong = Long.parseLong(accountId);
            Account account = null;
            if (getAccountBean().isTemplateAccount(accountIdLong)) {
                account = getAccountBean().findTemplateAccount(Long.parseLong(accountId));
            } else {
                AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
                if (accountUser == null) {
                    return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Not logged into: " + accountId).build();
                }
                account = accountUser.getAccount();
            }
            if (account == null) {
                return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Could not find account: " + accountId).build();
            }
            AccountMenuStructure ms = getMenuBean().findAccountMenuStructure(accountIdLong, path);
            updatePlaceholder(ms, page, sequence, title, visible, eventInstance, heading, initialSort);
            return Response.noContent().build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    private void updatePlaceholder(AccountMenuStructure ms, String page, String sequence, String title, String visible, String eventInstance, String heading, String initialSort) {
        if (page != null) {
            ms.setTemplate(page);
        }
        if (sequence != null) {
            ms.setSequence(Integer.parseInt(sequence));
        }
        if (title != null) {
            ms.setText(title);
        }
        if (visible != null) {
            ms.setVisible(visible);
        }
        if (eventInstance != null) {
            ms.setEventPath(eventInstance);
        }
        if (heading != null) {
            ms.setMenuTemplate(heading);
        }
        if (initialSort != null) {
            ms.setInitialSort(initialSort);
        }
    }
    
    /**
     * GET a placeholder and return HTTP status code 200, with an entity map of accountType attributes.
     * If accountId is not supplied, return HTTP status code 400.
     * If not found, return HTTP status code of 404.
     * Exceptions return HTTP status code 500.
     * @param path
     * @param accountId
     * @param fields
     */
    @Path("{path}")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public Response getPlaceholder(
            @PathParam("path") String path,
            @QueryParam("accountId") String accountId,
            @QueryParam("fields") String fields
            ) {
        return Response.status(501).build();
    }

    /**
     * Delete a Placeholder and return HTTP status code 200, with an entity of INACTIVE.
     * If accountId is not supplied, return HTTP status code 400.
     * If not found, return HTTP status code of 404.
     * Exceptions return HTTP status code 500.
     * @param path
     * @param accountId
     */
    @Path("{path}")
    @DELETE
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response deletePlaceholder(
            @PathParam("path") String path,
            @QueryParam("accountId") String accountId
            ) {
        return Response.status(501).build();
    }

    protected AccountDAOLocal getAccountBean() {
        if (accountBean == null) {
            String jndiName = "java:app/tolvenEJB/AccountDAOBean!org.tolven.core.AccountDAOLocal";
            try {
                InitialContext ctx = new InitialContext();
                accountBean = (AccountDAOLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return accountBean;
    }
    
    protected MenuLocal getMenuBean() {
        if (menuBean == null) {
            String jndiName = "java:app/tolvenEJB/MenuBean!org.tolven.app.MenuLocal";
            try {
                InitialContext ctx = new InitialContext();
                menuBean = (MenuLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return menuBean;
    }
    
    protected ApplicationMetadataLocal getAppMetadata() {
    	if (appMetadata == null) {
            String jndiName = "java:app/tolvenEJB/ApplicationMetadata!org.tolven.app.ApplicationMetadataLocal";
            try {
                InitialContext ctx = new InitialContext();
                appMetadata = (ApplicationMetadataLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
  		return appMetadata;
  	}

}
