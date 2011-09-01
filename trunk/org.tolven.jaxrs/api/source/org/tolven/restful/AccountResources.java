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
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.tolven.api.APIXMLUtil;
import org.tolven.api.accountuser.XAccountUser;
import org.tolven.api.accountuser.XAccountUserFactory;
import org.tolven.api.facade.accountuser.XFacadeAccountUserFactory;
import org.tolven.api.facade.accountuser.XFacadeAccountUsers;
import org.tolven.api.security.GeneralSecurityFilter;
import org.tolven.app.MenuLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;
import org.tolven.util.ExceptionFormatter;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * A class to support RESTful API to manage Accounts
 * 
 * @author Joseph Isaac
 *
 */
@Path("accounts")
@ManagedBean
public class AccountResources {
	
	//private Logger logger = Logger.getLogger(this.getClass());

    @EJB
    private AccountDAOLocal accountBean;

    @EJB
    private MenuLocal menuBean;

    @EJB
    private ActivationLocal activationBean;
    
    /*@EJB
    private AppResolverLocal appResolver;
    */
    @Context
    private HttpServletRequest request;

    /**
     * Create an template Account (no users and no accountTemplateID) and return HTTP status code 201, if the account is created.
     * If knownType is not supplied, return HTTP status code 400.
     * Exceptions return HTTP status code 500.
     * @param knownType
     * @param disableAutoRefresh
     * @param emailFormat
     * @param enableBackButton
     * @param locale
     * @param manualMetaUpdate
     * @param timeZone
     * @param title
     * @param template
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createAccount(
            @FormParam("knownType") String knownType,
            @FormParam("title") String title,
            @FormParam("timeZone") String timeZone,
            @FormParam("locale") String locale,
            @DefaultValue("html") @FormParam("emailFormat") String emailFormat,
            @DefaultValue("true") @FormParam("enableBackButton") String enableBackButton,
            @DefaultValue("false") @FormParam("disableAutoRefresh") String disableAutoRefresh,
            @DefaultValue("false") @FormParam("manualMetaUpdate") String manualMetaUpdate,
            @DefaultValue("false") @FormParam("template") String template) {
        if (knownType == null || knownType.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("knownType is missing").build();
        }
        AccountType accountType = accountBean.findAccountTypebyKnownType(knownType);
        if(accountType == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("accountType not found").build();
        }
        try {
            Account account = null;
            if (Boolean.parseBoolean(template)) {
                account = accountBean.createTemplateAccount(accountType);
            } else {
                Date timeNow = (Date) request.getAttribute("timeNow");
                String principal = request.getUserPrincipal().getName();
                TolvenUser user = getActivationBean().findUser(principal);
                if (user == null) {
                    throw new RuntimeException("Could not find TolvenUser: " + principal);
                }
                if(locale == null) {
                    locale = user.getLocale();
                }
                TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
                PublicKey userPublicKey = sessionWrapper.getUserPublicKey();
                AccountUser accountUser = accountBean.createAccount(accountType, user, userPublicKey, timeNow);
                account = accountUser.getAccount();
                getMenuBean().updateMenuStructure(account);
            }
            updateAccount(account, title, locale, timeZone, emailFormat, enableBackButton, disableAutoRefresh, manualMetaUpdate);
            URI uri = new URI(URLEncoder.encode(Long.toString(account.getId()), "UTF-8"));
            return Response.created(uri).entity(Long.toString(account.getId())).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * GET a template Account (no AccountTemplate, no users) and return HTTP status code 200, with an entity map of accountType attributes.
     * If accountId is not supplied, return HTTP status code 400.
     * If not found, return HTTP status code of 404.
     * Exceptions return HTTP status code 500.
     * @param accountId
     */
    @Path("{accountId}")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getAccount(@PathParam("accountId") String accountId) {
        if (accountId == null || accountId.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("accountId is missing").build();
        }
        Account account = accountBean.findTemplateAccount(Long.parseLong(accountId));
        if (account == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("Template account not found: " + accountId).build();
        }
        try {
            MultivaluedMap<String, String> mvMap = new MultivaluedMapImpl();
            mvMap.putSingle("knownType", account.getAccountType().getKnownType());
            mvMap.putSingle("title", account.getTitle());
            mvMap.putSingle("timezone", account.getTimeZone());
            mvMap.putSingle("locale", account.getLocale());
            mvMap.putSingle("emailFormat", account.getEmailFormat());
            mvMap.putSingle("enableBackButton", account.getEnableBackButton());
            if (account.getDisableAutoRefresh() != null) {
                mvMap.putSingle("disableAutoRefresh", String.valueOf(Boolean.TRUE.toString().equals(String.valueOf(account.getDisableAutoRefresh()))));
            }
            if (account.getManualMetadataUpdate() != null) {
                mvMap.putSingle("manualMetadataUpdate", String.valueOf(Boolean.TRUE.toString().equals(String.valueOf(account.getManualMetadataUpdate()))));
            }
            return Response.ok(mvMap).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Update a template Account and return HTTP status code 204, if the accountType was updated.
     * If no parameters to update are supplied, return HTTP status code 400.
     * If the accountType does not exist returns HTTP status code 404
     * Exceptions return HTTP status code 500.
     * @param accountId
     * @param knownType
     * @param creatable
     * @param title
     * @param timezone
     * @param locale
     * @param emailFormat
     * @param enableBackButton
     * @param disableAutoRefresh
     * @param manualMetadataUpdate
     * @return
     */
    @Path("{accountId}")
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response putAccount(
            @PathParam("accountId") String accountId,
            @FormParam("title") String title,
            @FormParam("timeZone") String timeZone,
            @FormParam("locale") String locale,
            @FormParam("emailFormat") String emailFormat,
            @FormParam("enableBackButton") String enableBackButton,
            @FormParam("disableAutoRefresh") String disableAutoRefresh,
            @FormParam("manualMetaUpdate") String manualMetaUpdate) {
        Account account = accountBean.findTemplateAccount(Long.parseLong(accountId));
        if (account == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("Template account not found: " + accountId).build();
        }
        try {
            updateAccount(account,  title,  locale,  timeZone,  emailFormat,  enableBackButton,  disableAutoRefresh,  manualMetaUpdate);
            return Response.noContent().build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }
    
    private void updateAccount(Account account, String title, String locale, String timeZone, String emailFormat, String enableBackButton, String disableAutoRefresh, String manualMetaUpdate) {
        if (title != null) {
            account.setTitle(title);
        }
        if (locale != null) {
            account.setLocale(ResourceBundleHelper.getLocale(locale).getDisplayName());
        }
        if (timeZone != null) {
            account.setTimeZone(timeZone);
        }
        if (emailFormat != null) {
            account.setEmailFormat(emailFormat);
        }
        if (enableBackButton != null) {
            account.setEnableBackButton(Boolean.valueOf(enableBackButton));
        }
        if (disableAutoRefresh != null) {
            account.setDisableAutoRefresh(Boolean.valueOf(disableAutoRefresh));
        }
        if (manualMetaUpdate != null) {
            account.setManualMetadataUpdate(Boolean.valueOf(manualMetaUpdate));
        }
    }

    /**
     * Delete an Account Not Implemented returns 501.
     * @param accountId
     */
    @Path("{accountId}")
    @DELETE
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response deleteAccount(@PathParam("accountId") String accountId) {
        return Response.status(501).build();
    }

    /**
     * Refresh account information.
     * @return
     */
    @Path("refresh")
    @GET
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response refreshAccount() {
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        boolean inAccount = "account".equals((String) sessionWrapper.getAttribute(GeneralSecurityFilter.USER_CONTEXT));
        if (!inAccount) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("You are not in an Account").build();
        }
        AccountUser accountUser = (AccountUser) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
        Date now = (Date) request.getAttribute("tolvenNow");
        XAccountUser xAccountUser = XAccountUserFactory.createXAccountUser(accountUser, now);
        String xAccountUserXML = APIXMLUtil.marshalXAccountUser(xAccountUser);
        List<AccountUser> accountUsers = getActivationBean().findUserAccounts(accountUser.getUser());
        XFacadeAccountUsers xFacadeAccountUsers = XFacadeAccountUserFactory.createXFacadeAccountUsers(accountUsers, accountUser.getUser(), now);
        String xFacadeAccountUsersXML = APIXMLUtil.marshalXFacadeAccountUsers(xFacadeAccountUsers);
        MultivaluedMap<String, String> mvMap = new MultivaluedMapImpl();
        mvMap.putSingle(GeneralSecurityFilter.ACCOUNTUSER, xAccountUserXML);
        mvMap.putSingle(GeneralSecurityFilter.ACCOUNTUSERS, xFacadeAccountUsersXML);
        return Response.ok(mvMap).build();
    }
    
    /**
     * Get an Account Property
     * @param pname
     * @param pvalue
     * @return
     */
    @Path("property")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    //@Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    //@Produces(MediaType.APPLICATION_XML)
    public Response getAccountProperty(
            @QueryParam("pname") String pname) {
        if (pname == null || pname.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("pname is missing").build();
        }
        Long accountUserId = (Long) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID);
        if (accountUserId == null) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Account required").build();
        }
        //AccountUser accountUser = (AccountUser) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
        AccountUser accountUser = getActivationBean().findAccountUser(accountUserId);
        
        try {
        	String pvalue = accountUser.getProperty().get(pname);
        	
        	/*if(pvalue.startsWith("<?xml")) {
        		return XSLTUtil.generateXSLTResponse(pvalue, accountUser);
	        	
        	} else {*/
	        	Response response = Response.ok().entity(pvalue).build(); 
	        	return response;
        	//}
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Set an Account Property
     * @param pname
     * @param pvalue
     * @return
     */
    @Path("property")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    //@Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
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
        AccountUser accountUser = (AccountUser) getActivationBean().findAccountUser(accountUserId);
        try {
        	getAccountBean().putAccountProperty(accountUser.getAccount().getId(), pname, pvalue);
        	
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
    
}
