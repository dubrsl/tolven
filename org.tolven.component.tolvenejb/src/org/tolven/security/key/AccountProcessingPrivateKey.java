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

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * This class encapsulates a key-based encrypted PrivateKey, which has been
 * encrypted with a randomly generated SecretKey, which is itself then encrypted
 * with a PublicKey. The encrypted PrivateKey is encapsulated as an
 * EncryptedPrivateKeyInfo and the SecretKey is stored in a
 * TolvenEncryptedSecretKey.
 * 
 * @author Joseph Isaac
 * 
 */
@Embeddable
public class AccountProcessingPrivateKey extends TolvenEncryptedPrivateKey implements Serializable {

    private static final long serialVersionUID = 2L;
    private static final String NOT_INITIALIZED = "AccountProcessingPrivateKey not initialized";
    private static final String INITIALIZED = "AccountProcessingPrivateKey already initialized";
    public static final String ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP = "tolven.security.account.privateKeyAlgorithm";
    public static final String ACCOUNT_PRIVATE_KEY_LENGTH_PROP = "tolven.security.account.keyLength";

    @Embedded
    private AccountSecretKey accountSecretKey;

    protected AccountProcessingPrivateKey() {
    }

    /**
     * Return an instance of AccountProcessingPrivateKey
     * 
     * @return
     */
    public static AccountProcessingPrivateKey getInstance() {
        return new AccountProcessingPrivateKey();
    }

    /**
     * Create a PrivateKey, encrypt it with a randomly generated SecretKey and
     * encrypt the SecretKey with a PublicKey. Use the system-specific
     * privateKeyAlgorithm and kbeKeyAlgorithm
     * 
     * @param anEncryptionKey
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public PublicKey init(X509Certificate anEncryptionCertificate, String privateKeyAlgorithm, int privateKeyLength, String kbeKeyAlgorithm, int kbeKeyLength) throws GeneralSecurityException, IOException {
        return init(privateKeyAlgorithm, privateKeyLength, anEncryptionCertificate, kbeKeyAlgorithm, kbeKeyLength);
    }

    /**
     * Create a PrivateKey, encrypt it with a randomly generated SecretKey and
     * encrypt the SecretKey with a PublicKey
     * 
     * @param aPrivateKeyAlgorithm
     * @param secretKeyAlgorithm
     * @param anEncryptionKey
     * @return
     * @throws GeneralSecurityException
     */
    private PublicKey init(String aPrivateKeyAlgorithm, int privateKeyLength, X509Certificate anEncryptionCertificate, String kbeKeyAlgorithm, int kbeKeyLength) throws GeneralSecurityException, IOException {
        if (accountSecretKey != null) {
            throw new IllegalStateException(INITIALIZED);
        }
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(aPrivateKeyAlgorithm);
        keyPairGenerator.initialize(privateKeyLength);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        accountSecretKey = AccountSecretKey.getInstance();
        SecretKey secretKey = accountSecretKey.init(anEncryptionCertificate.getPublicKey(), kbeKeyAlgorithm, kbeKeyLength);
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedPrivateKey = cipher.doFinal(keyPair.getPrivate().getEncoded());
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(aPrivateKeyAlgorithm, encryptedPrivateKey);
        setEncodedEncryptedPrivateKeyInfo(encryptedPrivateKeyInfo, privateKeyLength);
        setEncodedEncryptionCertificate(anEncryptionCertificate.getEncoded());
        return keyPair.getPublic();
    }

    /**
     * Create a PrivateKey, encrypt it with a randomly generated SecretKey and
     * encrypt the SecretKey with a PublicKey. Use the system-specific
     * privateKeyAlgorithm and kbeKeyAlgorithm
     * 
     * @param anEncryptionKey
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @Deprecated
    public PublicKey init(PublicKey anEncryptionKey, String privateKeyAlgorithm, int privateKeyLength, String kbeKeyAlgorithm, int kbeKeyLength) throws GeneralSecurityException, IOException {
        return init(privateKeyAlgorithm, privateKeyLength, anEncryptionKey, kbeKeyAlgorithm, kbeKeyLength);
    }

    /**
     * Create a PrivateKey, encrypt it with a randomly generated SecretKey and
     * encrypt the SecretKey with a PublicKey
     * 
     * @param aPrivateKeyAlgorithm
     * @param secretKeyAlgorithm
     * @param anEncryptionKey
     * @return
     * @throws GeneralSecurityException
     */
    @Deprecated
    private PublicKey init(String aPrivateKeyAlgorithm, int privateKeyLength, PublicKey anEncryptionKey, String kbeKeyAlgorithm, int kbeKeyLength) throws GeneralSecurityException, IOException {
        if (accountSecretKey != null) {
            throw new IllegalStateException(INITIALIZED);
        }
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(aPrivateKeyAlgorithm);
        keyPairGenerator.initialize(privateKeyLength);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        accountSecretKey = AccountSecretKey.getInstance();
        SecretKey secretKey = accountSecretKey.init(anEncryptionKey, kbeKeyAlgorithm, kbeKeyLength);
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedPrivateKey = cipher.doFinal(keyPair.getPrivate().getEncoded());
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(aPrivateKeyAlgorithm, encryptedPrivateKey);
        setEncodedEncryptedPrivateKeyInfo(encryptedPrivateKeyInfo, privateKeyLength);
        return keyPair.getPublic();
    }

    /**
     * Decrypt and return the PrivateKey using aDecryptionKey
     * 
     * @param aDecryptionKey
     * @return
     * @throws GeneralSecurityException
     */
    public PrivateKey getPrivateKey(PrivateKey aDecryptionKey) {
        if (accountSecretKey == null) {
            throw new IllegalStateException(NOT_INITIALIZED);
        }
        SecretKey secretKey = null;
        try {
            secretKey = accountSecretKey.getSecretKey(aDecryptionKey);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get SecretKey using a decryption PrivateKey", ex);
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception ex) {
            throw new RuntimeException("Could not intialize Cipher for decryption with SecretKey", ex);
        }
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = null;
        try {
            encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(getEncodedEncryptedPrivateKeyInfo());
        } catch (Exception ex) {
            throw new RuntimeException("Could not construct EncryptedPrivateKeyInfo", ex);
        }
        byte[] decryptedPrivateKey = null;
        try {
            decryptedPrivateKey = cipher.doFinal(encryptedPrivateKeyInfo.getEncryptedData());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedPrivateKey);
            return KeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName()).generatePrivate(keySpec);
        } catch (Exception ex) {
            throw new RuntimeException("Could not decrypt PrivateKey", ex);
        }
    }

}
