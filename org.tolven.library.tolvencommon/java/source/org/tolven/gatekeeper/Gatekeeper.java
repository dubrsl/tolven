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
 */
package org.tolven.gatekeeper;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface Gatekeeper {

    public PublicKey getUserPublicKey(String uid, String realm);

    public X509Certificate getUserX509Certificate(String uid, String realm);

    public String getUserX509CertificateString(String uid, String realm);

}
