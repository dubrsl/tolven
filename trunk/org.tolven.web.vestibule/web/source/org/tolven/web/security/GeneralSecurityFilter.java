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
package org.tolven.web.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.tolven.app.MenuLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;

/**
 * When a user attempts to access a resource whose URL is matched by the GeneralSecurityFilter, the request is
 * intercepted and the following checks made:
 * 1. Is the session valid?
 * 2. Does the user have a javax.security.auth.Subject?
 * 3. Does the Subject have a java.security.Principal which is granted during login authentication?
 * If none of the above criteria is met, the user is refused access, otherwise:
 * 
 * 1. Does the Subject have a org.tolven.security.key.UserPrivateKey which is granted during login authentication?
 * If not, access to the resource is denied and the user is redirected to the org.tolven.web.security.VestibuleSecurityFilter 
 * for further processing
 * 
 * 1. Is the user associated with an Account, and have access rights to that Account?
 * If not, the user is denied access to the account, and is redirected to the VestibuleSecurityFilter where they can
 * associate themselves with an account before proceeding
 * If all above checks are passed, then the user is allowed past this filter.
 * 
 * When a user attempts to access a resource whose URL is matched by the VestibuleSecurityFilter, the request is
 * intercepted and the following checks made:
 * 1. Is the session valid?
 * 2. Does the user have a javax.security.auth.Subject?
 * 3. Does the Subject have a java.security.Principal which is granted during login authentication?
 * If none of the above criteria is met, the user is refused access, otherwise:
 * 
 * The simplest passage through the vestibule, is if this not the first login for this session, and the user has a valid
 * org.tolven.core.entity.TolvenUser in the database associated with the Principal, and is associated with a valid 
 * org.tolven.core.entity.Account.
 * 
 * If the above criteria are met, then the user is allowed past this filter at this point, and can access the originally
 * requested page, which brought them here.
 * 
 * If the above criteria are not met, then the user has to pass through two phases of authentication, at which point 
 * they will be associated with a valid TolvenUser and Account.
 * 
 * Phase One: User Authentication
 * 1. Does the java.security.Principal have a corresponding TolvenUser in the database?
 * If not, an attempt is made to create the user, but only if a valid org.tolven.doc.entity.Invitation exists in the database. 
 * If this fails, then the user is denied access.
 * 
 * If successful, the user's database Id is stored in the javax.servlet.http.HttpSession.
 * 
 * If the user has just logged into the application for the first time, they are given a vestibule pass
 * and allowed to go onto the next phase of authentication. However, if they already have a vestibule pass from a 
 * previous visit, then they are disassociated with any previous org.tolven.core.entity.Account which must have
 * been obtained on that previous visit, and immediately redirected to a password verification page, before being 
 * allowed to move onto the next phase of authentication.
 * 
 * Phase Two: Account Authentication
 * If the user is not associated with an Account, but has a default Account set up, there is no need to require them to
 * select an Account, so they are associated with their default Account, and allowed to proceed to their default home
 * page. Note, that this is not necessarily the page they originally requested, but a consequence of being in the vestibule.
 * If the user is not associated with an Account, they a directed to a page where they can either create an Account or
 * select one from previously created Accounts.
 * 
 * The only way out of the vestibule, is to select an Account, which will then be placed in the HttpSession.
 * 
 * If the user had to undergo the two phases of the VestibuleSecurityFilter, then will end up on the home page of the Account
 * which they have selected.
 * 
 * @author Joseph Isaac
 */
public class GeneralSecurityFilter extends SecurityFilter implements Filter {

    public static String INVITATION_ID = "invitationId";
    public static String ACCOUNT_ID = "accountId";
    public static String ACCOUNTUSER = "accountUser";
    public static String ACCOUNTUSER_ID = "accountUserId";
    public static String PROPOSED_ACCOUNTUSER_ID = "proposedAccountUserId";
    public static String ACCOUNT_HOME = "accountHome";
    public static String PROPOSED_ACCOUNT_HOME = "proposedAccountHome";
    public static String TOLVENUSER_ID = "TolvenUserId";
    public static String VESTIBULE_PASS = "vestibulePass"; // true | false
    public static String TOLVENLOCALE = "tolvenLocale";

    @EJB
    private ActivationLocal activation;

    @EJB
    private MenuLocal menuBean;

    private static List<Class<?>> vestibuleClasses = new ArrayList<Class<?>>();

    public void init(FilterConfig config) throws ServletException {
    }

