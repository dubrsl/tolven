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

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;

/**
 * A class which wraps a TolvenSession
 * 
 * @author Joseph Isaac
 *
 */
public class OpenAMSessionWrapper implements TolvenSessionWrapper {

    private SSOToken ssoToken;

    private static CertificateFactory certificateFactory;
    private static KeyFactory keyFactory;
    private static SSOTokenManager ssoTokenManager;

    public OpenAMSessionWrapper(SSOToken ssoToken) {
        this.ssoToken = ssoToken;
    }

    @Override
    public Object getAttribute(Object name) {
        try {
            return getSsoToken().getProperty((String) name);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get SSO property: " + name, ex);
        }
    }

    @Override
    public Collection<Object> getAttributeKeys() {
        throw new RuntimeException("Not Supported");
    }

    private CertificateFactory getCertificateFactory() {
        if (certificateFactory == null) {
            try {
                certificateFactory = CertificateFactory.getInstance("X509");
            } catch (CertificateException ex) {
                throw new RuntimeException("Could not get instance of CertificateFactory", ex);
            }
        }
        return certificateFactory;
    }

    @Override
    public String getHost() {
        try {
            return getSsoToken().getHostName();
        } catch (Exception ex) {
            throw new RuntimeException("Could not get host", ex);
        }
    }

    @Override
    public Serializable getId() {
        return getSsoToken().getTokenID().toString();
    }

    private KeyFactory getKeyFactory(String keyAlgorithm) {
        if (keyFactory == null) {
            try {
                keyFactory = KeyFactory.getInstance(keyAlgorithm);
            } catch (Exception ex) {
                throw new RuntimeException("Could not get instance of KeyFactory", ex);
            }
        }
        return keyFactory;
    }

    @Override
    public Date getLastAccessTime() {
        throw new RuntimeException("Not Supported");
    }

    @Override
    public Object getPrincipal() {
        try {
            String name = getSsoToken().getPrincipal().getName();
            return name.substring(1 + name.indexOf("="), name.indexOf(","));
        } catch (Exception ex) {
            throw new RuntimeException("Could not get Principal from sso token", ex);
        }
    }

    private SSOToken getSsoToken() {
        if (ssoToken == null) {
            throw new RuntimeException("SSOToken is null");
        }
        return ssoToken;
    }

    @Override
    public Date getStartTimestamp() {
        throw new RuntimeException("Not Supported");
    }

    @Override
    public long getTimeout() {
        try {
            return getSsoToken().getTimeLeft();
        } catch (Exception ex) {
            throw new RuntimeException("Could not get timeout", ex);
        }
    }

    private SSOTokenManager getTokenManager() {
        if (ssoTokenManager == null) {
            try {
                ssoTokenManager = SSOTokenManager.getInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Could not get an instance of SSOTokenManager", ex);
            }
        }
        return ssoTokenManager;
    }

    public List<String> getTolvenPersonList(String name) {
        Set<String> set = (Set<String>) getAttribute(name);
        if (set == null || set.isEmpty()) {
            return new ArrayList<String>();
        } else {
            return (List<String>) new ArrayList<String>(set);
        }
    }

    public String getTolvenPersonString(String name) {
        Set<String> set = (Set<String>) getAttribute(name);
        if (set == null || set.isEmpty()) {
            return null;
        } else {
            return (String) set.iterator().next();
        }
    }

    @Override
    public PrivateKey getUserPrivateKey(String keyAlgorithm) {
        String userPKCS8EncodedKey = null;
        try {
            userPKCS8EncodedKey = getSsoToken().getProperty("userPKCS8EncodedKey");
        } catch (Exception ex) {
            throw new RuntimeException("Could not get userPKCS8EncodedKey from SSOToken", ex);
        }
        if (userPKCS8EncodedKey == null) {
            return null;
        } else {
            try {
                byte[] userPKCS8EncodedKeyBytes = userPKCS8EncodedKey.getBytes("UTF-8");
                PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(userPKCS8EncodedKeyBytes));
                return getKeyFactory(keyAlgorithm).generatePrivate(pkcs8EncodedKeySpec);
            } catch (Exception ex) {
                throw new RuntimeException("Could not get PrivateKey from pkcs8EncodedKey", ex);
            }
        }
    }

    @Override
    public PublicKey getUserPublicKey() {
        X509Certificate x509Certificate = getUserX509Certificate();
        if (x509Certificate == null) {
            return null;
        } else {
            return x509Certificate.getPublicKey();
        }
    }

    @Override
    public X509Certificate getUserX509Certificate() {
        String userX509Certificate = null;
        try {
            userX509Certificate = getSsoToken().getProperty("userX509Certificate");
        } catch (Exception ex) {
            throw new RuntimeException("Could not get userX509Certificate from SSOToken", ex);
        }
        if (userX509Certificate == null) {
            return null;
        } else {
            try {
                byte[] userX509CertificateBytes = userX509Certificate.getBytes("UTF-8");
                return (X509Certificate) getCertificateFactory().generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(userX509CertificateBytes)));
            } catch (Exception ex) {
                throw new RuntimeException("Could not get X509Certificate from SSO userCertificate", ex);
            }
        }
    }

    @Override
    public String getUserX509CertificateString() {
        X509Certificate x509Certificate = getUserX509Certificate();
        if (x509Certificate == null) {
            return null;
        }
        try {
            StringBuffer buff = new StringBuffer();
            buff.append("-----BEGIN CERTIFICATE-----");
            buff.append("\n");
            String pemFormat = new String(Base64.encodeBase64Chunked(x509Certificate.getEncoded()));
            buff.append(pemFormat);
            buff.append("\n");
            buff.append("-----END CERTIFICATE-----");
            buff.append("\n");
            return buff.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Could not convert X509Certificate into a String", ex);
        }
    }

    @Override
    public boolean isAuthenticated() {
        return getTokenManager().isValidToken(ssoToken);
    }

    @Override
    public void logout() {
        SSOToken ssoToken = getSsoToken();
        String principal = "unknown principal";
        if (getTokenManager().isValidToken(ssoToken)) {
            try {
                principal = ssoToken.getPrincipal().getName();
            } catch (Exception e) {
                // Will attempt to logout anyway
            }
        }
        try {
            getTokenManager().destroyToken(ssoToken);
        } catch (Exception ex) {
            throw new RuntimeException("Fail to logout: " + principal, ex);
        }
    }

    @Override
    public Object removeAttribute(Object name) {
        Object obj = getAttribute(name);
        setAttribute(name, null);
        return obj;
    }

    @Override
    public void setAttribute(Object name, Object value) {
        try {
            if (value == null) {
                getSsoToken().setProperty((String) name, "");
            } else {
                getSsoToken().setProperty((String) name, (String) value);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not set SSO property: " + name, ex);
        }
    }

    @Override
    public void setTimeout(long timeout) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    public void stop() {
        throw new RuntimeException("Not Supported");
    }

    @Override
    public void touch() {
        throw new RuntimeException("Not Supported");
    }

}
