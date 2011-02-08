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
package org.tolven.security.hash;

import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Convenience methods for MessageDigest hashing algorithms
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenMessageDigest {

    public static String checksum(URL url, String algorithm) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create an instance of MessageDigest", ex);
        }
        byte[] bytes = new byte[4 * 1024];
        try {
            InputStream in = null;
            try {
                in = url.openStream();
                int nBytesRead = 0;
                while ((nBytesRead = in.read(bytes)) > 0) {
                    messageDigest.update(bytes, 0, nBytesRead);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read message digest for: " + url.toExternalForm() + " using algorithm: " + algorithm);
        }
        byte[] checksumValue = messageDigest.digest();
        return digestAsString(checksumValue);

    }

    private static String digestAsString(byte[] bytes) {
        /*
         * No idea why Java did not encapsulate this kind of stuff within MessageDigest, and just give the String!!!
         */
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
