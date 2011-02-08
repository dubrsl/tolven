/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.api.security;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.tolven.core.ActivationLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.LoginLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.sso.TolvenSSO;

public class GeneralSecurityFilter implements Filter {

    public static final String INVITATION_ID = "invitationId";
    public static final String ACCOUNT_ID = "accountId";
    public static final String ACCOUNTUSER = "accountUser";
    public static final String ACCOUNTUSERS = "accountUsers";
    public static final String ACCOUNTUSER_TIMESTAMP = "accountUserTimestamp";
    public static final String USERACCOUNT = "userAccount";
    public static final String ACCOUNTUSER_ID = "accountUserId";
    public static final String PROPOSED_ACCOUNTUSER_ID = "proposedAccountUserId";
    public static final String PROPOSED_DEFAULT_ACCOUNT = "proposedDefaultAccount"; // true | false
    public static final String ACCOUNT_HOME = "accountHome";
    public static final String PROPOSED_ACCOUNT_HOME = "proposedAccountHome";
    public static final String TOLVENUSER_ID = "TolvenUserId";
    public static final String VESTIBULE_PASS = "vestibulePass"; // true | false
    public static final String VESTIBULE_REDIRECT = "vestibuleRedirect";
    public static final String USER_CONTEXT = "userContext";
    public static final String USER_KEYS_OPTIONAL = "tolven.security.user.keysOptional";

    @EJB
    private ActivationLocal activationBean;

    @EJB
    private LoginLocal loginBean;

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String tolvenUserIdString = TolvenSSO.getInstance().getSessionProperty(TOLVENUSER_ID, request);
        if (tolvenUserIdString == null || tolvenUserIdString.length() == 0) {
            /*
             * Just logged in so TolvenUserId must be set
             */
            Principal principal = request.getUserPrincipal();
            TolvenUser user = activationBean.findUser(principal.getName());
            String principalName = request.getUserPrincipal().getName();
            if (user == null) {
                //Activate the user
                Date now = (Date) request.getAttribute("tolvenNow");
                TolvenPerson tp = new TolvenPerson();
                tp.setUid(principalName);
                user = loginBean.activate(tp, now);
            }
            if (user == null) {
                throw new RuntimeException("Could not activate user: " + principalName);
            }
            activationBean.loginUser(user.getLdapUID(), (Date) request.getAttribute("tolvenNow"));
            /*
             * Finally set the all clear, by providing the necessary TolvenUserId
             */
            TolvenSSO.getInstance().setSessionProperty(TOLVENUSER_ID, String.valueOf(user.getId()), request);
        }
        String accountUserId = TolvenSSO.getInstance().getSessionProperty(ACCOUNTUSER_ID, request);
        if (accountUserId != null && accountUserId.length() > 0) {
            /*
             * User is in an account
             */
            
            AccountUser accountUser = activationBean.findAccountUser(Long.parseLong(accountUserId));
            Long tolvenUserId = Long.parseLong(tolvenUserIdString);
            if (accountUser.getUser().getId() != tolvenUserId) {
                throw new RuntimeException("Tolven User Id: " + tolvenUserId + " not authorized to use account: " + accountUser.getAccount().getId());
            }
            // Set accountUser in request for the duration of this request
            request.setAttribute(ACCOUNTUSER, accountUser);
            request.setAttribute(ACCOUNTUSER_ID, accountUser.getId());
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }

}