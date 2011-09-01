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
package org.tolven.connectors.mqkeystore.ra.outbound;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.resource.cci.ConnectionMetaData;
import javax.resource.spi.ConnectionEvent;

import org.tolven.connectors.mqkeystore.api.MQKeyStoreConnection;

public class MQKeyStoreConnectionImpl implements MQKeyStoreConnection {

    private MQKeyStoreManagedConnectionImpl mc;

    public MQKeyStoreConnectionImpl() {
    }

    public MQKeyStoreConnectionImpl(MQKeyStoreManagedConnectionImpl mc) {
        this.mc = mc;
    }

    /**
     * Returns the default PrivateKey, which is equivalent to getPrivateKey(null)
     * 
     * @return
     */
    public PrivateKey getDefaultPrivateKey() {
        return mc.getManagedConnectionFactory().getDefaultPrivateKey();
    }

    /**
     * Returns a PrivateKey for a certificate
     * 
     * @param alias
     * @return
     */
    public PrivateKey getPrivateKey(X509Certificate certificate) {
        return mc.getManagedConnectionFactory().getPrivateKey(certificate);
    }

    /**
     * Returns default PublicKey, which is equivalent to getPublicKey(null)
     * 
     * @return
     */
    public PublicKey getDefaultPublicKey() {
        return mc.getManagedConnectionFactory().getDefaultPublicKey();
    }

    /**
     * Returns a PublicKey for a certificate, after checking it validity as a queue processing certificate
     * 
     * @param alias
     * @return
     */
    public PublicKey getPublicKey(X509Certificate certificate) {
        return mc.getManagedConnectionFactory().getPublicKey(certificate);
    }

    /**
     * Returns the default X509 Certificate, which is associated with the getDefaultPublicKey(), where the latter is equivalent to getPublicKey(null)
     * 
     * @return
     */
    public X509Certificate getDefaultX509Certificate() {
        return mc.getManagedConnectionFactory().getDefaultX509Certificate();
    }

    /**
     * Returns a javax.resource.cci.LocalTransaction instance that enables a 
     * component to demarcate resource manager local transactions on the 
     * Connection.
     *
     * Because this implementation does not support transactions, the method
     * throws an exception.
     *
     * @return  a LocalTransaction instance
     */
    public javax.resource.cci.LocalTransaction getLocalTransaction() {
        throw new RuntimeException("NO_TRANSACTION");
    }

    /**
     * Returns the metadata for the ManagedConnection.
     *
     * @return  a ConnectionMetaData instance
     */
    public ConnectionMetaData getMetaData() {
        return new ConnectionMetaDataImpl(mc);
    }

    /**
     * Closes the connection.
     */
    public void close() {
        if (mc == null)
            return; // connection is already closed
        mc.removeMQKeyStoreConnection(this);

        // Send a close event to the App Server
        mc.sendEvent(ConnectionEvent.CONNECTION_CLOSED, null, this);
        mc = null;
    }

    /**
     * Associates connection handle with new managed connection.
     *
     * @param newMc  new managed connection
     */

    public void associateConnection(MQKeyStoreManagedConnectionImpl newMc) {
        checkIfValid();
        // dissociate handle from current managed connection
        mc.removeMQKeyStoreConnection(this);
        // associate handle with new managed connection
        newMc.addMQKeyStoreConnection(this);
        mc = newMc;
    }

    /**
     * Checks the validity of the physical connection to the EIS.
     */

    void checkIfValid() {
        if (mc == null) {
            throw new RuntimeException("INVALID_CONNECTION");
        }
    }

    /**
     * Sets the physical connection to the EIS as invalid.
     * The physical connection to the EIS cannot be used any more.
     */

    void invalidate() {
        mc = null;
    }

}
