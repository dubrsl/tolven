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

import java.nio.CharBuffer;
import java.util.Map;

import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.tolven.security.TolvenAlias;
import org.tolven.security.TolvenPrincipal;

/**
 * This PasswordStoreLoginModule allows the application itself to protect passwords it requires for access e.g. database, ldap etc, which
 * are located in an alias->encryptedPassword properties file. To access this login module requires code in the same JVM, which can supply
 * the keyStore password used during startup.
 * 
 * @author Joseph Isaac
 *
<pre>
 &lt;login-module code="org.tolven.security.auth.PasswordStoreLoginModule"
 flag="required">
 &lt;module-option name = "username">username&lt;/module-option>
 &lt;module-option name = "passwordStoreAlias">passwordStoreAlias&lt;/module-option>
 &lt;/login-module>
 </pre>
 */
public class PasswordStoreLoginModule implements LoginModule {

    private Subject subject;
    private String principalName;
    private CallbackHandler callbackHandler;
    private String passwordStoreAlias;
    private char[] password;
    private Map<String, ?> options;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        //TolvenLogger.info(getClass() + " initialize()", PasswordStoreLoginModule.class);
        setSubject(subject);
        setCallbackHandler(callbackHandler);
        setOptions(options);
    }

    private Subject getSubject() {
        return subject;
    }

    private void setSubject(Subject subject) {
        this.subject = subject;
    }

    private String getPrincipalName() {
        return principalName;
    }

    private void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    private CallbackHandler getCallbackHandler() {
        return callbackHandler;
    }

    private void setCallbackHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    private Map<String, ?> getOptions() {
        return options;
    }

    private void setOptions(Map<String, ?> options) {
        this.options = options;
    }

    private String getPasswordStoreAlias() {
        return passwordStoreAlias;
    }

    private void setPasswordStoreAlias(String passwordStoreAlias) {
        this.passwordStoreAlias = passwordStoreAlias;
    }

    private char[] getPassword() {
        return password;
    }

    private void setPassword(char[] password) {
        this.password = password;
    }

    public boolean login() throws LoginException {
        //TolvenLogger.info(getClass() + " begin login", PasswordStoreLoginModule.class);
        String passwordStoreAlias = null;
        try {
            /*
             * Ensure that the caller has access to the PasswordStore's keystore password
             */
            if (getCallbackHandler() == null) {
                throw new LoginException("No CallbackHandler");
            }
            PasswordCallback pc = new PasswordCallback("Password: ", false);
            Callback[] callback = { pc };
            getCallbackHandler().handle(callback);
            setPrincipalName((String) getOptions().get("username"));
            char[] callBackPassword = pc.getPassword();
            if (callBackPassword == null) {
                throw new LoginException("Callback password cannot be null");
            }
            if(!CharBuffer.wrap(PasswordStoreHolder.getInstance().getKeyStorePassword()).equals(CharBuffer.wrap(callBackPassword))) {
                throw new LoginException("PasswordStoreLogin Authorization Failed");
            }
            /*
             * Obtain the decrypted password related to the passwordStoreAlias from the PasswordStore
             */
            passwordStoreAlias = (String) getOptions().get("passwordStoreAlias");
            if (passwordStoreAlias == null)
                throw new LoginException("module option alias cannot be null");
            setPasswordStoreAlias(passwordStoreAlias);
            setPassword(PasswordStoreHolder.getInstance().getPassword(passwordStoreAlias));
        } catch (Exception ex) {
            ex.printStackTrace();
            LoginException le = new LoginException("Failed to obtain passwordStoreAlias '" + passwordStoreAlias + "': " + ex.getMessage());
            throw le;
        }
        return true;
    }

    public boolean commit() throws LoginException {
        /*
         * If a Principal is associated with this alias, then supply it
         */
        if(getPrincipalName() != null) {
            getSubject().getPrincipals().add(new TolvenPrincipal(getPrincipalName()));
        }
        /*
         * Provide the Subject with a TolvenAlias (passwordStoreAlias) public credential used by this LoginModule
         */
        getSubject().getPublicCredentials().add(new TolvenAlias(getPasswordStoreAlias()));
        /*
         * Provide the Subject with the password related to the passwordStoreAlias
         */
        PasswordCredential passwordCredential = new PasswordCredential(getPasswordStoreAlias(), getPassword());
        getSubject().getPrivateCredentials().add(passwordCredential);
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
        setPasswordStoreAlias(null);
        setPassword(null);
        setSubject(null);
        setCallbackHandler(null);
        setOptions(null);
    }
    
}
