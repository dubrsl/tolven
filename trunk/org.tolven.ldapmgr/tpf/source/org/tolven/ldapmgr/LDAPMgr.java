package org.tolven.ldapmgr;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.log4j.Logger;
import org.tolven.config.model.TolvenConfigWrapper;

public class LDAPMgr {

    public static String ABOUT_TO_TEST_MESSAGE = "About To Test";
    public static String SUCCESSFUL_CONNECTION_MESSAGE = "Connection Successful";

    private String providerURL;
    private String protocol;
    private String hostname;
    private String port;
    private String suffix;
    private String groups;
    private String people;
    private TolvenConfigWrapper tolvenConfigWrapper;

    private Logger logger = Logger.getLogger(LDAPMgr.class);

    public LDAPMgr() {
    }

    public String getProviderURL() {
        return providerURL;
    }

    public void setProviderURL(String providerURL) {
        this.providerURL = providerURL;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getPeople() {
        return people;
    }

    public void setPeopleDN(String people) {
        this.people = people;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public TolvenConfigWrapper getTolvenConfigWrapper() {
        return tolvenConfigWrapper;
    }

    public void setTolvenConfigWrapper(TolvenConfigWrapper tolvenConfigWrapper) {
        this.tolvenConfigWrapper = tolvenConfigWrapper;
    }

    private String getAdminDN() {
        return "uid=" + getAdminId() + "," + getPeopleContext();
    }

    public String getGroupsContext() {
        return getGroups() + "," + getSuffix();
    }

    public String getPeopleContext() {
        return getPeople() + "," + getSuffix();
    }

    public String getAdminId() {
        return getTolvenConfigWrapper().getAdminId();
    }

    public char[] getAdminPassword() {
        return getTolvenConfigWrapper().getPasswordHolder().getPassword(getAdminId());
    }

    public String getLDAPRootDN() {
        return getTolvenConfigWrapper().getLDAPServerRootUser();
    }

    public char[] getLDAPRootDNPassword() {
        return getTolvenConfigWrapper().getPasswordHolder().getPassword(getTolvenConfigWrapper().getLDAPServerRootPasswordId());
    }

    public String getLDAPId() {
        return getTolvenConfigWrapper().getLDAPServerId();
    }

    public String getAppServerId() {
        return getTolvenConfigWrapper().getAppServerId();
    }

    private DirContext getContext(String adminDN, char[] password) {
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getProviderURL());
        env.put(Context.SECURITY_PRINCIPAL, adminDN);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            return new InitialDirContext(env);
        } catch (NamingException ex) {
            throw new RuntimeException("Could not create an LDAP Directory Context for DN: " + adminDN + "\nto:\n" + getProviderURL(), ex);
        }
    }

    private void close(DirContext dirContext) {
        try {
            dirContext.close();
        } catch (NamingException ex) {
            System.out.println(getNestedMessage(ex));
            throw new RuntimeException("Could not close the LDAP context " + " for: " + getProviderURL(), ex);
        }
    }

    public String testRootDNLDAPConnection() {
        getTolvenConfigWrapper().initializeJSSE();
        String rootDN = getTolvenConfigWrapper().getLDAPServerRootUser();
        String adminId = getTolvenConfigWrapper().getAdminId();
        String messageSuffix = " for root DN: " + rootDN + " to: " + getProviderURL() + " using: " + adminId + " SSL certificate";
        String preTestString = "\n" + ABOUT_TO_TEST_MESSAGE + messageSuffix;
        logger.debug(preTestString);
        System.out.println(preTestString);
        testRootDNConnection();
        String result = "\n" + SUCCESSFUL_CONNECTION_MESSAGE + messageSuffix + "\n";
        logger.debug(result);
        System.out.println(result);
        return result;
    }

    public String testAdminLDAPConnection() {
        getTolvenConfigWrapper().initializeJSSE();
        String adminId = getTolvenConfigWrapper().getAdminId();
        String messageSuffix = " for admin DN: " + getAdminDN() + " to: " + getProviderURL() + " using: " + adminId + " SSL certificate";
        String preTestString = "\n" + ABOUT_TO_TEST_MESSAGE + messageSuffix;
        logger.debug(preTestString);
        System.out.println(preTestString);
        testLDAPConnection();
        String result = "\n" + SUCCESSFUL_CONNECTION_MESSAGE + messageSuffix + "\n";
        logger.debug(result);
        System.out.println(result);
        return result;
    }

    public String testSimulatedAppServerLDAPConnection() {
        try {
            String appServerSSLId = getTolvenConfigWrapper().getAppServer().getId();
            char[] appServerSSLPassword = getTolvenConfigWrapper().getPassword(appServerSSLId);
            getTolvenConfigWrapper().initializeJSSE(appServerSSLId, appServerSSLPassword);
            String messageSuffix = " for admin DN: " + getAdminDN() + " to: " + getProviderURL() + " using: " + appServerSSLId + " SSL certificate";
            String preTestString = "\n" + ABOUT_TO_TEST_MESSAGE + messageSuffix;
            logger.debug(preTestString);
            System.out.println(preTestString);
            testLDAPConnection();
            String result = "\n" + SUCCESSFUL_CONNECTION_MESSAGE + messageSuffix + "\n";
            logger.debug(result);
            System.out.println(result);
            return result;
        } finally {
            getTolvenConfigWrapper().initializeJSSE();
        }
    }

    private void testLDAPConnection() {
        DirContext ctx = null;
        try {
            ctx = getContext(getAdminDN(), getAdminPassword());
        } finally {
            if (ctx != null) {
                close(ctx);
            }
        }
    }

    private void testRootDNConnection() {
        DirContext ctx = null;
        try {
            String rootDN = getTolvenConfigWrapper().getLDAPServerRootUser();
            String rootDNPasswordId = getTolvenConfigWrapper().getLDAPServerRootPasswordId();
            char[] rootDNPassword = getTolvenConfigWrapper().getPassword(rootDNPasswordId);
            ctx = getContext(rootDN, rootDNPassword);
        } finally {
            if (ctx != null) {
                close(ctx);
            }
        }
    }

    private String getNestedMessage(Exception ex) {
        StringBuffer buff = new StringBuffer();
        Throwable throwable = ex;
        do {
            buff.append(throwable.getMessage() + "\n");
            throwable = throwable.getCause();
        } while (throwable != null);
        return buff.toString();
    }

}
