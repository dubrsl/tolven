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
package org.tolven.plugin.repository;

import java.io.InputStream;
import java.net.URL;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xml.sax.InputSource;

public class TolvenManifestParser {

    public static final String CMD_LINE_URL_OPTION = "url";
    public static final String CMD_LINE_PLUGIN_ID_OPTION = "pluginId";
    public static final String CMD_LINE_VERSION_OPTION = "pluginVersion";

    private static CommandLine getCommandLine(String[] args) {
        GnuParser parser = new GnuParser();
        try {
            return parser.parse(getCommandOptions(), args, true);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(TolvenManifestParser.class.getName(), getCommandOptions());
            throw new RuntimeException("Could not parse command line for: " + TolvenManifestParser.class.getName(), ex);
        }
    }

    private static Options getCommandOptions() {
        Options cmdLineOptions = new Options();
        Option urlOption = new Option(CMD_LINE_URL_OPTION, CMD_LINE_URL_OPTION, true, "manifest url");
        urlOption.setRequired(true);
        cmdLineOptions.addOption(urlOption);
        OptionGroup optionGroup = new OptionGroup();
        Option pluginIdOption = new Option(CMD_LINE_PLUGIN_ID_OPTION, CMD_LINE_PLUGIN_ID_OPTION, false, "manifest plugin id");
        optionGroup.addOption(pluginIdOption);
        Option versionOption = new Option(CMD_LINE_VERSION_OPTION, CMD_LINE_VERSION_OPTION, false, "manifest version");
        optionGroup.addOption(versionOption);
        cmdLineOptions.addOptionGroup(optionGroup);
        return cmdLineOptions;
    }

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = getCommandLine(args);
        String urlString = commandLine.getOptionValue(CMD_LINE_URL_OPTION);
        URL pluginManifestURL = new URL(urlString);
        XPathExpression expr = null;
        if (commandLine.hasOption(CMD_LINE_PLUGIN_ID_OPTION)) {
            expr = XPathFactory.newInstance().newXPath().compile("/urn:tolven-org:tpf:1.0:plugin/@id");
        } else {
            expr = XPathFactory.newInstance().newXPath().compile("/urn:tolven-org:tpf:1.0:plugin/@version");
        }
        InputStream in = null;
        try {
            in = pluginManifestURL.openStream();
            System.out.println(expr.evaluate(new InputSource(pluginManifestURL.openStream())));
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

}
