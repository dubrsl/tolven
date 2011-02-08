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

import junit.framework.TestCase;

import org.tolven.security.CertificateHelper;
import org.tolven.security.key.AccountPrivateKey;
import org.tolven.security.key.AccountSecretKey;

/**
 * This class is used to testing AccountPrivateKey.
 * 
 * @author Joseph Isaac
 * 
 */
public class AccountPrivateKeyTestCase extends TestCase {

    /*
     * Test method for
     * 'org.tolven.security.key.AccountPrivateKey.getInstance()'
     */
    public void testGetInstance() {
        AccountPrivateKey.getInstance();
    }

    /*
     * Test method for
     * 'org.tolven.security.key.AccountPrivateKey.init(PublicKey)'
     */
    public void testInitPublicKey() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        CertificateHelper certHelper = new CertificateHelper();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, "password".toCharArray());
        PublicKey publicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        PublicKey accountPublicKey = accountPrivateKey.init(publicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        assertTrue(accountPublicKey.getAlgorithm().equals(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP)));
    }

    /*
     * Test method for immutability of
     * 'org.tolven.security.key.AccountPrivateKey.init(PublicKey)'
     */
    public void testInitInitPublicKey() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        CertificateHelper certHelper = new CertificateHelper();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, "password".toCharArray());
        PublicKey publicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        accountPrivateKey.init(publicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        try {
            accountPrivateKey.init(publicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
            fail("Intializing twice is not allowed because keys are immutable");
        } catch (IllegalStateException ex) {
            // init should fail second time around, and thus this test passes
        }
    }

    /*
     * Test method for
     * 'org.tolven.security.key.AccountPrivateKey.init(AccountPrivateKey, UserPrivateKey, PublicKey)'
     */
    public void testInitAccountPrivateKeyUserPrivateKeyPublicKey() throws GeneralSecurityException, IOException {
        //Create UserPrivateKey1
        SecurityTestSuite.initProperties();
        CertificateHelper certHelper = new CertificateHelper();
        char[] password1 = "password1".toCharArray();
        KeyStore keyStore1 = CertificateHelperTestCase.getUserPKCS12("testUid1", password1);
        PublicKey publicKey1 = certHelper.getX509Certificate(keyStore1).getPublicKey();
        PrivateKey userPrivateKey1 = certHelper.getPrivateKey(keyStore1, password1);
        //Create AccountPrivateKey1
        AccountPrivateKey accountPrivateKey1 = AccountPrivateKey.getInstance();
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        accountPrivateKey1.init(publicKey1, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        //Create UserPrivateKey2
        char[] password2 = "password2".toCharArray();
        KeyStore keyStore2 = CertificateHelperTestCase.getUserPKCS12("testUid2", password2);
        PublicKey publicKey2 = certHelper.getX509Certificate(keyStore2).getPublicKey();
        PrivateKey userPrivateKey2 = certHelper.getPrivateKey(keyStore2, password2);
        //Transfer AccountPrivateKey1 to AccountPrivateKey2
        AccountPrivateKey accountPrivateKey2 = AccountPrivateKey.getInstance();
        kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        //UserPrivateKey theUserPrivateKey1 = UserPrivateKey.getInstance();
        //theUserPrivateKey1.setPrivateKey(userPrivateKey1);
        //accountPrivateKey2.init(accountPrivateKey1, theUserPrivateKey1, publicKey2, kbeKeyAlgorithm, kbeKeyLength);
        assertTrue(accountPrivateKey2.getPrivateKey(userPrivateKey2).equals(accountPrivateKey1.getPrivateKey(userPrivateKey1)));
    }

    /*
     * Test method for
     * 'org.tolven.security.key.AccountPrivateKey.getEncryptedSecretKey()'
     */
    public void testGetEncryptedSecretKey() {

    }

    /*
     * Test method for
     * 'org.tolven.security.key.AccountPrivateKey.getPrivateKey(PrivateKey)'
     */
    public void testGetPrivateKey() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        char[] password = "password".toCharArray();
        CertificateHelper certHelper = new CertificateHelper();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, password);
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        PrivateKey theUserPrivateKey = certHelper.getPrivateKey(keyStore, password);
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        accountPrivateKey.init(theUserPublicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        PrivateKey theAccountPrivateKey = accountPrivateKey.getPrivateKey(theUserPrivateKey);
        assertTrue(theAccountPrivateKey.getAlgorithm().equals(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP)));
    }

}
