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

import javax.crypto.SecretKey;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageAttachment;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.queue.RuleQueueKeyLocal;
import org.tolven.security.AccountProcessingProtectionLocal;

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
    private RuleQueueKeyLocal ruleQueueKeyBean;

    /**
     * Decrypt TolvenMessage payload(s) in place
     * @param tm
     */
    @Override
    public void decryptTolvenMessage(TolvenMessage tm) {
        if (tm.getPayload() == null || tm.isDecrypted() || tm.getEncryptedKey() == null) {
            return;
        }
        try {
            byte[] decryptedPayload = ruleQueueKeyBean.decryptMessage(tm.getPayload(), tm.getEncryptedKey(), tm.getEncryptionKeyAlgorithm(), tm.getAccountId());
            tm.setPayload(decryptedPayload);
        } catch (Exception ex) {
            throw new RuntimeException("Could not decrypt TolvenMessage content: " + tm.getId(), ex);
        }
        if (tm instanceof TolvenMessageWithAttachments) {
            TolvenMessageWithAttachments tma = (TolvenMessageWithAttachments) tm;
            // Do the same for the attachments
            for (TolvenMessageAttachment attachment : tma.getAttachments()) {
                if (attachment.getPayload() != null) {
                    try {
                        if (attachment.getPayload() != null) {
                            byte[] decryptedPayload = ruleQueueKeyBean.decryptMessage(attachment.getPayload(), tm.getEncryptedKey(), tm.getEncryptionKeyAlgorithm(), tm.getAccountId());
                            attachment.setPayload(decryptedPayload);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("Could not decrypt attachment " + tma.getId() + " to TolvenMessage: " + tm.getId(), ex);
                    }
                }
            }
        }
        tm.setDecrypted(true);
    }

    /**
     * Encrypt the payload(s) of a TolvenMessage for queuing. 
     * @param tm
     */
    @Override
    public void encryptTolvenMessage(TolvenMessage tm) {
        SecretKey messageSecretKey = ruleQueueKeyBean.generateMessageSecretKey(tm.getAccountId());
        byte[] encryptedMessageSecretKey = ruleQueueKeyBean.encryptMessageSecretKey(messageSecretKey, tm.getAccountId());
        tm.setEncryptedKeyAlgorithm(messageSecretKey.getAlgorithm());
        tm.setEncryptedKey(encryptedMessageSecretKey);
        // Encrypt the primary payload
        try {
            if (tm.getPayload() != null) {
                byte[] payload = ruleQueueKeyBean.encryptMessage(tm.getPayload(), messageSecretKey);
                tm.setPayload(payload);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not encrypt TolvenMessage: " + tm.getId(), ex);
        }
        // If this message has attachments, deal with them as well.
        if (tm instanceof TolvenMessageWithAttachments) {
            TolvenMessageWithAttachments tma = (TolvenMessageWithAttachments) tm;
            // Do the same for the attachments
            for (TolvenMessageAttachment attachment : tma.getAttachments()) {
                if (attachment.getPayload() != null) {
                    try {
                        if (attachment.getPayload() != null) {
                            byte[] payload = ruleQueueKeyBean.encryptMessage(attachment.getPayload(), messageSecretKey);
                            attachment.setPayload(payload);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("Could not encrypt attachment " + tma.getId() + " to TolvenMessage: " + tm.getId(), ex);
                    }
                }
            }
        }
        tm.setDecrypted(false);
    }

    /*
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
    */
    
    @Override
    public byte[] getDecryptedContent(TolvenMessage tm) {
        // If it's already been decrypted, don't do it again.
        if (tm.isDecrypted() || tm.getPayload() == null || tm.getEncryptedKey() == null) {
            return tm.getPayload();
        }
        try {
            byte[] decryptedContent = ruleQueueKeyBean.decryptMessage(tm.getPayload(), tm.getEncryptedKey(), tm.getEncryptionKeyAlgorithm(), tm.getAccountId());
            return decryptedContent;
        } catch (Exception ex) {
            throw new RuntimeException("Could not decrypt TolvenMessage content: " + tm.getId(), ex);
        }
    }

    @Override
    public InputStream getDecryptedContentAsInputStream(TolvenMessage tm) {
        return new ByteArrayInputStream(getDecryptedContent(tm));
    }

    @Override
    public void setAsEncryptedContent(byte[] unencryptedContent, TolvenMessage tm) {
        SecretKey messageSecretKey = ruleQueueKeyBean.generateMessageSecretKey(tm.getAccountId());
        try {
            byte[] payload = ruleQueueKeyBean.encryptMessage(unencryptedContent, messageSecretKey);
            tm.setPayload(payload);
        } catch (Exception ex) {
            throw new RuntimeException("Could not encrypt data to TolvenMessage: " + tm.getId(), ex);
        }
        tm.setEncryptedKeyAlgorithm(messageSecretKey.getAlgorithm());
        byte[] encryptedMessageSecretKey = ruleQueueKeyBean.encryptMessageSecretKey(messageSecretKey, tm.getAccountId());
        tm.setEncryptedKey(encryptedMessageSecretKey);
        tm.setDecrypted(false);
    }

}
