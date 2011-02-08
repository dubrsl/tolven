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
 * @author <your name>
 * @version $Id: KeyUtility.java,v 1.3.2.2 2010/08/17 08:41:18 joseph_isaac Exp $
 */

package org.tolven.core;

import java.security.PrivateKey;

import org.tolven.core.entity.AccountUser;
import org.tolven.security.key.AccountPrivateKey;

/**
 * Services used to manage private keys (part of a public-private key pair)
 */
public class KeyUtility {

    /**
     * Get the decrypted AccountPrivateKey - this also acquires the user's private key, which may be null if 
     * the AccountPrivateKey is not encrypted
     * 
     * @param accountUser
     * @return
     */

    public static PrivateKey getAccountPrivateKey(AccountUser accountUser, PrivateKey userPrivateKey) {
        if (accountUser == null) {
            throw new IllegalStateException("AccountUser must be specified in order to acquire AccountPrivateKey");
        }
        AccountPrivateKey accountPrivateKey = accountUser.getAccountPrivateKey();
        PrivateKey decryptedAccountPrivateKey;
        try {
            decryptedAccountPrivateKey = accountPrivateKey.getPrivateKey(userPrivateKey);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt Account Private Key for account=" + accountUser.getAccount().getId() + " User:" + accountUser.getUser().getLdapUID(), e);
        }
        return decryptedAccountPrivateKey;
    }

}
