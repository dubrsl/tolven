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
package org.tolven.sso;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.NewCookie;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;

/**
 * A class which supplies SSO functionality
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenSSO {

    private static TolvenSSO tolvenSSO;
    private DatatypeFactory datatypeFactory = null;
    private SSOTokenManager ssoTokenManager;
    private CertificateFactory certificateFactory;
    private KeyFactory keyFactory;

    private TolvenSSO() {
    }

    public static TolvenSSO getInstance() {
        if (tolvenSSO == null) {
            tolvenSSO = new TolvenSSO();
        }
        return tolvenSSO;
    }

    public SSOTokenManager getTokenManager() {
        if (ssoTokenManager == null) {
            try {
                ssoTokenManager = SSOTokenManager.getInstance();
            } catch (SSOException ex) {
                throw new RuntimeException("Could not get an instance of SSOTokenManager", ex);
            }
        }
        return ssoTokenManager;
    }

    public CertificateFactory getCertificateFactory() {
        if (certificateFactory == null) {
            try {
                certificateFactory = CertificateFactory.getInstance("X509");
            } catch (CertificateException ex) {
                throw new RuntimeException("Could not get instance of CertificateFactory", ex);
            }
        }
        return certificateFactory;
    }

    public KeyFactory getKeyFactory(String keyAlgorithm) {
        if (keyFactory == null) {
            try {
                keyFactory = KeyFactory.getInstance(keyAlgorithm);
            } catch (Exception ex) {
                throw new RuntimeException("Could not get instance of KeyFactory", ex);
            }
        }
        return keyFactory;
    }

    private DatatypeFactory getDatatypeFactory() {
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Could not create DatatypeFactory", ex);
            }
        }
        return datatypeFactory;
    }

    public SSOToken getSSOToken(HttpServletRequest request) {
        try {
            return getTokenManager().createSSOToken(request);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create SSO token from request", ex);
        }
    }

    public String getSessionProperty(String name, SSOToken ssoToken) {
        try {
            return ssoToken.getProperty(name);
        } catch (SSOException ex) {
            throw new RuntimeException("Could not get SSO property: " + name, ex);
        }
    }

    public String getSessionProperty(String name, HttpServletRequest request) {
        return getSessionProperty(name, getSSOToken(request));
    }

    public void setSessionProperty(String name, String value, SSOToken ssoToken) {
        try {
            if (value == null) {
                ssoToken.setProperty(name, "");
            } else {
                ssoToken.setProperty(name, value);
            }
        } catch (SSOException ex) {
            throw new RuntimeException("Could not set SSO property: " + name, ex);
        }
    }

    public void setSessionProperty(String name, String value, HttpServletRequest request) {
        setSessionProperty(name, value, getSSOToken(request));
    }

    public void removeSessionProperty(String name, SSOToken ssoToken) {
        setSessionProperty(name, null, ssoToken);
    }

    public void removeSessionProperty(String name, HttpServletRequest request) {
        removeSessionProperty(name, getSSOToken(request));
    }

    public NewCookie getSSOCookie(HttpServletRequest request) {
        return getSSOCookie(getSSOToken(request));
    }

    public NewCookie getSSOCookie(SSOToken ssoToken) {
        try {
            return new NewCookie("iPlanetDirectoryPro", URLEncoder.encode(ssoToken.getTokenID().toString(), "UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException("Could not create NewCookie for RESTful call", ex);
        }
    }

    public X509Certificate getUserX509Certificate(HttpServletRequest request) {
        return getUserX509Certificate(getSSOToken(request));
    }

    public X509Certificate getUserX509Certificate(SSOToken ssoToken) {
        String userX509Certificate = null;
        try {
            userX509Certificate = ssoToken.getProperty("userX509Certificate");
        } catch (SSOException ex) {
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

    public String getUserX509CertificateString(HttpServletRequest request) {
        return getUserX509CertificateString(getSSOToken(request));
    }

    public String getUserX509CertificateString(SSOToken ssoToken) {
        X509Certificate x509Certificate = getUserX509Certificate(ssoToken);
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

    public PublicKey getUserPublicKey(HttpServletRequest request) {
        return getUserPublicKey(getSSOToken(request));
    }

    public PublicKey getUserPublicKey(SSOToken ssoToken) {
        X509Certificate x509Certificate = getUserX509Certificate(ssoToken);
        if (x509Certificate == null) {
            return null;
        } else {
            return x509Certificate.getPublicKey();
        }
    }

    public PrivateKey getUserPrivateKey(HttpServletRequest request, String keyAlgorithm) {
        return getUserPrivateKey(getSSOToken(request), keyAlgorithm);
    }

    public PrivateKey getUserPrivateKey(SSOToken ssoToken, String keyAlgorithm) {
        String userPKCS8EncodedKey = null;
        try {
            userPKCS8EncodedKey = ssoToken.getProperty("userPKCS8EncodedKey");
        } catch (SSOException ex) {
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

    public boolean isValid(HttpServletRequest request) {
        return isValid(getSSOToken(request));
    }

    public boolean isValid(SSOToken ssoToken) {
        return getTokenManager().isValidToken(ssoToken);
    }

    public void logout(HttpServletRequest request) {
        logout(getSSOToken(request));
    }

    public void logout(SSOToken ssoToken) {
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

    public String getTolvenPersonString(String name, HttpServletRequest request) {
        Set<String> set = (Set<String>) request.getAttribute(name);
        if (set == null || set.isEmpty()) {
            return null;
        } else {
            return (String) set.iterator().next();
        }
    }

    public List<String> getTolvenPersonList(String name, HttpServletRequest request) {
        Set<String> set = (Set<String>) request.getAttribute(name);
        if (set == null || set.isEmpty()) {
            return new ArrayList<String>();
        } else {
            return (List<String>) new ArrayList<String>(set);
        }
    }

    public void updateAccountUserTimestamp(HttpServletRequest request) {
        Date now = (Date) request.getAttribute("tolvenNow");
        if (now == null) {
            throw new RuntimeException("No tolvenNow found in request");
        }
        setSessionProperty("accountUserTimestamp", getTimestamp(now), request);
    }

    private String getTimestamp(Date now) {
        GregorianCalendar nowGC = new GregorianCalendar();
        nowGC.setTime(now);
        XMLGregorianCalendar ts = getDatatypeFactory().newXMLGregorianCalendar(nowGC);
        return ts.toXMLFormat();
    }

}
