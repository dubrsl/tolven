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
package org.tolven.assembler.pluginframework;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.plugin.repository.RepositoryMetadata;

/**
 * 
 * @author Joseph Isaac
 *
 */
public class PluginFrameworkAssembler extends TolvenCommandPlugin {

    public static final String CMD_LINE_NOOP_OPTION = "noop";
    public static final String CMD_LINE_FORMAT_OPTION = "formatPluginsFile";

    private Logger logger = Logger.getLogger(PluginFrameworkAssembler.class);

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        CommandLine commandLine = getCommandLine();
        if (commandLine.hasOption(CMD_LINE_NOOP_OPTION)) {
            //Which is the current default anyway i.e. do nothing
        } else if (commandLine.hasOption(CMD_LINE_FORMAT_OPTION)) {
            String[] optionValues = commandLine.getOptionValues(CMD_LINE_FORMAT_OPTION);
            String inputFilname = optionValues[0];
            File pluginsXMLFile = new File(inputFilname);
            if (!pluginsXMLFile.exists()) {
                throw new RuntimeException("Could not find plugins.xml file: " + pluginsXMLFile.getPath());
            }
            if (optionValues.length < 2) {
                throw new RuntimeException("A second argument is required for the destination of the formatted plugins.xml");
            }
            String outputFilename = optionValues[1];
            File outputFile = new File(outputFilename);
            if (outputFile.getParentFile() == null) {
                //A relative path
                String userDir = System.getProperty("user.dir");
                outputFile = new File(userDir, outputFile.getPath());
            }
            outputFile.getParentFile().mkdirs();
            String outputPluginsXML = RepositoryMetadata.format(pluginsXMLFile.toURI().toURL());
            logger.info("Output to: " + outputFile.getPath());
            FileUtils.writeStringToFile(outputFile, outputPluginsXML);
        }
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

    private Options getCommandOptions() {
        Options cmdLineOptions = new Options();
        OptionGroup optionGroup = new OptionGroup();
        Option noopOption = new Option(CMD_LINE_NOOP_OPTION, CMD_LINE_NOOP_OPTION, false, "no op plugin starts the plugin framework as a test, but does nothing");
        optionGroup.addOption(noopOption);
        Option formatOption = new Option(CMD_LINE_FORMAT_OPTION, CMD_LINE_FORMAT_OPTION, false, "formatPluginsFile inputPlugins.xml outputPlugins.xml");
        formatOption.setArgs(2);
        optionGroup.addOption(formatOption);
        optionGroup.setRequired(true);
        cmdLineOptions.addOptionGroup(optionGroup);
        return cmdLineOptions;
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** end ***");
    }

}
