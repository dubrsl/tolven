/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.client.examples.ws.echo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.namespace.QName;

import org.tolven.client.examples.ws.common.HeaderHandlerResolver;
import org.tolven.client.examples.ws.echo.jaxws.Echo;
import org.tolven.client.examples.ws.echo.jaxws.EchoService;

/**
 * 
 * @author Joseph Isaac
 * 
 * A test class to look up a simple web service called EchoService and send a string which is echoed back.
 * 
 * This is an example class, and when first checked out of cvs will have compiler errors until the generated
 * classes it imports have been generated. These are generated using the generate-ws-clients ant target in
 * tolvenWSClient/build.xml.
 * 
 *
 */
public class EchoWebServiceClient {
    
    private String username;
    private char[] password;
    private String wsdl;
    private int expiresInSeconds;

    public EchoWebServiceClient(String username, char[] password, String wsdl) {
        this(username, password, wsdl, 60);
    }

    public EchoWebServiceClient(String username, char[] password, String wsdl, int expiresInSeconds) {
        this.username = username;
        this.password = password;
        this.wsdl = wsdl;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getWsdl() {
        return wsdl;
    }

    public void setWsdl(String wsdl) {
        this.wsdl = wsdl;
    }

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(int expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public String echo() {
        String string = "Is there anybody out there?";
        return echo(string);
    }

    public String echo(String string) {
        System.out.println("Starting EchoClient");
        URL url = null;
        try {
            url = new URL(getWsdl());
        } catch (Exception ex) {
            throw new RuntimeException("Could not convert to URL: " + getWsdl(), ex);
        }
        EchoService service = new EchoService(url, new QName("http://tolven.org/echo", "EchoService"));
        HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver(getUsername(), getPassword(), getExpiresInSeconds());
        service.setHandlerResolver(handlerResolver);
        Echo port = service.getEchoPort();
        System.out.println("Sending: " + string);
        String echoedString = port.echo(string);
        System.out.println("Received: \"" + echoedString + "\"");
        System.out.println("End EchoClient");
        return echoedString;
    }

    /**
     * Note that both EchoService and Echo are auto-generated classes using the wsimport tool.
     * A typical invokation of this class would be:
     * 
     * java -Djavax.net.ssl.keyStore=keyStoreFile -Djavax.net.ssl.trustStore=trustStoreFile org.tolven.client.examples.ws.echo.EchoWebServiceClient http://dev.able.com:8080/ws/EchoService?wsdl
     * Note that the common name in the wsdl is checked against that returned from the appserver's webserver certificate, and since there is no oppotunity to veto, like in a browser,
     * it must match. One way to do this is to temporarily add the following entry to your hosts file
     * <pre>
     * 127.0.0.1 dev.able.com
     * </pre>
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java -Djavax.net.ssl.keyStore=keyStoreFile -Djavax.net.ssl.keyStorePassword=tolven -Djavax.net.ssl.trustStore=trustStoreFile org.tolven.client.examples.ws.echo.EchoWebServiceClient https://dev.able.com:8443/ws/EchoService?wsdl");
            System.exit(1);
        }
        String wsdl = args[0];
        System.out.print("Please enter username: ");
        String username = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Please enter user password: ");
        char[] password = System.console().readPassword();
        EchoWebServiceClient client = new EchoWebServiceClient(username, password, wsdl, 60);
        client.echo();
    }

}