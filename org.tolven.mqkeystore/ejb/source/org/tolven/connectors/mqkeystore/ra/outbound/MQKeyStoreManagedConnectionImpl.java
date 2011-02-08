/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package org.tolven.connectors.mqkeystore.ra.outbound;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.IllegalStateException;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.tolven.connectors.mqkeystore.api.MQKeyStoreConnection;

public class MQKeyStoreManagedConnectionImpl implements ManagedConnection {
    
    private MQKeyStoreManagedConnectionFactoryImpl mcf;
    private MQKeyStoreConnectionEventListener eventListener;
    private Set<MQKeyStoreConnection> connectionSet; // set of Server Connections
    private PrintWriter logWriter;
    private boolean destroyed;

    private PasswordCredential passCred = null;

    /**
     * Constructor.
     *
     * @param mcf  the ManagedConnectionFactory that created this instance
     * @param subject  security context as JAAS subject
     * @param cxRequestInfo  ConnectionRequestInfo instance
     */

    MQKeyStoreManagedConnectionImpl(MQKeyStoreManagedConnectionFactoryImpl mcf, Subject subject, ConnectionRequestInfo cxRequestInfo) {
        this.mcf = mcf;
        connectionSet = new HashSet<MQKeyStoreConnection>();
        eventListener = new MQKeyStoreConnectionEventListener(this);
    }

