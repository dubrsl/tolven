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
 * @version $Id: QueueKeyLocal.java 1817 2011-07-23 05:46:53Z joe.isaac $
 */
package org.tolven.key;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface QueueKeyLocal {

    public PublicKey getUserPublicKey(String queueId);

    public X509Certificate getUserX509Certificate(String queueId);

    public String getUserX509CertificateString(String queueId);

}
