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
 * @version $Id: RuleQueueKeyBean.java 1816 2011-07-23 05:07:32Z joe.isaac $
 */
package org.tolven.queue.bean;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.tolven.core.AccountDAOLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.doc.RuleQueueLocal;
import org.tolven.queue.RuleQueueKeyLocal;
import org.tolven.security.key.AccountProcessingPrivateKey;
import org.tolven.security.key.AccountProcessingPublicKey;
import org.tolven.security.key.AccountSecretKey;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.session.TolvenSessionWrapperFactory;

@Stateless
@Local(RuleQueueKeyLocal.class)
public class RuleQueueKeyBean implements RuleQueueKeyLocal {

    @EJB
    private AccountDAOLocal accountDAOLocal;

    private Map<String, Cipher> cipherMap;

    @EJB
    private TolvenPropertiesLocal propertyBean;

    @EJB
    private RuleQueueLocal ruleQueueBean;

    public RuleQueueKeyBean() {
    }

    /**
     * Only a ruleQueue user can successfully decrypt ruleQueue messages
     */
    @Override
    public byte[] decryptMessage(byte[] message, byte[] messageSecretKey, String messageSecretKeyAlgorithm, long accountId) {
        Account account = accountDAOLocal.findAccount(accountId);
        if (account == null) {
            throw new RuntimeException("Could not find account: " + accountId);
        }
        AccountProcessingPrivateKey encryptedRuleQueuePrivateKey = getAccountProcessingPrivateKey(account);
        String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        PrivateKey ruleQueueOwnerPrivateKey = TolvenSessionWrapperFactory.getInstance().getUserPrivateKey(keyAlgorithm);
        PrivateKey ruleQueuePrivateKey = encryptedRuleQueuePrivateKey.getPrivateKey(ruleQueueOwnerPrivateKey);
        DocumentSecretKey documentSecretKey = DocumentSecretKey.getInstance();
        SecretKey docSecretKey = null;
        try {
            documentSecretKey.init(messageSecretKey, messageSecretKeyAlgorithm);
            docSecretKey = documentSecretKey.getSecretKey(ruleQueuePrivateKey);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get secret key to decrypt message for account: " + accountId, ex);
        }
        Cipher cipher = null;
        try {
            cipher = getCipher(messageSecretKeyAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, docSecretKey);
            byte[] decryptedMessage = cipher.doFinal(message);
            return decryptedMessage;
        } catch (Exception ex) {
            throw new RuntimeException("Could not decrypt message for account: " + accountId, ex);
        }
    }

    @Override
    public byte[] encryptMessage(byte[] message, SecretKey secretKey) {
        if (message == null) {
            throw new RuntimeException("Message to encrypt is null");
        }
        try {
            Cipher cipher = getCipher(secretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(message);
        } catch (Exception ex) {
            throw new RuntimeException("Could not encrypt mssage with SecretKey", ex);
        }
    }

    @Override
    public byte[] encryptMessageSecretKey(SecretKey secretKey, long accountId) {
        PublicKey ruleQueuePublicKey = getRuleQueuePublicKey(accountId);
        try {
            Cipher cipher = getCipher(ruleQueuePublicKey.getAlgorithm());
            cipher.init(Cipher.WRAP_MODE, ruleQueuePublicKey);
            return cipher.wrap(secretKey);
        } catch (Exception ex) {
            throw new RuntimeException("Could not encrypt SecretKey with ruleQueue PublicKey for account: " + accountId, ex);
        }
    }

    @Override
    public SecretKey generateMessageSecretKey(long accountId) {
        //Currently message SecretKeys are of the same type for all Accounts, so accountId not used
        String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(kbeKeyAlgorithm);
        } catch (Exception ex) {
            throw new RuntimeException("Could not generate SecretKey for account: " + accountId, ex);
        }
        keyGenerator.init(kbeKeyLength);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey;
    }

    //Get AccountProcessingPrivateKey, and generate keys for account if they do not exist, until a workflow is created for this
    private AccountProcessingPrivateKey getAccountProcessingPrivateKey(Account account) {
        AccountProcessingPrivateKey accountProcessingPrivateKey = account.getAccountProcessingPrivateKey();
        if (accountProcessingPrivateKey == null) {
            setupRuleQueueKeys(account);
            accountProcessingPrivateKey = account.getAccountProcessingPrivateKey();
        }
        return accountProcessingPrivateKey;
    }

    //Get AccountProcessingPublicKey, and generate keys for account if they do not exist, until a workflow is created for this
    private AccountProcessingPublicKey getAccountProcessingPublicKey(Account account) {
        AccountProcessingPublicKey accountProcessingPublicKey = account.getAccountProcessingPublicKey();
        if (accountProcessingPublicKey == null) {
            setupRuleQueueKeys(account);
            accountProcessingPublicKey = account.getAccountProcessingPublicKey();
        }
        return accountProcessingPublicKey;
    }

    private Cipher getCipher(String algorithm) {
        Cipher cipher = getCipherMap().get(algorithm);
        if (cipher == null) {
            try {
                cipher = Cipher.getInstance(algorithm);
            } catch (Exception ex) {
                throw new RuntimeException("Could not create Cipher with algorithm: " + algorithm, ex);
            }
            getCipherMap().put(algorithm, cipher);
        }
        return cipher;
    }

    private Map<String, Cipher> getCipherMap() {
        if (cipherMap == null) {
            cipherMap = new HashMap<String, Cipher>();
        }
        return cipherMap;
    }

    private PublicKey getRuleQueuePublicKey(long accountId) {
        Account account = accountDAOLocal.findAccount(accountId);
        if (account == null) {
            throw new RuntimeException("Could not find account: " + accountId);
        }
        return getAccountProcessingPublicKey(account).getPublicKey();
    }

    private void setupRuleQueueKeys(Account account) {
        try {
            X509Certificate ruleQueueOwnerX509Certificate = ruleQueueBean.getQueueOwnerX509Certificate();
            String privateKeyAlgorithm = propertyBean.getProperty(AccountProcessingPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
            int privateKeyLength = Integer.parseInt(propertyBean.getProperty(AccountProcessingPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
            String kbeKeyAlgorithm = propertyBean.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
            int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
            AccountProcessingPrivateKey accountProcessingPrivateKey = AccountProcessingPrivateKey.getInstance();
            account.setAccountProcessingPrivateKey(accountProcessingPrivateKey);
            PublicKey ruleQueuePublicKey = accountProcessingPrivateKey.init(ruleQueueOwnerX509Certificate, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
            account.setAccountProcessingPublicKey(ruleQueuePublicKey);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize the account processing key", ex);
        }
    }

}
