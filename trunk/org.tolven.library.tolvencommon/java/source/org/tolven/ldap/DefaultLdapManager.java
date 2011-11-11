/*
 *  Copyright (C) 2011 Tolven Inc 
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
package org.tolven.ldap;

import java.security.KeyStore;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NamingSecurityException;
import javax.naming.NoPermissionException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;
import org.tolven.exeption.GatekeeperAuthenticationException;
import org.tolven.exeption.GatekeeperAuthorizationException;
import org.tolven.security.cert.CertificateHelper;

/**
 * 
 * @author John Churin
 *
 */
public class DefaultLdapManager implements LdapManager {

    public static final String PASSWORD_EXPIRED_OID = "2.16.840.1.113730.3.4.4";
    public static final String PASSWORD_EXPIRING_OID = "2.16.840.1.113730.3.4.5";
    public static final String PASSWORD_POLICY_OID = "1.3.6.1.4.1.42.2.27.8.5.1";
    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";

    private CertificateHelper certificateHelper;
    private Hashtable<String, String> environment;
    private LdapContext ldapContext;
    private Logger logger = Logger.getLogger(DefaultLdapManager.class);
    private Control[] responseControls;

    /**
     * Connect to admin account
     * @throws NamingException
     */
    public void adminConnect() throws NamingException {
        connect(getUserDN(), getUserPassword());
    }

    public void adminDisplayConfigAttributes(String cn) {
        try {
            adminConnect();
            displayAttributes(cn);
        } catch (NamingException e) {
            logger.debug("AttributeDisplay failed for cn " + cn + " " + e.getMessage());
        } finally {
            disconnect();
        }

    }

    public void adminSetup() {
        try {
            connect("cn=Directory Manager", "password".toCharArray());
            Attributes domainAttrs = new BasicAttributes();
            domainAttrs.put(new BasicAttribute("objectClass", "organizationalunit"));
            //			domainAttrs.put(new BasicAttribute("ou", "testpeople"));
            getLdapContext().createSubcontext("ou=testpeople,dc=tolven,dc=com", domainAttrs);
            //			Attributes attrs = new BasicAttributes();
            //			attrs.put(new BasicAttribute("objectClass", "organizationalunit"));
            //			domainCtx.createSubcontext("ou=people", attrs);

        } catch (NamingException e) {
            throw new RuntimeException("Setup error ", e);
        } finally {
            disconnect();
        }
    }

    /**
     * Change password of user given by userDN
     * 
     * @param newPassword
     */
    @Override
    public void changePassword(char[] newPassword) {
        changePassword(getUserDN(), getUserPassword(), newPassword);
    }

