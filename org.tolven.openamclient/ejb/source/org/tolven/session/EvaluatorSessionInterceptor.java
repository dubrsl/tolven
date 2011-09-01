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
 * @version $Id: EvaluatorSessionInterceptor.java 1530 2011-06-30 09:43:03Z joe.isaac $
 */
package org.tolven.session;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.shared.encode.Base64;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * This default interceptor used for onMessage() does nothing
 * 
 * @author Joseph Isaac
 *
 */
@Interceptor
public class EvaluatorSessionInterceptor {

    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";
    public static final String USERPKCS12_BINARY_ATTRNAME = "userPKCS12";
    public static final String USER_CONTEXT = "userContext";

    public static final String COOKIE_NAME_FOR_TOKEN = "iPlanetDirectoryPro";
    public static final String LOGIN_PATH = "authenticate";

    private Client client;
    private WebResource authWebResource;
    private String authRestfulURL = "https://newton.teknomad.com:8444/openam/identity";

    private SSOTokenManager ssoTokenManager;

    protected Client getClient() {
        if (client == null) {
            ClientConfig config = new DefaultClientConfig();
            try {
                config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(new DefaultHostnameVerifier(), SSLContext.getDefault()));
            } catch (Exception e) {
                throw new RuntimeException("RuntimeException", e);
            }
            client = Client.create(config);
            client.setFollowRedirects(true);
        }
        return client;
    }

    private String getAuthRestfulURL() {
        return authRestfulURL;
    }

    private SSOTokenManager getTokenManager() {
        if (ssoTokenManager == null) {
            try {
                ssoTokenManager = SSOTokenManager.getInstance();
            } catch (SSOException ex) {
                throw new RuntimeException("Could not get an instance of SSOTokenManager", ex);
            }
        }
        return ssoTokenManager;
    }

    @AroundInvoke
    public Object initializeSession(InvocationContext invCtx) throws Exception {
        SSOToken ssoToken = null;
        try {
            String uid = "mdbuser";
            char[] password = "tolven".toCharArray();
            String tokenId = login(uid, password, "tolven");
            ssoToken = getTokenManager().createSSOToken(tokenId);
            ssoToken.setProperty("appEncryptionActive", Boolean.TRUE.toString());
            ssoToken.setProperty(USER_CONTEXT, "vestibule");
            AMIdentity amIdentity = new AMIdentity(ssoToken);
            Set<String> attributeNames = new HashSet<String>();
            attributeNames.add(USERPKCS12_BINARY_ATTRNAME);
            Map userPKCS12Map = amIdentity.getBinaryAttributes(attributeNames);
            if (userPKCS12Map != null && userPKCS12Map.get(USERPKCS12_BINARY_ATTRNAME) != null) {
                byte[][] userPKCS12ByteArrs = (byte[][]) userPKCS12Map.get(USERPKCS12_BINARY_ATTRNAME);
                byte[] userPKCS12 = userPKCS12ByteArrs[0];
                KeyStore keyStore = KeyStore.getInstance(TOLVEN_CREDENTIAL_FORMAT_PKCS12);
                ByteArrayInputStream bais = new ByteArrayInputStream(userPKCS12);
                keyStore.load(bais, password);
                /*
                 * Retrieve PrivateKey and X509Certificate from the userPKCS12 KeyStore and place them in SSO session
                 */
                Enumeration<String> aliases = keyStore.aliases();
                if (!aliases.hasMoreElements()) {
                    throw new RuntimeException(getClass() + ": userPKCS12 contains no aliases for principal " + uid);
                }
                String alias = aliases.nextElement();
                PrivateKey userPrivateKey = (PrivateKey) keyStore.getKey(alias, password);
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
            try {
                TolvenSessionWrapperThreadLocal.set(new OpenAMSessionWrapper(ssoToken));
                return invCtx.proceed();
            } finally {
                TolvenSessionWrapperThreadLocal.remove();
            }
        } finally {
            if (ssoToken != null) {
                logout(ssoToken);
            }
        }
    }

    private String login(String username, char[] password, String realm) {
        WebResource webResource = getAuthWebResource().path(LOGIN_PATH);
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("username", username);
        formData.add("password", new String(password));
        ClientResponse response = webResource.queryParam("uri", "realm=" + realm).post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " " + username + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        Cookie cookie = getOpenAMCookie(response);
        return cookie.getValue();
    }

    private WebResource getAuthWebResource() {
        if (authWebResource == null) {
            authWebResource = getClient().resource(getAuthRestfulURL());
        }
        return authWebResource;
    }

    private Cookie getOpenAMCookie(ClientResponse response) {
        try {
            String result = response.getEntity(String.class);
            String tokenString = null;
            try {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new StringReader(result));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("token.id")) {
                            tokenString = line;
                            break;
                        }
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException("Could not read the string response, ex");
            }
            if (tokenString == null) {
                throw new RuntimeException(COOKIE_NAME_FOR_TOKEN + " not found");
            }
            String tokenid = tokenString.substring(1 + tokenString.indexOf('='));
            return new Cookie(COOKIE_NAME_FOR_TOKEN, URLEncoder.encode(tokenid, "UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException("Could not encode token", ex);
        }
    }

    private void logout(SSOToken ssoToken) {
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

    class DefaultHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostName, SSLSession session) {
            //          System.out.println("Verify Host: " + hostName + " for peer host: " + session.getPeerHost() + " Port: " + session.getPeerPort());
            //          System.out.println("Cert: " + session.getPeerCertificates()[0]);
            return true;
        }

    }
}
