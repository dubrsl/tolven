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
package org.tolven.connectors.mqkeystore;

import java.io.Serializable;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

/**
 * This class provides a resource adaptor for PrivateKeys required for queue processing.
 * 
 * @author Joseph Isaac
 *
 */
public class MQKeyStoreResourceAdapterImpl implements ResourceAdapter, Serializable {

    private String keyStoreURL;
    private String keyStoreType;
    private String defaultAlias;

    private transient String keyStorePassword;
    private transient KeyStore keyStore;

    public MQKeyStoreResourceAdapterImpl() {
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

    public String getDefaultAlias() {
        return defaultAlias;
    }

    public void setDefaultAlias(String defaultAlias) {
        this.defaultAlias = defaultAlias;
    }

    private KeyStore getKeyStore() {
        return keyStore;
    }

    private void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    /**
     * Returns the default PrivateKey, which is equivalent to getPrivateKey(null)
     * 
     * @return
     */
    public PrivateKey getDefaultPrivateKey() {
        return getPrivateKey(null);
    }

    /**
     * Returns a PrivateKey for a certificate
     * 
     * @param alias
     * @return
     */
    public PrivateKey getPrivateKey(X509Certificate certificate) {
        try {
            String alias = null;
            if (certificate == null) {
                alias = getDefaultAlias();
            } else {
                alias = getKeyStore().getCertificateAlias(certificate);
            }
            PrivateKey privateKey = (PrivateKey) getKeyStore().getKey(alias, getKeyStorePassword().toCharArray());
            if (privateKey == null) {
                throw new RuntimeException("Could not locate PrivateKey for certificate: " + certificate);
            }
            return privateKey;
        } catch (Exception ex) {
            throw new RuntimeException("Could not retrieve PrivateKey for certificate: " + certificate, ex);
        }
    }

    /**
     * Returns default PublicKey, which is equivalent to getPublicKey(null)
     * 
     * @return
     */
    public PublicKey getDefaultPublicKey() {
        return getPublicKey(null);
    }

    /**
     * Returns a PublicKey for a certificate, after checking it validity as a queue processing certificate
     * 
     * @param alias
     * @return
     */
    public PublicKey getPublicKey(X509Certificate certificate) {
        try {
            String alias = null;
            if (certificate == null) {
                alias = getDefaultAlias();
            } else {
                alias = getKeyStore().getCertificateAlias(certificate);
            }
            if (alias == null) {
                throw new RuntimeException("Could not locate PublicKey for certificate: " + certificate);
            }
            certificate = (X509Certificate) getKeyStore().getCertificate(alias);
            if (certificate == null) {
                throw new RuntimeException("Could not locate certificate with alias: " + alias);
            }
            return (PublicKey) certificate.getPublicKey();
        } catch (Exception ex) {
            throw new RuntimeException("Could not retrieve PrivateKey for certificate: " + certificate, ex);
        }
    }

    /**
     * Returns the default X509 Certificate, which is associated with the getDefaultPublicKey(), where the latter is equivalent to getPublicKey(null)
     * 
     * @return
     */
    public X509Certificate getDefaultX509Certificate() {
        try {
            String alias = getDefaultAlias();
            if (alias == null) {
                throw new RuntimeException("There is no defaultAlias defined");
            }
            X509Certificate certificate = (X509Certificate) getKeyStore().getCertificate(alias);
            if (certificate == null) {
                throw new RuntimeException("Could not locate certificate with alias: " + alias);
            }
            return (X509Certificate) certificate;
        } catch (Exception ex) {
            throw new RuntimeException("Could not retrieve default X509Certificate", ex);
        }
    }

    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        try {
            if (getKeyStorePassword() == null) {
                throw new RuntimeException("A keyStorePassword must be supplied");
            }
            if (getKeyStoreURL() == null) {
                throw new RuntimeException("keyStoreURL must be supplied");
            }
            URL keyStoreURL = new URL(getKeyStoreURL());
            setKeyStore(KeyStore.getInstance(getKeyStoreType()));
            getKeyStore().load(keyStoreURL.openStream(), getKeyStorePassword().toCharArray());
        } catch (Exception ex) {
            throw new RuntimeException("Could not load MQKeyStore", ex);
        }
    }

    public void stop() {
        setKeyStore(null);
        setKeyStorePassword(null);
        setKeyStoreURL(null);
        setKeyStoreType(null);
        setDefaultAlias(null);
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
