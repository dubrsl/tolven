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
 */
package org.tolven.shiro.authc;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UsernamePasswordRealmToken extends UsernamePasswordToken implements RealmAuthenticationToken {

    private String realm;

    public UsernamePasswordRealmToken(final String username, final char[] password, final String realm) {
        this(username, password, realm, false, null);
    }

    public UsernamePasswordRealmToken(final String username, char[] password, final String realm, final boolean rememberMe, final String host) {
        super(username, password, rememberMe, host);
        setRealm(realm);
    }

    public UsernamePasswordRealmToken(final String username, final String password, final String realm, final boolean rememberMe, final String host) {
        super(username, password, rememberMe, host);
        setRealm(realm);
    }

    @Override
    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

}
