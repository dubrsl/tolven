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
package org.tolven.scheduler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.scheduler.model.Scheduler;

/**
 * This plugin provides commands to the TolvenMessage scheduler
 * 
 * @author Joseph Isaac
 *
 */
public class SchedulerPlugin extends TolvenCommandPlugin {

    public static final String CMD_LINE_START_OPTION = "start";
    public static final String CMD_LINE_STOP_OPTION = "stop";
    public static final String CMD_LINE_STATUS_OPTION = "status";

    private Logger logger = Logger.getLogger(SchedulerPlugin.class);

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    @Override
    public void execute(String[] args) {
        logger.info("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        Scheduler scheduler = getScheduler();
        if (commandLine.hasOption(CMD_LINE_START_OPTION)) {
            String[] values = commandLine.getOptionValues(CMD_LINE_START_OPTION);
            //String schedulerBeanId = values[0];
            String intervalDurationString = values[1];
            Long intervalDuration = Long.parseLong(intervalDurationString);
            scheduler.start(intervalDuration);
        } else if (commandLine.hasOption(CMD_LINE_STOP_OPTION)) {
            scheduler.stop();
        } else if (commandLine.hasOption(CMD_LINE_STATUS_OPTION)) {
            scheduler.timeout();
        }
    }

    private Scheduler getScheduler() {
        String adminId = getTolvenConfigWrapper().getAdminId();
        char[] adminPassword = getTolvenConfigWrapper().getAdminPassword();
        String appRestfulURL = getTolvenConfigWrapper().getApplication().getAppRestfulURL();
        String authRestfulURL = getTolvenConfigWrapper().getApplication().getAuthRestfulURL();
        return new Scheduler(appRestfulURL, authRestfulURL, adminId, adminPassword);
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
        Option startOption = new Option(CMD_LINE_START_OPTION, CMD_LINE_START_OPTION, true, "\"-start schedulerId intervalDuration(ms)\"");
        startOption.setArgs(2);
        optionGroup.addOption(startOption);
        Option stopOption = new Option(CMD_LINE_STOP_OPTION, CMD_LINE_STOP_OPTION, true, "\"-stop schedulerId\"");
        optionGroup.addOption(stopOption);
        Option statusOption = new Option(CMD_LINE_STATUS_OPTION, CMD_LINE_STATUS_OPTION, true, "\"-status schedulerId\"");
        optionGroup.addOption(statusOption);
        optionGroup.setRequired(true);
        cmdLineOptions.addOptionGroup(optionGroup);
        return cmdLineOptions;
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** stop ***");
    }

}
