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
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.persistence.*;

/**
 * This class encapsulates a SecretKey which has been encrypted using a
 * PublicKey during initialization. To obtain the unencrypted SecretKey, the
 * PrivateKey companion of the encrypting PublicKey must be supplied.
 * 
 * @author Joseph Isaac
 * 
 */
@Embeddable
public class AccountSecretKey extends TolvenEncryptedSecretKey implements Serializable {

    public static final String ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP = "tolven.security.accountUser.kbeKeyAlgorithm";

    public static final String ACCOUNT_USER_KBE_KEY_LENGTH = "tolven.security.accountUser.kbeKeyLength";

    private static final long serialVersionUID = 2L;
    
    protected AccountSecretKey() {
    }

    /**
     * Return an instance of AccountSecretKey
     * 
     * @return
     */
    public static AccountSecretKey getInstance() {
        return new AccountSecretKey();
    }

    /**
     * Encrypt a SecretKey using a PublicKey
     * 
     * @param aPublicKey
     * @throws GeneralSecurityException
     */
    public SecretKey init(PublicKey aPublicKey, String kbeKeyAlgorithm, int kbeKeyLength) throws GeneralSecurityException {
        // The initialization check seems to belong in the superclass, but it's abstract?
        if (getEncryptedKey() != null || getAlgorithm() != null)
            throw new IllegalStateException(getClass() + " already initialized");
        KeyGenerator keyGenerator = KeyGenerator.getInstance(kbeKeyAlgorithm);
        keyGenerator.init(kbeKeyLength);
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance(aPublicKey.getAlgorithm());
        cipher.init(Cipher.WRAP_MODE, aPublicKey);
        setEncryptedKey(cipher.wrap(secretKey));
        setAlgorithm(secretKey.getAlgorithm());
        return secretKey;
    }

}
