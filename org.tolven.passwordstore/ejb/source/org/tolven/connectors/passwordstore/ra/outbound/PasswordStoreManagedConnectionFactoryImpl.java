/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package org.tolven.connectors.passwordstore.ra.outbound;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.security.auth.Subject;

import org.tolven.connectors.passwordstore.PasswordStoreResourceAdapterImpl;

/**
 * An object of this class is a factory of both ManagedConnection and
 * connection factory instances.
 * This class supports connection pooling by defining methods for
 * matching and creating connections.
 * This class is implemented as a JavaBeans component.
 */

public class PasswordStoreManagedConnectionFactoryImpl implements ManagedConnectionFactory, ResourceAdapterAssociation {

    private PasswordStoreResourceAdapterImpl resourceAdapter;
    private transient PrintWriter out;
    private transient PropertyChangeSupport changes = new PropertyChangeSupport(this);

    //
    //  Properties
    //

    public PasswordStoreManagedConnectionFactoryImpl() {
    }

    /**
     * Creates a Connection Factory instance. The Connection Factory instance 
     * is initialized with a default ConnectionManager. In the non-managed 
     * scenario, the ConnectionManager is provided by the resource adapter.
     *
     * @return EIS-specific Connection Factory instance
     *
     * @exception ResourceException if the attempt to create a connection 
     *                              factory fails
     */

    public Object createConnectionFactory() throws ResourceException {
        ConnectionManager cm = new ConnectionManagerImpl();
        return new PasswordStoreConnectionFactoryImpl(this, cm);
    }

    /**
     * Creates a Connection Factory instance. The ConnectionFactory instance 
     * is initialized with the passed ConnectionManager. In the managed 
     * scenario, ConnectionManager is provided by the application server.
     *
     * @param cm ConnectionManager to be associated with created EIS 
     *                  connection factory instance
     *
     * @return    EIS-specific Connection Factory instance
     *
     * @exception ResourceException if the attempt to create a connection 
     *                              factory fails
     */

    public Object createConnectionFactory(ConnectionManager cm) throws ResourceException {
        return new PasswordStoreConnectionFactoryImpl(this, cm);
    }

    /**
     * ManagedConnectionFactory uses the security information (passed as 
     * Subject) and additional ConnectionRequestInfo (which is specific to 
     * ResourceAdapter and opaque to application server) to create this new 
     * connection.
     *
     * @param subject   caller's security information
     * @param requestInfo   additional resource adapter specific connection 
     *                        request information
     *
     * @return  ManagedConnection instance
     *
     * @exception ResourceException if the attempt to create a connection fails
     */

    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo requestInfo) throws ResourceException {
        return new PasswordStoreManagedConnectionImpl(this, subject, requestInfo);
    }

    /**
     * Returns a matched managed  connection from the candidate set of connections.
     * ManagedConnectionFactory uses the security info (as in Subject) and 
     * information provided through ConnectionRequestInfo and additional 
     * Resource Adapter specific criteria to do matching.
     * A MC that has the requested store is returned as a match
     *
     * @param connectionSet  candidate connection set
     * @param subject  caller's security information
     * @param cxRequestInfo  additional resource adapter specific connection 
     *                       request information
     *
     * @return ManagedConnection  if resource adapter finds an acceptable 
     *                            match, otherwise null
     *
     * @exception ResourceException if the match fails
     */

    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        /*
        PasswordCredential pc = getPasswordCredential(this, subject, cxRequestInfo);
        Iterator<ManagedConnection> it = connectionSet.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof PasswordStoreManagedConnectionImpl) {
                PasswordStoreManagedConnectionImpl mc = (PasswordStoreManagedConnectionImpl) obj;
                PasswordStoreManagedConnectionFactoryImpl mcf = mc.getManagedConnectionFactory();
                if (isPasswordCredentialEqual(mc.getPasswordCredential(), pc) && mcf.equals(this)) {
                    return mc;
                }
            }
        }
        */
        return (PasswordStoreManagedConnectionImpl) connectionSet.iterator().next();
    }

    /**
     * Sets the log writer for this ManagedConnectionFactory instance.
     * The log writer is a character output stream to which all logging and 
     * tracing messages for this ManagedConnectionfactory instance will be 
     * printed.
     *
     * @param out  an output stream for error logging and tracing
     *
     * @exception ResourceException if the method fails
     */

    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.out = out;
    }

    /**
     * Gets the log writer for this ManagedConnectionFactory instance.
     *
     * @return PrintWriter  an output stream for error logging and tracing
     *
     * @exception ResourceException if the method fails
     */

    public PrintWriter getLogWriter() throws ResourceException {
        return this.out;
    }

    /**
     * Returns the hash code for the ManagedConnectionFactory.
     *(Concatenation of the MCF property values)
     *
     * @return int  hash code for the ManagedConnectionFactory   
     */

    public int hashCode() {
        //The rule here is that if two objects have the same Id
        //i.e. they are equal and the .equals method returns true
        //     then the .hashCode method *must* return the same
        //     hash code for those two objects
        /*
                int hashcode = new String("").hashCode();

                if (userName != null)
                    hashcode += userName.hashCode();

                if (password != null)
                    hashcode += password.hashCode();

                if (serverName != null)
                    hashcode += serverName.hashCode();

                if (protocol != null)
                    hashcode += protocol.hashCode();

                if (folderName != null)
                    folderName += folderName.hashCode();
        */
        return super.hashCode();
    }

    /**
     * Check if this ManagedConnectionFactory is equal to another 
     * ManagedConnectionFactory.
     *
     * @param obj  another ManagedConnectionFactory
     *
     * @return boolean  true if the properties are the same
     */

    public boolean equals(Object obj) {
        /*
        if (obj != null) {
            if (obj instanceof PasswordStoreManagedConnectionFactoryImpl) {
                PasswordStoreManagedConnectionFactoryImpl other = (PasswordStoreManagedConnectionFactoryImpl) obj;

                                if (!userName.equals(other.getUserName()))
                                    return false;
                                if (!password.equals(other.getPassword()))
                                    return false;
                                if (!serverName.equals(other.getServerName()))
                                    return false;
                                if (!folderName.equals(other.getFolderName()))
                                    return false;
                                if (!protocol.equals(other.getProtocol()))
                                    return false;
                return obj.equals(other);
            }
        }
        */
        return super.equals(obj);
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return resourceAdapter;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter resourceAdapter) throws ResourceException {
        this.resourceAdapter = (PasswordStoreResourceAdapterImpl) resourceAdapter;

    }

    /**
     * Associate PropertyChangeListener with the ManagedConnectionFactory, 
     * in order to notify about properties changes. 
     *
     * @param lis  the PropertyChangeListener to be associated with the
     *             ManagedConnectionFactory
     */

    public void addPropertyChangeListener(PropertyChangeListener lis) {
        changes.addPropertyChangeListener(lis);
    }

    /**
     * Delete association of PropertyChangeListener with the 
     * ManagedConnectionFactory.
     *
     * @param lis  the PropertyChangeListener to be removed
     */

    public void removePropertyChangeListener(PropertyChangeListener lis) {
        changes.removePropertyChangeListener(lis);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.changes = new PropertyChangeSupport(this);
        this.out = null;
    }

    public char[] getPassword(String alias) {
        return resourceAdapter.getPassword(alias);
    }

}
