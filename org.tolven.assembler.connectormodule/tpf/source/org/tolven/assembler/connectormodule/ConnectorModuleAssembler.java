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
 * @version $Id: ConnectorModuleAssembler.java 1578 2011-07-08 00:12:49Z joe.isaac $
 */
package org.tolven.assembler.connectormodule;

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
public class ConnectorModuleAssembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_ABSTRACT_RAR = "abstractRAR";

    public static final String CMD_LINE_RAR_PLUGIN_OPTION = "rarPlugin";
    public static final String CMD_LINE_DESTDIR_OPTION = "destDir";

    private Logger logger = Logger.getLogger(ConnectorModuleAssembler.class);

    protected PluginDescriptor getAbstractRARPluginDescriptor() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_ABSTRACT_RAR);
        String parentPluginId = extensionPoint.getParentPluginId();
        PluginDescriptor pluginDescriptor = getManager().getRegistry().getPluginDescriptor(parentPluginId);
        return pluginDescriptor;
    }

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
        logger.debug("deleting: " + getPluginTmpDir());
        FileUtils.deleteDirectory(getPluginTmpDir());
        getPluginTmpDir().mkdirs();
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        String rarPluginId = commandLine.getOptionValue(CMD_LINE_RAR_PLUGIN_OPTION);
        String destDirname = commandLine.getOptionValue(CMD_LINE_DESTDIR_OPTION);
        if (destDirname == null) {
            destDirname = new File(getStageDir(), rarPluginId).getPath();
        }
        File destDir = new File(destDirname);
        destDir.mkdirs();
        PluginDescriptor rarPluginDescriptor = getManager().getRegistry().getPluginDescriptor(rarPluginId);
        ExtensionPoint abstractExtensionPoint = getAbstractRARPluginDescriptor().getExtensionPoint("rar");
        for (Extension rarExtension : abstractExtensionPoint.getConnectedExtensions()) {
            if (rarExtension.getDeclaringPluginDescriptor().getId().equals(rarPluginDescriptor.getId())) {
                String rarFilename = rarExtension.getParameter("rar").valueAsString();
                File sourceRAR = getFilePath(rarPluginDescriptor, rarFilename);
                logger.debug("Copy " + sourceRAR.getPath() + " to " + destDir.getPath());
                FileUtils.copyFileToDirectory(sourceRAR, destDir);
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
        Option rarPluginOption = new Option(CMD_LINE_RAR_PLUGIN_OPTION, CMD_LINE_RAR_PLUGIN_OPTION, true, "rar plugin");
        rarPluginOption.setRequired(true);
        cmdLineOptions.addOption(rarPluginOption);
        Option destDirPluginOption = new Option(CMD_LINE_DESTDIR_OPTION, CMD_LINE_DESTDIR_OPTION, true, "destination directory");
        cmdLineOptions.addOption(destDirPluginOption);
        return cmdLineOptions;
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
