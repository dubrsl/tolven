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
import java.net.URLEncoder;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.tolven.naming.DefaultTolvenContext;
import org.tolven.naming.JndiManager;
import org.tolven.naming.TolvenContext;
import org.tolven.naming.WebContext;
import org.tolven.plugin.TolvenPlugin;
import org.tolven.plugin.repository.bean.PluginPropertyDetail;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RESTfulClient {

    class DefaultHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostName, SSLSession session) {
            //          System.out.println("Verify Host: " + hostName + " for peer host: " + session.getPeerHost() + " Port: " + session.getPeerPort());
            //          System.out.println("Cert: " + session.getPeerCertificates()[0]);
            return true;
        }

    }

    public static final String TOLVEN_REALM = "tolven";
    private String userId;
    private char[] password;
    private String realm;

    private Cookie tokenCookie;
    private WebResource authWebResource;

    private WebResource appWebResource;

    private static Client client;

    private TolvenContext tolvenContext;

    public String getAppRestfulURL() {
        WebContext webContext = (WebContext) getTolvenContext().getWebContext("tolvenapi");
        return webContext.getRsInterface();
    }

    public WebResource getAppWebResource() {
        if (appWebResource == null) {
            setAppWebResource(getClient().resource(getAppRestfulURL()));
        }
        return appWebResource;
    }

    public WebResource getAuthWebResource() {
        if (authWebResource == null) {
            setAuthWebResource(getClient().resource(getGatekeeperRsInterface()));
        }
        return authWebResource;
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

    private String getGatekeeperRsInterface() {
        WebContext webContext = (WebContext) getTolvenContext().getRsGatekeeperWebContext();
        return webContext.getRsInterface();
    }

    private String getGatekeeperRsLoginPath() {
        WebContext webContext = (WebContext) getTolvenContext().getRsGatekeeperWebContext();
        return webContext.getRsLoginPath();
    }

    protected Cookie getOpenAMCookie(ClientResponse response) {
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
                throw new RuntimeException(getSSOCookieName() + " not found");
            }
            String tokenid = tokenString.substring(1 + tokenString.indexOf('='));
            return new Cookie(getSSOCookieName(), URLEncoder.encode(tokenid, "UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException("Could not encode token", ex);
        }
    }

    public char[] getPassword() {
        return password;
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

    public WebResource getRoot() {
        return getAppWebResource();
    }

    protected Cookie getShiroCookie(ClientResponse response) {
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(getSSOCookieName())) {
                return cookie;
            }
        }
        throw new RuntimeException(getSSOCookieName() + " not found");
    }

    public String getSSOCookieName() {
        return getTolvenContext().getSsoCookieName();
    }

    protected Cookie getTokenCookie() {
        if (tokenCookie == null) {
            tokenCookie = login();
            setTokenCookie(tokenCookie);
        }
        return tokenCookie;
    }

    private TolvenContext getTolvenContext() {
        if (tolvenContext == null) {
            PluginPropertyDetail tolvenContextPluginProperty = TolvenPlugin.getPluginProperty(JndiManager.TOLVEN_ID_REF);
            Properties jndiProperties = new Properties();
            for (PluginPropertyDetail property : tolvenContextPluginProperty.getProperty()) {
                jndiProperties.setProperty(property.getName(), property.getValue());
            }
            DefaultTolvenContext defaultTolvenContext = new DefaultTolvenContext();
            defaultTolvenContext.setMapping(jndiProperties);
            tolvenContext = defaultTolvenContext;
        }
        return tolvenContext;
    }

    public String getTolvenSecurityCode() {
        //TODO This needs to be part of the TolvenContext
        return "shiro";
    }

    public String getUserId() {
        return userId;
    }

    public void init(RESTfulClient client) {
        setUserId(client.getUserId());
        setPassword(client.getPassword());
        setTokenCookie(client.getTokenCookie());
        setAuthWebResource(client.getAuthWebResource());
        setAppWebResource(client.getAppWebResource());
    }

    public void init(String userId, char[] password) {
        this.userId = userId;
        this.password = password;
    }

    @Deprecated
    public void init(String userId, char[] password, String appRestfulURL, String authRestful) {
        this.userId = userId;
        this.password = password;
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

    protected Cookie login() {
        WebResource webResource = getAuthWebResource().path(getGatekeeperRsLoginPath());
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("username", getUserId());
        formData.add("password", new String(getPassword()));
        formData.add("realm", new String(getRealm()));
        ClientResponse response = webResource.post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " " + userId + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        if ("shiro".equals(getTolvenSecurityCode())) {
            return getShiroCookie(response);
        } else {
            return getOpenAMCookie(response);
        }
    }

    public ClientResponse logout() {
        WebResource login = getAuthWebResource().path("authenticate/logout");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("subjectid", getTokenCookie().getValue());
        ClientResponse response = login.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        return response;
    }

    @Deprecated
    public void setAppRestfulURL(String appRestfulURL) {
    }

    public void setAppWebResource(WebResource appWebResource) {
        this.appWebResource = appWebResource;
    }

    @Deprecated
    public void setAuthRestfulURL(String authRestfulURL) {
    }

    public void setAuthWebResource(WebResource authWebResource) {
        this.authWebResource = authWebResource;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setTokenCookie(Cookie tokenCookie) {
        this.tokenCookie = tokenCookie;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
