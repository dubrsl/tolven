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
package org.tolven.test.jmeter;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TolvenPUTRequest extends AbstractJavaSamplerClient implements Serializable {

    public static final String DEFAULT_PROTOCOL = "${DEFAULT_HTTP_PROTOCOL}";
    public static final String DEFAULT_SAMPLE_LABEL = "TolvenPUTRequest";
    public static final String DEFAULT_TOKEN_NAME = "${SSOTOKEN_NAME}";
    public static final String DEFAULT_SERVERNAME_OR_IP_PROP = "${APP_HOST}";
    public static final String DEFAULT_PORT_PROP = "${APP_PORT}";
    public static final String DEFAULT_RESOURCEPATH_PROP = "${APP_RESTFUL_INTERFACE}";
    public static final String DEFAULT_FORMPARAMSXML_PROP = "<properties></properties>";
    public static final String DEFAULT_SSOTOKEN_ID_PROP = "${SSOTOKEN}";

    public static final String PROTOCOL_PROP = "protocol";
    public static final String SERVERNAME_OR_IP_PROP = "serverNameOrIP";
    public static final String PORT_PROP = "port";
    public static final String FORMPARAMS_XML_PROP = "formParamsXML";
    public static final String RESOURCEPATH_PROP = "resourcePath";
    public static final String SAMPLE_LABEL_PROP = "sampleLabel";
    public static final String SSOTOKEN_NAME_PROP = "ssoTokenName";
    public static final String SSOTOKEN_ID_PROP = "ssoTokenId";

    private String protocol;
    private String serverNameOrIP;
    private String port;
    private String resourcePath;
    private String formParamsXML;
    private String sampleLabel;
    private String ssoTokenName;
    private String ssoTokenId;

    private static Client client;

    public TolvenPUTRequest() {
    }

    private String getProtocol() {
        return protocol;
    }

    private String getServerNameOrIP() {
        return serverNameOrIP;
    }

    private String getPort() {
        return port;
    }

    private String getResourcePath() {
        return resourcePath;
    }

    private String getFormParamsXML() {
        return formParamsXML;
    }

    private String getSampleLabel() {
        return sampleLabel;
    }

    private String getSSOTokenName() {
        return ssoTokenName;
    }

    private String getSSOTokenId() {
        return ssoTokenId;
    }

    private Client getClient() {
        return client;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("protocol", DEFAULT_PROTOCOL);
        arguments.addArgument("serverNameOrIP", DEFAULT_SERVERNAME_OR_IP_PROP);
        arguments.addArgument("port", DEFAULT_PORT_PROP);
        arguments.addArgument("resourcePath", DEFAULT_RESOURCEPATH_PROP);
        arguments.addArgument("formParamsXML", DEFAULT_FORMPARAMSXML_PROP);
        arguments.addArgument("sampleLabel", DEFAULT_SAMPLE_LABEL);
        arguments.addArgument("ssoTokenName", DEFAULT_TOKEN_NAME);
        arguments.addArgument("ssoTokenId", DEFAULT_SSOTOKEN_ID_PROP);
        return arguments;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
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
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        protocol = context.getParameter("protocol");
        if (protocol == null || protocol.length() == 0) {
            throw new RuntimeException("protocol is required");
        }
        serverNameOrIP = context.getParameter("serverNameOrIP");
        if (serverNameOrIP == null || serverNameOrIP.length() == 0) {
            throw new RuntimeException("serverNameOrIP is required");
        }
        port = context.getParameter("port");
        if (port == null || port.length() == 0) {
            throw new RuntimeException("port is required");
        }
        resourcePath = context.getParameter("resourcePath");
        if (resourcePath == null || resourcePath.length() == 0) {
            throw new RuntimeException("resourcePath is required");
        }
        formParamsXML = context.getParameter("formParamsXML");
        if (formParamsXML == null || formParamsXML.length() == 0) {
            throw new RuntimeException("formParamsXML is required");
        }
        sampleLabel = context.getParameter("sampleLabel");
        if (sampleLabel == null || sampleLabel.length() == 0) {
            throw new RuntimeException("sampleLabel is required");
        }
        ssoTokenName = context.getParameter("ssoTokenName");
        if (ssoTokenName == null || ssoTokenName.length() == 0) {
            throw new RuntimeException("ssoTokenName is required");
        }
        ssoTokenId = context.getParameter("ssoTokenId");
        if (ssoTokenId == null || ssoTokenId.length() == 0) {
            throw new RuntimeException("ssoTokenId is required");
        }
        SampleResult results = new SampleResult();
        results.setSampleLabel(getSampleLabel());
        try {
            results.setDataType(SampleResult.TEXT);
            results.sampleStart();
            ClientResponse clientResponse = getClientResponse(results);
            if (clientResponse.getStatus() != 204) {
                results.setResponseCode(String.valueOf(clientResponse.getStatus()));
                results.setResponseMessage("Failed to: " + clientResponse.toString());
                results.setSuccessful(false);
                results.setResponseData(clientResponse.getEntity(String.class), null);
                return results;
            }
            if (clientResponse.getLength() > 0) {
                results.setBytes(clientResponse.getLength());
            }
            if (clientResponse.getType() != null) {
                results.setContentType(clientResponse.getType().toString());
            }
            results.setResponseCode(String.valueOf(clientResponse.getStatus()));
            MultivaluedMap<String, String> headers = clientResponse.getHeaders();
            StringBuffer buff = new StringBuffer();
            for (String name : headers.keySet()) {
                buff.append(name);
                buff.append(": ");
                buff.append(headers.getFirst(name));
                buff.append("\n");
            }
            results.setResponseHeaders(buff.toString());
            if (clientResponse.getStatus() == 200 && clientResponse.hasEntity()) {
                results.setResponseData(clientResponse.getEntity(Object.class).toString(), null);
            }
            results.setSuccessful(true);
        } catch (Exception ex) {
            results.setResponseCode("Exception");
            results.setResponseMessage(ex.getMessage());
            results.setSuccessful(false);
            StringWriter writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            results.setResponseData(writer.toString(), null);
            return results;
        } finally {
            results.sampleEnd();
        }
        return results;
    }

    private ClientResponse getClientResponse(SampleResult results) {
        StringBuffer buff = new StringBuffer();
        String resource = getProtocol() + "://" + getServerNameOrIP() + ":" + getPort();
        WebResource webResource = getClient().resource(resource).path(getResourcePath());
        buff.append(webResource);
        buff.append("\n\nPUT data:\n");
        Properties properties = getProperties();
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        for (Object obj : properties.keySet()) {
            String name = (String) obj;
            String value = properties.getProperty(name);
            formData.putSingle(name, value);
            buff.append(name);
            buff.append("=");
            buff.append(value);
            buff.append("\n");
        }
        Cookie ssoTokenCookie = null;
        try {
            ssoTokenCookie = new Cookie(getSSOTokenName(), URLEncoder.encode(getSSOTokenId(), "UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException("Could not encode ssotoken", ex);
        }
        buff.append("\n\nCookie data:\n");
        buff.append(ssoTokenCookie);
        buff.append("\n\n");
        try {
            ClientResponse clientResponse =  webResource.cookie(ssoTokenCookie).put(ClientResponse.class, formData);
            results.setSamplerData(buff.toString());
            return clientResponse;
        } catch (Exception ex) {
            throw new RuntimeException("Could not process resource: " + resource, ex);
        }
    }

    private Properties getProperties() {
        ByteArrayInputStream bais = new ByteArrayInputStream(getFormParamsXML().getBytes());
        Properties properties = new Properties();
        try {
            properties.loadFromXML(bais);
            return properties;
        } catch (Exception ex) {
            throw new RuntimeException("Could not read formParams property as Java properties XML String", ex);
        }
    }

    private class DefaultHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostName, SSLSession session) {
            //          System.out.println("Verify Host: " + hostName + " for peer host: " + session.getPeerHost() + " Port: " + session.getPeerPort());
            //          System.out.println("Cert: " + session.getPeerCertificates()[0]);
            return true;
        }

    }
}
