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
 * @author John Churin
 * @version $Id$
 */  

package org.tolven.restful.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class DefaultHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(String hostName, SSLSession session) {
//		System.out.println("Verify Host: " + hostName + " for peer host: " + session.getPeerHost() + " Port: " + session.getPeerPort());
//		System.out.println("Cert: " + session.getPeerCertificates()[0]);
		return true;
	}

}
