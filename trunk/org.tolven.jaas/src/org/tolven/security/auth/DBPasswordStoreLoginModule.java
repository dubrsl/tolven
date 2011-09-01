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
package org.tolven.security.auth;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * This PasswordStoreLoginModule allows the application itself to protect passwords it requires for access e.g. database, ldap etc.
 * 
 * @author Joseph Isaac
 *
<pre>
 &lt;login-module code="org.tolven.security.auth.PasswordStoreLoginModule"
 flag="required">
 &lt;module-option name = "username">username&lt;/module-option>
 &lt;module-option name = "alias">passwordStoreAlias&lt;/module-option>
 &lt;/login-module>
 </pre>
 */
public class DBPasswordStoreLoginModule implements LoginModule {

    private String principalName;
    private char[] password;
    private Map sharedState;
    private Map options;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.sharedState = sharedState;
        this.options = options;
    }

    public boolean login() throws LoginException {
        String passwordStoreAlias = null;
        try {
            principalName = (String) options.get("username");
            if (principalName == null) {
                throw new LoginException("module option username cannot be null");
            }
            passwordStoreAlias = (String) options.get("passwordStoreAlias");
            if (passwordStoreAlias == null) {
                throw new LoginException("module option alias cannot be null");
            }
            password = PasswordStoreHolder.getInstance().getPassword(passwordStoreAlias);
            sharedState.put("javax.security.auth.login.name", principalName);
            sharedState.put("javax.security.auth.login.password", password);
        } catch (Exception ex) {
            ex.printStackTrace();
            LoginException le = new LoginException("Failed to obtain passwordStoreAlias '" + passwordStoreAlias + "': " + ex.getMessage());
            throw le;
        }
        return true;
    }

    public boolean commit() throws LoginException {
        return true;
    }

    public boolean abort() throws LoginException {
        removeAllCredentials();
        return true;
    }

    public boolean logout() throws LoginException {
        removeAllCredentials();
        return true;
    }

    private void removeAllCredentials() {
        principalName = null;
        password = null;
        sharedState = null;
        options = null;
    }

}
