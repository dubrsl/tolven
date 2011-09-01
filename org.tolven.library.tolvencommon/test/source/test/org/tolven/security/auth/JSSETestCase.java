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
 * @version $Id: JSSETestCase.java 1790 2011-07-22 20:42:40Z joe.isaac $
 */
package test.org.tolven.security.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Enumeration;

import junit.framework.TestCase;

/**
 * Test the JSSE aspects upon which the application relies
 * 
 * @author Joseph Isaac
 *
 */
public class JSSETestCase extends TestCase {

    /**
     * Application server expects to find trusted certificates cacerts truststore in Java's installation
     * 
     */
    public void testCacertsAvailable() throws GeneralSecurityException, IOException {
        File cacertsFile = new File(System.getProperty("java.home"), "/lib/security/cacerts");
        if (cacertsFile.exists()) {
            KeyStore cacertsTrustStore = getKeyStore(cacertsFile.getPath(), "jks", null);
            Enumeration<String> enumeration = cacertsTrustStore.aliases();
            if (!enumeration.hasMoreElements()) {
                fail("Java installed cacerts is empty: " + cacertsFile.getPath());
            }
        } else {
            fail("Java installed cacerts was not found at: " + cacertsFile.getPath());
        }
    }

    private static KeyStore getKeyStore(String keyPath, String keyType, char[] password) throws IOException, GeneralSecurityException {
        File keyStoreFile = new File(keyPath);
        if (!keyStoreFile.exists()) {
            throw new RuntimeException("Cannot find KeyStore file: " + keyPath);
        }
        KeyStore keyStore = KeyStore.getInstance(keyType);
        FileInputStream in = null;
        try {
            in = new FileInputStream(keyStoreFile);
            keyStore.load(in, password);
        } finally {
            if (in != null)
                in.close();
        }
        return keyStore;
    }

}
