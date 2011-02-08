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
package org.tolven.ldapmgr;

import java.awt.Component;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.java.plugin.registry.ExtensionPoint;
import org.tolven.ldapmgr.gui.LDAPMgrUI;
import org.tolven.ldapmgr.gui.LDAPConnectionPanel;
import org.tolven.ldapmgr.gui.LDAPMgrPanel;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.plugin.gui.GUIComponent;
import org.tolven.security.hash.SSHA;

/**
 * This plugin provides information about the LDAP connection
 * 
 * @author Joseph Isaac
 *
 */
public class LDAPMgrPlugin extends TolvenCommandPlugin implements GUIComponent {

    public static final String CMD_LINE_TEST_ROOTDN_LDAP_OPTION = "testRootDNLDAP";
    public static final String CMD_LINE_TEST_ADMIN_LDAP_OPTION = "testAdminLDAP";
    public static final String CMD_LINE_TEST_APPSERVER_LDAP_OPTION = "testAppServerLDAP";
    public static final String CMD_LINE_UPDATE_SCHEMAS_OPTION = "updateSchemas";
    public static final String CMD_LINE_GUI_OPTION = "gui";

    public static final String EXTENSION_LDAPSOURCE = "ldapSource";

    private LDAPMgr ldapMgr;
    private LDAPMgrPanel ldapMgrPanel;
    private LDAPMgrUI ldapMgrUI;
    private ExtensionPoint ldapSourceExtensionPoint;

    private Logger logger = Logger.getLogger(LDAPMgrPlugin.class);

    private ExtensionPoint getLdapSourceExtensionPoint() {
        if (ldapSourceExtensionPoint == null) {
            ExtensionPoint ldapSourceExtensionPoint = getDescriptor().getExtensionPoint(EXTENSION_LDAPSOURCE);
            ExtensionPoint parentLDAPSourceExtensionPoint = getParentExtensionPoint(ldapSourceExtensionPoint);
            setLdapSourceExtensionPoint(parentLDAPSourceExtensionPoint);
        }
        return ldapSourceExtensionPoint;
    }

    private void setLdapSourceExtensionPoint(ExtensionPoint ldapSourceExtensionPoint) {
        this.ldapSourceExtensionPoint = ldapSourceExtensionPoint;
    }

    private String getLDAPProtocol() {
        String ldapProtocol = getLdapSourceExtensionPoint().getParameterDefinition("ldap.protocol").getDefaultValue();
        String eval_ldapProtocol = (String) evaluate(ldapProtocol, getLdapSourceExtensionPoint().getDeclaringPluginDescriptor());
        if (eval_ldapProtocol == null) {
            throw new RuntimeException(getLdapSourceExtensionPoint().getUniqueId() + "@ldap.protocol" + "evaluated to null using: " + ldapProtocol);
        }
        return eval_ldapProtocol;
    }

    private String getLDAPHostname() {
        String ldapHostname = getLdapSourceExtensionPoint().getParameterDefinition("ldap.hostname").getDefaultValue();
        String eval_ldapHostname = (String) evaluate(ldapHostname, getLdapSourceExtensionPoint().getDeclaringPluginDescriptor());
        if (eval_ldapHostname == null) {
            throw new RuntimeException(getLdapSourceExtensionPoint().getUniqueId() + "@ldap.hostname" + "evaluated to null using: " + ldapHostname);
        }
        return eval_ldapHostname;
    }

    private String getLDAPPort() {
        String ldapPort = getLdapSourceExtensionPoint().getParameterDefinition("ldap.port").getDefaultValue();
        String eval_ldapPort = (String) evaluate(ldapPort, getLdapSourceExtensionPoint().getDeclaringPluginDescriptor());
        if (eval_ldapPort == null) {
            throw new RuntimeException(getLdapSourceExtensionPoint().getUniqueId() + "@ldap.port" + "evaluated to null using: " + ldapPort);
        }
        return eval_ldapPort;
    }

    private String getLDAPSuffix() {
        String ldapSuffix = getLdapSourceExtensionPoint().getParameterDefinition("ldap.suffix").getDefaultValue();
        String eval_ldapSuffix = (String) evaluate(ldapSuffix, getLdapSourceExtensionPoint().getDeclaringPluginDescriptor());
        if (eval_ldapSuffix == null) {
            throw new RuntimeException(ldapSourceExtensionPoint.getUniqueId() + "@ldap.suffix" + "evaluated to null using: " + ldapSuffix);
        }
        return eval_ldapSuffix;
    }

    private String getLDAPGroups() {
        String ldapGroups = getLdapSourceExtensionPoint().getParameterDefinition("ldap.groups").getDefaultValue();
        String eval_ldapGroups = (String) evaluate(ldapGroups, getLdapSourceExtensionPoint().getDeclaringPluginDescriptor());
        if (eval_ldapGroups == null) {
            throw new RuntimeException(ldapSourceExtensionPoint.getUniqueId() + "@ldap.groups" + "evaluated to null using: " + ldapGroups);
        }
        return eval_ldapGroups;
    }

