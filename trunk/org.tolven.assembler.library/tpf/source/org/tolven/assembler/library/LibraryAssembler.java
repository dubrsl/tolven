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
package org.tolven.assembler.library;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin allows connectors to be added to ear files
 * 
 * @author Joseph Isaac
 *
 */
public class LibraryAssembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_ABSTRACT_LIB = "abstractLib";

    public static final String CMD_LINE_LIB_PLUGIN_OPTION = "libPlugin";
    public static final String CMD_LINE_DESTDIR_OPTION = "destDir";

    private Logger logger = Logger.getLogger(LibraryAssembler.class);

    protected PluginDescriptor getAbstractLibPluginDescriptor() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_ABSTRACT_LIB);
        String parentPluginId = extensionPoint.getParentPluginId();
        PluginDescriptor pluginDescriptor = getManager().getRegistry().getPluginDescriptor(parentPluginId);
        return pluginDescriptor;
    }

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        String libPluginId = commandLine.getOptionValue(CMD_LINE_LIB_PLUGIN_OPTION);
        String destDirname = commandLine.getOptionValue(CMD_LINE_DESTDIR_OPTION);
        if (destDirname == null) {
            destDirname = new File(getStageDir(), libPluginId).getPath();
        }
        File destDir = new File(destDirname);
        destDir.mkdirs();
        PluginDescriptor libPluginDescriptor = getManager().getRegistry().getPluginDescriptor(libPluginId);
        ExtensionPoint abstractExtensionPoint = getAbstractLibPluginDescriptor().getExtensionPoint("lib");
        for (Extension libExtension : abstractExtensionPoint.getConnectedExtensions()) {
            if (libExtension.getDeclaringPluginDescriptor().getId().equals(libPluginDescriptor.getId())) {
                String libFilename = libExtension.getParameter("lib").valueAsString();
                File sourceLib = getFilePath(libPluginDescriptor, libFilename);
                logger.debug("Copy " + sourceLib.getPath() + " to " + destDir.getPath());
                FileUtils.copyFileToDirectory(sourceLib, destDir);
            }
        }
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
        Option libPluginOption = new Option(CMD_LINE_LIB_PLUGIN_OPTION, CMD_LINE_LIB_PLUGIN_OPTION, true, "lib plugin");
        libPluginOption.setRequired(true);
        cmdLineOptions.addOption(libPluginOption);
        Option destDirPluginOption = new Option(CMD_LINE_DESTDIR_OPTION, CMD_LINE_DESTDIR_OPTION, true, "destination directory");
        cmdLineOptions.addOption(destDirPluginOption);
        return cmdLineOptions;
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
