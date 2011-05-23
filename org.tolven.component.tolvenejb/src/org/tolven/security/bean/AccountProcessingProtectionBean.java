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
package org.tolven.security.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.tolven.connectors.mqkeystore.api.MQKeyStoreConnection;
import org.tolven.connectors.mqkeystore.api.MQKeyStoreConnectionFactory;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageAttachment;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.security.AccountProcessingProtectionLocal;
import org.tolven.security.key.AccountProcessingPrivateKey;
import org.tolven.security.key.AccountProcessingPublicKey;
import org.tolven.security.key.AccountSecretKey;
import org.tolven.security.key.DocumentSecretKey;

/**
 * This class protects documents while they are in the tolvenRule
 * 
 * @author Joseph Isaac
 * 
 */
@Stateless()
@Local(AccountProcessingProtectionLocal.class)
public class AccountProcessingProtectionBean implements AccountProcessingProtectionLocal {

    @EJB
    private AccountDAOLocal accountDAOLocal;

    @EJB
    private TolvenPropertiesLocal propertyBean;

    /*
     * JBoss-5.1.0.GA complains that mappedName must be used, when mappedName is not even portable, and according to spec does not have to be used.
     * Furthermore, it does not even accept the <mappedName> tag in ejb-jar.xml.
     * Used to work in JBoss-4.2.2.GA
     */
    @Resource(name = "mqKeyStore")
    private MQKeyStoreConnectionFactory mqKeyStoreCF;

