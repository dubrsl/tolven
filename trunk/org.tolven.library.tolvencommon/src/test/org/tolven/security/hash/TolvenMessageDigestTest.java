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
 * @version $Id$
 */
package test.org.tolven.security.hash;

import java.net.URL;
import junit.framework.TestCase;

import org.tolven.logging.TolvenLogger;
import org.tolven.security.hash.TolvenMessageDigest;

/**
 * Class for testing MessageDigest hashing algorithms
 * 
 * @author Joseph Isaac
 *
 */

public class TolvenMessageDigestTest extends TestCase {

    /**
     * Verify TolvenMessageDigest MD5 forURLs is working
     */
    public void testMD5ForURL() {
        URL url = getClass().getResource("digest-test-file.txt");
        String origDigest = "ef24bdb7faeba173d09ff67f4b4decb9";
        String currentDigest = TolvenMessageDigest.checksum(url, "md5");
        TolvenLogger.info("MD5 for file digest-test-file.txt is:" + currentDigest, TolvenMessageDigestTest.class);
        assertTrue(origDigest.equals(currentDigest));
    }

    /**
     * Verify TolvenMessageDigest SHA1 forURLs is working
     */
    public void testSHA1ForURL() {
        URL url = getClass().getResource("digest-test-file.txt");
        String origDigest = "ddadfd7141a954681cd7d96d41f8996d9312c0a9";
        String currentDigest = TolvenMessageDigest.checksum(url, "sha1");
        TolvenLogger.info("MD5 for file digest-test-file.txt is:" + currentDigest, TolvenMessageDigestTest.class);
        assertTrue(origDigest.equals(currentDigest));
    }

}
