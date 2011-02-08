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

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;

import org.apache.log4j.Logger;

/**
 * This class logs into the JMSProvider using the username and the password is obtained from the start up keyStore using the passwordStoreAlias.
 * 
 * @author Joseph Isaac
 * <p>
<pre>
 tolvenJMS {
    &lt;authentication>
        &lt;login-module code="org.tolven.security.auth.DatabaseLoginModule" flag="required">
            &lt;module-option name="managedConnectionFactoryKey">"ManagedConnectionFactory"&lt;/module-option>
            &lt;module-option name="managedConnectionFactoryName">managedConnectionFactoryName&lt;/module-option>
            &lt;module-option name="serverId">serverId&lt;/module-option>
        &lt;/login-module>
    &lt;/authentication>
 };
 </pre>
 * <p>
 */

public class JMSLoginModule implements LoginModule {

    private static final String MANAGED_CONNECTION_FACTORY_KEY = "managedConnectionFactoryKey";
    private static final String MANAGED_CONNECTION_FACTORY_NAME = "managedConnectionFactoryName";
    public static final String SERVER_ID = "serverId";

    private Subject subject;
    private Subject callerSubject;
    private CallbackHandler callbackHandler;
    private String principalName;
    private char[] password;
    private Map<String, ?> options;

    private String managedConnectionFactoryKey;
    private String managedConnectionFactoryName;
    private String serverId;

    private List<String> mdbPrincipalNames;

    private Logger logger = Logger.getLogger(JMSLoginModule.class);

    private String getManagedConnectionFactoryKey() {
        return managedConnectionFactoryKey;
    }

    private String getManagedConnectionFactoryName() {
        return managedConnectionFactoryName;
    }

    private String getServerId() {
        return serverId;
    }

    private List<String> getMDBPrincipalNames() {
        if (mdbPrincipalNames == null) {
            mdbPrincipalNames = new ArrayList<String>();
            for (String mdbPrincipalName : new String[] {
                    "tolvenAdminApp",
                    "tolvenGen",
                    "tolvenInvitation",
                    "mdbuser" }) {
                mdbPrincipalNames.add(mdbPrincipalName);
            }
        }
        return mdbPrincipalNames;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        //logger.debug("initialize()");
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.options = options;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        checkRequiredModuleOptions();
        try {
            if (callbackHandler == null) {
                throw new LoginException("No CallbackHandler");
            }
            NameCallback nc = new NameCallback("User name: ");
            PasswordCallback pc = new PasswordCallback("Password: ", false);
            Callback[] callback = { nc, pc };
            callbackHandler.handle(callback);
            principalName = nc.getName();
            password = pc.getPassword();
            if (principalName == null) {
                logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ SUBJECT PRINCIPAL IS NULL");
            } else {
                logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ SUBJECT PRINCIPAL=" + principalName);
            }
            if (password == null) {
                logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ SUBJECT PASSWORD IS NULL");
            } else {
                logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ SUBJECT PASSWORD=" + new String(password));
            }
            try {
                callerSubject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
            } catch (PolicyContextException ex) {
                throw new RuntimeException("Could not get caller Subject from PolicyContext", ex);
            }
            if (callerSubject == null) {
                throw new IllegalStateException("No caller Subject found in PolicyContext");
            }
            if (principalName == null || password == null) {
                principalName = getCallerPrincipal();
                if (principalName == null) {
                    logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ CALLER SUBJECT PRINCIPAL IS NULL");
                } else {
                    logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ CALLER PRINCIPAL=" + principalName);
                }
                password = getCallerPassword();
                if (password == null) {
                    logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ CALLER SUBJECT PASSWORD IS NULL");
                } else {
                    logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ CALLER SUBJECT PASSWORD=" + new String(password));
                }
                if (password == null && getMDBPrincipalNames().contains(principalName)) {
                    logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ CALLER SUBJECT IS MDB");
                    password = PasswordStoreHolder.getInstance().getPassword(principalName);
                    if (password == null) {
                        password = principalName.toCharArray();
                    }
                    logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ MDB PASSWORD=" + new String(password));
                }
            } else {
                logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ CORRECT SUBJECT PRINCIPAL=" + principalName + ", PASSWORD=" + new String(password));
            }
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
            /*
             * Ensure there is only one Group called Roles to hold roles
             */
            for (Principal principal : callerSubject.getPrincipals()) {
                subject.getPrincipals().add(principal);
            }
            for (Object obj : callerSubject.getPublicCredentials()) {
                subject.getPublicCredentials().add(obj);
            }
            for (Object obj : callerSubject.getPrivateCredentials()) {
                subject.getPrivateCredentials().add(obj);
            }
            PasswordCredential credential = new PasswordCredential(principalName, password);
            subject.getPrivateCredentials().add(credential);
            for (MBeanServer server : MBeanServerFactory.findMBeanServer(null)) {
                if (server.getDefaultDomain() != null && server.getDefaultDomain().equals(getServerId())) {
                    ObjectName managedConnectionFactoryObject = new ObjectName(getManagedConnectionFactoryName());
                    ManagedConnectionFactory mcf = (ManagedConnectionFactory) server.getAttribute(managedConnectionFactoryObject, getManagedConnectionFactoryKey());
                    credential.setManagedConnectionFactory(mcf);
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
        callbackHandler = null;
        principalName = null;
        password = null;
        options = null;
        subject = null;
        callerSubject = null;
    }

    private String getCallerPrincipal() {
        //TODO: Assume one Principal at this time. Should the Principal be identified in the Subject or via ejbContext?
        Principal principal = null;
        for (Principal callerSubjectPrincipal : callerSubject.getPrincipals()) {
            if (!(callerSubjectPrincipal instanceof Group)) {
                principal = callerSubjectPrincipal;
                break;
            }
        }
        return principal.getName();
    }

    private char[] getCallerPassword() {
        Set<PasswordCredential> credentials = callerSubject.getPrivateCredentials(PasswordCredential.class);
        if (credentials.isEmpty()) {
            return null;
        } else {
            return credentials.iterator().next().getPassword();
        }
    }

}
