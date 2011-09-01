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

import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;

import org.tolven.connectors.passwordstore.api.PasswordStoreConnection;
import org.tolven.connectors.passwordstore.api.PasswordStoreConnectionFactory;

//public class PasswordStoreConnectionFactoryImpl implements PasswordStoreConnectionFactory, Serializable, Referenceable, ObjectFactory {

/**
 * The connection factory implementation to the PasswordStore.
 * 
 * @author Joseph Isaac
 *
 */
public class PasswordStoreConnectionFactoryImpl implements PasswordStoreConnectionFactory {

    private ManagedConnectionFactory mcf;
    private ConnectionManager cm;
    private Reference reference;

    public PasswordStoreConnectionFactoryImpl(ManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }

    /**
     * Gets a connection to the PasswordStore.

     * @return PasswordStoreConnection
     */
    @Override
    public PasswordStoreConnection getConnection() {
        PasswordStoreConnection con = null;
        try {
            con = (PasswordStoreConnection) cm.allocateConnection(mcf, null);
        } catch (ResourceException ex) {
            throw new RuntimeException("Could not allocate a connection", ex);
        }
        return con;
    }

    /**
     * This method is declared in the javax.resource.Referenceable interface 
     * and should be implemented in order to support JNDI registration.
     *
     * @param reference  a Reference instance
     */

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    /**
     * This method is declared in the javax.naming.Referenceable interface 
     * and should be implemented in order to support JNDI registration.
     *
     * @return  a Reference instance
     */

    @Override
    public Reference getReference() {
        return reference;
    }
    /*
        @Override
        public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
            return new PasswordStoreConnectionFactoryImpl();
        }
    */
}