    private String getLDAPPeople() {
        String ldapPeople = getLdapSourceExtensionPoint().getParameterDefinition("ldap.people").getDefaultValue();
        String eval_ldapPeople = (String) evaluate(ldapPeople, getLdapSourceExtensionPoint().getDeclaringPluginDescriptor());
        if (eval_ldapPeople == null) {
            throw new RuntimeException(ldapSourceExtensionPoint.getUniqueId() + "@ldap.people" + "evaluated to null using: " + ldapPeople);
        }
        return eval_ldapPeople;
    }

    private String getRootDN() {
        String ldapRootDN = getLdapSourceExtensionPoint().getParameterDefinition("ldap.rootDN").getDefaultValue();
        String eval_ldapRootDN = (String) evaluate(ldapRootDN, getLdapSourceExtensionPoint().getDeclaringPluginDescriptor());
        if (eval_ldapRootDN == null) {
            throw new RuntimeException(ldapSourceExtensionPoint.getUniqueId() + "@ldap.rootDN" + "evaluated to null using: " + ldapRootDN);
        }
        return eval_ldapRootDN;
    }

    private String getProviderURL() {
        return getLDAPProtocol() + "://" + getLDAPHostname() + ":" + getLDAPPort();
    }

    private LDAPMgrUI getLDAPMgrUI() {
        if (ldapMgrUI == null) {
            ldapMgrUI = new LDAPMgrUI(getComponent());
        }
        return ldapMgrUI;
    }

    private LDAPMgr getLDAPMgr() {
        if (ldapMgr == null) {
            ldapMgr = new LDAPMgr();
            ldapMgr.setGroups(getLDAPGroups());
            ldapMgr.setHostname(getLDAPHostname());
            ldapMgr.setPeopleDN(getLDAPPeople());
            ldapMgr.setPort(getLDAPPort());
            ldapMgr.setProtocol(getLDAPProtocol());
            ldapMgr.setProviderURL(getProviderURL());
            ldapMgr.setSuffix(getLDAPSuffix());
            ldapMgr.setTolvenConfigWrapper(getTolvenConfigWrapper());
        }
        return ldapMgr;
    }

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    @Override
    public void execute(String[] args) {
        logger.info("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        if (commandLine.hasOption(CMD_LINE_TEST_ROOTDN_LDAP_OPTION)) {
            try {
                getLDAPMgr().testRootDNLDAPConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(getNestedMessage(ex));
            }
        } else if (commandLine.hasOption(CMD_LINE_TEST_ADMIN_LDAP_OPTION)) {
            try {
                getLDAPMgr().testAdminLDAPConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(getNestedMessage(ex));
            }
        } else if (commandLine.hasOption(CMD_LINE_TEST_APPSERVER_LDAP_OPTION)) {
            try {
                getLDAPMgr().testSimulatedAppServerLDAPConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(getNestedMessage(ex));
            }
        } else if (commandLine.hasOption(CMD_LINE_UPDATE_SCHEMAS_OPTION)) {
            try {
                updateSchemas();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(getNestedMessage(ex));
            }
        } else if (commandLine.hasOption(CMD_LINE_GUI_OPTION)) {
            logger.info("Starting the ldap manager gui...");
            getLDAPMgrUI().setVisible(true);
        }
    }

    public void updateSchemas() {
        DirContext dirContext = null;
        try {
            dirContext = getContext();
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setCountLimit(1);
            updateSuffix(dirContext);
            updateGroups(dirContext, controls);
            updatePeople(dirContext, controls);
            updateRootDN(dirContext, controls);
            updateUsers(dirContext, controls);
        } finally {
            if (dirContext != null) {
                try {
                    dirContext.close();
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not close the LDAP context", ex);
                }
            }
        }
    }

