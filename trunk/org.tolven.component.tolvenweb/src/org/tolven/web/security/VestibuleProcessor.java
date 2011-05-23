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
package org.tolven.web.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.xml.datatype.DatatypeFactory;

import org.tolven.api.APIXMLUtil;
import org.tolven.api.accountuser.XAccountUser;
import org.tolven.api.accountuser.XProperty;
import org.tolven.api.facade.accountuser.XFacadeAccountUsers;
import org.tolven.core.ActivationLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.sso.TolvenSSO;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@ManagedBean
public class VestibuleProcessor {

    @EJB
    private ActivationLocal activation;

    private DatatypeFactory datatypeFactory;

    private DatatypeFactory getDatatypeFactory() {
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Could not create DatatypeFactory", ex);
            }
        }
        return datatypeFactory;
    }

    public void refreshAccount(HttpServletRequest request) {
        String accountUserIdString = TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID, request);
        if (accountUserIdString == null || accountUserIdString.length() == 0) {
            throw new RuntimeException("No SSO session property: " + GeneralSecurityFilter.ACCOUNTUSER_ID);
        }
        AccountUser accountUser = activation.findAccountUser(Long.parseLong(accountUserIdString));
        if (accountUser == null) {
            throw new RuntimeException("accountUser does not exist with Id: " + accountUserIdString);
        }
        String ssoAccountUserTS = TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_TIMESTAMP, request);
        if (ssoAccountUserTS == null || ssoAccountUserTS.length() == 0) {
            throw new RuntimeException("The SSO session " + GeneralSecurityFilter.ACCOUNTUSER_TIMESTAMP + " is null while in Account");
        }
        Date ssoAccountUserTime = getDatatypeFactory().newXMLGregorianCalendar(ssoAccountUserTS).toGregorianCalendar().getTime();
        Date localAccountUserTime = (Date) request.getSession(false).getAttribute(GeneralSecurityFilter.ACCOUNTUSER_TIMESTAMP);
        if (localAccountUserTime == null || localAccountUserTime.before(ssoAccountUserTime)) {
            process("/accounts/refresh", request);
        }
        //Ensure that TolvenUser is always available, whether in the Vestibule or an Account
        TolvenUser user = activation.findTolvenUser(accountUser.getUser().getId());
        request.setAttribute(GeneralSecurityFilter.TOLVENUSER, user);
        request.setAttribute(GeneralSecurityFilter.ACCOUNTUSER, accountUser);
        request.setAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID, accountUser.getId());
    }

    public void refreshVestibule(HttpServletRequest request) {
        String ssoAccountUserTS = TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_TIMESTAMP, request);
        if (ssoAccountUserTS == null) {
            throw new RuntimeException("The SSO session " + GeneralSecurityFilter.ACCOUNTUSER_TIMESTAMP + " is null while in Account");
        }
        Date ssoAccountUserTime = getDatatypeFactory().newXMLGregorianCalendar(ssoAccountUserTS).toGregorianCalendar().getTime();
        Date localAccountUserTime = (Date) request.getSession(false).getAttribute(GeneralSecurityFilter.ACCOUNTUSER_TIMESTAMP);
        if (localAccountUserTime == null || localAccountUserTime.before(ssoAccountUserTime)) {
            process("/vestibule/enter", request);
        }
        //Ensure that TolvenUser is always available, whether in the Vestibule or an Account
        TolvenUser user = activation.findUser(request.getUserPrincipal().getName());
        request.setAttribute(GeneralSecurityFilter.TOLVENUSER, user);
    }

    public String enterVestibule(HttpServletRequest request) {
        MultivaluedMap<String, String> responseMap = process("/vestibule/enter", request);
        String vestibuleRedirect = responseMap.getFirst(GeneralSecurityFilter.VESTIBULE_REDIRECT);
        return vestibuleRedirect;
    }

    public String exitVesitbule(HttpServletRequest request) {
        MultivaluedMap<String, String> responseMap = process("/vestibule/exit", request);
        String vestibuleRedirect = responseMap.getFirst(GeneralSecurityFilter.VESTIBULE_REDIRECT);
        if (vestibuleRedirect == null || vestibuleRedirect.length() == 0) {
            throw new RuntimeException("No destination found for vestibule exit");
        } else {
            return vestibuleRedirect;
        }
    }

    private MultivaluedMap<String, String> process(String vestibuleURL, HttpServletRequest request) {
        try {
            ClientResponse clientResponse = getVestibuleResponse(vestibuleURL, request);
            MultivaluedMap<String, String> responseMap = clientResponse.getEntity(MultivaluedMap.class);
            XAccountUser xAccountUser = getXAccountUser(responseMap, request);
            Map<String, String> propertiesMap = new HashMap<String, String>();
            for (XProperty xProperty : xAccountUser.getProperties()) {
                propertiesMap.put(xProperty.getName(), xProperty.getValue());
            }
            String accountLocale = null;
            TolvenUser user = activation.findTolvenUser(xAccountUser.getUser().getId());
            if(user == null) {
                throw new RuntimeException("TolvenUser not returned for: " + request.getUserPrincipal());
            }
            request.setAttribute(GeneralSecurityFilter.TOLVENUSER, user);
            if (xAccountUser.getId() != null) {
                accountLocale = xAccountUser.getAccount().getLocale();
                //setting the accountUserId here is legacy since it exists in the SSO session
                request.setAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID, xAccountUser.getId());
                AccountUser accountUser = activation.findAccountUser(xAccountUser.getId());
                if (accountUser == null) {
                    throw new RuntimeException("accountUser does not exist with Id: " + xAccountUser.getId());
                }
                request.setAttribute(GeneralSecurityFilter.ACCOUNTUSER, accountUser);
            }
            Locale locale = ResourceBundleHelper.getLocale(xAccountUser.getUser().getLocale(), accountLocale);
            TolvenResourceBundle tolvenResourceBundle = new TolvenResourceBundle(propertiesMap, locale);
            XFacadeAccountUsers xFacadeAccountUsers = getXFacadeAccountUsers(responseMap, request);
            request.getSession(false).setAttribute(GeneralSecurityFilter.ACCOUNTUSERS, xFacadeAccountUsers);
            request.getSession(false).setAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE, tolvenResourceBundle);
            request.getSession(false).setAttribute(GeneralSecurityFilter.ACCOUNTUSER_TIMESTAMP, xAccountUser.getTimestamp());
            return responseMap;
        } catch (Exception ex) {
            ex.printStackTrace();
            String message = "Security Exception for " + request.getRequestURI() + " " + ex.getMessage();
            throw new RuntimeException(message);
        }
    }

    private ClientResponse getVestibuleResponse(String vestibuleURL, HttpServletRequest request) {
        ClientResponse clientResponse = null;
        WebResource webResource = null;
        try {
            String appRestfulURL = (String) request.getSession(false).getServletContext().getInitParameter("appRestful.url");
            Client client = (Client) request.getSession().getServletContext().getAttribute("restfulClient");
            webResource = client.resource(appRestfulURL).path(vestibuleURL);
            NewCookie ssoCookie = TolvenSSO.getInstance().getSSOCookie((HttpServletRequest) request);
            clientResponse = webResource.cookie(ssoCookie).accept(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);
        } catch (Exception ex) {
            throw new RuntimeException("Failed while getting Vestibule response", ex);
        }
        if (clientResponse.getStatus() != 200) {
            String reason = "Status: " + clientResponse.getStatus() + " " + request.getUserPrincipal() + " GET " + webResource.getURI() + " " + clientResponse.getEntity(String.class);
            throw new RuntimeException(reason);
        }
        return clientResponse;
    }

    private XAccountUser getXAccountUser(MultivaluedMap<String, String> responseMap, HttpServletRequest request) {
        String xAccountUserXML = responseMap.getFirst(GeneralSecurityFilter.ACCOUNTUSER);
        if (xAccountUserXML == null || xAccountUserXML.length() == 0) {
            throw new RuntimeException("No XAccountUser XML found in response property: " + GeneralSecurityFilter.ACCOUNTUSER);
        }
        XAccountUser xAccountUser = null;
        try {
            xAccountUser = APIXMLUtil.unMarshalXAccountUser(xAccountUserXML);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to unmarshal XAccountUser XML", ex);
        }
        if (!xAccountUser.getUser().getUid().equalsIgnoreCase(request.getUserPrincipal().getName())) {
            throw new RuntimeException("User: " + request.getUserPrincipal().getName() + " not authorized to use account: " + xAccountUser.getAccount().getId());
        }
        return xAccountUser;
    }

    private XFacadeAccountUsers getXFacadeAccountUsers(MultivaluedMap<String, String> responseMap, HttpServletRequest request) {
        String xFacadeAccountUsersXML = responseMap.getFirst(GeneralSecurityFilter.ACCOUNTUSERS);
        if (xFacadeAccountUsersXML == null || xFacadeAccountUsersXML.length() == 0) {
            throw new RuntimeException("No XFacadeAccountUsers XML found in response property: " + GeneralSecurityFilter.ACCOUNTUSERS);
        }
        XFacadeAccountUsers xFacadeAccountUsers = null;
        try {
            xFacadeAccountUsers = APIXMLUtil.unMarshalXFacadeAccountUsers(xFacadeAccountUsersXML);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to unmarshal XFacadeAccountUsers XML", ex);
        }
        if (!xFacadeAccountUsers.getUsername().equalsIgnoreCase(request.getUserPrincipal().getName())) {
            throw new RuntimeException("User: " + request.getUserPrincipal().getName() + " not authorized to use account: " + xFacadeAccountUsers.getUsername());
        }
        return xFacadeAccountUsers;
    }
    
}
