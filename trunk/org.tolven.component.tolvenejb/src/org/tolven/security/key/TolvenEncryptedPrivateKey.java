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

import javax.crypto.EncryptedPrivateKeyInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.persistence.*;

/**
 * An abstract class which encapsulates an EncryptedPrivateKeyInfo.
 * 
 * @author Joseph Isaac
 * 
 */
@MappedSuperclass
public abstract class TolvenEncryptedPrivateKey implements Serializable {

    @Lob
    @Basic
    @Column
    private byte[] encodedEncryptedPrivateKeyInfo;

    @Lob
    @Basic
    @Column
    private byte[] encodedEncryptionCertificate;

    @Lob
    @Basic
    @Column
    private byte[] encodedUnencryptedPrivateKey;

    @Column
    private int keySize;

    @Column
    private String algorithm;

    @Transient
    private CertificateFactory certificateFactory;

    /**
     * Return the EncryptedPrivateKeyInfo
     * 
     * @return
     */
    public byte[] getEncodedEncryptedPrivateKeyInfo() {
        return encodedEncryptedPrivateKeyInfo;
    }

    public void setEncodedEncryptedPrivateKeyInfo(byte[] encodedEncryptedPrivateKeyInfo) {
        this.encodedEncryptedPrivateKeyInfo = encodedEncryptedPrivateKeyInfo;
    }

    protected byte[] getEncodedUnencryptedPrivateKey() {
        return encodedUnencryptedPrivateKey;
    }

    protected void setEncodedUnencryptedPrivateKey(byte[] encodedUnencryptedPrivateKey) {
        this.encodedUnencryptedPrivateKey = encodedUnencryptedPrivateKey;
    }

    public byte[] getEncodedEncryptionCertificate() {
        return encodedEncryptionCertificate;
    }

    public void setEncodedEncryptionCertificate(byte[] encodedEncryptionCertificate) {
        this.encodedEncryptionCertificate = encodedEncryptionCertificate;
    }

    private CertificateFactory getCertificateFactory() {
        if (certificateFactory == null) {
            try {
                certificateFactory = CertificateFactory.getInstance("X.509");
            } catch (CertificateException ex) {
                throw new RuntimeException("Could not get an X.509 CertificateFactory", ex);
            }
        }
        return certificateFactory;
    }

    /**
     * Return the X509Certificate that was used to encrypt the PrivateKey. Creators of PrivateKeys do not required to provide one
     * although it may be usefule in determining which PublicKey among many was used to encrypt this key
     * 
     * @return
     */
    public X509Certificate getEncryptionX509Certificate() {
        if (getEncodedEncryptionCertificate() == null) {
            return null;
        } else {
            ByteArrayInputStream bais = new ByteArrayInputStream(getEncodedEncryptionCertificate());
            try {
                X509Certificate x509Certificate = (X509Certificate) getCertificateFactory().generateCertificate(bais);
                return x509Certificate;
            } catch (CertificateException ex) {
                throw new RuntimeException("Could not generate X509Certificate", ex);
            }
        }
    }

    /**
     * Set the EncryptedPrivateKeyInfo
     * 
     * @param privateKeyAlgorithm
     * @param anEncryptedPrivateKeyInfo
     */
    protected void setEncodedEncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo anEncryptedPrivateKeyInfo, int aKeySize) throws IOException {
        setAlgorithm(anEncryptedPrivateKeyInfo.getAlgName());
        setKeySize(aKeySize);
        setEncodedEncryptedPrivateKeyInfo(anEncryptedPrivateKeyInfo.getEncoded());
    }

    /**
     * return the PrivateKey size
     * 
     * @return
     */
    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    /**
     * return the PrivateKey algorithm
     * 
     * @return
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Keep the PrivateKey algorithm. It it is part of an EncryptedPrivateKeyInfo but one has to decrypt to find out what it is
     * 
     * @param aString
     */
    public void setAlgorithm(String aString) {
        algorithm = aString;
    }

}
