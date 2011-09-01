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
package org.tolven.api.accountuser;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;

import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountRole;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.AccountUserRole;
import org.tolven.core.entity.TolvenUser;

/**
 * A class to assist with populating an AccountUser object
 * 
 * @author Joseph Isaac
 *
 */

public class XAccountUserFactory {

    private static ObjectFactory objectFactory;

    private static ObjectFactory getObjectFactory() {
        if (objectFactory == null) {
            objectFactory = new ObjectFactory();
        }
        return objectFactory;
    }

    public static XAccountUser createXAccountUser(TolvenUser user, Date now) {
        XAccountUser xAccountUser = getObjectFactory().createXAccountUser();
        xAccountUser.setTimestamp(now);
        xAccountUser.setUid(user.getLdapUID());
        xAccountUser.setUser(createXTolvenUser(user));
        xAccountUser.setTimeZone(getTimeZone(user, null));
        Properties properties = new Properties();
        Properties accountUserProperties = getAccountBean().resolveTolvenUserProperties(user);
        for (Object obj : accountUserProperties.keySet()) {
            String name = (String) obj;
            String value = accountUserProperties.getProperty(name);
            properties.setProperty(name, value);
        }
        for (Object obj : properties.keySet()) {
            String name = (String) obj;
            String value = properties.getProperty(name);
            XProperty xProperty = getObjectFactory().createXProperty();
            xProperty.setName(name);
            xProperty.setValue(value);
            xAccountUser.getProperties().add(xProperty);
        }
        return xAccountUser;
    }

    public static XAccountUser createXAccountUser(AccountUser accountUser, Date now) {
        XAccountUser xAccountUser = getObjectFactory().createXAccountUser();
        xAccountUser.setAccount(createXAccount(accountUser.getAccount(), now));
        xAccountUser.setAccountPermission(accountUser.isAccountPermission());
        xAccountUser.setDefaultAccount(accountUser.isDefaultAccount());
        xAccountUser.setEffectiveTime(accountUser.getEffectiveDate());
        xAccountUser.setExpirationTime(accountUser.getExpirationDate());
        xAccountUser.setId(accountUser.getId());
        xAccountUser.setLastLoginTime(accountUser.getLastLoginTime());
        xAccountUser.setOpenMeFirst(accountUser.getOpenMeFirst());
        if (accountUser.getStatus() != null) {
            xAccountUser.setStatus(XStatus.fromValue(accountUser.getStatus()));
        }
        xAccountUser.setTimestamp(now);
        xAccountUser.setUid(accountUser.getUser().getLdapUID());
        xAccountUser.setUser(createXTolvenUser(accountUser.getUser()));
        xAccountUser.setTimeZone(getTimeZone(accountUser.getUser(), accountUser.getAccount()));
        for (AccountUserRole accountUserRole : accountUser.getRoles()) {
            XRole xRole = getObjectFactory().createXRole();
            populate(xRole, accountUserRole);
            xAccountUser.getRoles().add(xRole);
        }
        Properties properties = new Properties();
        Properties accountUserProperties = getAccountBean().resolveAccountUserProperties(accountUser);
        for (Object obj : accountUserProperties.keySet()) {
            String name = (String) obj;
            String value = accountUserProperties.getProperty(name);
            properties.setProperty(name, value);
        }
        for (Object obj : properties.keySet()) {
            String name = (String) obj;
            String value = properties.getProperty(name);
            XProperty xProperty = getObjectFactory().createXProperty();
            xProperty.setName(name);
            xProperty.setValue(value);
            xAccountUser.getProperties().add(xProperty);
        }
        return xAccountUser;
    }

    private static String getTimeZone(TolvenUser user, Account account) {
        String timeZone = user.getTimeZone();
        if (timeZone != null) {
            return timeZone;
        }
        if (account != null) {
            timeZone = account.getTimeZone();
            if (timeZone != null) {
                return timeZone;
            }
        }
        //TODO This needs to use the property bean
        timeZone = System.getProperty("tolven.timezone");
        if (timeZone != null) {
            return timeZone;
        }
        timeZone = java.util.TimeZone.getDefault().getID();
        return timeZone;
    }

    public static XAccount createXAccount(Account account, Date now) {
        XAccount xAccount = getObjectFactory().createXAccount();
        xAccount.setAccountType(createXAccountType(account.getAccountType(), now));
        if (account.isDisableAutoRefresh() != null) {
            xAccount.setDisableAutoRefresh(account.isDisableAutoRefresh());
        }
        if (account.getEmailFormat() != null) {
            xAccount.setEmailFormat(XEmailFormat.fromValue(account.getEmailFormat()));
        }
        if (account.getEnableBackButton() != null) {
            xAccount.setEnableBackButton("true".equalsIgnoreCase(account.getEnableBackButton()));
        }
        xAccount.setId(account.getId());
        xAccount.setLocale(account.getLocale());
        xAccount.setManualMetadataUpdate(account.getManualMetadataUpdate());
        xAccount.setTimestamp(now);
        xAccount.setTimeZone(account.getTimeZone());
        xAccount.setTitle(account.getTitle());
        for (AccountRole accountRole : account.getAccountRoles()) {
            XRole xRole = getObjectFactory().createXRole();
            populate(xRole, accountRole);
            xAccount.getRoles().add(xRole);
        }
        return xAccount;
    }

    public static XAccountType createXAccountType(AccountType accountType, Date now) {
        XAccountType xAccountType = getObjectFactory().createXAccountType();
        xAccountType.setCSS(accountType.getCSS());
        xAccountType.setDescription(accountType.getLongDesc());
        xAccountType.setId(accountType.getId());
        xAccountType.setKnownType(accountType.getKnownType());
        xAccountType.setLogo(accountType.getLogo());
        xAccountType.setName(accountType.getKnownType());
        xAccountType.setStatus(XStatus.fromValue(accountType.getStatus()));
        xAccountType.setTimestamp(now);
        xAccountType.setUserCanCreate(accountType.isCreatable());
        return xAccountType;
    }

    public static XTolvenUser createXTolvenUser(TolvenUser user) {
        XTolvenUser xTolvenUser = getObjectFactory().createXTolvenUser();
        xTolvenUser.setId(user.getId());
        xTolvenUser.setUid(user.getLdapUID());
        return xTolvenUser;
    }

    private static AccountDAOLocal getAccountBean() {
        try {
            InitialContext ctx = new InitialContext();
            return (AccountDAOLocal) ctx.lookup("java:global/tolven/tolvenEJB/AccountDAOBean!org.tolven.core.AccountDAOLocal");
        } catch (Exception ex) {
            throw new RuntimeException("Could not lookup java:global/tolven/tolvenEJB/AccountDAOBean!org.tolven.core.AccountDAOLocal", ex);
        }
    }

    private static void populate(XRole xRole, AccountUserRole accountUserRole) {
        xRole.setId(accountUserRole.getId());
        xRole.setValue(accountUserRole.getRole());
    }

    private static void populate(XRole xRole, AccountRole accountRole) {
        xRole.setId(accountRole.getId());
        xRole.setValue(accountRole.getRole());
    }

    public static Properties asProperties(List<XProperty> xPropertys) {
        Properties properties = new Properties();
        for (XProperty xProperty : xPropertys) {
            properties.setProperty(xProperty.getName(), xProperty.getValue());
        }
        return properties;
    }

}
