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
package org.tolven.connectors.mqkeystore.api;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * A connection to the MQKeyStore.
 * 
 * @author Joseph Isaac
 *
 */
public interface MQKeyStoreConnection {

    /**
     * Returns the default PrivateKey, which is equivalent to getPrivateKey(null)
     * 
     * @return
     */
    public PrivateKey getDefaultPrivateKey();

    /**
     * Returns a PrivateKey for a certificate
     * 
     * @param alias
     * @return
     */
    public PrivateKey getPrivateKey(X509Certificate certificate);

    /**
     * Returns default PublicKey, which is equivalent to getPublicKey(null)
     * 
     * @return
     */
    public PublicKey getDefaultPublicKey();

    /**
     * Returns a PublicKey for a certificate, after checking it validity as a queue processing certificate
     * 
     * @param alias
     * @return
     */
    public PublicKey getPublicKey(X509Certificate certificate);

    /**
     * Returns the default X509 Certificate, which is associated with the getDefaultPublicKey(), where the latter is equivalent to getPublicKey(null)
     * 
     * @return
     */
    public X509Certificate getDefaultX509Certificate();

    /**
     * Closes the connection.
     */
    public void close();
}
