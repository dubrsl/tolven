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
package org.tolven.core.bean;
import java.io.IOException;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.Scheme;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.impl.DefaultHttpParams;
import org.apache.http.impl.io.PlainSocketFactory;
import org.apache.http.io.SocketFactory;
import org.apache.http.message.HttpGet;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.tolven.logging.TolvenLogger;

public class TemplateGen {
	HttpRequestExecutor httpexecutor = null;
    HttpClientConnection conn = null;
	HttpHost host = null;		// eg localhost:8080
	String contextPath = null;	// eg "/Tolven"
	
	public TemplateGen( String hostName, int port, String contextPath) {
        SocketFactory socketfactory = PlainSocketFactory.getSocketFactory();
        // This is how we talk to the tolven web app, not the port it listens on which is specified in HttpHost below.
        Scheme.registerScheme("http", new Scheme("http", socketfactory, 80));

        HttpParams params = new DefaultHttpParams(null);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "Jakarta-HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);
        
        httpexecutor = new HttpRequestExecutor();
        httpexecutor.setParams(params);
        // Required protocol interceptors
        httpexecutor.addInterceptor(new RequestContent());
        httpexecutor.addInterceptor(new RequestTargetHost());
        // Recommended protocol interceptors
        httpexecutor.addInterceptor(new RequestConnControl());
        httpexecutor.addInterceptor(new RequestUserAgent());
        httpexecutor.addInterceptor(new RequestExpectContinue());
        // Now decide the host name and port we'll connect to.
		host = new HttpHost(hostName, port);
		this.contextPath = contextPath;
	}

	public void connect() {
        conn = new DefaultHttpClientConnection(host);
	}

	public void disconnect() {
        try {
        	if (conn!=null) {
        		conn.close();
        		conn = null;
        	}
		} catch (IOException e) {
		}
	}

	/**
	 * Connect to a page and return the result as 
	 * @param target
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public String expandTemplate(String target) throws IOException, HttpException {
		connect();
	        try {
                HttpGet request = new HttpGet(contextPath + target);
//                TolvenLogger.info("Request URI: " + request.getRequestLine().getUri(), TemplateGen.class);
                HttpResponse response = httpexecutor.execute(request, conn);
                TolvenLogger.info("Response: " + response.getStatusLine(), TemplateGen.class);
	        	disconnect();
    	        return EntityUtils.toString(response.getEntity());
	        } finally {
	        	disconnect();
	        }
    }
		
}
