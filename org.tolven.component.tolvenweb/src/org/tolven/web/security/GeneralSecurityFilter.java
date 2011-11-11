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

/**
 * Constants used by security filters
 * 
 * @author Joseph Isaac
 */
public class GeneralSecurityFilter {

    public static final String ACCOUNT_ID = "accountId";
    public static final String ACCOUNTUSER = "accountUser";
    public static final String ACCOUNTUSER_ID = "accountUserId";
    public static final String INVITATION_ID = "invitationId";
    public static final String PROPOSED_ACCOUNTUSER_ID = "proposedAccountUserId";
    public static final String REMEMBER_DEFAULT_ACCOUNT = "rememberDefaultAccount"; // true | false
    public static final String TIME_NOW = "tolvenNow";
    public static final String TOLVEN_REDIRECT = "tolvenRedirect";
    public static final String TOLVEN_RESOURCEBUNDLE = "tolvenResourceBundle";
    public static final String TOLVENUSER = "tolvenUser";
    public static final String TOLVENUSER_ID = "TolvenUserId";
    public static final String USER_CONTEXT = "userContext";
    public static final String USER_KEYS_OPTIONAL = "tolven.security.user.keysOptional"; // true | false
    
}