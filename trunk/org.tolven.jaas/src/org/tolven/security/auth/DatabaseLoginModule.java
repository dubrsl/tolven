/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.security.auth;

import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * This class logs into the JMSProvider using the username and the password is obtained from the start up keyStore using the passwordStoreAlias.
 * 
 * @author Joseph Isaac
 * <p>
<pre>
 tolvenDB {
    &lt;authentication>
        &lt;login-module code="org.tolven.security.auth.DatabaseLoginModule" flag="required">
            &lt;module-option name = "username">username&lt;/module-option>
            &lt;module-option name = "alias">passwordStoreAlias&lt;/module-option>
            &lt;module-option name="managedConnectionFactoryKey">"ManagedConnectionFactory"&lt;/module-option>
            &lt;module-option name="managedConnectionFactoryName">managedConnectionFactoryName&lt;/module-option>
            &lt;module-option name="serverId">serverId&lt;/module-option>
        &lt;/login-module>
    &lt;/authentication>
 };
</pre>
 * <p>
 */
 
public class DatabaseLoginModule implements LoginModule {

    private static final String MANAGED_CONNECTION_FACTORY_KEY = "managedConnectionFactoryKey";
    private static final String MANAGED_CONNECTION_FACTORY_NAME = "managedConnectionFactoryName";
    public static final String SERVER_ID = "serverId";

    private Subject subject;
    private String principalName;
    private char[] password;
    private Map<String, ?> options;

    private String managedConnectionFactoryKey;
    private String managedConnectionFactoryName;
    private String serverId;

    private String getManagedConnectionFactoryKey() {
        return managedConnectionFactoryKey;
    }

    private String getManagedConnectionFactoryName() {
        return managedConnectionFactoryName;
    }

    private String getServerId() {
        return serverId;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        //logger.debug("initialize()");
        this.subject = subject;
        this.options = options;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        checkRequiredModuleOptions();
        principalName = (String) options.get("username");
        try {
            if (principalName == null) {
                throw new LoginException("module option username cannot be null");
            }
            String passwordStoreAlias = (String) options.get("passwordStoreAlias");
            if (passwordStoreAlias == null) {
                throw new LoginException("module option alias cannot be null");
            }
            password = PasswordStoreHolder.getInstance().getPassword(passwordStoreAlias);
            if (principalName == null) {
                throw new LoginException("null principalName not permitted");
            }
            if (password == null) {
                throw new LoginException("null password not permitted");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LoginException le = new LoginException("Authentication Failed for: '" + principalName + "': " + ex.getMessage());
            throw le;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    @Override
    public boolean commit() throws LoginException {
        //logger.debug("begin commit");
        try {
            for (MBeanServer server : MBeanServerFactory.findMBeanServer(null)) {
                if (server.getDefaultDomain() != null && server.getDefaultDomain().equals(getServerId())) {
                    ObjectName managedConnectionFactoryObject = new ObjectName(getManagedConnectionFactoryName());
                    ManagedConnectionFactory mcf = (ManagedConnectionFactory) server.getAttribute(managedConnectionFactoryObject, getManagedConnectionFactoryKey());
                    PasswordCredential cred = new PasswordCredential(principalName, password);
                    subject.getPrivateCredentials().add(cred);
                    cred.setManagedConnectionFactory(mcf);
                }
            }
            //logger.debug("login for " + subject);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new LoginException(ex.getMessage());
        }
        return true;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    @Override
    public boolean abort() throws LoginException {
        removeAllCredentials();
        return true;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    @Override
    public boolean logout() throws LoginException {
        //logger.debug("logout for " + subject);
        removeAllCredentials();
        return true;
    }

    /**
     * Check that all the required login module options are available
     * @throws LoginException
     */
    private void checkRequiredModuleOptions() throws LoginException {
        managedConnectionFactoryKey = (String) options.get(MANAGED_CONNECTION_FACTORY_KEY);
        if (managedConnectionFactoryKey == null) {
            throw new LoginException(getClass() + " requires the module option: " + MANAGED_CONNECTION_FACTORY_KEY);
        }
        managedConnectionFactoryName = (String) options.get(MANAGED_CONNECTION_FACTORY_NAME);
        if (managedConnectionFactoryName == null) {
            throw new LoginException(getClass() + " requires the module option: " + MANAGED_CONNECTION_FACTORY_NAME);
        }
        serverId = (String) options.get(SERVER_ID);
        if (serverId == null) {
            throw new LoginException(getClass() + " requires the module option: " + SERVER_ID);
        }
    }

    /**
     * Remove all security related information
     * @throws LoginException
     */
    private void removeAllCredentials() {
        principalName = null;
        password = null;
        options = null;
        subject = null;
    }

}
