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

import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class MessagingLoginModule implements LoginModule {

    private CallbackHandler callbackHandler;
    private String principalName;
    private char[] password;
    private Map sharedState;
    private List<String> mdbPrincipalNames;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
    }

    @Override
    public boolean login() throws LoginException {
        principalName = (String) sharedState.get("javax.security.auth.login.name");
        Object obj = sharedState.get("javax.security.auth.login.password");
        if (obj instanceof char[]) {
            password = (char[]) obj;
        } else if (obj instanceof String) {
            password = ((String) obj).toCharArray();
        }
        try {
            if (principalName == null || password == null) {
                if (callbackHandler == null) {
                    throw new LoginException("No CallbackHandler");
                }
                NameCallback nc = new NameCallback("User name: ");
                PasswordCallback pc = new PasswordCallback("Password Alias: ", false);
                Callback[] callback = { nc, pc };
                callbackHandler.handle(callback);
                principalName = nc.getName();
                password = pc.getPassword();
            }
            sharedState.put("javax.security.auth.login.name", principalName);
            sharedState.put("javax.security.auth.login.password", password);
        } catch (Exception ex) {
            ex.printStackTrace();
            LoginException le = new LoginException("Authentication failed for '" + principalName + "': " + ex.getMessage());
            throw le;
        }
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        removeAllCredentials();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        removeAllCredentials();
        return true;
    }

    private void removeAllCredentials() {
        principalName = null;
        password = null;
        sharedState = null;
    }
}