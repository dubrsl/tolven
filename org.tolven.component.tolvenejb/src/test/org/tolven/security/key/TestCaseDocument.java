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
package test.org.tolven.security.key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.tolven.security.key.DocumentSecretKey;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * This class is used to test document encryption, decryption and signature
 * 
 * @author Joseph Isaac
 * 
 */
public class TestCaseDocument {

    public static final String DOC_SIGNATURE_ALGORITHM_PROP = "tolven.security.doc.signatureAlgorithm";
    private byte[] encryptedContent;
    private DocumentSecretKey documentSecretKey;
    private byte[] signature;
    private String signatureAlgorithm;

    /**
     * Genrate a document signature for some plainText, using a document
     * signature algorithm, then encrypt the plainText using the document
     * encryption algorithm of a randomly generated SecretKey, and encrypt the
     * SecretKey with anEncryptionKey
     * 
     * @param plainText
     * @param anEncryptionKey
     * @param aSignatureKey
     * @param aSignatureAlgorithm
     * @return
     * @throws GeneralSecurityException
     */
    public void init(byte[] plainText, PublicKey anEncryptionKey, PrivateKey aSignatureKey) throws GeneralSecurityException {
        signatureAlgorithm = System.getProperty(DOC_SIGNATURE_ALGORITHM_PROP);
        Signature sig = Signature.getInstance(signatureAlgorithm);
        sig.initSign(aSignatureKey);
        sig.update(plainText);
        signature = sig.sign();
        documentSecretKey = DocumentSecretKey.getInstance();
        String kbeKeyAlgorithm = System.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        SecretKey docSecretKey = documentSecretKey.init(anEncryptionKey, kbeKeyAlgorithm, kbeKeyLength);
        encryptedContent = createEncryptedContent(plainText, docSecretKey);
    }

    /**
     * return an encrypted Document
     * 
     * @return
     */
    private byte[] getEncryptedContent() {
        return encryptedContent;
    }

    /**
     * Decrypt the SecretKey using aDecryptionKey
     * 
     * @param aDecryptionKey
     * @return
     * @throws GeneralSecurityException
     */
    private SecretKey getSecretKey(PrivateKey aDecryptionKey) throws GeneralSecurityException {
        return documentSecretKey.getSecretKey(aDecryptionKey);
    }

    /**
     * Verify the document signature belongs to aPublicKey using aDecryptionKey
     * to decrypt the document
     * 
     * @param aPublicKey
     * @param aDecryptionKey
     * @return
     * @throws GeneralSecurityException
     */
    public boolean verify(PublicKey aPublicKey, PrivateKey aDecryptionKey) throws GeneralSecurityException {
        Signature sig = Signature.getInstance(signatureAlgorithm);
        sig.initVerify(aPublicKey);
        sig.update(decryptDocument(aDecryptionKey));
        return sig.verify(signature);
    }

    /**
     * Decrypt a Document using aDecryptionKey
     * 
     * @param aDecryptionKey
     * @return
     * @throws GeneralSecurityException
     */
    public byte[] decryptDocument(PrivateKey aDecryptionKey) throws GeneralSecurityException {
        SecretKey docSecretKey = getSecretKey(aDecryptionKey);
        Cipher cipher = Cipher.getInstance(docSecretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, docSecretKey);
        return cipher.doFinal(getEncryptedContent());
    }

    /**
     * Encrypt plainText with a SecretKey
     * 
     * @param plainText
     * @param secretKey
     * @return
     * @throws Exception
     */
    private byte[] createEncryptedContent(byte[] plainText, SecretKey aSecretKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(aSecretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, aSecretKey);
        return cipher.doFinal(plainText);
    }

}
