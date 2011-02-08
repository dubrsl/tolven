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
package org.tolven.plugin.boot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.java.plugin.boot.Boot;
import org.tolven.plugin.repository.ConfigPluginsWrapper;
import org.tolven.plugin.repository.RepositoryMetadata;
import org.tolven.plugin.repository.RepositorySnapshot;
import org.tolven.plugin.repository.RepositoryUpgrade;

/**
 * The main entry point class for the Tolven Plugin Framework
 * 
 * @author Joseph Isaac
 *
 */
public class TPFBoot {

    public static final String TPF_VERSION_PROPERTY = "TPF_VERSION";
    public static String TPF_VERSION;
    public static final String CMD_LINE_CONF_OPTION = "conf";
    public static final String ENV_CONF = "TOLVEN_CONFIG_DIR";
    public static final String CMD_LINE_GETPLUGINS_OPTION = "getPlugins";
    public static final String CMD_LINE_REPOSITORYSNAPSHOT_OPTION = "repositorySnapshot";
    public static final String CMD_LINE_PLUGIN_OPTION = "plugin";
    public static final String CMD_LINE_GENMETADATA_OPTION = "genMetadata";
    public static final String CMD_LINE_VERSION_OPTION = "version";
    public static final String BOOT_PROPERTIES_CONFIG = "jpf.boot.config";
    public static final String JPF_BOOT_PLUGINS_REPOSITORIES = "org.java.plugin.boot.pluginsRepositories";
    public static final String JPF_BOOT_SHADOW_FOLDER_REPOSITORIES = "org.java.plugin.standard.ShadingPathResolver.shadowFolder";

    public static ConfigPluginsWrapper pluginsWrapper;

    public static void main(String[] args) throws Exception {
        URL manifestURL = ClassLoader.getSystemResource("tolven-plugin.xml");
        TPF_VERSION = RepositoryMetadata.getVersion(manifestURL);
        System.setProperty(TPF_VERSION_PROPERTY, TPF_VERSION);
        Option confOption = new Option(CMD_LINE_CONF_OPTION, CMD_LINE_CONF_OPTION, true, "configuration directory");
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setRequired(true);
        Option upgradeOption = new Option(CMD_LINE_GETPLUGINS_OPTION, CMD_LINE_GETPLUGINS_OPTION, false, "getPlugins from repositories");
        optionGroup.addOption(upgradeOption);
        Option pluginOption = new Option(CMD_LINE_PLUGIN_OPTION, CMD_LINE_PLUGIN_OPTION, true, "execute one or more plugins");
        optionGroup.addOption(pluginOption);
        Option genMetadataOption = new Option(CMD_LINE_GENMETADATA_OPTION, CMD_LINE_GENMETADATA_OPTION, true, "generate a repository metadata file");
        optionGroup.addOption(genMetadataOption);
        Option versionOption = new Option(CMD_LINE_VERSION_OPTION, CMD_LINE_VERSION_OPTION, false, "TPF Version");
        optionGroup.addOption(versionOption);
        Option repositorySnapshotOption = new Option(CMD_LINE_REPOSITORYSNAPSHOT_OPTION, CMD_LINE_REPOSITORYSNAPSHOT_OPTION, false, "create snapshot of repositories");
        optionGroup.addOption(repositorySnapshotOption);
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption(confOption);
        cmdLineOptions.addOptionGroup(optionGroup);
        try {
            GnuParser parser = new GnuParser();
            CommandLine commandLine = parser.parse(cmdLineOptions, args, true);
            String configDirname = getCommandLineConfigDir(commandLine);
            File configDir = new File(configDirname);
            pluginsWrapper = new ConfigPluginsWrapper();
            pluginsWrapper.loadConfigDir(configDir);
            Logger.getLogger(TPFBoot.class.getName()).info("TPF Version: " + TPF_VERSION);
            Logger.getLogger(TPFBoot.class.getName()).info("Loaded configDir " + configDir.getPath());
            boolean upgrade = commandLine.hasOption(CMD_LINE_GETPLUGINS_OPTION);
            String plugins = commandLine.getOptionValue(CMD_LINE_PLUGIN_OPTION);
            boolean genMetadata = commandLine.hasOption(CMD_LINE_GENMETADATA_OPTION);
            boolean versionRequest = commandLine.hasOption(CMD_LINE_VERSION_OPTION);
            boolean repositorySnapshot = commandLine.hasOption(CMD_LINE_REPOSITORYSNAPSHOT_OPTION);
            if (upgrade) {
                //RepositoryUpgrade.main(commandLine.getArgs()); //CLI seems to only recognize the first letter of an option?
                StringBuffer buff = new StringBuffer();
                for (String arg : args) {
                    if (!("-" + CMD_LINE_GETPLUGINS_OPTION).equals(arg)) {
                        buff.append(arg + ",");
                    }
                }
                RepositoryUpgrade.main(buff.toString().split(","));
            } else if (genMetadata) {
                StringBuffer buff = new StringBuffer();
                for (String arg : args) {
                    if (!("-" + CMD_LINE_GENMETADATA_OPTION).equals(arg)) {
                        buff.append(arg + ",");
                    }
                }
                RepositoryMetadata.main(buff.toString().split(","));
            } else if (versionRequest) {
                System.out.println("\nTPF Version: " + TPF_VERSION);
            } else if (repositorySnapshot) {
                StringBuffer buff = new StringBuffer();
                for (String arg : args) {
                    if (!("-" + CMD_LINE_REPOSITORYSNAPSHOT_OPTION).equals(arg)) {
                        buff.append(arg + ",");
                    }
                }
                RepositorySnapshot.main(buff.toString().split(","));
            } else if (plugins != null) {
                addBootRepositoryRuntime(pluginsWrapper, configDir);
                Boot.main(args);
            }
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(TPFBoot.class.getName(), cmdLineOptions);
            throw new RuntimeException("Could not parse command line for: " + TPFBoot.class.getName(), ex);
        }
    }

