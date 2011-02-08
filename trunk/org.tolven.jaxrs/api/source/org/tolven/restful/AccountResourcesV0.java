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
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.tolven.api.APIXMLUtil;
import org.tolven.api.accountuser.XAccountUser;
import org.tolven.api.accountuser.XAccountUserFactory;
import org.tolven.api.security.GeneralSecurityFilter;
import org.tolven.app.MenuLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.LoginLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;
import org.tolven.util.ExceptionFormatter;

import com.sun.jersey.core.util.MultivaluedMapImpl;

@Path("account")
@ManagedBean
public class AccountResourcesV0 {

    @EJB
    private ActivationLocal activationBean;

    @EJB
    private AccountDAOLocal accountBean;

    @EJB
    private MenuLocal menuBean;

    @EJB
    private TolvenPropertiesLocal propertyBean;
    
    @EJB
    private LoginLocal loginBean;

    @Context
    private HttpServletRequest request;

    /**
     * Get general information about the current account for the current user
     * @return
     */
    @Path("info")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getInfo() {
        HttpSession session = request.getSession();
        StringBuffer sb = new StringBuffer();
        sb.append("Session Attributes:\n");
        for (Enumeration<String> e = session.getAttributeNames(); e.hasMoreElements();) {
            String attr = e.nextElement();
            sb.append("  ");
            sb.append(attr);
            sb.append("=");
            sb.append(session.getAttribute(attr));
            sb.append("\n");
        }
        Long accountUserId = (Long) session.getAttribute("accountUserId");
        if (accountUserId!=null) {
            sb.append("\nUser Attributes:\n");
            AccountUser accountUser = activationBean.findAccountUser(accountUserId);
            TolvenPerson tp = getTolvenPerson();
            sb.append("  UID=");sb.append(tp.getUid());sb.append("\n");
            sb.append("  GivenName=");sb.append(tp.getGivenName());sb.append("\n");
            sb.append("  SN=");sb.append(tp.getSn());sb.append("\n");
            try {
                sb.append("  email=");sb.append(tp.getPrimaryMail());sb.append("\n");
            } catch (Exception e) {
                sb.append("Exception: " + e.getMessage());
            }
            sb.append("\nAccount Attributes:\n");
            Account account = accountUser.getAccount();
            sb.append("  accountId=");sb.append(account.getId());sb.append("\n");
            sb.append("  Title=");sb.append(account.getTitle());sb.append("\n");
            {
                Account accountTemplate = account.getAccountTemplate();
                if (accountTemplate!=null) {
                    sb.append("  templateAccountId=");sb.append(accountTemplate.getId());sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
    
    private TolvenPerson getTolvenPerson() {
        TolvenPerson tp = new TolvenPerson();
        Set<String> cnSet = (Set<String>) request.getAttribute("cn");
        if (cnSet != null && !cnSet.isEmpty()) {
            tp.setCn(cnSet.iterator().next());
        }
        Set<String> dnSet = (Set<String>) request.getAttribute("dn");
        if (dnSet != null && !dnSet.isEmpty()) {
            tp.setDn(dnSet.iterator().next());
        }
        Set<String> givenNameSet = (Set<String>) request.getAttribute("givenName");
        if (givenNameSet != null && !givenNameSet.isEmpty()) {
            tp.setGivenName(givenNameSet.iterator().next());
        }
        Set<String> oSet = (Set<String>) request.getAttribute("o");
        if (oSet != null && !oSet.isEmpty()) {
            tp.setOrganizationName(oSet.iterator().next());
        }
        Set<String> ouSet = (Set<String>) request.getAttribute("ou");
        if (ouSet != null && !ouSet.isEmpty()) {
            tp.setOrganizationUnitName(ouSet.iterator().next());
        }
        Set<String> snSet = (Set<String>) request.getAttribute("sn");
        if (snSet != null && !snSet.isEmpty()) {
            tp.setSn(snSet.iterator().next());
        }
        Set<String> stSet = (Set<String>) request.getAttribute("st");
        if (stSet != null && !stSet.isEmpty()) {
            tp.setStateOrProvince(stSet.iterator().next());
        }
        Set<String> uidSet = (Set<String>) request.getAttribute("uid");
        if (uidSet != null && !uidSet.isEmpty()) {
            tp.setUid(uidSet.iterator().next());
        }
        Set<String> mailSet = (Set<String>) request.getAttribute("dn");
        if (mailSet != null) {
            tp.setMail(new ArrayList<String>(mailSet));
        }
        return tp;
    }

    /**
     * Create a new Tolven account
     * Parameters include attributes of account object.
     * Parameters can also include account properties.
     * The initialUser must be specified. This user will have the account adminitrator permission
     * and will be the user capable of "inviting" other users to the account.
     * The response status code will be "created" if the account was created.
     * The location url will contain the account id.
     */
    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Deprecated
	public Response createAccount(
			@FormParam("accountType") String accountTypeName,
			@FormParam("initialUser") String initialUserString,
			@FormParam("title") String title,
			@FormParam("timezone") String timezone,
			@FormParam("locale") String localeName,
			@DefaultValue("html") @FormParam("emailFormat") String emailFormat,
			@DefaultValue("true" ) @FormParam("enableBackButton") String enableBackButton,
			@DefaultValue("false" )@FormParam("disableAutoRefresh") String disableAutoRefresh,
			@DefaultValue("false" )@FormParam("manualMetadataUpdate") String manualMetadataUpdate,
			@FormParam("property") String property,
			@Context SecurityContext sc,
			MultivaluedMap<String, String> formParams) {
        AccountType accountType = accountBean.findAccountTypebyKnownType(accountTypeName);
        if (accountType == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Account Type not provided").build();
        }
        if (!accountType.isCreatable()) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Account Type is not creatable").build();
        }
        if (initialUserString == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Missing Initial User").build();
        }
        TolvenUser initialUser = activationBean.findUser(initialUserString);
        if (initialUser == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Invalid Initial User").build();
        }
        Account account = null;
        try {
            account = accountBean.createAccount2(title, timezone, accountType);
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(e, "\\n")).build();
        }
        account.setAccountType(accountType);
        account.setEmailFormat(emailFormat);
        if (localeName != null) {
            Locale[] availableLocales = Locale.getAvailableLocales();
            Locale foundLocale = null;
            for (Locale locale : availableLocales) {
                if (localeName.equalsIgnoreCase(locale.getDisplayName())) {
                    foundLocale = locale;
                    break;
                }
            }
            account.setLocale(foundLocale.getDisplayName());
        }
        account.setEnableBackButton(enableBackButton.equalsIgnoreCase("true"));
        account.setDisableAutoRefresh(disableAutoRefresh.equalsIgnoreCase("true"));
        account.setManualMetadataUpdate(manualMetadataUpdate.equalsIgnoreCase("true"));
        // Add the initial user to the account
        PublicKey userPublicKey = TolvenSSO.getInstance().getUserPublicKey(request);
        accountBean.addAccountUser(account, initialUser, new Date(), true, userPublicKey);
        menuBean.updateMenuStructure(account);
        URI uri = null;
        try {
            uri = new URI(URLEncoder.encode(Long.toString(account.getId()), "UTF-8"));
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(e, "\\n")).build();
        }
        return Response.created(uri).type(MediaType.TEXT_PLAIN).entity(String.valueOf(account.getId())).build();
    }

    @Path("default")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setDefaultAccount(@FormParam("defaulted") String defaulted) {
        AccountUser accountUser = (AccountUser) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
        if("true".equals(defaulted)) {
            activationBean.setDefaultAccountUser(accountUser);
        } else {
             accountUser.setDefaultAccount(false);
        }
        return Response.ok().type(MediaType.TEXT_PLAIN).build();
    }

    /**
     * Add a user to a Tolven account
     * The logged-in user must be a user on the account
     * Properties may also be added to the accountUser
     */
    @Path("user/add")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addUser(
			@FormParam("user") String invitedUser,
			@FormParam ("userX509Certificate") String invitedUserCertificate,
			@FormParam("account") String accountIdString,
			@FormParam("property") String property,
			@Context SecurityContext sc,
			MultivaluedMap<String, String> formParams) {
        if (invitedUser == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Missing User").build();
        }
        if (accountIdString == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Missing Account Id").build();
        }
        Long accountId = null;
        try {
            accountId = Long.parseLong(accountIdString);
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Error parsing Account Id").build();
        }
        Account account = null;
        try {
            account = accountBean.findAccount(accountId);
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Invalid Account").build();
        }
        // Make sure that the new user is not already an active member of the account
        AccountUser newAccountUser = accountBean.findAccountUser(invitedUser, accountId);
        if (newAccountUser != null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("User already a member of this account").build();
        }
        Principal principal = request.getUserPrincipal();
        AccountUser inviterAccountUser = accountBean.findAccountUser(principal.getName(), account.getId());
        if (inviterAccountUser == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("Inviting user must a member of this account").build();
        }
        try {
            Date now = (Date) request.getAttribute("tolvenNow");
            TolvenUser invitedTolvenUser = activationBean.findUser(invitedUser);
            if (invitedTolvenUser == null) {
                TolvenPerson tp = new TolvenPerson();
                tp.setUid(invitedUser);
                invitedTolvenUser = loginBean.activate(tp, now);
            }
            String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
            PrivateKey inviterUserPrivateKey = TolvenSSO.getInstance().getUserPrivateKey(request, keyAlgorithm);
            PublicKey invitedUserPublicKey = null;
            if (invitedUserCertificate != null) {
                byte[] userCertificateBytes = Base64.decodeBase64(URLDecoder.decode(invitedUserCertificate, "UTF-8").getBytes("UTF-8"));
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(userCertificateBytes));
                invitedUserPublicKey = x509Certificate.getPublicKey();
            }
            /*
             * A userPublicKey is required for the added user, in order to protect the AccountPrivateKey, which will be associated with them
             * A null key means their data will not be encrypted in the account
             */
            newAccountUser = accountBean.inviteAccountUser(account, inviterAccountUser, invitedTolvenUser, inviterUserPrivateKey, now, false, invitedUserPublicKey);
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity("Adding a user to an Account must be done by a user with a UserPublicKey").build();
        }
        URI uri = null;
        try {
            uri = new URI(URLEncoder.encode(Long.toString(newAccountUser.getId()), "UTF-8"));
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(e, "\\n")).build();
        }
        return Response.created(uri).type(MediaType.TEXT_PLAIN).entity(String.valueOf(newAccountUser.getId())).build();
    }

    /**
     * Create or update Account Properties
     * @return
     */
    @Path("properties/set")
    @POST
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response putAccountProperties(MultivaluedMap<String, String> mvMap) {
        try {
            if (mvMap == null) {
                return Response.status(400).type(MediaType.TEXT_PLAIN).entity("No properties supplied").build();
            }
            AccountUser accountUser = (AccountUser) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
            if (accountUser == null) {
                throw new RuntimeException("No accountUser found");
            }
            Properties properties = new Properties();
            for (String name : mvMap.keySet()) {
                properties.setProperty(name, mvMap.getFirst(name));
            }
            accountBean.putAccountProperties(accountUser.getAccount().getId(), properties);
            TolvenSSO.getInstance().updateAccountUserTimestamp(request);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

}