    private List<Vestibule> getVestibules() {
        List<Vestibule> vestibules = new ArrayList<Vestibule>();
        for (Class<?> clazz : vestibuleClasses) {
            try {
                vestibules.add((Vestibule) clazz.newInstance());
            } catch (Exception ex) {
                throw new RuntimeException("Could not instantiate class: " + clazz, ex);
            }
        }
        return vestibules;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            //Logger.getLogger(GeneralSecurityFilter.class).info("REQUEST=" + request.getRequestURL());
            if (!request.isRequestedSessionIdValid()) {
                logout("INVALID SESSION", request, response);
                return;
            }
            Principal principal = request.getUserPrincipal();
            if (principal == null) {
                logout("NO PRINCIPAL", request, response);
                return;
            }
            HttpSession session = request.getSession();
            boolean inVestibule = session.getAttribute(ACCOUNTUSER_ID) == null;
            boolean requestingVestibulePage = request.getRequestURI().startsWith(request.getContextPath() + "/vestibule/");
            if (inVestibule && !requestingVestibulePage || !inVestibule && requestingVestibulePage) {
                /*
                 * User is attempting to switch into or out of the vestibule
                 */
                List<Vestibule> vestibules = getVestibules();
                try {
                    enterVestibules(vestibules, servletRequest);
                    for (Vestibule vestibule : vestibules) {
                        String redirect = vestibule.validate(servletRequest);
                        if (redirect != null) {
                            response.sendRedirect(redirect);
                            return;
                        }
                    }
                    exitVestibules(principal.getName(), vestibules, servletRequest);
                    String redirect = null;
                    if (session.getAttribute(ACCOUNT_HOME) == null) {
                        redirect = request.getRequestURL().toString();
                    } else {
                        redirect = (String) session.getAttribute(ACCOUNT_HOME);
                    }
                    response.sendRedirect(redirect);
                    return;
                } catch (VestibuleException ex) {
                    session.removeAttribute(PROPOSED_ACCOUNTUSER_ID);
                    session.removeAttribute(ACCOUNTUSER_ID);
                    session.removeAttribute(ACCOUNT_ID);
                    for (Vestibule vestibule : vestibules) {
                        try {
                            vestibule.abort(servletRequest);
                        } catch (Exception e) {
                            //something has already gone wrong, do the best to clean up
                        }
                    }
                    if (ex.getRedirect() == null) {
                        logout(ex.getMessage(), request, response);
                        return;
                    } else {
                        logout(ex.getRedirect(), ex.getMessage(), request, response);
                        return;
                    }
                } catch (Exception ex) {
                    session.removeAttribute(PROPOSED_ACCOUNTUSER_ID);
                    session.removeAttribute(ACCOUNTUSER_ID);
                    session.removeAttribute(ACCOUNT_ID);
                    for (Vestibule vestibule : vestibules) {
                        try {
                            vestibule.abort(servletRequest);
                        } catch (Exception e) {
                            //something has already gone wrong, do the best to clean up
                        }
                    }
                    throw new RuntimeException(ex);
                }
            }
            if (!inVestibule) {
                Long accountUserId = (Long) session.getAttribute(ACCOUNTUSER_ID);
                AccountUser accountUser = activation.findAccountUser(accountUserId);
                if (accountUser == null) {
                    String message = "No accountUser exists with Id: " + accountUserId;
                    throw new RuntimeException(message);
                }
                // Verify that an accountUserIf specified in the request, if any, matches what we think it is
                String reqAccountUserId = request.getParameter("accountUserId");
                if (reqAccountUserId != null) {
                    if (accountUser.getId() != Long.parseLong(reqAccountUserId)) {
                        String message = "AccountUser is not valid for this session";
                        throw new RuntimeException(message);
                    }
                }
                // Set accountUser in request for the duration of this request
                request.setAttribute(ACCOUNTUSER, accountUser);
            }
            //Logger.getLogger(GeneralSecurityFilter.class).info("GOT REQUEST=" + request.getRequestURL());
        } catch (Exception ex) {
            String message = "Security Exception for " + request.getRequestURI();
            logout(message + ": " + ex.getMessage(), request, response);
            throw new ServletException(message, ex);
        }
        // Don't let 
        chain.doFilter(servletRequest, servletResponse);
    }

    private void enterVestibules(List<Vestibule> vestibules, ServletRequest servletRequest) throws VestibuleException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        if (session.getAttribute(ACCOUNTUSER_ID) != null) {
            session.removeAttribute(ACCOUNTUSER_ID);
            session.removeAttribute(PROPOSED_ACCOUNTUSER_ID);
            session.removeAttribute(ACCOUNT_ID);
            for (Vestibule vestibule : vestibules) {
                vestibule.enter(servletRequest);
            }
        }
        if (session.getAttribute(TOLVENUSER_ID) == null) {
            session.setAttribute(GeneralSecurityFilter.VESTIBULE_PASS, "true");
        }
    }

    private void exitVestibules(String principalName, List<Vestibule> vestibules, ServletRequest servletRequest) throws VestibuleException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        Long accountUserId = (Long) session.getAttribute(PROPOSED_ACCOUNTUSER_ID);
        if (accountUserId == null) {
            throw new RuntimeException(PROPOSED_ACCOUNTUSER_ID + " not set by vestibule");
        }
        AccountUser accountUser = activation.findAccountUser(accountUserId);
        if (accountUser == null) {
            throw new RuntimeException("accountUser does not exist with Id: " + accountUserId);
        }
        // Save ACCOUNTUSER_ID in session for subsequent request so the security filters can intercept appropriately
        session.setAttribute(ACCOUNTUSER_ID, accountUserId);
        session.setAttribute(ACCOUNT_ID, new Long(accountUser.getAccount().getId()));
        String accountHome = (String) session.getAttribute(PROPOSED_ACCOUNT_HOME);
        session.setAttribute(ACCOUNT_HOME, accountHome);
        for (Vestibule vestibule : vestibules) {
            vestibule.exit(servletRequest);
            session.removeAttribute(vestibule.getName());
        }
        session.removeAttribute(PROPOSED_ACCOUNTUSER_ID);
        session.removeAttribute(PROPOSED_ACCOUNT_HOME);
        session.removeAttribute(VESTIBULE_PASS);
        /*
         * SAFETY CHECK HERE - User should match the principal
         */
        Long tolvenUserId = (Long) session.getAttribute(TOLVENUSER_ID);
        TolvenUser sessionUser = activation.findTolvenUser(tolvenUserId);
        TolvenUser principalUser = activation.findUser(principalName);
        if (principalUser == null || sessionUser == null || principalUser.getId() != sessionUser.getId()) {
            Long sessionUserId = sessionUser == null ? null : sessionUser.getId();
            Long principalUserId = principalUser == null ? null : principalUser.getId();
            throw new RuntimeException("Session TolvenUser " + sessionUserId + " != principal TolvenUser " + principalUserId);
        }
        /*
         * SAFETY CHECK HERE - Don't trust the accountUserId alone, it must match user.
         */
        if (accountUser.getUser().getId() != sessionUser.getId()) {
            throw new RuntimeException("ACCOUNTUSER DOES NOT BELONG TO USER");
        }
        // Record the time when the user logged into this particular account
        accountUser.setLastLoginTime((Date) request.getAttribute(TOLVEN_NOW));
        // Set maxInactive interval (we leave a buffer since the browser may be later than the server in timing out.
        // Browser should time out first.
        String logoutInterval = accountUser.getProperty().get("tolven.web.cvLogout");
        if (logoutInterval != null && logoutInterval.length() > 0) {
            session.setMaxInactiveInterval(Integer.parseInt(logoutInterval) * 2);
        }
        Account account = accountUser.getAccount();
        if (account.isDisableAutoRefresh() == null || account.isDisableAutoRefresh() == false) {
            menuBean.updateMenuStructure(account);
        }
    }

    public void destroy() {
    }

    static {
        String propertyFileName = GeneralSecurityFilter.class.getSimpleName() + ".properties";
        InputStream in = GeneralSecurityFilter.class.getResourceAsStream(propertyFileName);
        if (in != null) {
            Properties properties = new Properties();
            try {
                properties.load(in);
            } catch (Exception ex) {
                throw new RuntimeException("Could not load vestibules from: " + propertyFileName, ex);
            }
            String vestibuleClassNames = properties.getProperty("vestibuleClasses");
            if (vestibuleClassNames == null) {
                throw new RuntimeException("Could not find property name vestibuleClasses in file: " + propertyFileName);
            }
            for (String className : vestibuleClassNames.split(",")) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException("Could not load vestibules from: " + propertyFileName, ex);
                }
                vestibuleClasses.add(clazz);
            }
        }
    }
}