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
 */
package org.tolven.session;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;

public class SecretKeyThreadLocal {

    @SuppressWarnings("rawtypes")
    private static ThreadLocal threadLocal = new ThreadLocal();

    public static void set(byte[] secretKey) {
        threadLocal.set(secretKey);
    }

    public static byte[] get() {
        return (byte[]) threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static String getExtendedSessionId(String sessionId) {
        byte[] secretKey = SecretKeyThreadLocal.get();
        if (secretKey == null) {
            throw new RuntimeException("No secretKey found in SecretKeyThreadLocal. secretKey for extended session Ids cannot be null");
        }
        return getExtendedSessionId(sessionId, secretKey);
    }

    public static String getExtendedSessionId(String sessionId, byte[] secretKey) {
        if (sessionId == null) {
            throw new RuntimeException("sessionId for extended session Ids cannot be null");
        } else if (secretKey == null) {
            throw new RuntimeException("secretKey for extended session Ids cannot be null");
        } else {
            String urlEncodedSecretKey = null;
            try {
                String encodedSecretKey = new String(Base64.encodeBase64(secretKey), "UTF-8");
                urlEncodedSecretKey = URLEncoder.encode(encodedSecretKey, "UTF-8");
            } catch (Exception ex) {
                throw new RuntimeException("Could not encode secret key", ex);
            }
            return sessionId + "_" + urlEncodedSecretKey;
        }
    }

    public static String getSessionId(String extendedSessionId) {
        if (extendedSessionId == null || extendedSessionId.trim().length() == 0) {
            return null;
        } else {
            int index = extendedSessionId.indexOf("_");
            if (index == -1) {
                return extendedSessionId;
            } else {
                return extendedSessionId.substring(0, index);
            }
        }
    }

    public static byte[] getSecretKey(String extendedSessionId) {
        if (extendedSessionId == null || extendedSessionId.trim().length() == 0) {
            return null;
        } else {
            int index = extendedSessionId.indexOf("_");
            if (index == -1) {
                return null;
            } else {
                try {
                    String urlDecodedSecretKey = URLDecoder.decode(extendedSessionId.substring(1 + extendedSessionId.indexOf("_")), "UTF-8");
                    byte[] secretKey = Base64.decodeBase64(urlDecodedSecretKey.getBytes("UTF-8"));
                    return secretKey;
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException("Could not base64 decode the extended session Id", ex);
                }
            }
        }
    }

}
