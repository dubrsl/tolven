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
package org.tolven.security;

import java.io.Serializable;

import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.tolven.security.auth.PasswordStoreHolder;

/**
 * A Tolven specific principal
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenAlias implements Serializable {

    private static final long serialVersionUID = 2L;
    private String name;

    public TolvenAlias(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object anObject) {
        return anObject instanceof TolvenAlias && name.equals(((TolvenAlias) anObject).getName());
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return getName();
    }

    /**
     * Return the Subject when logging into the passwordStoreLoginModule. The supplied password in the callback is 
     * that of the PasswordStore's keystore.
     * 
     * @param passwordStoreLoginModule
     * @return
     * @throws LoginException
     */
    public static Subject getAliasSubject(String passwordStoreLoginModule) throws LoginException {
        LoginContext loginContext = null;
        try {
            loginContext = new LoginContext(passwordStoreLoginModule, new CallbackHandler() {
                public void handle(Callback[] callbacks) {
                    int len = callbacks.length;
                    Callback cb;
                    for (int i = 0; i < len; i++) {
                        cb = callbacks[i];
                        if (cb instanceof PasswordCallback) {
                            PasswordCallback pcb = (PasswordCallback) cb;
                            pcb.setPassword(PasswordStoreHolder.getInstance().getKeyStorePassword());
                        }
                    }
                }
            });
            loginContext.login();
            return loginContext.getSubject();
        } finally {
            if (loginContext != null)
                loginContext.logout();
        }
    }

    /**
     * Return the TolvenAlias name from the Subject
     * @param subject
     * @return
     */
    public static String getTolvenUserName(Subject subject) {
        for (TolvenPrincipal tolvenPrincipal : subject.getPrincipals(TolvenPrincipal.class)) {
            return tolvenPrincipal.getName();
        }
        return null;
    }

    /**
     * Return the TolvenAlias name from the Subject
     * @param subject
     * @return
     */
    public static String getTolvenAliasName(Subject subject) {
        for (TolvenAlias tolvenAlias : subject.getPublicCredentials(TolvenAlias.class)) {
            return tolvenAlias.getName();
        }
        return null;
    }

    /**
     * Return the TolvenAlias password from the Subject
     * @param subject
     * @return
     */
    public static char[] getTolvenAliasPassword(Subject subject) {
        for (PasswordCredential passwordCredentials : subject.getPrivateCredentials(PasswordCredential.class)) {
            return passwordCredentials.getPassword();
        }
        return null;
    }

}
