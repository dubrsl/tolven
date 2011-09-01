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
package org.tolven.security.key;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

/**
 * This class encapsulates an x509EncodedKeySpec for a Public Key
 * 
 * @author Joseph Isaac
 * 
 */
@MappedSuperclass
public abstract class TolvenPublicKey implements Serializable {

    protected static final String NOT_INITIALIZED = "TolvenPublicKey not initialized";
    protected static final String INITIALIZED = "TolvenPublicKey already initialized";

    @Lob
    @Basic
    @Column
    private byte[] x509EncodedKeySpec;

    @Column
    private String algorithm;

    private transient PublicKey publicKey;

    protected TolvenPublicKey() {
    }

    public byte[] getX509EncodedKeySpec() {
        return x509EncodedKeySpec;
    }

    public void setX509EncodedKeySpec(byte[] x509EncodedKeySpec) {
        this.x509EncodedKeySpec = x509EncodedKeySpec;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Initialize TolvenPublicKey with aPublicKey
     * @param aPublicKey
     */
    public void init(PublicKey aPublicKey) {
        if (x509EncodedKeySpec != null || algorithm != null)
            throw new IllegalStateException(INITIALIZED);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(aPublicKey.getEncoded());
        x509EncodedKeySpec = keySpec.getEncoded();
        algorithm = aPublicKey.getAlgorithm();
    }

    /**
     * Decode and return the encapsulated PublicKey
     * @return
     * @throws GeneralSecurityException
     */
    public PublicKey getPublicKey() {
        if (publicKey == null) {
            if (x509EncodedKeySpec == null || algorithm == null)
                throw new IllegalStateException(NOT_INITIALIZED);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(x509EncodedKeySpec);
            try {
                publicKey = KeyFactory.getInstance(algorithm).generatePublic(keySpec);
            } catch (Exception ex) {
                throw new RuntimeException("Could not PublicKey with algorithm: " + algorithm, ex);
            }
        }
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

}
