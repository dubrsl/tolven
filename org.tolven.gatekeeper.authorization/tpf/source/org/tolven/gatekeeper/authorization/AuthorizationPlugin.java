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
package org.tolven.gatekeeper.authorization;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.tools.ant.TolvenSQL;

/**
 * This plugin outputs an authorization file (URLs)
 * 
 * @author Joseph Isaac
 *
 */
public class AuthorizationPlugin extends TolvenCommandPlugin {

    public static final String CMD_DBURL = "dbUrl";
    public static final String CMD_DRIVERCLASS = "driverClass";
    public static final String CMD_DRIVERCLASSPATH = "driverClasspath";
    public static final String CMD_PASSWORD = "password";
    public static final String CMD_SQLFILE = "sqlFile";
    public static final String CMD_USER = "user";

    private Logger logger = Logger.getLogger(AuthorizationPlugin.class);

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** stop ***");
    }

    @Override
    public void execute(String[] args) {
        logger.info("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        String url = commandLine.getOptionValue(CMD_DBURL);
        String driverClass = commandLine.getOptionValue(CMD_DRIVERCLASS);
        String driverClasspath = commandLine.getOptionValue(CMD_DRIVERCLASSPATH);
        String user = commandLine.getOptionValue(CMD_USER);
        char[] password = commandLine.getOptionValue(CMD_PASSWORD).toCharArray();
        String sqlFilename = commandLine.getOptionValue(CMD_SQLFILE);
        File sqlFile = null;
        if (sqlFilename == null) {
            String zipSqlFilename = "authorization.sql";
            sqlFile = getFilePath(zipSqlFilename);
            logger.info("Execute SQL file " + getPluginZip().getPath() + "!/" + zipSqlFilename + " with URL: " + url);
        } else {
            sqlFile = new File(sqlFilename);
            logger.info("Execute SQL file " + sqlFile.getPath() + " with URL: " + url);
        }
        TolvenSQL.sql(sqlFile, url, driverClass, user, password, driverClasspath, TolvenSQL.ABORT);
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
        Option dbUrlOption = new Option(CMD_DBURL, CMD_DBURL, true, "\"dbUrl\"");
        dbUrlOption.setRequired(true);
        cmdLineOptions.addOption(dbUrlOption);
        Option driverClassOption = new Option(CMD_DRIVERCLASS, CMD_DRIVERCLASS, true, "\"driverClass\"");
        driverClassOption.setRequired(true);
        cmdLineOptions.addOption(driverClassOption);
        Option userOption = new Option(CMD_USER, CMD_USER, true, "\"user\"");
        userOption.setRequired(true);
        cmdLineOptions.addOption(userOption);
        Option passwordOption = new Option(CMD_PASSWORD, CMD_PASSWORD, true, "\"password\"");
        passwordOption.setRequired(true);
        cmdLineOptions.addOption(passwordOption);
        Option sqlFileOption = new Option(CMD_SQLFILE, CMD_SQLFILE, true, "\"sqlFile\"");
        cmdLineOptions.addOption(sqlFileOption);
        Option driverClasspathOption = new Option(CMD_DRIVERCLASSPATH, CMD_DRIVERCLASSPATH, true, "\"driverClasspath\"");
        cmdLineOptions.addOption(driverClasspathOption);
        return cmdLineOptions;
    }

}
