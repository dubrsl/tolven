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

import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.tolven.app.MenuLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.LoginLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;
import org.tolven.util.ExceptionFormatter;

@ManagedBean
public class VestibuleProcessor {

    public static final String DEFAULT_ACCOUNT_HOME = "/private/application.jsf";

    @EJB
    private LoginLocal loginBean;

    @EJB
    private ActivationLocal activationBean;

    @EJB
    private MenuLocal menuBean;

    @EJB
    private TolvenPropertiesLocal propertyBean;

    private javax.naming.Context ctx;
    private static List<String> vestibuleJNDINames = new ArrayList<String>();

    private Logger logger = Logger.getLogger(VestibuleProcessor.class);

    /**
     * On entering the vestibule, a TolvenUser must already be associated with the Principal.
     * The user is provided with a Vestibule pass and the user login time is updated.
     * The TolvenUser Id is added to the session and the TolvenUser is added as a request attribute.
     * If a default account exists, then an attempt is made to automatically log the user into it.
     * 
     * @param request
     * @return
     */
    public String enterVestibule(HttpServletRequest request) {
        logger.info("Vestibule entered: " + request.getUserPrincipal());
        boolean inAccount = "account".equals(TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.USER_CONTEXT, request));
        if (inAccount) {
            throw new RuntimeException("Cannot access vestibule while in an account");
        }
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        Date now = (Date) request.getAttribute("tolvenNow");
        String principalName = request.getUserPrincipal().getName();
        TolvenUser user = activationBean.findUser(request.getUserPrincipal().getName());
        if (user == null) {
            //Activate the user
            TolvenPerson tp = new TolvenPerson();
            tp.setUid(principalName);
            user = loginBean.activate(tp, now);
        }
        if (user == null) {
            throw new RuntimeException("Could not activate user: " + principalName);
        }
        activationBean.loginUser(user.getLdapUID(), (Date) request.getAttribute("tolvenNow"));
        /*
         * Finally set the all clear for the user, by providing the necessary TolvenUserId
         */
        TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.TOLVENUSER_ID, String.valueOf(user.getId()), request);
        request.setAttribute(GeneralSecurityFilter.TOLVENUSER, user);
        AccountUser defaultAccountUser = activationBean.findDefaultAccountUser(user);
        String vestibuleRedirect = null;
        if (defaultAccountUser == null) {
            /*
             * With no default account, can only enter the vestibule
             */
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.VESTIBULE_PASS, "true", request);
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.USER_CONTEXT, "vestibule", request);
            if (logger.isDebugEnabled()) {
                logger.debug("TOLVEN_PERF: @Path(vestibule/enter): " + (System.currentTimeMillis() - start));
            }
            Map<String, Object> responseMap = getVestibuleResponse(request);
            vestibuleRedirect = (String) responseMap.get(GeneralSecurityFilter.VESTIBULE_REDIRECT);
        } else {
            /*
             * If a default account is present, attempt to allow the user to exit to it
             */
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, Long.toString(defaultAccountUser.getId()), request);
            vestibuleRedirect = exitVesitbule(request);
        }
        return vestibuleRedirect;
    }

    /**
     * All vestibule request attributes contain the tolvenUser
     * 
     * @param request
     */
    public void refreshVestibule(HttpServletRequest request) {
        TolvenUser user = activationBean.findUser(request.getUserPrincipal().getName());
        if (user == null) {
            throw new RuntimeException("No TolvenUser found for: " + request.getUserPrincipal());
        }
        request.setAttribute(GeneralSecurityFilter.TOLVENUSER, user);
    }

    /**
     * On exiting the vestibule, required request and session attributes are set, and an appropriate redirect may occur
     * @param request
     * @return
     */
    public String exitVesitbule(HttpServletRequest request) {
        Map<String, Object> responseMap = getVestibuleResponse(request);
        String vestibuleRedirect = (String) responseMap.get(GeneralSecurityFilter.VESTIBULE_REDIRECT);
        if (vestibuleRedirect == null || vestibuleRedirect.length() == 0) {
            throw new RuntimeException("No destination found for vestibule exit");
        } else {
            return vestibuleRedirect;
        }
    }

    /**
     * All account request attributes contain the tolvenUser (plus the TolvenUserId for backward compatibility)
     * 
     * @param request
     */
    public void refreshAccount(HttpServletRequest request) {
        boolean inAccount = "account".equals(TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.USER_CONTEXT, request));
        if (!inAccount) {
            throw new RuntimeException("User " + request.getUserPrincipal() + " is not in an Account");
        }
        String accountUserIdString = TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID, request);
        if (accountUserIdString == null || accountUserIdString.trim().length() == 0) {
            throw new RuntimeException("No accountUser Id found in session for user: " + request.getUserPrincipal());
        }
        AccountUser accountUser = activationBean.findAccountUser(Long.parseLong(accountUserIdString));
        if (accountUser == null) {
            throw new RuntimeException("Could not find accountUser " + accountUserIdString + " for user " + request.getUserPrincipal());
        }
        TolvenUser user = activationBean.findUser(request.getUserPrincipal().getName());
        if (user == null) {
            throw new RuntimeException("No TolvenUser found for: " + request.getUserPrincipal());
        }
        if (accountUser.getUser().getId() != user.getId()) {
            throw new RuntimeException("Tolven User Id: " + user.getId() + " not authorized to use account: " + accountUser.getAccount().getId() + " for principal: " + request.getUserPrincipal());
        }
        request.setAttribute(GeneralSecurityFilter.TOLVENUSER, user);
        request.setAttribute(GeneralSecurityFilter.ACCOUNTUSER, accountUser);
        //The accountUser Id is required only for legacy, since the accountUser is in the request
        request.setAttribute(GeneralSecurityFilter.ACCOUNTUSER_ID, accountUser.getId());
    }

    private Map<String, Object> getVestibuleResponse(HttpServletRequest request) {
        boolean inAccount = "account".equals(TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.USER_CONTEXT, request));
        if (inAccount) {
            throw new RuntimeException("You are not in the Vestibule");
        }
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        List<Vestibule> vestibules = getVestibules();
        try {
            for (Vestibule vestibule : vestibules) {
                String vestibuleRedirect = vestibule.validate(request);
                if (vestibuleRedirect != null) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(GeneralSecurityFilter.VESTIBULE_REDIRECT, vestibuleRedirect);
                    return map;
                }
            }
            Map<String, Object> map = exitVestibule(vestibules, request);
            logger.info("Vestibule exit: " + request.getUserPrincipal());
            if (logger.isDebugEnabled()) {
                logger.debug("TOLVEN_PERF: @Path(vestibule/exit): " + (System.currentTimeMillis() - start));
            }
            return map;
        } catch (VestibuleException ex) {
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, request);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID, request);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.ACCOUNT_ID, request);
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.USER_CONTEXT, "vestibule", request);
            for (Vestibule vestibule : vestibules) {
                try {
                    vestibule.abort(request);
                } catch (Exception e) {
                    //something has already gone wrong, do the best to clean up
                }
            }
            if (ex.getRedirect() == null) {
                throw new RuntimeException("Logout: " + ExceptionFormatter.toSimpleString(ex, "\\n"));
            } else {
                throw new RuntimeException("Logout & redirect: " + ex.getRedirect() + " " + ExceptionFormatter.toSimpleString(ex, "\\n"));
            }
        } catch (Exception ex) {
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, request);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID, request);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.ACCOUNT_ID, request);
            TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.USER_CONTEXT, "vestibule", request);
            for (Vestibule vestibule : vestibules) {
                try {
                    vestibule.abort(request);
                } catch (Exception e) {
                    //something has already gone wrong, do the best to clean up
                }
            }
            throw new RuntimeException(ExceptionFormatter.toSimpleString(ex, "\\n"));
        }
    }

    private Map<String, Object> exitVestibule(List<Vestibule> vestibules, HttpServletRequest request) throws VestibuleException {
        String proposedAccountUserIdString = TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, request);
        if (proposedAccountUserIdString == null || proposedAccountUserIdString.length() == 0) {
            throw new RuntimeException(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID + " not set by vestibule");
        }
        Long accountUserId = Long.parseLong(proposedAccountUserIdString);
        AccountUser accountUser = activationBean.findAccountUser(accountUserId);
        if (accountUser == null) {
            throw new RuntimeException("accountUser does not exist with Id: " + accountUserId);
        }
        String userKeysOptional = propertyBean.getProperty(GeneralSecurityFilter.USER_KEYS_OPTIONAL);
        if (!Boolean.parseBoolean(userKeysOptional)) {
            String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
            PrivateKey privateKey = TolvenSSO.getInstance().getUserPrivateKey(request, keyAlgorithm);
            if (privateKey == null) {
                throw new RuntimeException("User requires a UserPrivateKey to log into account: " + accountUser.getAccount().getId());
            }
        }
        // Save ACCOUNTUSER in session for subsequent request so the security filters can intercept appropriately
        TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID, String.valueOf(accountUser.getId()), request);
        TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.ACCOUNT_ID, String.valueOf(accountUser.getAccount().getId()), request);
        for (Vestibule vestibule : vestibules) {
            vestibule.exit(request);
        }
        TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, request);
        TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNT_HOME, request);
        TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.VESTIBULE_PASS, request);
        /*
         * SAFETY CHECK HERE - Don't trust the accountUserId alone, it must match user.
         */
        TolvenUser user = activationBean.findUser(request.getUserPrincipal().getName());
        if (accountUser.getUser().getId() != user.getId()) {
            throw new RuntimeException("ACCOUNTUSER DOES NOT BELONG TO USER");
        }
        // Record the time when the user logged into this particular account
        Date now = (Date) request.getAttribute("tolvenNow");
        accountUser.setLastLoginTime(now);
        String proposedDefaultAccount = TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.PROPOSED_DEFAULT_ACCOUNT, request);
        if ("true".equals(proposedDefaultAccount)) {
            activationBean.setDefaultAccountUser(accountUser);
            TolvenSSO.getInstance().removeSessionProperty(GeneralSecurityFilter.PROPOSED_DEFAULT_ACCOUNT, request);
        }
        Account account = accountUser.getAccount();
        if (account.isDisableAutoRefresh() == null || account.isDisableAutoRefresh() == false) {
            menuBean.updateMenuStructure(account);
        }
        String accountHome = (String) TolvenSSO.getInstance().getSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNT_HOME, request);
        if (accountHome == null || accountHome.length() == 0) {
            accountHome = DEFAULT_ACCOUNT_HOME;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(GeneralSecurityFilter.VESTIBULE_REDIRECT, accountHome);
        //Setting the property can trigger a session notification, so set it last
        TolvenSSO.getInstance().setSessionProperty(GeneralSecurityFilter.USER_CONTEXT, "account", request);
        return map;
    }

    private List<Vestibule> getVestibules() {
        List<Vestibule> vestibules = new ArrayList<Vestibule>();
        for (String vestibuleJNDIName : vestibuleJNDINames) {
            try {
                Vestibule vestibule = (Vestibule) getContext().lookup(vestibuleJNDIName);
                vestibules.add(vestibule);
            } catch (Exception ex) {
                throw new RuntimeException("Could lookup: " + vestibuleJNDIName, ex);
            }
        }
        return vestibules;
    }

    private javax.naming.Context getContext() {
        if (ctx == null) {
            try {
                ctx = new InitialContext();
            } catch (Exception ex) {
                throw new RuntimeException("Could not create InitialContext", ex);
            }
        }
        return ctx;
    }

    static {
        String propertyFileName = VestibuleProcessor.class.getSimpleName() + ".properties";
        InputStream in = VestibuleProcessor.class.getResourceAsStream(propertyFileName);
        if (in != null) {
            Properties properties = new Properties();
            try {
                properties.load(in);
            } catch (Exception ex) {
                throw new RuntimeException("Could not load vestibuleJNDINames from: " + propertyFileName, ex);
            }
            String vestibuleJNDINamesString = properties.getProperty("vestibuleJNDINames");
            if (vestibuleJNDINamesString == null) {
                vestibuleJNDINames = new ArrayList<String>();
            } else {
                vestibuleJNDINames = Arrays.asList(vestibuleJNDINamesString.split(","));
            }
        }
    }

}
