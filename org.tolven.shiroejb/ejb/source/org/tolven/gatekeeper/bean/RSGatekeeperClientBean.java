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
 * @version $Id: RSGatekeeperClientBean.java 2365 2011-08-11 05:29:59Z joe.isaac $
 */
package org.tolven.gatekeeper.bean;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import org.tolven.gatekeeper.RSGatekeeperClientLocal;
import org.tolven.naming.TolvenContext;
import org.tolven.naming.WebContext;
import org.tolven.session.SecretKeyThreadLocal;
import org.tolven.session.TolvenSessionWrapperFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Stateless
@Local(RSGatekeeperClientLocal.class)
public class RSGatekeeperClientBean implements RSGatekeeperClientLocal {

    private class DefaultHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostName, SSLSession session) {
            //          System.out.println("Verify Host: " + hostName + " for peer host: " + session.getPeerHost() + " Port: " + session.getPeerPort());
            //          System.out.println("Cert: " + session.getPeerCertificates()[0]);
            return true;
        }

    }

    private static Client client;

    private Client getClient() {
        if (client == null) {
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getDefault();
            } catch (Exception ex) {
                throw new RuntimeException("Could not get default SSLContext", ex);
            }
            ClientConfig config = new DefaultClientConfig();
            config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(new DefaultHostnameVerifier(), sslContext));
            client = Client.create(config);
            client.setFollowRedirects(true);
        }
        return client;
    }
    
    private String getCookie(ClientResponse response) {
        String ssoCookieName = getTolvenContext().getSsoCookieName();
        for (Cookie cookie : response.getCookies()) {
            if (ssoCookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new RuntimeException("Cookie " + ssoCookieName + " not found");
    }

    private WebContext getRsWebContext() {
        String jndiName = "tolvenContext";
        try {
            InitialContext ictx = new InitialContext();
            TolvenContext tolvenContext = (TolvenContext) ictx.lookup(jndiName);
            return (WebContext) tolvenContext.getRsGatekeeperWebContext();
        } catch (Exception ex) {
            throw new RuntimeException("Could not lookup " + jndiName, ex);
        }
    }

    private Cookie getSessionCookie() {
        TolvenContext context = getTolvenContext();
        String sessionId = (String) TolvenSessionWrapperFactory.getInstance().getId();
        Cookie cookie = new Cookie(context.getSsoCookieName(), SecretKeyThreadLocal.getExtendedSessionId(sessionId), context.getSsoCookiePath(), context.getSsoCookieDomain());
        return cookie;
    }

    private TolvenContext getTolvenContext() {
        String jndiName = "tolvenContext";
        try {
            InitialContext ictx = new InitialContext();
            return (TolvenContext) ictx.lookup(jndiName);
        } catch (Exception ex) {
            throw new RuntimeException("Could not lookup " + jndiName, ex);
        }
    }

    /**
     * Callers of this method are required to have previously authenticated with the gatekeeper
     */
    @Override
    public WebResource getWebResource(String path) {
        /*
         * Add the existing SSOCookie to the WebResource request
         */
        return getWebResource(path, true);
    }

    private WebResource getWebResource(String path, boolean addSSOCookie) {
        WebContext webContext = getRsWebContext();
        WebResource webResource = getClient().resource(webContext.getRsInterface()).path(path);
        if (addSSOCookie) {
            /*
             * Add the existing SSOCookie to the WebResource request.
             */
            webResource.addFilter(new ClientFilter() {
                @Override
                public ClientResponse handle(ClientRequest clientRequest) throws ClientHandlerException {
                    Cookie sessionCookie = getSessionCookie();
                    clientRequest.getHeaders().putSingle("Cookie", sessionCookie.getName() + "=" + sessionCookie.getValue());
                    return getNext().handle(clientRequest);
                }
            });
        } else {
            /*
             * No SSOCookie added to the WebResource
             */
        }
        return webResource;
    }

    /**
     * User RESTful to log into the gatekeeper and return the extended session Id
     * @param username
     * @param password
     * @param realm
     * @return
     */
    @Override
    public String login(String username, char[] password, String realm) {
        WebContext webContext = getRsWebContext();
        /*
         * Do not attempt to add SSOCookie, because this is a request for a new login
         */
        WebResource webResource = getWebResource(webContext.getRsLoginPath(), false);
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("uid", username);
        formData.add("password", new String(password));
        formData.add("realm", realm);
        ClientResponse response = webResource.post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " " + username + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        return getCookie(response);
    }

    @Override
    public boolean verifyUserPassword(String uid, String realm, char[] password) {
        String principal = (String) TolvenSessionWrapperFactory.getInstance().getPrincipal();
        WebResource webResource = getWebResource("user/" + principal + "/user/" + uid + "/verifyPassword");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("userId", principal);
        formData.add("uid", uid);
        formData.add("uidPassword", new String(password));
        formData.add("realm", realm);
        ClientResponse response = webResource.post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " " + uid + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        return Boolean.parseBoolean(response.getEntity(String.class));
    }

}
