/*
 * Copyright (C) 2010 Tolven Inc

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
 * @author <your name>
 * @version $Id$
 */

package org.tolven.restful.client;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RESTfulClient {

    public static final String TOLVEN_REALM = "tolven";
    public static final String COOKIE_NAME_FOR_TOKEN = "iPlanetDirectoryPro";

    private String userId;
    private char[] password;
    private String realm;
    private String authRestfulURL;
    private String appRestfulURL;
    private String token;
    private Cookie tokenCookie;

    private WebResource authWebResource;
    private WebResource appWebResource;

    private static Client client;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getRealm() {
        if (realm == null) {
            realm = System.getenv("TOLVEN_REALM");
            if (realm == null) {
                realm = TOLVEN_REALM;
            }
        }
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getAuthRestfulURL() {
        return authRestfulURL;
    }

    public void setAuthRestfulURL(String authRestfulURL) {
        this.authRestfulURL = authRestfulURL;
    }

    public String getAppRestfulURL() {
        return appRestfulURL;
    }

    public void setAppRestfulURL(String appRestfulURL) {
        this.appRestfulURL = appRestfulURL;
    }

    protected Client getClient() {
        if (client == null) {
            ClientConfig config = new DefaultClientConfig();
            try {
                config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(new DefaultHostnameVerifier(), SSLContext.getDefault()));
            } catch (Exception ex) {
                throw new RuntimeException("Could not include DefaultHostnameVerifier", ex);
            }
            client = Client.create(config);
            client.setFollowRedirects(true);
        }
        return client;
    }

    public void init(String userId, char[] password, String appRestfulURL, String authRestful) {
        this.userId = userId;
        this.password = password;
        this.appRestfulURL = appRestfulURL;
        this.authRestfulURL = authRestful;
    }

    public void init(RESTfulClient client) {
        setUserId(client.getUserId());
        setPassword(client.getPassword());
        setToken(client.getToken());
        setTokenCookie(client.getTokenCookie());
        setAuthWebResource(client.getAuthWebResource());
        setAppWebResource(client.getAppWebResource());
    }

    public WebResource getAuthWebResource() {
        if (authWebResource == null) {
            setAuthWebResource(getClient().resource(getAuthRestfulURL()));
        }
        return authWebResource;
    }

    public void setAuthWebResource(WebResource authWebResource) {
        this.authWebResource = authWebResource;
    }

    public WebResource getAppWebResource() {
        if (appWebResource == null) {
            setAppWebResource(getClient().resource(getAppRestfulURL()));
        }
        return appWebResource;
    }

    public void setAppWebResource(WebResource appWebResource) {
        this.appWebResource = appWebResource;
    }

    public WebResource getRoot() {
        return getAppWebResource();
    }

    protected Cookie getTokenCookie() {
        if (tokenCookie == null) {
            try {
                tokenCookie = new Cookie(COOKIE_NAME_FOR_TOKEN, URLEncoder.encode(getToken(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("Could not encode token", ex);
            }
            setTokenCookie(tokenCookie);
        }
        return tokenCookie;
    }

    public void setTokenCookie(Cookie tokenCookie) {
        this.tokenCookie = tokenCookie;
    }

    protected String getToken() {
        if (token == null) {
            WebResource login = getAuthWebResource().path("authenticate");
            MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
            if (getUserId() == null) {
                throw new RuntimeException("A userId must be supplied to get an SSO token");
            }
            formData.add("username", getUserId());
            if (getPassword() == null) {
                throw new RuntimeException("A password must be supplied to get an SSO token");
            }
            formData.add("password", new String(getPassword()));
            ClientResponse response = login.queryParam("uri", "realm=" + getRealm()).type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Could not authenticate " + getUserId() + " in realm: " + getRealm() + ". Error " + response.getStatus());
            }
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
                throw new RuntimeException("No token found for: " + getUserId());
            }
            String tokenid = tokenString.substring(1 + tokenString.indexOf('='));
            if (isTokenValid(tokenid)) {
                setToken(tokenid);
            } else {
                throw new RuntimeException("token invalid for: " + getUserId());
            }
        }
        return token;
    }

    protected void setToken(String token) {
        this.token = token;
    }

    protected boolean isTokenValid(String tokenid) {
        WebResource login = getAuthWebResource().path("isTokenValid");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("tokenid", tokenid);
        ClientResponse response = login.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Could not check token validity. " + "Error " + response.getStatus());
        }
        String resultString = response.getEntity(String.class);
        return (resultString.startsWith("boolean=true"));
    }

    public ClientResponse logout() {
        WebResource login = getAuthWebResource().path("logout");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("subjectid", getToken());
        ClientResponse response = login.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        return response;
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
