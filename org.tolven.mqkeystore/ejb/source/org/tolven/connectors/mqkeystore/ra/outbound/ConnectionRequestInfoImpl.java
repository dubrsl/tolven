/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package org.tolven.connectors.mqkeystore.ra.outbound;

import javax.resource.spi.*;
import java.lang.Object;

/**
 * This class implments the ConnectionRequestInfo interface, which enables a 
 * resource adapter to pass its own request-specific data structure across the 
 * connection request flow.
 */

public class ConnectionRequestInfoImpl implements ConnectionRequestInfo {
    private String userName = null;
    private String password = null;
    private String folderName = null;
    private String serverName = null;
    private String protocol = null;

    /**
     * Constructor.
     *
     * @param userName  user name
     * @param password  user password
     * @param folderName  folder name
     * @param serverName  server name
     * @param protocol  protocol
     */

    public ConnectionRequestInfoImpl(String userName, String password, String folderName, String serverName, String protocol) {
        this.userName = userName;
        this.password = password;
        this.folderName = folderName;
        this.serverName = serverName;
        this.protocol = protocol;
    }

    //
    //	Getter methods
    //

    /**
     * Returns the user name value.
     *
     * @return   String containing the user name
     */

    public String getUserName() {
        return userName;
    }

    /**
     * Returns the password value.
     *
     * @return   String containing the password
     */

    public String getPassword() {
        return password;
    }

    /**
     * Returns the server name value.
     *
     * @return   String containing the server name
     */

    public String getServerName() {
        return serverName;
    }

    /**
     * Returns the folder name value.
     *
     * @return   String containing the folder name
     */

    public String getFolderName() {
        return folderName;
    }

    /**
     * Returns the protocol value.
     *
     * @return   String containing the protocol
     */

    public String getProtocol() {
        return protocol;
    }

    /**
     * Checks whether this instance is equal to another.
     *
     * @param obj  other object
     *
     * @return  true if the two instances are equal, false otherwise
     */

    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (obj instanceof ConnectionRequestInfoImpl) {
                ConnectionRequestInfoImpl other = (ConnectionRequestInfoImpl) obj;

                equal = (this.userName).equals(other.userName) && (this.password).equals(other.password) && (this.serverName).equals(other.serverName) && (this.protocol).equals(other.protocol);
                //equal = Util.isEqual(this.userName, other.userName) &&
                //    	Util.isEqual(this.password, other.password) &&
                //    	Util.isEqual(this.serverName, other.serverName) &&
                //    	Util.isEqual(this.protocol, other.protocol);
            }
        }
        return equal;

    }

    /**
     * Returns the hashCode of the ConnectionRequestInfoImpl.
     *
     * @return  the hash code of this instance
     */

    public int hashCode() {
        int hashcode = new String("").hashCode();

        if (userName != null)
            hashcode += userName.hashCode();

        if (password != null)
            hashcode += password.hashCode();

        if (serverName != null)
            hashcode += serverName.hashCode();

        if (protocol != null)
            hashcode += protocol.hashCode();

        return hashcode;
    }
}
