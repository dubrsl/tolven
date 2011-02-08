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
package org.tolven.connectors.passwordstore.ra.outbound;

import javax.resource.cci.ConnectionMetaData;
import javax.resource.spi.ConnectionEvent;

import org.tolven.connectors.passwordstore.api.PasswordStoreConnection;

public class PasswordStoreConnectionImpl implements PasswordStoreConnection {

    private PasswordStoreManagedConnectionImpl mc;

    public PasswordStoreConnectionImpl() {
    }

    public PasswordStoreConnectionImpl(PasswordStoreManagedConnectionImpl mc) {
        this.mc = mc;
    }

    public char[] getPassword(String alias) {
        return mc.getManagedConnectionFactory().getPassword(alias);
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
        mc.removePasswordStoreConnection(this);

        // Send a close event to the App Server
        mc.sendEvent(ConnectionEvent.CONNECTION_CLOSED, null, this);
        mc = null;
    }

    /**
     * Associates connection handle with new managed connection.
     *
     * @param newMc  new managed connection
     */

    public void associateConnection(PasswordStoreManagedConnectionImpl newMc) {
        checkIfValid();
        // dissociate handle from current managed connection
        mc.removePasswordStoreConnection(this);
        // associate handle with new managed connection
        newMc.addPasswordStoreConnection(this);
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