    /**
     * User given by getUserDN() changes password or the parameter userDN
     * 
     * @param userDN
     * @param oldPassword
     * @param newPassword
     */
    @Override
    public void changePassword(String userDN, char[] oldPassword, char[] newPassword) {
        try {
            // Prepare to get reason for change failure, if needed
            Control[] controls = new Control[] { new BasicControl(PASSWORD_POLICY_OID) };
            getLdapContext().setRequestControls(controls);
            // Setup extended operation
            PasswordChangeRequest req = new PasswordChangeRequest(userDN, newPassword);
            getLdapContext().extendedOperation(req);
            Attributes attrs = getAttributes(userDN, new String[] { "userPKCS12" });
            Attribute attr = attrs.get("userPKCS12");
            if (attr != null) {
                byte[] newBytes = null;
                try {
                    byte[] oldBytes = (byte[]) attr.get();
                    KeyStore keyStore = CertificateHelper.getKeyStore(oldBytes, oldPassword);
                    CertificateHelper.changeKeyStorePassword(keyStore, oldPassword, newPassword);
                    newBytes = CertificateHelper.toByteArray(keyStore, newPassword);
                } catch (Exception ex) {
                    throw new GatekeeperAuthorizationException("change password for: " + userDN, getUserDN(), getRealm(), ex);
                }
                attrs.put("userPKCS12", newBytes);
                getLdapContext().modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, attrs);
            }
            setUserPassword(newPassword);
            logger.debug(getUserDN() + " changed password for " + userDN + " in realm: " + getRealm());
        } catch (GatekeeperAuthorizationException ex) {
            throw ex;
        } catch (AuthenticationException ex) {
            throw new GatekeeperAuthenticationException("Change password for:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NoPermissionException ex) {
            throw new GatekeeperAuthorizationException("Change password for:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NamingException ex) {
            throw new RuntimeException("Password change failed: " + userDN, ex);
        }
    }

    /**
     * Check password of user given by getUserDN()
     * 
     */
    @Override
    public void checkPassword() {
        checkPassword(getUserDN(), getUserPassword());
    }

    /**
     * User given by getUserDN() checks password of the user given by parameter userDN
     * 
     * @param userDN
     * @param password
     */
    @Override
    public void checkPassword(String userDN, char[] password) {
        try {
            logger.debug(getUserDN() + " checking password for: " + userDN + " at " + getProviderURL());
            //getLdapContext() authenticates the user.
            LdapContext ctx = getLdapContext();
            //The controls are saved and apply to the authenticated user
            setResponseControls(ctx.getResponseControls());
            logger.debug(getUserDN() + " obtained successful password check for " + userDN + " in realm: " + getRealm());
        } catch (AuthenticationException ex) {
            throw new GatekeeperAuthenticationException("Check password for:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NoPermissionException ex) {
            throw new GatekeeperAuthorizationException("Change password for:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NamingException ex) {
            throw new RuntimeException("Exception in password check for user: " + userDN + " ", ex);
        }
    }

    /**
     * Connect to LDAP object using the supplied URL and SSL properties 
     * <ul>
     * <li>-Djavax.net.ssl.keyStore=keystore.jks</li>
     * <li>-Djavax.net.ssl.trustStore=cacerts.jks</li>
     * <li>-Djavax.net.ssl.keyStorePassword=tolven</li>
     * </ul>
     * @param userObject Specifies the DN of the object to connect to
     * @param password The (user's) password in plan text
     * @throws NamingException
     */
    public void connect(String userObject, char[] password) throws NamingException {
        logger.debug("Connecting to " + userObject + " at " + getProviderURL());
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getProviderURL());
        // env.put(Context.SECURITY_PROTOCOL, "ssl");

        // env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userObject);
        env.put(Context.SECURITY_CREDENTIALS, new String(password));

        // Create the initial context
        //		Control[] controls = new Control[]{new PasswordPolicyRequestControl(),new PasswordExpirationWarningControl()};
        //		ctx.setRequestControls(controls);
        setLdapContext(new InitialLdapContext(env, null));
        //		ctx = new InitialLdapContext(env, controls);
        setResponseControls(getLdapContext().getResponseControls());
        logger.debug("Connected to " + userObject + " at " + getProviderURL());
    }

    /**
     * User given by getUserDN() creates user given by parameter userDN and userAttributes.
     * A generated password is returned if the userPassword is null, otherwise null is returned
     * 
     * @param userDN
     * @param userPassword
     * @param userAttributes
     * @return
     */
    public char[] createUser(String userDN, char[] userPassword, Attributes userAttributes) {
        try {
            logger.debug(getUserDN() + " creating user: " + userDN + " in realm: " + getRealm() + " at " + getProviderURL());
            Attributes attrs = new BasicAttributes(false);
            NamingEnumeration<String> namingEnum = userAttributes.getIDs();
            String attrID = null;
            while (namingEnum.hasMoreElements()) {
                attrID = namingEnum.next();
                Attribute attr = userAttributes.get(attrID);
                if (!"userPassword".equalsIgnoreCase(attrID)) {
                    /*
                     * Do not send the SSHA of the password, since the password is handled by the PasswordChangeRequest
                     */
                    attrs.put(attr);
                }
            }
            getLdapContext().createSubcontext(userDN, userAttributes);
            PasswordChangeRequest req = new PasswordChangeRequest(userDN, userPassword);
            getLdapContext().extendedOperation(req);
            char[] generatedPassword = null;
            if (userPassword == null) {
                generatedPassword = req.getNewPassword();
                userPassword = generatedPassword;
                logger.debug(getUserDN() + " successfully generated password for user: " + userDN + " in realm: " + getRealm());
            }
            if (userAttributes.get("userPKCS12") == null) {
                /*
                 * TODO gatekeeper needs access to a bean like TolvenProperties, rather than use System.getProperty() directly
                 */
                String userKeysOptional = System.getProperty("tolven.security.user.keysOptional");
                if (Boolean.parseBoolean(userKeysOptional)) {
                    logger.info("tolven.security.user.keysOptional=true, so no user credentials will be generated for " + userDN);
                } else {
                    Attributes certAttrs = generateNewUserPKCS12Attributes(userDN, userPassword);
                    getLdapContext().modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, certAttrs);
                    logger.info(getUserDN() + " generated userPKCS12 for " + userDN + " in realm: " + getRealm());
                }
            }
            logger.debug(getUserDN() + " successfully created user: " + userDN + " in realm: " + getRealm());
            //Only return generated passwords
            return generatedPassword;
        } catch (AuthenticationException ex) {
            throw new GatekeeperAuthenticationException("Create user:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NoPermissionException ex) {
            throw new GatekeeperAuthorizationException("Create user:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NamingException ex) {
            throw new RuntimeException(getUserDN() + " got exception in password check for user: " + userDN + " ", ex);
        }
    }

    /**
     * Disconnect from LDAP
     */
    @Override
    public void disconnect() {
        logger.debug(getUserDN() + " disconnect from " + getProviderURL());
        try {
            if (getLdapContext() != null) {
                getLdapContext().close();
            }
        } catch (Exception e) {
            logger.debug("Ignoring Ldap context close exception " + e.getMessage());
        } finally {
            setLdapContext(null);
        }
    }

    /**
     * Display all attributes from the specified LDAP DN. The connection must already be open.
     * @param userObject
     * @throws NamingException
     */
    public void displayAttributes(String userObject) throws NamingException {
        Attributes attrs = getLdapContext().getAttributes(userObject);
        //		Attributes attrs = ctx.getAttributes(userObject, attrIDs);
        setResponseControls(getLdapContext().getResponseControls());
        NamingEnumeration<? extends Attribute> ae = attrs.getAll();
        logger.debug(" Attributes ");
        while (ae.hasMore()) {
            Attribute attr = (Attribute) ae.next();
            if (attr.get(0) instanceof byte[]) {
                logger.debug(attr.getID() + "=" + new String((byte[]) attr.get(0)) + ", ");
            } else {
                logger.debug(attr.getID() + "=" + attr.get(0) + ", ");
            }
        }
    }

    /**
     * If the connection is still floating around when we destroy this object, then disconnect now.
     */
    protected void finalize() {
        if (getLdapContext() != null) {
            disconnect();
        }
    }

    private Attributes generateNewUserPKCS12Attributes(String username, char[] generatedPassword) {
        Attributes currentAttrs = getAttributes(username, new String[] {
                "cn",
                "ou",
                "o",
                "st" });
        String cn = null;
        String ou = null;
        String o = null;
        String st = null;
        try {
            cn = (String) currentAttrs.get("cn").get();
            ou = (String) currentAttrs.get("ou").get();
            o = (String) currentAttrs.get("o").get();
            st = (String) currentAttrs.get("st").get();
        } catch (NamingException ex) {
            throw new RuntimeException("Could not get current cert creation attributes for: " + username, ex);
        }
        Attributes attrs = new BasicAttributes(true);
        KeyStore userPKCS12KeyStore = getCertificateHelper().createPKCS12KeyStore(username, cn, ou, o, st, generatedPassword);
        byte[] userPKCS12Bytes = CertificateHelper.toByteArray(userPKCS12KeyStore, generatedPassword);
        Attribute keystoreAttr = new BasicAttribute("userPKCS12");
        keystoreAttr.add(userPKCS12Bytes);
        attrs.put(keystoreAttr);
        Attribute certAttr = new BasicAttribute("userCertificate;binary");
        byte[] certBytes = CertificateHelper.getX509CertificateByteArray(userPKCS12KeyStore);
        certAttr.add(certBytes);
        attrs.put(certAttr);
        return attrs;
    }

    /**
     * User given by getUserDN() gets attributes of user given by parameter userDN
     * 
     * @param userDN
     * @param attrIds
     * @return
     */
    @Override
    public Attributes getAttributes(String userDN, String[] attrIds) {
        try {
            return getLdapContext().getAttributes(userDN, attrIds);
        } catch (AuthenticationException ex) {
            throw new GatekeeperAuthenticationException("Get attributes for user:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NoPermissionException ex) {
            throw new GatekeeperAuthorizationException("Get attributes for user:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NamingException ex) {
            throw new RuntimeException("Could not get ldap attributes for user: " + userDN, ex);
        }
    }

    /**
     * Get atributes of user given by getUserDN()
     * 
     * @param attrIds
     * @return
     */
    @Override
    public Attributes getAttributes(String[] attrIds) {
        return getAttributes(getUserDN(), attrIds);
    }

    public CertificateHelper getCertificateHelper() {
        if (certificateHelper == null) {
            certificateHelper = new CertificateHelper();
        }
        return certificateHelper;
    }

    public Hashtable<String, String> getEnvironment() {
        return environment;
    }

    public LdapContext getLdapContext() {
        if (ldapContext == null) {
            try {
                /*
                 * The creation of the InitialLdapContext triggers an LDAP authentication
                 */
                ldapContext = new InitialLdapContext(getEnvironment(), null);
            } catch (AuthenticationException ex) {
                throw new GatekeeperAuthenticationException("Authentication failed", getUserDN(), getRealm(), ex);
            } catch (NoPermissionException ex) {
                throw new GatekeeperAuthorizationException("Authorization failed", getUserDN(), getRealm(), ex);
            } catch (Exception ex) {
                throw new RuntimeException("Could not get InitialLdapContext for: " + getUserDN(), ex);
            }
        }
        return ldapContext;
    }

    /**
     * Determine whether the password of user given by getUserDN() is expiring.
     * null means it is not, otherwise the remaining time can be determined from the returned PasswordExpiring
     * 
     * @return
     */
    @Override
    public PasswordExpiring getPasswordExpiring() {
        if (getResponseControls() == null) {
            return null;
        }
        for (int x = 0; x < getResponseControls().length; x++) {
            if (PASSWORD_EXPIRING_OID.equals(getResponseControls()[x].getID())) {
                return new PasswordExpiring(getResponseControls()[x]);
            }
        }
        return null;
    }

    /**
     * Return the control that specifies that the password is about to expire.
     * @return 
     */
    public PasswordPolicy getPasswordPolicy() {
        if (getResponseControls() == null)
            return null;
        for (int x = 0; x < getResponseControls().length; x++) {
            if (PASSWORD_POLICY_OID.equals(getResponseControls()[x].getID())) {
                return new PasswordPolicy(getResponseControls()[x]);
            }
        }
        return null;
    }

    public String getProviderURL() {
        return (String) getEnvironment().get(Context.PROVIDER_URL);
    }

    public Control[] getResponseControls() {
        return responseControls;
    }

    /**
     * Return the userDN which is connected to LDAP by this instance
     * 
     * @return
     */
    @Override
    public String getUserDN() {
        return getEnvironment().get(Context.SECURITY_PRINCIPAL);
    }

    /**
     * Return the realm for this LDAP
     * 
     * @return
     */
    @Override
    public String getRealm() {
        return getEnvironment().get("realm");
    }

    public char[] getUserPassword() {
        if (getEnvironment().get(Context.SECURITY_CREDENTIALS) == null) {
            return null;
        } else {
            return getEnvironment().get(Context.SECURITY_CREDENTIALS).toCharArray();
        }
    }

    /**
     * Determine whether the password of user given by getUserDN() is expired.
     * 
     * @return
     */
    @Override
    public boolean isPasswordExpired() {
        if (getResponseControls() != null) {
            for (int x = 0; x < getResponseControls().length; x++) {
                if (PASSWORD_EXPIRED_OID.equals(getResponseControls()[x].getID())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void printControls() {
        if (getResponseControls() != null) {
            for (int x = 0; x < getResponseControls().length; x++) {
                logger.debug("responseControl: " + getResponseControls()[x].getID() + " " + new String(getResponseControls()[x].getEncodedValue()));
            }
        }
    }

    /**
     * User given by getUserDN() resets the password of user given by parameter userDN
     * 
     * @param userDN
     * @return
     * @throws NamingSecurityException
     */
    @Override
    public char[] resetPassword(String userDN) {
        char[] generatedPassword = null;
        try {
            adminConnect();
            // Setup extended operation
            PasswordChangeRequest req = new PasswordChangeRequest(userDN, null);
            getLdapContext().extendedOperation(req);
            generatedPassword = req.getNewPassword();
            /*
             * TODO gatekeeper needs access to a bean like TolvenProperties, rather than use System.getProperty() directly
             */
            String userKeysOptional = System.getProperty("tolven.security.user.keysOptional");
            if (Boolean.parseBoolean(userKeysOptional)) {
                logger.info(getUserDN() + " has tolven.security.user.keysOptional=true, so no user credentials generated for " + userDN + " in realm: " + getRealm());
            } else {
                Attributes certAttrs = generateNewUserPKCS12Attributes(userDN, generatedPassword);
                getLdapContext().modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, certAttrs);
                logger.info(getUserDN() + " generated userPKCS12 for " + userDN + " in realm: " + getRealm());
            }
        } catch (AuthenticationException ex) {
            throw new GatekeeperAuthenticationException("Reset password for user:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NoPermissionException ex) {
            throw new GatekeeperAuthorizationException("Reset password for user:" + userDN, getUserDN(), getRealm(), ex);
        } catch (NamingException e) {
            throw new RuntimeException("Password reset failed: " + userDN, e);
        }
        return generatedPassword;
    }

    public void setCertificateHelper(CertificateHelper certificateHelper) {
        this.certificateHelper = certificateHelper;
    }

    public void setEnvironment(Hashtable<String, String> environment) {
        this.environment = environment;
    }

    public void setLdapContext(LdapContext ldapContext) {
        this.ldapContext = ldapContext;
    }

    public void setResponseControls(Control[] responseControls) {
        this.responseControls = responseControls;
    }

    public void setUserDN(String username) {
        getEnvironment().put(Context.SECURITY_PRINCIPAL, username);
    }

    public void setUserPassword(char[] userPassword) {
        getEnvironment().put(Context.SECURITY_CREDENTIALS, new String(userPassword));
    }

}
