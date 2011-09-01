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
package org.tolven.connectors.passwordstore;

import java.security.KeyStore;
import java.util.Properties;

/**
 * 
 * @author Joseph Isaac
 *
 */
class PasswordStoreHolder {

    private transient PasswordStore passwordStore;
    private transient char[] keyStorePassword;

    PasswordStoreHolder(Properties encryptedPasswords, KeyStore keyStore, char[] keyStorePassword) {
        PasswordStore passwordStore = new PasswordStoreImpl(keyStore, keyStorePassword, encryptedPasswords);
        setKeyStorePassword(keyStorePassword);
        setPasswordStore(passwordStore);
    }

    private PasswordStore getPasswordStore() {
        return passwordStore;
    }

    private void setPasswordStore(PasswordStore passwordStore) {
        this.passwordStore = passwordStore;
    }

    /**
     * Return a copy of the password related to the alias. It is the responsibility of the caller to erase the password after use.
     * @return
     */
    public char[] getPassword(String alias) {
        return getCopy(getPasswordStore().getPassword(alias));
    }

    /**
     * Return a copy of the password related to the alias. It is the responsibility of the caller to erase the password after use.
     * @return
     */
    public char[] getPassword(char[] alias) {
        return getCopy(getPasswordStore().getPassword(alias));
    }

    public void setPassword(String alias, char[] password) {
        getPasswordStore().setPassword(alias, password);
    }

    /**
     * Return a copy of the KeyStore password. It is the responsibility of the caller to erase the password after use.
     * 
     * @return
     */

    public char[] getKeyStorePassword() {
        return getCopy(keyStorePassword);
    }

    private void setKeyStorePassword(char[] keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * Return a copy of a password, so that on finalize, the original can be erased.
     * 
     * @param aPassword
     * @return
     */
    private char[] getCopy(char[] aPassword) {
        if (aPassword == null) {
            return null;
        } else {
            char[] passwordCopy = new char[aPassword.length];
            for (int i = 0; i < aPassword.length; i++) {
                passwordCopy[i] = aPassword[i];
            }
            return passwordCopy;
        }
    }

}
