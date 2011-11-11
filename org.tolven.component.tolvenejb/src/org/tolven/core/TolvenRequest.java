/*
 *  Copyright (C) 2011 Tolven Inc 
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
package org.tolven.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.naming.TolvenPerson;

/**
 * This class holds request information regardless of the source of the request (HTTP, JMS, etc).
 * The information originates from a combination of TolvenUser, AccountUser, and system properties.
 * These settings are determined at the beginning of the request and are never modified during the request.
 * For example, if the user changes their timezone in a preferences page, the next request will carry the new
 * timezone. 
 * The algorithm for determining the timezone returned is applied at the time that this object is created.
 * Remember that this object lifetime exists only for a particular request. 
 * @author John Churin
 *
 */
public class TolvenRequest {

    private static final ThreadLocal<TolvenRequest> instance = new ThreadLocal<TolvenRequest>();

    /**
     * Return an instance of the Preferences object
     * @return The Preferences object
     */
    public static TolvenRequest getInstance() {
        if (instance.get() == null) {
            instance.set(new TolvenRequest());
        }
        return instance.get();
    }

    private AccountUser accountUser;
    private String brand;
    private Locale locale;
    private GregorianCalendar now;
    private String timeZone;
    private TolvenUser tolvenUser;
    private TolvenPerson tp;

    // Construct a new preferences instance
    private TolvenRequest() {
        this.now = new GregorianCalendar();
    }

    public void clear() {
        if (instance != null) {
            instance.set(null);
        }
    }

    public Account getAccount() {
        if (getAccountUser() == null) {
            return null;
        } else {
            return getAccountUser().getAccount();
        }
    }

    public AccountUser getAccountUser() {
        return accountUser;
    }

    public String getBrand() {
        return brand;
    }

    public Locale getLocale() {
        if (locale == null) {
            String userLocale = null;
            if (getTolvenUser() != null) {
                userLocale = getTolvenUser().getLocale();
            }
            String accountLocale = null;
            if (getAccountUser() != null) {
                accountLocale = getAccountUser().getLocale();
            }
            if (userLocale == null) {
                if (accountLocale == null) {
                    locale = Locale.getDefault();
                } else {
                    locale = ResourceBundleHelper.getLocale(accountLocale);
                }
            } else {
                locale = ResourceBundleHelper.getLocale(userLocale);
            }
        }
        return locale;
    }

    /**
     * Return the uniform timestamp used for this request (transaction).
     * 
     * @return Java date/time
     */
    public Date getNow() {
        return now.getTime();
    }

    /**
     * Get a property based on the current context. This will include
     * accountUser, account, and system properties.
     * Also, brand may also be included in the search.
     * @param name
     * @return The property or null if the property cannot be found
     */
    protected String getProperty(String name) {
        return null;
    }

    public TolvenResourceBundle getResourceBundle() {
        //Perhaps this should be cached? But the presence of the accountUser can vary within a request?
        List<TolvenResourceBundle> parentBundles = new ArrayList<TolvenResourceBundle>();
        Locale locale = getLocale();
        if (getAccountUser() != null) {
            String accountType = getAccountUser().getAccount().getAccountType().getKnownType();
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
        TolvenResourceBundle globalBundle = new TolvenResourceBundle(ResourceBundleHelper.GLOBALBUNDLENAME, locale);
        if (globalBundle != null) {
            if (globalBundle != null) {
                parentBundles.add(globalBundle);
            }
        }
        TolvenResourceBundle resourceBundle = new TolvenResourceBundle(locale, parentBundles);
        return resourceBundle;
    }

    public String getTimeZone() {
        if (timeZone == null) {
            if (getTolvenUser() != null) {
                timeZone = getTolvenUser().getTimeZone();
            }
            if (timeZone == null) {
                if (getAccount() != null) {
                    timeZone = getAccount().getTimeZone();
                }
            }
            if (timeZone == null) {
                timeZone = getProperty("tolven.timezone");
            }
            if (timeZone == null) {
                timeZone = java.util.TimeZone.getDefault().getID();
            }
            //			if (timeZone==null) timeZone = getTolvenPropertiesBean().getProperty("tolven.timezone");
        }
        return timeZone;
    }

    /**
     * Convenience method to get the timezone object based on the selected timezone
     * @return
     */
    public TimeZone getTimeZoneObject() {
        return TimeZone.getTimeZone(getTimeZone());
    }

    public TolvenPerson getTolvenPerson() {
        return tp;
    }

    public TolvenUser getTolvenUser() {
        return tolvenUser;
    }

    //	protected void setNow(Date now) {
    //		this.now = now;
    //	}

    public void initializeAccountUser(AccountUser accountUser) {
        // No penalty to initialize this to the same value a second time
        if (this.accountUser != accountUser) {
            if (this.accountUser != null) {
                throw new RuntimeException("Preferences for a thread can only be initialized once");
            }
            this.accountUser = accountUser;
        }
    }

    /**
     * Set the brand appropriate for this request
     * @param brand
     */
    public void initializeBrand(String brand) {
        // No penalty to initialize this to the same value a second time
        if (this.brand != brand) {
            if (this.brand != null) {
                throw new RuntimeException("Preferences for a thread can only be initialized once");
            }
            this.brand = brand;
        }
    }

    public void initializeTolvenPerson(TolvenPerson tp) {
        this.tp = tp;
    }

    public void initializeTolvenUser(TolvenUser tolvenUser) {
        // No penalty to initialize this to the same value a second time
        if (this.tolvenUser != tolvenUser) {
            if (this.tolvenUser != null) {
                throw new RuntimeException("Preferences for a thread can only be initialized once");
            }
            this.tolvenUser = tolvenUser;
        }
    }

    public boolean isInAccount() {
        return (getAccount() != null);
    }

    protected void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