    /**
     * Encrypt the payload(s) of a TolvenMessage for queuing. 
     * @param tm
     */
    public void encryptTolvenMessage(TolvenMessage tm) {
        Account account = accountDAOLocal.findAccount(tm.getAccountId());
        if (account == null) {
            throw new RuntimeException("Cannot find account Id=" + tm.getAccountId() + " for TolvenMessage");
        }
        SecretKey docSecretKey = null;
        AccountProcessingPublicKey accountProcessingPublicKey = getAccountProcessingPublicKey(account);
        DocumentSecretKey documentSecretKey = DocumentSecretKey.getInstance();
        String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        try {
            docSecretKey = documentSecretKey.init(accountProcessingPublicKey.getPublicKey(), kbeKeyAlgorithm, kbeKeyLength);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get secret key to encrypt TolvenMessage: " + tm.getId(), ex);
        }
        tm.setEncryptedKeyAlgorithm(docSecretKey.getAlgorithm());
        tm.setEncryptedKey(documentSecretKey.getEncryptedKey());
        Cipher cipher = null;
        // Encrypt the primary payload
        try {
            cipher = Cipher.getInstance(docSecretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, docSecretKey);
            if (tm.getPayload() != null) {
                tm.setPayload(cipher.doFinal(tm.getPayload()));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not encrypt TolvenMessage: " + tm.getId(), ex);
        }
        tm.setDecrypted(false);
        // If this message has attachments, deal with them as well.
        if (tm instanceof TolvenMessageWithAttachments) {
            TolvenMessageWithAttachments tma = (TolvenMessageWithAttachments) tm;
            // Do the same for the attachments
            for (TolvenMessageAttachment attachment : tma.getAttachments()) {
                if (attachment.getPayload() != null) {
                    try {
                        if (attachment.getPayload() != null) {
                            attachment.setPayload(cipher.doFinal(attachment.getPayload()));
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("Could not encrypt attachment " + tma.getId() + " to TolvenMessage: " + tm.getId(), ex);
                    }
                }
            }
        }
    }

    public void setAsEncryptedContent(byte[] unencryptedContent, TolvenMessage tm) {
        Account account = accountDAOLocal.findAccount(tm.getAccountId());
        if (account == null) {
            throw new RuntimeException("Cannot find account Id=" + tm.getAccountId() + " for TolvenMessage");
        }
        AccountProcessingPublicKey accountProcessingPublicKey = getAccountProcessingPublicKey(account);
        DocumentSecretKey documentSecretKey = DocumentSecretKey.getInstance();
        String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        SecretKey docSecretKey = null;
        try {
            docSecretKey = documentSecretKey.init(accountProcessingPublicKey.getPublicKey(), kbeKeyAlgorithm, kbeKeyLength);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get secret key to encrypt TolvenMessage: " + tm.getId(), ex);
        }
        try {
            Cipher cipher = Cipher.getInstance(docSecretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, docSecretKey);
            tm.setPayload(cipher.doFinal(unencryptedContent));
        } catch (Exception ex) {
            throw new RuntimeException("Could not encrypt data to TolvenMessage: " + tm.getId(), ex);
        }
        tm.setEncryptedKeyAlgorithm(docSecretKey.getAlgorithm());
        tm.setEncryptedKey(documentSecretKey.getEncryptedKey());
        tm.setDecrypted(false);
    }

    public TolvenMessage getAsEncryptedTolvenMessage(byte[] unencryptedContent, TolvenMessage tm) throws GeneralSecurityException {
        Account account = accountDAOLocal.findAccount(tm.getAccountId());
        if (account == null) {
            throw new RuntimeException("Cannot find account Id=" + tm.getAccountId() + " for TolvenMessage");
        }
        AccountProcessingPublicKey accountProcessingPublicKey = getAccountProcessingPublicKey(account);
        DocumentSecretKey documentSecretKey = DocumentSecretKey.getInstance();
        String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        SecretKey docSecretKey = documentSecretKey.init(accountProcessingPublicKey.getPublicKey(), kbeKeyAlgorithm, kbeKeyLength);
        Cipher cipher = Cipher.getInstance(docSecretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, docSecretKey);
        tm.setEncryptedKeyAlgorithm(docSecretKey.getAlgorithm());
        tm.setEncryptedKey(documentSecretKey.getEncryptedKey());
        tm.setPayload(cipher.doFinal(unencryptedContent));
        tm.setDecrypted(false);
        return tm;
    }

    public byte[] getDecryptedContent(TolvenMessage tm) {
        // If it's already been decrypted, don't do it again.
        if (tm.isDecrypted() || tm.getPayload() == null || tm.getEncryptedKey() == null) {
            return tm.getPayload();
        }
        Account account = accountDAOLocal.findAccount(tm.getAccountId());
        if (account == null) {
            throw new RuntimeException("Cannot find account Id=" + tm.getAccountId() + " for TolvenMessage");
        }
        AccountProcessingPrivateKey accountProcessingPrivateKey = getAccountProcessingPrivateKey(account);
        //TODO: Get the DocumentSecretKey from the TolvenMessage's encryptedKey
        DocumentSecretKey documentSecretKey = DocumentSecretKey.getInstance();
        SecretKey docSecretKey;
        try {
            documentSecretKey.init(tm.getEncryptedKey(), tm.getEncryptionKeyAlgorithm());
            X509Certificate x509Certificate = accountProcessingPrivateKey.getEncryptionX509Certificate();
            PrivateKey processorPrivateKey = getProcessorPrivateKey(x509Certificate);
            docSecretKey = documentSecretKey.getSecretKey(accountProcessingPrivateKey.getPrivateKey(processorPrivateKey));
        } catch (Exception ex) {
            throw new RuntimeException("Could not get secret key to decrypt TolvenMessage: " + tm.getId(), ex);
        }
        try {
            Cipher cipher = Cipher.getInstance(tm.getEncryptionKeyAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, docSecretKey);
            return cipher.doFinal(tm.getPayload());
        } catch (Exception ex) {
            throw new RuntimeException("Could not decrypt TolvenMessage content: " + tm.getId(), ex);
        }
    }

    /**
     * Decrypt TolvenMessage payload(s) in place
     * @param tm
     */
    public void decryptTolvenMessage(TolvenMessage tm) {
        if (tm.getPayload() == null || tm.isDecrypted() || tm.getEncryptedKey() == null) {
            return;
        }
        Account account = accountDAOLocal.findAccount(tm.getAccountId());
        if (account == null) {
            throw new RuntimeException("Cannot find account Id=" + tm.getAccountId() + " for TolvenMessage");
        }
        AccountProcessingPrivateKey accountProcessingPrivateKey = getAccountProcessingPrivateKey(account);
        //TODO: Get the DocumentSecretKey from the TolvenMessage's encryptedKey
        DocumentSecretKey documentSecretKey = DocumentSecretKey.getInstance();
        SecretKey docSecretKey;
        try {
            documentSecretKey.init(tm.getEncryptedKey(), tm.getEncryptionKeyAlgorithm());
            X509Certificate x509Certificate = accountProcessingPrivateKey.getEncryptionX509Certificate();
            PrivateKey processorPrivateKey = getProcessorPrivateKey(x509Certificate);
            docSecretKey = documentSecretKey.getSecretKey(accountProcessingPrivateKey.getPrivateKey(processorPrivateKey));
        } catch (Exception ex) {
            throw new RuntimeException("Could not get secret key to decrypt TolvenMessage: " + tm.getId(), ex);
        }
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(tm.getEncryptionKeyAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, docSecretKey);
            tm.setPayload(cipher.doFinal(tm.getPayload()));
        } catch (Exception ex) {
            throw new RuntimeException("Could not decrypt TolvenMessage content: " + tm.getId(), ex);
        }
        tm.setDecrypted(true);
        if (tm instanceof TolvenMessageWithAttachments) {
            TolvenMessageWithAttachments tma = (TolvenMessageWithAttachments) tm;
            // Do the same for the attachments
            for (TolvenMessageAttachment attachment : tma.getAttachments()) {
                if (attachment.getPayload() != null) {
                    try {
                        cipher = Cipher.getInstance(tm.getEncryptionKeyAlgorithm());
                        cipher.init(Cipher.DECRYPT_MODE, docSecretKey);
                        if (attachment.getPayload() != null) {
                            attachment.setPayload(cipher.doFinal(attachment.getPayload()));
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("Could not decrypt attachment " + tma.getId() + " to TolvenMessage: " + tm.getId(), ex);
                    }
                }
            }
        }
    }

    public InputStream getDecryptedContentAsInputStream(TolvenMessage tm) {
        return new ByteArrayInputStream(getDecryptedContent(tm));
    }

    private AccountProcessingPublicKey getAccountProcessingPublicKey(Account account) {
        AccountProcessingPublicKey accountProcessingPublicKey = account.getAccountProcessingPublicKey();
        if (accountProcessingPublicKey == null) {
            setupProcessKeys(account);
            accountProcessingPublicKey = account.getAccountProcessingPublicKey();
        }
        return accountProcessingPublicKey;
    }

    private AccountProcessingPrivateKey getAccountProcessingPrivateKey(Account account) {
        AccountProcessingPrivateKey accountProcessingPrivateKey = account.getAccountProcessingPrivateKey();
        if (accountProcessingPrivateKey == null) {
            setupProcessKeys(account);
            accountProcessingPrivateKey = account.getAccountProcessingPrivateKey();
        }
        return accountProcessingPrivateKey;
    }

    private void setupProcessKeys(Account account) {
        if (account.getAccountProcessingPublicKey() == null && account.getAccountProcessingPrivateKey() == null) {
            try {
                X509Certificate xprocessor509Certificate = getProcessorX509Certificate();
                String privateKeyAlgorithm = propertyBean.getProperty(AccountProcessingPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
                int privateKeyLength = Integer.parseInt(propertyBean.getProperty(AccountProcessingPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
                String kbeKeyAlgorithm = propertyBean.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
                int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
                AccountProcessingPrivateKey accountProcessingPrivateKey = AccountProcessingPrivateKey.getInstance();
                account.setAccountProcessingPrivateKey(accountProcessingPrivateKey);
                PublicKey accountProcessingPublicKey = accountProcessingPrivateKey.init(xprocessor509Certificate, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
                account.setAccountProcessingPublicKey(accountProcessingPublicKey);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to initialize the account processing key", ex);
            }
        } else {
            throw new RuntimeException("Cannot set up account processing keys, if keys exists on account: " + account.getId());
        }
    }

    private final PrivateKey getProcessorPrivateKey(X509Certificate x509Certificate) {
        try {
            MQKeyStoreConnection storeConnection = null;
            try {
                storeConnection = mqKeyStoreCF.getConnection();
                return storeConnection.getPrivateKey(x509Certificate);
            } finally {
                if (storeConnection != null) {
                    storeConnection.close();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not get PrivateKey for queue processing", ex);
        }
    }

    private final X509Certificate getProcessorX509Certificate() {
        try {
            MQKeyStoreConnection storeConnection = null;
            try {
                storeConnection = mqKeyStoreCF.getConnection();
                return storeConnection.getDefaultX509Certificate();
            } finally {
                if (storeConnection != null) {
                    storeConnection.close();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not get default X509Certificate for queue processing", ex);
        }
    }

}
