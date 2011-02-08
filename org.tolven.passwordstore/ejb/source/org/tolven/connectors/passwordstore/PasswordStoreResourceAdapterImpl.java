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
package org.tolven.connectors.passwordstore;

import java.io.Serializable;
import java.net.URL;
import java.security.KeyStore;
import java.util.Properties;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

/**
 * This class provides a resource adaptor for credentials required by the application, which are not
 * entered by users at runtime.
 * 
 * @author Joseph Isaac
 *
 */
public class PasswordStoreResourceAdapterImpl implements ResourceAdapter, Serializable {

    private String keyStoreURL;
    private String keyStoreType;
    private String passwordStoreURL;

    private transient String keyStorePassword;
    private transient PasswordStoreHolder passwordStoreHolder;

    public PasswordStoreResourceAdapterImpl() {
    }

    public String getKeyStoreURL() {
        return keyStoreURL;
    }

    public void setKeyStoreURL(String keyStoreURL) {
        this.keyStoreURL = keyStoreURL;
    }

    public String getKeyStoreType() {
        if (keyStoreType == null) {
            keyStoreType = "JKS";
        }
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keytStorePassword) {
        this.keyStorePassword = keytStorePassword;
    }

    public String getPasswordStoreURL() {
        return passwordStoreURL;
    }

    public void setPasswordStoreURL(String passwordStoreURL) {
        this.passwordStoreURL = passwordStoreURL;
    }

    private PasswordStoreHolder getPasswordStoreHolder() {
        return passwordStoreHolder;
    }

    private void setPasswordStoreHolder(PasswordStoreHolder passwordStoreHolder) {
        this.passwordStoreHolder = passwordStoreHolder;
    }

    public char[] getPassword(String alias) {
        if (alias == null) {
            throw new RuntimeException("alias cannot be null");
        }
        return getPasswordStoreHolder().getPassword(alias);
    }

    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        if (getPasswordStoreURL() == null) {
            throw new RuntimeException("A passwordStoreURL must be supplied");
        }
        Properties encryptedPasswords = null;
        KeyStore keyStore = null;
        try {
            URL passwordStoreURL = new URL(getPasswordStoreURL());
            encryptedPasswords = new Properties();
            encryptedPasswords.load(passwordStoreURL.openStream());
            if (encryptedPasswords.isEmpty()) {
                throw new RuntimeException("No passwords found in PasswordStore: " + passwordStoreURL);
            }
            if (getKeyStoreURL() == null) {
                throw new RuntimeException("keyStoreURL must be supplied");
            }
            URL keyStoreURL = new URL(getKeyStoreURL());
            keyStore = KeyStore.getInstance(getKeyStoreType());
            keyStore.load(keyStoreURL.openStream(), getKeyStorePassword().toCharArray());
        } catch (Exception ex) {
            throw new RuntimeException("Could not intialize with Tolven password", ex);
        }
        if (getKeyStorePassword() == null) {
            throw new RuntimeException("A keyStorePassword must be supplied");
        }
        PasswordStoreHolder passwordStoreHolder = new PasswordStoreHolder(encryptedPasswords, keyStore, getKeyStorePassword().toCharArray());
        setPasswordStoreHolder(passwordStoreHolder);
    }

    public void stop() {
        setKeyStorePassword(null);
        setPasswordStoreHolder(null);
        setKeyStoreURL(null);
        setKeyStoreType(null);
        setPasswordStoreURL(null);
    }

    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) throws NotSupportedException {
        throw new UnsupportedOperationException("NOT SUPPORTED");
    }

    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
        throw new UnsupportedOperationException("NOT SUPPORTED");
    }

    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return new XAResource[0];
    }

}
