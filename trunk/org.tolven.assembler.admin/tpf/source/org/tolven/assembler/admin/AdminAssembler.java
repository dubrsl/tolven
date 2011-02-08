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
package org.tolven.assembler.admin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.plugin.boot.TPFBoot;

/**
 * This class generates credentials based plugins in the repository and the tolven configuration.
 * 
 * @author Joseph Isaac
 *
 */
public class AdminAssembler extends TolvenCommandPlugin {

    public static final String CMD_LINE_TOLVEN_USER_OPTION = "user";
    public static final String CMD_LINE_TOLVEN_PASSWORD_OPTION = "password";
    public static final String CMD_LINE_TOLVEN_REALM_OPTION = "realm";
    public static final String ENV_TOLVEN_USER = "TOLVEN_USER";
    public static final String ENV_TOLVEN_PASSWORD = "TOLVEN_PASSWORD";
    public static final String ENV_TOLVEN_REALM = "TOLVEN_REALM";
    public static final String CMD_LINE_CONF_OPTION = "conf";

    public static final String EXTENSION_POINT_AUTHRESTFUL = "authRestful";
    public static final String EXTENSION_POINT_APPRESTFUL = "appRestful";

    private Logger logger = Logger.getLogger(AdminAssembler.class);

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        setInitArgs(args);
        initialize();
        CommandLine commandLine = getCommandLine();
        getTolvenConfigWrapper().getAdmin().setId(getCommandLineUser(commandLine));
        char[] adminPassword = getCommandLinePassword(commandLine);
        getTolvenConfigWrapper().getAdmin().setPassword(new String(adminPassword));
        getTolvenConfigWrapper().getAdmin().setRealm(getCommandLineRealm(commandLine));
        setupAuthRestfulRootURL();
        setupAppRestfulRootURL();
    }

    private CommandLine getCommandLine() {
        GnuParser parser = new GnuParser();
        try {
            return parser.parse(getCommandOptions(), getInitArgs(), true);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(getClass().getName(), getCommandOptions());
            throw new RuntimeException("Could not parse command line for: " + getClass().getName(), ex);
        }
    }

    private void initialize() {
        File configDir = TPFBoot.pluginsWrapper.getConfigDir();
        getTolvenConfigWrapper().loadConfigDir(configDir);
        File tmpDir = TPFBoot.pluginsWrapper.getRepositoryTmpDir();
        Long tmpDirLastModified = tmpDir.lastModified();
        //Can't use the pluginsWrapper.getPluginsFile(), until the distributed tpf-boot.jar is updated
        File configPluginsFile = new File(configDir, "plugins.xml");
        Long configPluginsFileLastModified = configPluginsFile.lastModified();
        if (tmpDir.exists() && tmpDir.isDirectory() && configPluginsFileLastModified > tmpDirLastModified) {
            logger.debug(configPluginsFile.getPath() + " more recent, so deleting " + tmpDir.getPath());
            deleteDir(tmpDir);
        }
        File runtimePluginsFile = new File(TPFBoot.pluginsWrapper.getRepositoryRuntimeDir(), "plugins.xml");
        Long runtimePluginsFileLastModified = runtimePluginsFile.lastModified();
        if (tmpDir.exists() && tmpDir.isDirectory() && runtimePluginsFileLastModified > tmpDirLastModified) {
            logger.debug(runtimePluginsFile.getPath() + " more recent, so deleting " + tmpDir.getPath());
            deleteDir(tmpDir);
        }
        logger.debug("tmpDir - " + tmpDir.getPath());
        if (!tmpDir.isDirectory() && !tmpDir.mkdirs()) {
            throw new RuntimeException("tmpDir " + tmpDir + " not found");
        }
        File stageDir = TPFBoot.pluginsWrapper.getRepositoryStageDir();
        Long stageDirLastModified = stageDir.lastModified();
        if (stageDir.exists() && stageDir.isDirectory() && configPluginsFileLastModified > stageDirLastModified) {
            logger.debug(configPluginsFile.getPath() + " more recent, so deleting " + stageDir.getPath());
            deleteDir(stageDir);
        }
        if (stageDir.exists() && stageDir.isDirectory() && runtimePluginsFileLastModified > stageDirLastModified) {
            logger.debug(runtimePluginsFile.getPath() + " more recent, so deleting " + stageDir.getPath());
            deleteDir(stageDir);
        }
        logger.debug("stageDir - " + stageDir.getPath());
        if (!stageDir.isDirectory() && !stageDir.mkdirs()) {
            throw new RuntimeException("stageDir " + stageDir + " not found");
        }
        File devLib = TPFBoot.pluginsWrapper.getRepositoryDevLibDir();
        logger.debug("devLib - " + devLib.getPath());
        if (!devLib.isDirectory() && !devLib.mkdirs()) {
            throw new RuntimeException("devLib " + devLib + " not found");
        }
        setPluginsWrapper(TPFBoot.pluginsWrapper);
    }

    private void deleteDir(File dir) {
        String tmpDirname = dir.getAbsolutePath() + ".tmp";
        File tmpDir = new File(tmpDirname);
        try {
            FileUtils.deleteDirectory(tmpDir);
            if (dir.exists()) {
                FileUtils.moveDirectory(dir, tmpDir);
            }
            FileUtils.deleteDirectory(tmpDir);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete the directory: " + dir, ex);
        } finally {
            try {
                FileUtils.deleteDirectory(tmpDir);
            } catch (IOException ex) {
                throw new RuntimeException("Could not delete build directory: " + tmpDir, ex);
            }
        }
    }

    private void setupAuthRestfulRootURL() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSION_POINT_AUTHRESTFUL);
        Extension extension = getSingleConnectedExtension(extensionPoint);
        PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
        String authRestfulURL = extension.getParameter("authRestful.url").valueAsString();
        String e_authRestfulURL = evaluate(authRestfulURL, pluginDescriptor);
        if (e_authRestfulURL == null) {
            throw new RuntimeException(extension.getUniqueId() + "@authRestful.url evaluated to null using: " + authRestfulURL);
        }
        getTolvenConfigWrapper().getApplication().setAuthRestfulURL(e_authRestfulURL);
    }

    private void setupAppRestfulRootURL() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSION_POINT_APPRESTFUL);
        Extension extension = getSingleConnectedExtension(extensionPoint);
        PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
        String appRestfulURL = extension.getParameter("appRestful.url").valueAsString();
        String e_appRestfulURL = evaluate(appRestfulURL, pluginDescriptor);
        if (e_appRestfulURL == null) {
            throw new RuntimeException(extension.getUniqueId() + "@appRestful.url evaluated to null using: " + appRestfulURL);
        }
        getTolvenConfigWrapper().getApplication().setAppRestfulURL(e_appRestfulURL);
    }

    private Options getCommandOptions() {
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption(CMD_LINE_CONF_OPTION, CMD_LINE_CONF_OPTION, true, "configuration directory");
        cmdLineOptions.addOption(CMD_LINE_TOLVEN_USER_OPTION, CMD_LINE_TOLVEN_USER_OPTION, true, "user Id");
        cmdLineOptions.addOption(CMD_LINE_TOLVEN_PASSWORD_OPTION, CMD_LINE_TOLVEN_PASSWORD_OPTION, true, "user password");
        cmdLineOptions.addOption(CMD_LINE_TOLVEN_REALM_OPTION, CMD_LINE_TOLVEN_REALM_OPTION, true, "user realm");
        return cmdLineOptions;
    }

    protected String getCommandLineUser(CommandLine commandLine) {
        String user = null;
        if (commandLine.getOptionValue(CMD_LINE_TOLVEN_USER_OPTION) != null) {
            user = commandLine.getOptionValue(CMD_LINE_TOLVEN_USER_OPTION);
        }
        if (user == null) {
            if (System.getenv(ENV_TOLVEN_USER) == null) {
                System.out.print("User Id: ");
                user = System.console().readLine();
            } else {
                user = System.getenv(ENV_TOLVEN_USER);
            }
        }
        return user;
    }

    protected char[] getCommandLinePassword(CommandLine commandLine) {
        char[] password = null;
        if (commandLine.getOptionValue(CMD_LINE_TOLVEN_PASSWORD_OPTION) != null) {
            password = commandLine.getOptionValue(CMD_LINE_TOLVEN_PASSWORD_OPTION).toCharArray();
        }
        if (password == null) {
            if (System.getenv(ENV_TOLVEN_PASSWORD) == null) {
                System.out.print("Password: ");
                password = System.console().readPassword();
            } else {
                password = System.getenv(ENV_TOLVEN_PASSWORD).toCharArray();
            }
        }
        return password;
    }

    protected String getCommandLineRealm(CommandLine commandLine) {
        String realm = null;
        if (commandLine.getOptionValue(CMD_LINE_TOLVEN_REALM_OPTION) != null) {
            realm = commandLine.getOptionValue(CMD_LINE_TOLVEN_REALM_OPTION);
        }
        if (realm == null) {
            if (System.getenv(ENV_TOLVEN_REALM) == null) {
                System.out.print("Realm: ");
                realm = System.console().readLine();
            } else {
                realm = System.getenv(ENV_TOLVEN_REALM);
            }
        }
        return realm;
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
