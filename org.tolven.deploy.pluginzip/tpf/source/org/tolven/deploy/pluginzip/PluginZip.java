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
package org.tolven.deploy.pluginzip;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.plugin.registry.xml.ManifestParser;
import org.tolven.plugin.registry.xml.ModelPluginManifest;
import org.tolven.tools.ant.TolvenDependSet;
import org.tolven.tools.ant.TolvenZip;

/**
 * This plugin zips plugins to the correct name format i.e. pluginId-version.zip, based on the manifest entry
 * 
 * @author Joseph Isaac
 *
 */
public class PluginZip extends TolvenCommandPlugin {

    private Logger logger = Logger.getLogger(PluginZip.class);

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        Options cmdLineOptions = new Options();
        Option sourceDirOption = new Option("sourceDir", "sourceDir", true, "source directory");
        sourceDirOption.setRequired(true);
        cmdLineOptions.addOption(sourceDirOption);
        Option destinationDirOption = new Option("destDir", "destDir", true, "destination directory");
        destinationDirOption.setRequired(true);
        cmdLineOptions.addOption(destinationDirOption);
        Option excludesOption = new Option("excludes", "excludes", true, "comma-separated excludes list");
        cmdLineOptions.addOption(excludesOption);
        GnuParser parser = new GnuParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(cmdLineOptions, args);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(getClass().getName(), cmdLineOptions);
            throw new RuntimeException("Could not parse command line for: " + getClass().getName(), ex);
        }
        String sourceDirname = commandLine.getOptionValue("sourceDir");
        String destDirname = commandLine.getOptionValue("destDir");
        String excludes = commandLine.getOptionValue("excludes");
        zip(sourceDirname, destDirname, excludes);
    }

    public void zip(String sourceDirname, String destDirname, String excludes) throws Exception {
        File sourceDir = new File(sourceDirname);
        if (!sourceDir.exists()) {
            throw new RuntimeException("Could not locate: " + sourceDir.getPath());
        }
        logger.info("Source directory: " + sourceDir.getPath());
        File destDir = new File(destDirname);
        destDir.mkdirs();
        logger.info("Destination directory: " + destDir.getPath());
        List<File> files = Arrays.asList(sourceDir.listFiles());
        Comparator<File> comparator = new Comparator<File>() {
            public int compare(File file1, File file2) {
                return (file1.getPath().compareTo(file2.getPath()));
            };
        };
        Collections.sort(files, comparator);
        Set<String> basenames = new HashSet<String>();
        destDir.mkdirs();
        ManifestParser manifestParser = new ManifestParser(false);
        for (File file : files) {
            if (file.isDirectory()) {
                File pluginXML = new File(file, "tolven-plugin.xml");
                if (pluginXML.exists()) {
                    ModelPluginManifest pluginManifest = manifestParser.parseManifest(pluginXML.toURI().toURL());
                    String version = pluginManifest.getVersion().toString();
                    if (version == null || version.length() == 0) {
                        throw new RuntimeException(pluginXML.getPath() + " has no version");
                    }
                    File zipFile = new File(destDir, file.getName() + "-" + version + ".zip");
                    if (zipFile.exists()) {
                        TolvenDependSet.process(file, zipFile);
                        if (!zipFile.exists()) {
                            logger.info("Zipping: " + file.getPath());
                            TolvenZip.zip(file, zipFile, null, excludes);
                        }
                    } else {
                        logger.info("Zipping: " + file.getPath());
                        TolvenZip.zip(file, zipFile, null, excludes);
                    }
                    basenames.add(zipFile.getName());
                }
            }
        }
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** end ***");
    }

    public static void main(String[] args) throws Exception {
        (new PluginZip()).execute(args);
    }

}
