/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package org.tolven.connectors.mqkeystore.ra.outbound;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionMetaData;


/**
 * This class provides information about an EIS instance connected through a 
 * Connection instance.
 */

public class ConnectionMetaDataImpl implements ConnectionMetaData {

    private MQKeyStoreManagedConnectionImpl mc;

    /**
     * Constructor.
     *
     * @param mc the physical connection of the JavaMailConnection that 
     *           created this instance of ConnectionMetaDataImpl
     */

    public ConnectionMetaDataImpl(MQKeyStoreManagedConnectionImpl mc) {
        this.mc = mc;
    }

    /**
     * Returns product name of the underlying EIS instance connected through 
     * the Connection that produced this metadata.
     *
     * @return product name of the EIS instance
     */

    public String getEISProductName() throws ResourceException {
        String productName = "JavaMail Connector";
        return productName;
    }

    /**
     * Returns product version of the underlying EIS instance.
     *
     * @return product version of the EIS instance
     */

    public String getEISProductVersion() throws ResourceException {
        String productVersion = "0.1";
        return productVersion;
    }

    /**
     * Returns the user name for an active connection known to the Mail 
     * Server.
     *
     * @return String representing the user name
     */

    public String getUserName() throws ResourceException {

        if (mc.isDestroyed()) {
            throw new ResourceException("DESTROYED_CONNECTION");
        }
        return mc.getUserName();
    }

    // Could return other connection info (serverName, etc.)
}