    private DirContext getContext() {
        char[] rootPassword = getPassword(getTolvenConfigWrapper().getLDAPServerRootPasswordId());
        if (rootPassword == null) {
            throw new RuntimeException("LDAP password is null for alias: " + getTolvenConfigWrapper().getLDAPServerRootPasswordId());
        }
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getProviderURL());
        env.put(Context.SECURITY_PRINCIPAL, getTolvenConfigWrapper().getLDAPServerRootUser());
        env.put(Context.SECURITY_CREDENTIALS, new String(rootPassword));
        try {
            return new InitialDirContext(env);
        } catch (NamingException ex) {
            throw new RuntimeException("Could not create an IntialDirContext", ex);
        }
    }

    protected void updateSuffix(DirContext dirContext) {
        String ldapSuffix = getLDAPSuffix();
        NamingEnumeration<SearchResult> namingEnum = null;
        try {
            try {
                String dn = ldapSuffix;
                Attributes attributes = new BasicAttributes();
                Attribute objclass = new BasicAttribute("objectclass");
                objclass.add("organization");
                objclass.add("dcObject");
                attributes.put(objclass);
                attributes.put("dc", "tolven");
                attributes.put("o", "myOrg");
                dirContext.createSubcontext(dn, attributes);
                logger.info("Executed a createSubContext LDAP schema for " + ldapSuffix);
            } catch (NamingException ex) {
                //For some reason the search can fail, when the suffix is available, and when not available
                // The only certainty is to attempt to create it for now
            }
        } finally {
            if (namingEnum != null) {
                try {
                    namingEnum.close();
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not close the naming enumeration for the ldap suffix schema", ex);
                }
            }
        }
    }

    protected void updateGroups(DirContext dirContext, SearchControls controls) {
        String ldapSuffix = getLDAPSuffix();
        String ldapGroups = getLDAPGroups();
        NamingEnumeration<SearchResult> namingEnum = null;
        try {
            boolean schemaExists = false;
            try {
                namingEnum = dirContext.search(ldapSuffix, ldapGroups, controls);
                schemaExists = namingEnum.hasMore();
            } catch (NamingException ex) {
                throw new RuntimeException("Could find groups schema", ex);
            }
            if (schemaExists) {
                logger.info("LDAP schema for " + ldapGroups + " already exists");
            } else {
                String dn = ldapGroups + "," + ldapSuffix;
                Attributes attributes = new BasicAttributes();
                Attribute objclass = new BasicAttribute("objectclass");
                objclass.add("organizationalUnit");
                attributes.put(objclass);
                attributes.put(ldapGroups.substring(0, ldapGroups.indexOf("=")), ldapGroups.substring(ldapGroups.indexOf("=") + 1));
                try {
                    dirContext.createSubcontext(dn, attributes);
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not create groups schema", ex);
                }
                logger.info("Created LDAP schema for " + ldapGroups);
            }
        } finally {
            if (namingEnum != null) {
                try {
                    namingEnum.close();
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not close the naming enumeration for the ldap groups schema", ex);
                }
            }
        }
    }

    protected void updatePeople(DirContext dirContext, SearchControls controls) {
        String ldapSuffix = getLDAPSuffix();
        String ldapPeople = getLDAPPeople();
        NamingEnumeration<SearchResult> namingEnum = null;
        try {
            boolean schemaExists = false;
            try {
                namingEnum = dirContext.search(ldapSuffix, ldapPeople, controls);
                schemaExists = namingEnum.hasMore();
            } catch (NamingException ex) {
                throw new RuntimeException("Could find people schema", ex);
            }
            if (schemaExists) {
                logger.info("LDAP schema for " + ldapPeople + " already exists");
            } else {
                String dn = ldapPeople + "," + ldapSuffix;
                Attributes attributes = new BasicAttributes();
                Attribute objclass = new BasicAttribute("objectclass");
                objclass.add("organizationalUnit");
                attributes.put(objclass);
                attributes.put(ldapPeople.substring(0, ldapPeople.indexOf("=")), ldapPeople.substring(ldapPeople.indexOf("=") + 1));
                try {
                    dirContext.createSubcontext(dn, attributes);
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not create people schema", ex);
                }
                logger.info("Created LDAP schema for " + ldapPeople);
            }
        } finally {
            if (namingEnum != null) {
                try {
                    namingEnum.close();
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not close the naming enumeration for the ldap people schema", ex);
                }
            }
        }
    }

    protected void updateRootDN(DirContext dirContext, SearchControls controls) {
        String ldapRootDN = getRootDN();
        NamingEnumeration<SearchResult> namingEnum = null;
        try {
            boolean schemaExists = false;
            String name = null;
            String base = null;
            try {
                int index = ldapRootDN.indexOf(",");
                if(index == -1) {
                    throw new RuntimeException("Expected to find at least one comma in the rootDN");
                } else {
                    name = ldapRootDN.substring(0, index);
                    base = ldapRootDN.substring(index + 1);
                }
                namingEnum = dirContext.search(base, name, controls);
                schemaExists = namingEnum.hasMore();
            } catch (NamingException ex) {
                throw new RuntimeException("Could find rootDN schema", ex);
            }
            if (schemaExists) {
                logger.info("LDAP schema for " + ldapRootDN + " already exists");
            } else {
                String dn = name + "," + base;
                Attributes attributes = new BasicAttributes();
                Attribute objclass = new BasicAttribute("objectclass");
                objclass.add("organizationalRole");
                attributes.put(objclass);
                attributes.put(name.substring(0, name.indexOf("=")), name.substring(name.indexOf("=") + 1));
                try {
                    dirContext.createSubcontext(dn, attributes);
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not create rootDN schema", ex);
                }
                logger.info("Created LDAP schema for " + ldapRootDN);
            }
        } finally {
            if (namingEnum != null) {
                try {
                    namingEnum.close();
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not close the naming enumeration for the ldap rootDN schema", ex);
                }
            }
        }
    }

    protected void updateUsers(DirContext dirContext, SearchControls controls) {
        String admin = getTolvenConfigWrapper().getAdmin().getId();
        String encryptedAdminPassword;
        try {
            encryptedAdminPassword = SSHA.encodePassword(getPassword(admin));
        } catch (Exception ex) {
            throw new RuntimeException("Could not SSHA encode password for: " + admin, ex);
        }
        updateUser(admin, encryptedAdminPassword, dirContext, controls);
    }

    protected void updateUser(String user, String encryptedPassword, DirContext dirContext, SearchControls controls) {
        NamingEnumeration<SearchResult> namingEnum = null;
        try {
            String ldapPeople = getLDAPPeople();
            String ldapSuffix = getLDAPSuffix();
            boolean schemaExists = false;
            try {
                namingEnum = dirContext.search(ldapPeople + "," + ldapSuffix, "uid=" + user, controls);
                schemaExists = namingEnum.hasMore();
            } catch (NamingException ex) {
                throw new RuntimeException("Could find schema for: " + user, ex);
            }
            if (schemaExists) {
                logger.info("LDAP schema for user " + user + " already exists");
            } else {
                String dn = "uid=" + user + "," + ldapPeople + "," + ldapSuffix;
                Attributes attributes = new BasicAttributes();
                Attribute objclass = new BasicAttribute("objectclass");
                objclass.add("inetOrgPerson");
                attributes.put(objclass);
                attributes.put("uid", user);
                attributes.put("sn", user);
                attributes.put("cn", user);
                attributes.put("userPassword", encryptedPassword);
                try {
                    dirContext.createSubcontext(dn, attributes);
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not create schema for: " + user, ex);
                }
                logger.info("Created LDAP schema for " + user);
            }
        } finally {
            if (namingEnum != null) {
                try {
                    namingEnum.close();
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not close the naming enumeration for the ldap schema: " + user, ex);
                }
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

    private CommandLine getCommandLine(String[] args) {
        GnuParser parser = new GnuParser();
        try {
            return parser.parse(getCommandOptions(), args, true);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(getClass().getName(), getCommandOptions());
            throw new RuntimeException("Could not parse command line for: " + getClass().getName(), ex);
        }
    }

    private Options getCommandOptions() {
        Options cmdLineOptions = new Options();
        OptionGroup optionGroup = new OptionGroup();
        Option testRootDNLDAPOption = new Option(CMD_LINE_TEST_ROOTDN_LDAP_OPTION, CMD_LINE_TEST_ROOTDN_LDAP_OPTION, false, "\"test rootDN to LDAP connection\"");
        optionGroup.addOption(testRootDNLDAPOption);
        Option testAdminLDAPOption = new Option(CMD_LINE_TEST_ADMIN_LDAP_OPTION, CMD_LINE_TEST_ADMIN_LDAP_OPTION, false, "\"test admin to LDAP connection\"");
        optionGroup.addOption(testAdminLDAPOption);
        Option testAppServerLDAPOption = new Option(CMD_LINE_TEST_APPSERVER_LDAP_OPTION, CMD_LINE_TEST_APPSERVER_LDAP_OPTION, false, "\"simulate appserver to LDAP connection\"");
        optionGroup.addOption(testAppServerLDAPOption);
        Option updateSchemasOption = new Option(CMD_LINE_UPDATE_SCHEMAS_OPTION, CMD_LINE_UPDATE_SCHEMAS_OPTION, false, "\"update schemas\"");
        optionGroup.addOption(updateSchemasOption);
        Option guiOption = new Option(CMD_LINE_GUI_OPTION, CMD_LINE_GUI_OPTION, false, "\"start the LDAP manager gui\"");
        optionGroup.addOption(guiOption);
        optionGroup.setRequired(true);
        cmdLineOptions.addOptionGroup(optionGroup);
        return cmdLineOptions;
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** stop ***");
    }

    @Override
    public String getComponentId() {
        return getDescriptor().getId();
    }

    @Override
    public Component getComponent() {
        return getLDAPMgrPanel();
    }

    @Override
    public String getComponentName() {
        return "LDAP";
    }

    private LDAPMgrPanel getLDAPMgrPanel() {
        if (ldapMgrPanel == null) {
            ldapMgrPanel = new LDAPMgrPanel();
            ldapMgrPanel.addTab("Connection", new LDAPConnectionPanel(getLDAPMgr()));
        }
        return ldapMgrPanel;
    }

}
