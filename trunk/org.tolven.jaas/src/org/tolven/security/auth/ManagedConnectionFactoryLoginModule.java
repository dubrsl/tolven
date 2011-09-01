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
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.log4j.Logger;

/**
 * This class works in conjuction with other login modules in order to supply a resource with the users credentials.
 * 
 * @author Joseph Isaac
 * <p>
<pre>
 tolvenLDAP {
    &lt;authentication>
        &lt;module-option name="password-stacking">useFirstPass&lt;/module-option>
        &lt;module-option name="managedConnectionFactoryKey">"ManagedConnectionFactory"&lt;/module-option>
        &lt;module-option name="managedConnectionFactoryName">server.jca:service=TxCM,name=JmsXA&lt;/module-option>
        &lt;module-option name="serverId">serverId&lt;/module-option>
        &lt;/login-module>
    &lt;/authentication>
 };
 </pre>
 * <p>
 */

/*
 * Note that it is important that this class does not make use of EJB beans during the intial login process, since
 * it is responsible for authenticating, and would be caught in a recursive loop.
 */
public class ManagedConnectionFactoryLoginModule implements LoginModule {

    private static final String USE_FIRST_PASS = "useFirstPass";
    private static final String PASSWORD_STACKING = "password-stacking";
    private static final String MANAGED_CONNECTION_FACTORY_KEY = "managedConnectionFactoryKey";
    private static final String MANAGED_CONNECTION_FACTORY_NAME = "managedConnectionFactoryName";
    public static final String SERVER_ID = "serverId";

    private Subject subject;
    private CallbackHandler callbackHandler;
    private String principalName;
    private char[] password;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    private boolean useFirstPass;
    private String managedConnectionFactoryKey;
    private String managedConnectionFactoryName;
    private String serverId;
    
    private Logger logger = Logger.getLogger(ManagedConnectionFactoryLoginModule.class);

    private boolean useFirstPass() {
        return useFirstPass;
    }

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
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        //logger.debug("begin login");
        checkRequiredModuleOptions();
        try {
            if (useFirstPass()) {
                principalName = (String) sharedState.get("javax.security.auth.login.name");
                Object obj = sharedState.get("javax.security.auth.login.password");
                if (obj instanceof char[]) {
                    password = (char[]) obj;
                } else if (obj instanceof String) {
                    password = ((String) obj).toCharArray();
                }
            }
            if (principalName == null || password == null) {
                /*
                 * If either the principalName or password is null, then even with useFirstPass, use the callbackHandler
                 */
                if (callbackHandler == null) {
                    throw new LoginException("No CallbackHandler");
                }
                NameCallback nc = new NameCallback("User name: ");
                PasswordCallback pc = new PasswordCallback("Password: ", false);
                Callback[] callback = { nc, pc };
                callbackHandler.handle(callback);
                principalName = nc.getName();
                password = pc.getPassword();
            }
            /*
             * Safety Check
             */
            if (principalName == null) {
                throw new LoginException("null principalName not permitted");
            }
            if (password == null) {
                throw new LoginException("null password not permitted");
            }
        } catch (UnsupportedCallbackException e) {
            LoginException le = new LoginException("CallbackHandler does not support: " + e.getCallback() + ": " + e.getMessage());
            throw le;
        } catch (Exception e) {
            LoginException le = new LoginException("Authentication Failed: " + e.getMessage());
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
        String passwordStacking = (String) options.get(PASSWORD_STACKING);
        useFirstPass = passwordStacking != null && USE_FIRST_PASS.equals(passwordStacking);
        managedConnectionFactoryKey = (String) options.get(MANAGED_CONNECTION_FACTORY_KEY);
        if (managedConnectionFactoryKey == null) {
            throw new LoginException("The ManagedConnectionFactoryLoginModule requires the module option: " + MANAGED_CONNECTION_FACTORY_KEY);
        }
        managedConnectionFactoryName = (String) options.get(MANAGED_CONNECTION_FACTORY_NAME);
        if (managedConnectionFactoryName == null) {
            throw new LoginException("The ManagedConnectionFactoryLoginModule requires the module option: " + MANAGED_CONNECTION_FACTORY_NAME);
        }
        serverId = (String) options.get(SERVER_ID);
        if (serverId == null) {
            throw new LoginException("The ManagedConnectionFactoryLoginModule requires the module option: " + SERVER_ID);
        }
    }

    /**
     * Remove all security related information
     * @throws LoginException
     */
    private void removeAllCredentials() {
        callbackHandler = null;
        principalName = null;
        password = null;
        sharedState = null;
        options = null;
        subject = null;
    }

}