    /**
     * Creates a new connection represented by the
     * ManagedConnection instance. This connection handle is used by the 
     * application code to refer to the underlying physical connection.
     *
     * @param subject        security context as JAAS subject
     * @param cxRequestInfo  ConnectionRequestInfo instance
     *
     * @return   Connection instance representing the connection handle
     *
     * @exception ResourceException if the method fails to get a connection
     */

    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        checkIfDestroyed();
        MQKeyStoreConnection mqKeyStoreConnection = new MQKeyStoreConnectionImpl(this);
        addMQKeyStoreConnection(mqKeyStoreConnection);
        return mqKeyStoreConnection;
    }

    /**
     * Destroys the physical connection.
     *
     * @exception ResourceException if the method fails to destroy the 
     *                              connection
     */

    public void destroy() throws ResourceException {
        if (destroyed) {
            return;
        }
        destroyed = true;
        invalidateMQKeyStoreConnections();
    }

    /**
     * Initiates a cleanup of the client-specific state maintained by a 
     * ManagedConnection instance. The cleanup should invalidate all connection 
     * handles created using this ManagedConnection instance.
     *
     * @exception ResourceException  if the cleanup fails
     */

    public void cleanup() throws ResourceException {
        checkIfDestroyed();
        invalidateMQKeyStoreConnections();
    }

    private void invalidateMQKeyStoreConnections() {
        Iterator<MQKeyStoreConnection> it = connectionSet.iterator();
        while (it.hasNext()) {
            MQKeyStoreConnectionImpl mqKeyStoreConnection = (MQKeyStoreConnectionImpl) it.next();
            mqKeyStoreConnection.invalidate();
        }
        connectionSet.clear();
    }

    /**
     * Used by the container to change the association of an application-level
     * connection handle with a ManagedConnection instance. The container 
     * should find the right ManagedConnection instance and call the 
     * associateConnection method.
     *
     * @param connection  application-level connection handle
     *
     * @exception ResourceException  if the attempt to change the association
     *                               fails
     */

    public void associateConnection(Object connection) throws ResourceException {
        checkIfDestroyed();

        if (connection instanceof MQKeyStoreConnection) {
            MQKeyStoreConnectionImpl con = (MQKeyStoreConnectionImpl) connection;
            con.associateConnection(this);
        } else {
            throw new IllegalStateException("INVALID_CONNECTION");
        }
    }

    /**
     * Adds a connection event listener to the ManagedConnection instance. 
     * The registered ConnectionEventListener instances are notified of 
     * connection close and error events as well as local-transaction-related 
     * events on the Managed Connection.
     *
     * @param listener  a new ConnectionEventListener to be registered
     */

    public void addConnectionEventListener(ConnectionEventListener listener) {
        eventListener.addConnectorListener(listener);
    }

    /**
     * Removes an already registered connection event listener from the 
     * ManagedConnection instance.
     *
     * @param listener  already registered connection event listener to be 
     *                  removed
     */

    public void removeConnectionEventListener(ConnectionEventListener listener) {
        eventListener.removeConnectorListener(listener);
    }

    /**
     * Returns a javax.transaction.xa.XAresource instance. An application 
     * server enlists this XAResource instance with the Transaction Manager 
     * if the ManagedConnection instance is being used in a JTA transaction 
     * that is being coordinated by the Transaction Manager.
     *
     * Because this implementation does not support transactions, the method
     * throws an exception.
     *
     * @return   the XAResource instance
     *
     * @exception ResourceException  if transactions are not supported
     */

    public XAResource getXAResource() throws ResourceException {
        throw new NotSupportedException("NO_XATRANSACTION");
    }

    /**
     * Returns a javax.resource.spi.LocalTransaction instance. The 
     * LocalTransaction interface is used by the container to manage local 
     * transactions for a RM instance.
     *
     * Because this implementation does not support transactions, the method
     * throws an exception.
     *
     * @return   javax.resource.spi.LocalTransaction instance
     *
     * @exception ResourceException  if transactions are not supported
     */

    public javax.resource.spi.LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException("NO_TRANSACTION");
    }

    /**
     * Gets the metadata information for this connection's underlying EIS 
     * resource manager instance. The ManagedConnectionMetaData interface 
     * provides information about the underlying EIS instance associated with 
     * the ManagedConnection instance.
     *
     * @return ManagedConnectionMetaData  ManagedConnectionMetaData instance
     *
     * @exception ResourceException  if the metadata cannot be retrieved
     */

    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        checkIfDestroyed();
        return new ManagedConnectionMetaDataImpl(this);
    }

    /**
     * Sets the log writer for this ManagedConnection instance. 
     * The log writer is a character output stream to which all logging and 
     * tracing messages for this ManagedConnection instance will be printed.
     *
     * @param out  character output stream to be associated
     *
     * @exception ResourceException  if the method fails
     */

    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    /**
     * Gets the log writer for this ManagedConnection instance.
     *
     * @return   the character output stream associated with this 
     *           ManagedConnection instance
     *
     * @exception ResourceException  if the method fails
     */

    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    /**
     * Gets the user name of the user associated with the ManagedConnection 
     * instance.
     *
     * @return  the username for this connection
     */

    public String getUserName() {
        if (passCred != null)
            return passCred.getUserName();
        else
            return null;
    }

    /**
     * Gets the password for the user associated with the ManagedConnection 
     * instance.
     *
     * @return  the password for this connection
     */

    public PasswordCredential getPasswordCredential() {
        return passCred;
    }

    /**
     * Associate connection handle with the physical connection.
     *
     * @param mqKeyStoreConnection  connection handle
     */

    public void addMQKeyStoreConnection(MQKeyStoreConnection mqKeyStoreConnection) {
        connectionSet.add(mqKeyStoreConnection);
    }

    /**
     * Check validation of the physical connection.
     *
     * @exception ResourceException  if the connection has been destroyed
     */

    private void checkIfDestroyed() throws ResourceException {
        if (destroyed) {
            throw new IllegalStateException("DESTROYED_CONNECTION");
        }
    }

    public void removeMQKeyStoreConnection(MQKeyStoreConnection mqKeyStoreConnection) {
        connectionSet.remove(mqKeyStoreConnection);
    }

    /**
     * Checks validation of the physical connection.
     *
     * @return  true if the connection has been destroyed; false otherwise
     */

    boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Returns the ManagedConnectionFactory that created this instance of 
     * ManagedConnection.
     *
     * @return  the ManagedConnectionFactory for this connection
     */

    public MQKeyStoreManagedConnectionFactoryImpl getManagedConnectionFactory() {
        return this.mcf;
    }

    /**
     * Sends a connection event to the application server.
     *
     * @param eventType  the ConnectionEvent type
     * @param ex  exception indicating a connection-related error
     */

    public void sendEvent(int eventType, Exception ex) {
        eventListener.sendEvent(eventType, ex, null);
    }

    /**
     * Sends a connection event to the application server.
     *
     * @param eventType  the ConnectionEvent type
     * @param ex  exception indicating a connection-related error
     * @param connectionHandle  the connection handle associated with the 
     *                          ManagedConnection instance
     */

    public void sendEvent(int eventType, Exception ex, Object connectionHandle) {
        eventListener.sendEvent(eventType, ex, connectionHandle);
    }

}
