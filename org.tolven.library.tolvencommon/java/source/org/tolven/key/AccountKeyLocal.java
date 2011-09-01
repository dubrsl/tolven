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
 * @version $Id: AccountKeyLocal.java 1790 2011-07-22 20:42:40Z joe.isaac $
 */
package org.tolven.key;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface AccountKeyLocal {

    public X509Certificate getX509Certificate(long accountId);

    public String getX509CertificateString(long accountId);

    public PublicKey getPublicKey(long accountId);

}
