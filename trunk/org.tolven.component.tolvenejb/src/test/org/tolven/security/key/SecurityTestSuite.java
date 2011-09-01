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

import org.tolven.security.CertificateHelper;
import org.tolven.security.key.AccountPrivateKey;
import org.tolven.security.key.AccountSecretKey;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.security.key.UserPrivateKey;

/**
 * This class carries out testing for a number of security features.
 * 
 * @author Joseph Isaac
 * 
 */
public class SecurityTestSuite {

    public static void initProperties() {
        System.setProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP, "RSA");
        System.setProperty(UserPrivateKey.USER_PRIVATE_KEY_LENGTH_PROP, "1024");
        System.setProperty(UserPrivateKey.PBE_KEY_ALGORITHM_PROP, "PBEWithMD5AndDES");
        System.setProperty(UserPrivateKey.USER_PASSWORD_SALT_LENGTH_PROP, "8");
        System.setProperty(UserPrivateKey.USER_PASSWORD_ITERATION_COUNT_PROP, "20");

        System.setProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP, "RSA");
        System.setProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP, "1024");

        System.setProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP, "DESede");
        System.setProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH, "112");

        System.setProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP, "DESede");
        System.setProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH, "112");
        System.setProperty(TestCaseDocument.DOC_SIGNATURE_ALGORITHM_PROP, "SHA1withRSA");
    }

    public static KeyStore getUserPKCS12(String uid, char[] password) {
        CertificateHelper certHelper = new CertificateHelper();
        String cn = "testCn";
        String organizationUnitName = "testOrganizationUnitName";
        String organizationName = "testOrganizationName";
        String stateOrProvince = "testStateOrProvince";
        String countryName = "testCountryName";
        return certHelper.createPKCS12KeyStore(uid, cn, organizationUnitName, organizationName, stateOrProvince, countryName, password);
    }

}
