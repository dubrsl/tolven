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
package org.tolven.deploy.plugincopy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.tolven.logging.TolvenLogger;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.plugin.TolvenPlugin;
import org.tolven.plugin.registry.xml.bean.Plugin;
import org.tolven.plugin.registry.xml.bean.PluginFragment;
import org.tolven.plugin.registry.xml.bean.Requires;
import org.tolven.plugin.repository.RepositoryMetadata;
import org.tolven.plugin.repository.bean.PluginDetail;
import org.tolven.plugin.repository.bean.PluginVersionDetail;
import org.tolven.plugin.repository.bean.Plugins;
import org.tolven.tools.ant.TolvenZip;

/**
 * This plugin deploys the JBoss configuration files.
 * 
 * @author Joseph Isaac
 *
 */
public class PluginCopy extends TolvenCommandPlugin {

    public static final String CMD_LINE_SRC_REPOSITORYURL_OPTION = "src";
    public static final String CMD_LINE_DEST_DIR_OPTION = "dest";
    public static final String CMD_LINE_SRC_PLUGIN_ID_OPTION = "srcId";
    public static final String CMD_LINE_SRC_PLUGIN_VERSION_OPTION = "srcVersion";
    public static final String CMD_LINE_DEST_PLUGIN_ID_OPTION = "destId";
    public static final String CMD_LINE_DEST_PLUGIN_VERSION_OPTION = "destVersion";
    public static final String DEFAULT_DEST_VERSION = "0.0.1";

    @Override
    protected void doStart() throws Exception {
        TolvenLogger.info("*** start ***", PluginCopy.class);
    }

    private Options getCommandOptions() {
        Options cmdLineOptions = new Options();
        Option sourceRepositoryURLOption = new Option(CMD_LINE_SRC_REPOSITORYURL_OPTION, "srcRepositoryURL", true, "src repository URL");
        sourceRepositoryURLOption.setRequired(true);
        cmdLineOptions.addOption(sourceRepositoryURLOption);
        Option sourcePluginIdOption = new Option(CMD_LINE_SRC_PLUGIN_ID_OPTION, "srcPluginId", true, "src plugin id");
        sourcePluginIdOption.setRequired(true);
        cmdLineOptions.addOption(sourcePluginIdOption);
        Option sourcePluginVersionOption = new Option(CMD_LINE_SRC_PLUGIN_VERSION_OPTION, "srcPluginVersion", true, "src plugin version");
        cmdLineOptions.addOption(sourcePluginVersionOption);
        Option destinationRepositoryURLOption = new Option(CMD_LINE_DEST_DIR_OPTION, "destRepositoryURL", true, "dest repository URL");
        destinationRepositoryURLOption.setRequired(true);
        cmdLineOptions.addOption(destinationRepositoryURLOption);
        Option destinationPluginIdOption = new Option(CMD_LINE_DEST_PLUGIN_ID_OPTION, "destPluginId", true, "destiation plugin id");
        destinationPluginIdOption.setRequired(true);
        cmdLineOptions.addOption(destinationPluginIdOption);
        Option destinationPluginVersionOption = new Option(CMD_LINE_DEST_PLUGIN_VERSION_OPTION, "destPluginVersion", true, "dest plugin version");
        cmdLineOptions.addOption(destinationPluginVersionOption);
        return cmdLineOptions;
    }

