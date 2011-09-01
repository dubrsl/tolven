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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.tolven.api.security.GeneralSecurityFilter;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.util.ExceptionFormatter;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * A class to support RESTful API to manage AccountTypes
 * 
 * @author Joseph Isaac
 *
 */
@Path("accountTypes")
@ManagedBean
public class AccountTypeResources {

    @EJB
    private AccountDAOLocal accountBean;
    
    @EJB
    private ActivationLocal activationBean;
    
    @Context
    private HttpServletRequest request;

    /**
     * Create an AccountType and return HTTP status code 201, if the accountType is created.
     * If knownType is not supplied, return HTTP status code 400.
     * If the accountType already exists, returns HTTP status code 409 (use PUT instead)
     * Exceptions return HTTP status code 500.
     * @param knownType
     * @param creatable
     * @param createAccountPage
     * @param css
     * @param homePage
     * @param logo
     * @param title
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createAccountType(
            @FormParam("knownType") String knownType,
            @FormParam("creatable") String creatable,
            @FormParam("createAccountPage") String createAccountPage,
            @FormParam("css") String css,
            @FormParam("homePage") String homePage,
            @FormParam("logo") String logo,
            @FormParam("title") String title) {
        if (knownType == null || knownType.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("knownType is missing").build();
        }
        try {
            AccountType accountType = getAccountBean().findAccountTypebyKnownType(knownType);
            if (accountType == null) {
                accountType = getAccountBean().createAccountType(knownType, homePage, title, false, org.tolven.core.entity.Status.ACTIVE.value(), Boolean.TRUE.toString().equals(creatable));
                updateAccountType(accountType, creatable, createAccountPage, css, homePage, logo, title);
                URI uri = new URI(URLEncoder.encode(knownType, "UTF-8"));
                return Response.created(uri).entity(accountType.getStatus()).build();
            } else {
                return Response.status(Status.CONFLICT).entity(knownType + " already exists").build();
            }
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * GET an AccountType and return HTTP status code 200, with an entity map of accountType attributes.
     * If knownType is not supplied, return HTTP status code 400.
     * If not found, return HTTP status code of 404.
     * Exceptions return HTTP status code 500.
     * @param knownType
     */
    @Path("{knownType}")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getAccountType(@PathParam("knownType") String knownType) {
        if (knownType == null || knownType.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("knownType is missing").build();
        }
        AccountType accountType = getAccountBean().findAccountTypebyKnownType(knownType);
        if (accountType == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity(knownType + " not found: " + knownType).build();
        }
        try {
            MultivaluedMap<String, String> mvMap = new MultivaluedMapImpl();
            mvMap.putSingle("creatable", Boolean.toString(accountType.isCreatable()));
            mvMap.putSingle("createAccountPage", accountType.getCreateAccountPage());
            mvMap.putSingle("css", accountType.getCSS());
            mvMap.putSingle("homePage", accountType.getHomePage());
            mvMap.putSingle("logo", accountType.getLogo());
            mvMap.putSingle("title", accountType.getLongDesc());
            return Response.ok(mvMap).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Update an AccountType and return HTTP status code 204, if the accountType was updated.
     * If knownType is not supplied, return HTTP status code 400.
     * If the accountType does not exist returns HTTP status code 404
     * Exceptions return HTTP status code 500.
     * @param knownType
     * @param creatable
     * @param createAccountPage
     * @param css
     * @param homePage
     * @param logo
     * @param title
     * @param accountId
     * @return
     */
    @Path("{knownType}")
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response putAccountType(
            @PathParam("knownType") String knownType,
            @FormParam("creatable") String creatable,
            @FormParam("createAccountPage") String createAccountPage,
            @FormParam("css") String css,
            @FormParam("homePage") String homePage,
            @FormParam("logo") String logo,
            @FormParam("title") String title,
            @FormParam("accountId") String accountId) {
        if (knownType == null || knownType.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("knownType is missing").build();
        }
        try {
            AccountType accountType = getAccountBean().findAccountTypebyKnownType(knownType);
            if (accountType == null) {
                return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountType not found: " + knownType).build();
            } else {
                updateAccountType(accountType, creatable, createAccountPage, css, homePage, logo, title);
                if(accountId != null) {
                    Account account = getAccountBean().findTemplateAccount(Long.parseLong(accountId));
                    if (account == null) {
                        return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("Template account not found: " + accountId).build();
                    }
                    getAccountBean().setAccountTemplate(knownType, account);
                }
                return Response.noContent().build();
            }
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Delete an AccountType and return HTTP status code 200, with an entity of INACTIVE.
     * If knownType is not supplied, return HTTP status code 400.
     * If not found, return HTTP status code of 404.
     * Exceptions return HTTP status code 500.
     * @param knownType
     */
    @Path("{knownType}")
    @DELETE
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response deleteAccountType(@PathParam("knownType") String knownType) {
        if (knownType == null || knownType.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("knownType is missing").build();
        }
        AccountType accountType = getAccountBean().findAccountTypebyKnownType(knownType);
        if (accountType == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("knownType not found: " + knownType).build();
        }
        try {
            accountType.setStatus(org.tolven.core.entity.Status.INACTIVE.value());
            return Response.ok().entity(org.tolven.core.entity.Status.INACTIVE.value()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Update accountType with the provided parameters
     * 
     * @param accountType
     * @param knownType
     * @param creatable
     * @param createAccountPage
     * @param css
     * @param homePage
     * @param logo
     * @param title
     */
    private void updateAccountType(AccountType accountType, String creatable, String createAccountPage, String css, String homePage, String logo, String title) {
        if (creatable != null) {
            accountType.setCreatable(Boolean.TRUE.toString().equals(creatable));
        }
        if (createAccountPage != null) {
            accountType.setCreateAccountPage(createAccountPage);
        }
        if (css != null) {
            accountType.setCSS(css);
        }
        if (homePage != null) {
            accountType.setHomePage(homePage);
        }
        if (logo != null) {
            accountType.setLogo(logo);
        }
        if (title != null) {
            accountType.setLongDesc(title);
        }
    }
    
    
    /**
     * Set an Account Type Property
     * @param pname
     * @param pvalue
     * @return
     */
    @Path("property")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setAccountProperty(
            @FormParam("pname") String pname,		
            @FormParam("pvalue") String pvalue) {
        if (pname == null || pname.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("pname is missing").build();
        }
        Long accountUserId = (Long) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID);
        if (accountUserId == null) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Account required").build();
        }
        AccountUser accountUser = getActivationBean().findAccountUser(accountUserId);
        Account templateAccount = accountUser.getAccount().getAccountTemplate(); 
        try {
        	getAccountBean().putAccountProperty(templateAccount.getId(), pname, pvalue);
        	
        	return Response.noContent().build();
        	/*URI uri = new URI(URLEncoder.encode(Long.toString(accountUser.getAccount().getId()), "UTF-8"));
            return Response.created(uri).entity(Long.toString(accountUser.getAccount().getId())).build();*/
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
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
    
    protected ActivationLocal getActivationBean() {
        if (activationBean == null) {
            String jndiName = "java:app/tolvenEJB/ActivationBean!org.tolven.core.ActivationLocal";
            try {
                InitialContext ctx = new InitialContext();
                activationBean = (ActivationLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return activationBean;
    }

}
