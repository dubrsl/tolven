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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

import junit.framework.TestCase;

import org.tolven.security.CertificateHelper;
import org.tolven.security.key.UserPrivateKey;

/**
 * This class is used to testing UserPrivateKey.
 * 
 * @author Joseph Isaac
 * 
 */
public class CertificateHelperTestCase extends TestCase {

    public static String UID = "testUid";
    public static String CN = "testCn";
    public static String ORGANIZATIONUNIT_NAME = "testOrganizationUnitName";
    public static String ORGANIZATION_NAME = "testOrganizationName";
    public static String STATE_OR_PROVINCE = "testStateOrProvince";
    public static String COUNTRY_NAME = "testCountryName";

    public static KeyStore getUserPKCS12(String uid, char[] password) {
        CertificateHelper certHelper = new CertificateHelper();
        return certHelper.createPKCS12KeyStore(uid, CN, ORGANIZATIONUNIT_NAME, ORGANIZATION_NAME, STATE_OR_PROVINCE, COUNTRY_NAME, password);
    }

    public void testCreatePKCS12KeyStore() {
        SecurityTestSuite.initProperties();
        KeyStore keyStore = getUserPKCS12(UID, "password".toCharArray());
        assertTrue("Could not get KeyStore", keyStore != null);
        Enumeration<String> aliases = null;
        try {
            aliases = keyStore.aliases();
        } catch (KeyStoreException e) {
            throw new RuntimeException("Could not get aliases from KeyStore", e);
        }
        assertTrue("KeyStore must have at least one alias", aliases.hasMoreElements());
        aliases.nextElement();
        assertTrue("KeyStore must have no more than one alias", !aliases.hasMoreElements());
        assertTrue("PKCS12", CertificateHelper.TOLVEN_CREDENTIAL_FORMAT_PKCS12.equals(keyStore.getType()));
        try {
            assertTrue("KeyStore must have one and only one entry", keyStore.size() == 1);
        } catch (KeyStoreException ex) {
            throw new RuntimeException("Could not determine keyStore size ", ex);
        }
    }

    public void testGetX509Certificate_KeyStore() {
        SecurityTestSuite.initProperties();
        KeyStore keyStore = getUserPKCS12(CertificateHelperTestCase.UID, "password".toCharArray());
        CertificateHelper certHelper = new CertificateHelper();
        X509Certificate x509Certificate = (X509Certificate) certHelper.getX509Certificate(keyStore);
        assertTrue("Could not get X509Certificate from KeyStore", x509Certificate != null);
        X500Principal x500Principal = CertificateHelper.getX500Principal(UID, CN, ORGANIZATIONUNIT_NAME, ORGANIZATION_NAME, STATE_OR_PROVINCE, COUNTRY_NAME);
        assertTrue("The X500Principal is not correct", x509Certificate.getSubjectX500Principal().equals(x500Principal));
        PublicKey publicKey = x509Certificate.getPublicKey();
        String privateKeyAlgorithm = System.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        assertTrue("The user public key algortithm is incorrect", publicKey.getAlgorithm().equals(privateKeyAlgorithm));
    }

    public void testGetX509CertificateByteArray_KeyStore() {
        SecurityTestSuite.initProperties();
        KeyStore keyStore = getUserPKCS12(CertificateHelperTestCase.UID, "password".toCharArray());
        CertificateHelper certHelper = new CertificateHelper();
        byte[] x509Certificate = certHelper.getX509CertificateByteArray(keyStore);
        assertTrue("Could not get X509Certificate as byte[] from KeyStore", x509Certificate != null);
    }
    
    public void testGetPrivateKey_KeyStore_charArr() {
        SecurityTestSuite.initProperties();
        char[] password = "password".toCharArray();
        KeyStore keyStore = getUserPKCS12(CertificateHelperTestCase.UID, password);
        CertificateHelper certHelper = new CertificateHelper();
        PrivateKey privateKey = certHelper.getPrivateKey(keyStore, password);
        assertTrue("Could not get PrivateKey from KeyStore", privateKey != null);
        String privateKeyAlgorithm = System.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        assertTrue("The user private key algortithm is incorrect", privateKey.getAlgorithm().equals(privateKeyAlgorithm));
    }

}
