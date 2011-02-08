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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import junit.framework.TestCase;

import org.tolven.security.CertificateHelper;
import org.tolven.security.key.AccountPrivateKey;
import org.tolven.security.key.AccountSecretKey;
import org.tolven.security.key.DocumentSecretKey;

/**
 * This class is used to testing DocumentSecretKey.
 * 
 * @author Joseph Isaac
 * 
 */
public class DocumentSecretKeyTestCase extends TestCase {

    /*
     * Test method for
     * 'org.tolven.security.key.DocumentSecretKey.getSecretKey(PrivateKey)'
     */
    public void testGetSecretKey() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        char[] password = "password".toCharArray();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, password);
        CertificateHelper certHelper = new CertificateHelper();
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        PrivateKey theUserPrivateKey = certHelper.getPrivateKey(keyStore, password);
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        PublicKey theAccountPublicKey = accountPrivateKey.init(theUserPublicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        PrivateKey theAccountPrivateKey = accountPrivateKey.getPrivateKey(theUserPrivateKey);
        DocumentSecretKey accountSecretKey = DocumentSecretKey.getInstance();
        kbeKeyAlgorithm = System.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        kbeKeyLength = Integer.parseInt(System.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        SecretKey theOriginalSecretKey = accountSecretKey.init(theAccountPublicKey, kbeKeyAlgorithm, kbeKeyLength);
        SecretKey requestedSecretKey = accountSecretKey.getSecretKey(theAccountPrivateKey);
        assertTrue(requestedSecretKey.equals(theOriginalSecretKey));
    }

    /*
     * Test method for
     * 'org.tolven.security.key.DocumentSecretKey.getInstance()'
     */
    public void testGetInstance() {
        DocumentSecretKey.getInstance();
    }

    /*
     * Test method for
     * 'org.tolven.security.key.DocumentSecretKey.init(PublicKey)'
     */
    public void testInitPublicKey() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        char[] password = "password".toCharArray();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, password);
        CertificateHelper certHelper = new CertificateHelper();
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        PublicKey accountPublicKey = accountPrivateKey.init(theUserPublicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        DocumentSecretKey accountSecretKey = DocumentSecretKey.getInstance();
        kbeKeyAlgorithm = System.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        kbeKeyLength = Integer.parseInt(System.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        SecretKey secretKey = accountSecretKey.init(accountPublicKey, kbeKeyAlgorithm, kbeKeyLength);
        assertTrue(secretKey.getAlgorithm().equals(System.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP)));
    }

    /*
     * Test method for immutability of
     * 'org.tolven.security.key.DocumentSecretKey.init(PublicKey)'
     */
    public void testInitInitPublicKey() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        char[] password = "password".toCharArray();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, password);
        CertificateHelper certHelper = new CertificateHelper();
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        PublicKey accountPublicKey = accountPrivateKey.init(theUserPublicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        DocumentSecretKey accountSecretKey = DocumentSecretKey.getInstance();
        kbeKeyAlgorithm = System.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        kbeKeyLength = Integer.parseInt(System.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        accountSecretKey.init(accountPublicKey, kbeKeyAlgorithm, kbeKeyLength);
        try {
            accountSecretKey.init(accountPublicKey, kbeKeyAlgorithm, kbeKeyLength);
            fail("Intializing twice is not allowed because keys are immutable");
        } catch (IllegalStateException ex) {
            // init should fail second time around, and thus this test passes
        }
    }

}
