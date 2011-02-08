/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package org.tolven.connectors.passwordstore.ra.outbound;

import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.IllegalStateException;
import javax.resource.spi.ManagedConnectionMetaData;


/**
 * The ManagedConnectionMetaData interface provides information about the 
 * underlying EIS instance associated with a ManagedConnection instance. An 
 * application server uses this information to get runtime information about 
 * a connected EIS instance.
 */

public class ManagedConnectionMetaDataImpl implements ManagedConnectionMetaData {
    private PasswordStoreManagedConnectionImpl mc;

    static Logger logger = Logger.getLogger("samples.connectors.mailconnector.ra.outbound", "samples.connectors.mailconnector.ra.outbound.LocalStrings");

    /**
     * Constructor.
     *
     * @param mc  the managed connection that created this instance of 
     *            ManagedConnectionMetaData
     */

    public ManagedConnectionMetaDataImpl(PasswordStoreManagedConnectionImpl mc) {
        logger.info("ManagedConnectionMetaDataImpl::Constructor");
        this.mc = mc;
    }

    /**
     * Returns the product name of the underlying EIS instance connected 
     * through the ManagedConnection.
     * 
     * @return  product name of the EIS instance
     */

    public String getEISProductName() throws ResourceException {
        String productName = null;

        // ToDo: Add service specific code here

        return productName;
    }

    /**
     * Returns the product version of the underlying EIS instance connected 
     * through the ManagedConnection.
     *
     * @return  product version of the EIS instance
     */

    public String getEISProductVersion() throws ResourceException {
        String productVersion = null;
        // ToDo: Add service specific code here

        return productVersion;
    }

    /**
     * Returns the maximum number of active concurrent connections that
     * an EIS instance can support across client processes. If an EIS
     * instance does not know about (or does not have) any such limit, it 
     * returns zero.
     *
     * @return  maximum number of active concurrent connections
     */

    public int getMaxConnections() throws ResourceException {
        int maxConnections = 0;

        // ToDo: Add service specific code here

        return maxConnections;
    }

    /**
     * Returns the name of the user associated with the ManagedConnection 
     * instance. The name corresponds to the resource principal under whose 
     * security context a connection to the EIS instance has been established.
     *
     * @return  name of the user
     */

    public String getUserName() throws ResourceException {
        if (mc.isDestroyed()) {
            throw new IllegalStateException("DESTROYED_CONNECTION");
        }
        return mc.getUserName();
    }
}