    private static String getCommandLineConfigDir(CommandLine commandLine) {
        String configDir = commandLine.getOptionValue(CMD_LINE_CONF_OPTION);
        if (configDir == null) {
            configDir = System.getenv(ENV_CONF);
            if (configDir == null) {
                System.out.print("Please enter config directory: ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                try {
                    configDir = input.readLine();
                } catch (IOException ex) {
                    throw new RuntimeException("Could not read configuration directory from System.in", ex);
                }
            }
        }
        return configDir;
    }

    private static void addBootRepositoryRuntime(ConfigPluginsWrapper pluginsWrapper, File configDir) {
        File bootPropertiesFile = new File(configDir, "boot.properties");
        if (!bootPropertiesFile.exists()) {
            throw new RuntimeException("Could not locate boot file: " + bootPropertiesFile.getPath());
        }
        Properties bootProperties = null;
        try {
            bootProperties = load(bootPropertiesFile);
        } catch (Exception ex) {
            throw new RuntimeException("Could not load boot properties file:" + bootPropertiesFile.getPath());
        }
        bootProperties.put(JPF_BOOT_PLUGINS_REPOSITORIES, pluginsWrapper.getRepositoryRuntimePluginsDir().getPath().replace("\\", "/"));
        bootProperties.put(JPF_BOOT_SHADOW_FOLDER_REPOSITORIES, pluginsWrapper.getRepositoryRuntimeUnpackedDir().getPath().replace("\\", "/"));
        File generatedBootPropertiesFile = null;
        try {
            generatedBootPropertiesFile = File.createTempFile("tpf_", "_" + bootPropertiesFile.getName());
            generatedBootPropertiesFile.deleteOnExit();
            store(bootProperties, generatedBootPropertiesFile);
            Logger.getLogger(TPFBoot.class.getName()).info("Generated boot.properties: " + generatedBootPropertiesFile.getPath());
            System.setProperty(BOOT_PROPERTIES_CONFIG, generatedBootPropertiesFile.getPath());
        } catch (Exception ex) {
            throw new RuntimeException("Could not store generate boot properties file to:" + generatedBootPropertiesFile.getPath(), ex);
        }
    }

    private static Properties load(File propertiesFile) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(propertiesFile);
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private static Properties store(Properties properties, File propertiesFile) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(propertiesFile);
            properties.store(out, null);
            return properties;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
