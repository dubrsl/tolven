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
 * @version $Id: SessionResourceBundleFactory.java 1009 2011-05-18 22:37:37Z joe.isaac $
 */
package org.tolven.locale;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.tolven.core.entity.AccountUser;

/**
 * WebResourceBundle to encapsulate all Tolven messages
 * @author Joseph Isaac
 *
 */
public class SessionResourceBundleFactory {

    public static TolvenResourceBundle getBundle() {
        List<TolvenResourceBundle> parentBundles = new ArrayList<TolvenResourceBundle>();
        Locale locale = getLocale();
        HttpServletRequest request = getRequest();
        if (request != null) {
            AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
            if (accountUser != null) {
                String accountType = accountUser.getAccount().getAccountType().getKnownType();
                TolvenResourceBundle appBundle = new TolvenResourceBundle(ResourceBundleHelper.getAppBundleName(accountType), locale);
                if (appBundle != null) {
                    parentBundles.add(appBundle);
                }
                TolvenResourceBundle appGlobalBundle = new TolvenResourceBundle(ResourceBundleHelper.getAppGlobalBundleName(accountType), locale);
                if (appGlobalBundle != null) {
                    if (appGlobalBundle != null) {
                        parentBundles.add(appGlobalBundle);
                    }
                }
            }
        }
        TolvenResourceBundle globalBundle = new TolvenResourceBundle(ResourceBundleHelper.GLOBALBUNDLENAME, locale);
        if (globalBundle != null) {
            if (globalBundle != null) {
                parentBundles.add(globalBundle);
            }
        }
        TolvenResourceBundle bundle = new TolvenResourceBundle(locale, parentBundles);
        return bundle;
    }

    private static HttpServletRequest getRequest() {
        try {
            return (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
        } catch (PolicyContextException ex) {
            throw new RuntimeException("Could not obtain javax.servlet.http.HttpServletRequest from PolicyContext", ex);
        }
    }

    private static Locale getLocale() {
        String accountUserLocale = null;
        String accountLocale = null;
        HttpServletRequest request = getRequest();
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                accountUserLocale = (String) session.getAttribute(ResourceBundleHelper.USER_LOCALE);
                if (accountUserLocale != null && accountUserLocale.trim().length() == 0) {
                    accountUserLocale = null;
                }
                accountLocale = (String) session.getAttribute(ResourceBundleHelper.ACCOUNT_LOCALE);
                if (accountLocale != null && accountLocale.trim().length() == 0) {
                    accountLocale = null;
                }
            }
        }
        return ResourceBundleHelper.getLocale(accountUserLocale, accountLocale);
    }

}
