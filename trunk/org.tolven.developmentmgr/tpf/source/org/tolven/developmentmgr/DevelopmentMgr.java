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
package org.tolven.developmentmgr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin collects a devLib.jar from each plugin, which should contain source code and classes, which
 * together assist in debugging the code supplied by the plugin.
 * 
 * @author Joseph Isaac
 *
 */
public class DevelopmentMgr extends TolvenCommandPlugin {

    public static final String CMD_LINE_DEVLIB_OPTION = "devLib";

    public static final String MESSAGE_DIGEST_ALGORITHM = "md5";
    private static final String EXTENSIONPOINT_DEVLIB = "devLib";

    private Logger logger = Logger.getLogger(DevelopmentMgr.class);

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        if (commandLine.hasOption(CMD_LINE_DEVLIB_OPTION)) {
            deployPluginJARs();
        }
        logger.debug("*** end ***");
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
        optionGroup.setRequired(true);
        Option devLibOption = new Option(CMD_LINE_DEVLIB_OPTION, CMD_LINE_DEVLIB_OPTION, false, "\"deploy development library\"");
        optionGroup.addOption(devLibOption);
        cmdLineOptions.addOptionGroup(optionGroup);
        return cmdLineOptions;
    }

    protected void deployPluginJARs() throws IOException {
        File devLibDir = getDevLib();
        if (!devLibDir.exists()) {
            devLibDir.mkdirs();
        }
        File tmpDevLibDir = new File(getPluginTmpDir(), "tmpDevLib");
        if (tmpDevLibDir.exists()) {
            FileUtils.deleteDirectory(tmpDevLibDir);
        }
        tmpDevLibDir.mkdirs();
        Set<File> newSourceJars = new HashSet<File>();
        for (final PluginDescriptor pluginDescriptor : getManager().getRegistry().getPluginDescriptors()) {
            ExtensionPoint devLibExtensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_DEVLIB);
            if (devLibExtensionPoint != null) {
                Set<File> sourceJars = new HashSet<File>();
                for (ParameterDefinition parameterDefinition : devLibExtensionPoint.getParameterDefinitions()) {
                    if ("jarDir".equals(parameterDefinition.getId())) {
                        String defaultJARDirValue = parameterDefinition.getDefaultValue();
                        File jarDir = getFilePath(pluginDescriptor, defaultJARDirValue);
                        if (!jarDir.exists()) {
                            throw new RuntimeException(pluginDescriptor.getUniqueId() + " does not contain the directory called: " + defaultJARDirValue);
                        }
                        for (String sourceJarname : jarDir.list(new SuffixFileFilter("jar"))) {
                            File sourceJar = new File(jarDir.getPath(), sourceJarname);
                            sourceJars.add(sourceJar);
                        }
                    } else {
                        String defaultJARValue = parameterDefinition.getDefaultValue();
                        File sourceJar = getFilePath(pluginDescriptor, defaultJARValue);
                        if (!sourceJar.exists()) {
                            throw new RuntimeException("Could not locate: " + sourceJar.getPath() + " for plugin: " + pluginDescriptor.getId());
                        }
                        sourceJars.add(sourceJar);
                    }
                }
                for (File sourceJar : sourceJars) {
                    String extendedSourceJarname = getExtendedJarname(sourceJar.getName(), pluginDescriptor);
                    File extendSourceJar = new File(tmpDevLibDir, extendedSourceJarname);
                    FileUtils.copyFile(sourceJar, extendSourceJar);
                    newSourceJars.add(extendSourceJar);
                }
            }
        }
        if (devLibDir.exists()) {
            FileUtils.deleteDirectory(devLibDir);
        }
        for (File newSourceJar : newSourceJars) {
            logger.debug("Add to development library " + newSourceJar.getPath());
            FileUtils.moveFileToDirectory(newSourceJar, devLibDir, true);
        }
    }

    private String getExtendedJarname(String jarname, PluginDescriptor pluginDescriptor) {
        return pluginDescriptor.getId() + "." + jarname;
    }

    @Override
    protected void doStop() throws Exception {
    }

}