    @Override
    public void execute(String[] args) throws Exception {
        Options cmdLineOptions = getCommandOptions();
        try {
            GnuParser parser = new GnuParser();
            CommandLine commandLine = parser.parse(cmdLineOptions, TolvenPlugin.getInitArgs());
            String srcRepositoryURLString = commandLine.getOptionValue(CMD_LINE_SRC_REPOSITORYURL_OPTION);
            Plugins libraryPlugins = RepositoryMetadata.getRepositoryPlugins(new URL(srcRepositoryURLString));
            String srcPluginId = commandLine.getOptionValue(CMD_LINE_SRC_PLUGIN_ID_OPTION);
            PluginDetail plugin = RepositoryMetadata.getPluginDetail(srcPluginId, libraryPlugins);
            if (plugin == null) {
                throw new RuntimeException("Could not locate plugin: " + srcPluginId + " in repository: " + srcRepositoryURLString);
            }
            String srcPluginVersionString = commandLine.getOptionValue(CMD_LINE_SRC_PLUGIN_VERSION_OPTION);
            PluginVersionDetail srcPluginVersion = null;
            if (srcPluginVersion == null) {
                srcPluginVersion = RepositoryMetadata.getLatestVersion(plugin);
            } else {
                srcPluginVersion = RepositoryMetadata.getPluginVersionDetail(srcPluginVersionString, plugin);
            }
            if (plugin == null) {
                throw new RuntimeException("Could not find a plugin version for: " + srcPluginId + " in repository: " + srcRepositoryURLString);
            }
            String destPluginId = commandLine.getOptionValue(CMD_LINE_DEST_PLUGIN_ID_OPTION);
            FileUtils.deleteDirectory(getPluginTmpDir());
            URL srcURL = new URL(srcPluginVersion.getUri());
            File newPluginDir = new File(getPluginTmpDir(), destPluginId);
            try {
                InputStream in = null;
                FileOutputStream out = null;
                File tmpZip = new File(getPluginTmpDir(), new File(srcURL.getFile()).getName());
                try {
                    in = srcURL.openStream();
                    out = new FileOutputStream(tmpZip);
                    IOUtils.copy(in, out);
                    TolvenZip.unzip(tmpZip, newPluginDir);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (tmpZip != null) {
                        tmpZip.delete();
                    }
                }
                File pluginManifestFile = new File(newPluginDir, "tolven-plugin.xml");
                if (!pluginManifestFile.exists()) {
                    throw new RuntimeException(srcURL.toExternalForm() + "has no plugin manifest");
                }
                Plugin pluginManifest = RepositoryMetadata.getPlugin(pluginManifestFile.toURI().toURL());
                pluginManifest.setId(destPluginId);
                String destPluginVersion = commandLine.getOptionValue(CMD_LINE_DEST_PLUGIN_VERSION_OPTION);
                if (destPluginVersion == null) {
                    destPluginVersion = DEFAULT_DEST_VERSION;
                }
                pluginManifest.setVersion(destPluginVersion);
                String pluginManifestXML = RepositoryMetadata.getPluginManifest(pluginManifest);
                FileUtils.writeStringToFile(pluginManifestFile, pluginManifestXML);
                File pluginFragmentManifestFile = new File(newPluginDir, "tolven-plugin-fragment.xml");
                if (pluginFragmentManifestFile.exists()) {
                    PluginFragment pluginManifestFragment = RepositoryMetadata.getPluginFragment(pluginFragmentManifestFile.toURI().toURL());
                    Requires requires = pluginManifestFragment.getRequires();
                    if (requires == null) {
                        throw new RuntimeException("No <requires> detected for plugin fragment in: " + srcURL.toExternalForm());
                    }
                    if (requires.getImport().size() != 1) {
                        throw new RuntimeException("There should be only one import for plugin fragment in: " + srcURL.toExternalForm());
                    }
                    requires.getImport().get(0).setPluginId(destPluginId);
                    requires.getImport().get(0).setPluginVersion(destPluginVersion);
                    String pluginFragmentManifestXML = RepositoryMetadata.getPluginFragmentManifest(pluginManifestFragment);
                    FileUtils.writeStringToFile(pluginFragmentManifestFile, pluginFragmentManifestXML);
                }
                String destDirname = commandLine.getOptionValue(CMD_LINE_DEST_DIR_OPTION);
                File destDir = new File(destDirname);
                File destZip = new File(destDir, destPluginId + "-" + destPluginVersion + ".zip");
                destDir.mkdirs();
                TolvenZip.zip(newPluginDir, destZip);
            } finally {
                if (newPluginDir != null) {
                    FileUtils.deleteDirectory(newPluginDir);
                }
            }
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(getClass().getName(), cmdLineOptions);
            throw new RuntimeException("Could not parse command line for: " + getClass().getName(), ex);
        }
    }

    @Override
    protected void doStop() throws Exception {
        TolvenLogger.info("*** end ***", PluginCopy.class);
    }

}
