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

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PublicKey;

import junit.framework.TestCase;

import org.tolven.security.CertificateHelper;
import org.tolven.security.key.AccountPublicKey;

/**
 * This class is used to testing AccountPublicKey.
 * 
 * @author Joseph Isaac
 * 
 */
public class AccountPublicKeyTestCase extends TestCase {

    /*
     * Test method for 'org.tolven.security.key.AccountPublicKey.getInstance()'
     */
    public void testGetInstance() {
        AccountPublicKey.getInstance();
    }

    /*
     * Test method for
     * 'org.tolven.security.key.AccountPublicKey.init(PublicKey)'
     */
    public void testInitPublicKey() {
        SecurityTestSuite.initProperties();
        CertificateHelper certHelper = new CertificateHelper();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, "password".toCharArray());
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        AccountPublicKey accountPublicKey = AccountPublicKey.getInstance();
        accountPublicKey.init(theUserPublicKey);
    }

    /*
     * Test method for immutability of
     * 'org.tolven.security.key.AccountPublicKey.init(PublicKey)'
     */
    public void testInitInitPublicKey() {
        SecurityTestSuite.initProperties();
        CertificateHelper certHelper = new CertificateHelper();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, "password".toCharArray());
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        AccountPublicKey accountPublicKey = AccountPublicKey.getInstance();
        accountPublicKey.init(theUserPublicKey);
        try {
            accountPublicKey.init(theUserPublicKey);
            fail("Intializing twice is not allowed because keys are immutable");
        } catch (IllegalStateException ex) {
            // init should fail second time around, and thus this test passes
        }
    }

    /*
     * Test method for 'org.tolven.security.key.AccountPublicKey.getPublicKey()'
     */
    public void testGetPublicKey() throws GeneralSecurityException {
        SecurityTestSuite.initProperties();
        CertificateHelper certHelper = new CertificateHelper();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, "password".toCharArray());
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        AccountPublicKey accountPublicKey = AccountPublicKey.getInstance();
        accountPublicKey.init(theUserPublicKey);
        PublicKey publicKey = accountPublicKey.getPublicKey();
        assertTrue(publicKey.equals(theUserPublicKey));
    }

}
