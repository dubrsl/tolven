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
 */
package org.tolven.opends.config;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.tools.ant.TolvenZip;

/**
 * This plugin outputs files required to configure OpenDS
 * 
 * @author Joseph Isaac
 *
 */
public class OpenDSConfigPlugin extends TolvenCommandPlugin {

    public static final String CMD_UNZIP = "unzip";
    public static final String CMD_DIRECTORY = "dir";

    private Logger logger = Logger.getLogger(OpenDSConfigPlugin.class);

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    @Override
    public void execute(String[] args) {
        logger.info("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        String dirname = commandLine.getOptionValue(CMD_DIRECTORY);
        if(dirname == null) {
            throw new RuntimeException("A value for the dir option must be supplied");
        }
        File pluginZip = getPluginZip();
        File destDir = new File(dirname);
        TolvenZip.unzip(pluginZip, destDir);
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
        Option unzipOption = new Option(CMD_UNZIP, CMD_UNZIP, false, "\"unzip\"");
        unzipOption.setRequired(true);
        cmdLineOptions.addOption(unzipOption);
        Option dirOption = new Option(CMD_DIRECTORY, CMD_DIRECTORY, true, "\"dir\"");
        dirOption.setRequired(true);
        cmdLineOptions.addOption(dirOption);
        return cmdLineOptions;
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** stop ***");
    }

}
