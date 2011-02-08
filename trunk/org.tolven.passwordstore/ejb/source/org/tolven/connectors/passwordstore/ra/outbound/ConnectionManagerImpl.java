/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package org.tolven.connectors.passwordstore.ra.outbound;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;

/**
 * The default ConnectionManager implementation for the non-managed scenario.
 * This provides a hook for a resource adapter to pass a connection
 * request to an application server.
 */

public class ConnectionManagerImpl implements ConnectionManager {

    public ConnectionManagerImpl() {
    }

    /**
     * The method allocateConnection is called by the resource adapter's 
     * connection factory instance. This lets the connection factory instance 
     * provided by the resource adapter pass a connection request to the 
     * ConnectionManager instance. The connectionRequestInfo parameter 
     * represents information specific to the resource adapter for handling 
     * the connection request.
     *
     * @param mcf	used by application server to delegate connection 
     *                  matching/creation
     * @param cxRequestInfo  connection request information
     *
     * @return  connection handle with an EIS specific connection interface
     *
     * @exception ResourceException if an error occurs
     */

    public Object allocateConnection(ManagedConnectionFactory mcf, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        ManagedConnection mc = mcf.createManagedConnection(null, cxRequestInfo);
        return mc.getConnection(null, cxRequestInfo);
    }
}
