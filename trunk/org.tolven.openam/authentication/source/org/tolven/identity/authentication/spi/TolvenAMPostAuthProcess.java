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
package org.tolven.identity.authentication.spi;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.spi.AMPostAuthProcessInterface;
import com.sun.identity.authentication.spi.AuthenticationException;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.shared.encode.Base64;

/**
 * Post authentication class which collects the userPKCS12 from LDAP and ensures that its
 * X509Certificate and PrivateKey are available to the SSO session. The user's password
 * is used to decrypt the userPKCS12 and the X509Certifcate and PrivateKey are store in
 * the userCertificate and userPrivateKey of the SSO session as encoded bytes converted 
 * to Strings using charSet UTF-8
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenAMPostAuthProcess implements AMPostAuthProcessInterface {

    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";
    public static final String USERPKCS12_BINARY_ATTRNAME = "userPKCS12";
    public static final String USER_CONTEXT = "userContext";

    public TolvenAMPostAuthProcess() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onLoginFailure(Map map, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onLoginSuccess(Map map, HttpServletRequest request, HttpServletResponse response, SSOToken ssoToken) throws AuthenticationException {
        try {
            ssoToken.setProperty("appEncryptionActive", Boolean.TRUE.toString());
            ssoToken.setProperty(USER_CONTEXT, "vestibule");
            String uid = (String) map.get("IDToken1");
            String password = (String) map.get("IDToken2");
            AMIdentity amIdentity =  new AMIdentity(ssoToken);
            Set<String> attributeNames = new HashSet<String>();
            attributeNames.add(USERPKCS12_BINARY_ATTRNAME);
            Map userPKCS12Map = amIdentity.getBinaryAttributes(attributeNames);
            if (userPKCS12Map != null && userPKCS12Map.get(USERPKCS12_BINARY_ATTRNAME) != null) {
                byte[][] userPKCS12ByteArrs = (byte[][]) userPKCS12Map.get(USERPKCS12_BINARY_ATTRNAME);
                byte[] userPKCS12 = userPKCS12ByteArrs[0];
                KeyStore keyStore = KeyStore.getInstance(TOLVEN_CREDENTIAL_FORMAT_PKCS12);
                ByteArrayInputStream bais = new ByteArrayInputStream(userPKCS12);
                keyStore.load(bais, password.toCharArray());
                /*
                 * Retrieve PrivateKey and X509Certificate from the userPKCS12 KeyStore and place them in SSO session
                 */
                Enumeration<String> aliases = keyStore.aliases();
                if (!aliases.hasMoreElements()) {
                    throw new RuntimeException(getClass() + ": userPKCS12 contains no aliases for principal " + uid);
                }
                String alias = aliases.nextElement();
                PrivateKey userPrivateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
                if (userPrivateKey == null) {
                    throw new RuntimeException(getClass() + ": userPKCS12 contains no key with alias " + alias + " for " + uid);
                }
                PKCS8EncodedKeySpec userPKCS8EncodedKey = new PKCS8EncodedKeySpec(userPrivateKey.getEncoded());
                ssoToken.setProperty("userPKCS8EncodedKey", Base64.encode(userPKCS8EncodedKey.getEncoded()));
                Certificate[] certificateChain = keyStore.getCertificateChain(alias);
                if (certificateChain == null || certificateChain.length == 0) {
                    throw new LoginException(getClass() + ": LDAP's userPKCS12 contains no certificate with alias " + alias + " for " + uid);
                }
                X509Certificate certificate = (X509Certificate) certificateChain[0];
                ssoToken.setProperty("userX509Certificate", Base64.encode(certificate.getEncoded()));
            }
        } catch (Exception ex) {
            throw new AuthenticationException(ex.getMessage());
        }
    }

    @Override
    public void onLogout(HttpServletRequest map, HttpServletResponse request, SSOToken ssoToken) throws AuthenticationException {
        try {
            ssoToken.setProperty("userPKCS8EncodedKey", "");
            ssoToken.setProperty("userX509Certificate", "");
            ssoToken.setProperty("appEncryptionActive", "");
        } catch (Exception ex) {
            throw new AuthenticationException(ex.getMessage());
        }
    }

}
