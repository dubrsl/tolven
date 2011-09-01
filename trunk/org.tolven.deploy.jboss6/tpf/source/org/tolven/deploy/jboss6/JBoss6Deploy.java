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
 * @version $Id: JBoss6Deploy.java 1604 2011-07-08 00:49:30Z joe.isaac $
 */
package org.tolven.deploy.jboss6;

import java.io.File;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.security.hash.TolvenMessageDigest;

/**
 * This plugin deploys the JBoss configuration files.
 * 
 * @author Joseph Isaac
 *
 */
public class JBoss6Deploy extends TolvenCommandPlugin {

    public static final String ATTRIBUTE_DEPLOY_DIR = "deployDir";

    public static final String CMD_LINE_EAR_PLUGINS_OPTION = "earPlugins";
    public static final String CMD_LINE_RAR_PLUGINS_OPTION = "rarPlugins";
    public static final String CMD_LINE_CONFIG_OPTION = "config";

    public static final String MESSAGE_DIGEST_ALGORITHM = "md5";

    private Logger logger = Logger.getLogger(JBoss6Deploy.class);

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        String appserverHomeDirname = (String) evaluate("#{globalProperty['appserver.home']}", getDescriptor());
        File appserverHomeDir = new File(appserverHomeDirname);
        if (!appserverHomeDir.exists()) {
            throw new RuntimeException("appserver home does not exist at: " + appserverHomeDir.getPath());
        }
        boolean deployConfigFiles = commandLine.hasOption(CMD_LINE_CONFIG_OPTION);
        if (deployConfigFiles) {
            deployConfig();
        }
        String earPluginIds = commandLine.getOptionValue(CMD_LINE_EAR_PLUGINS_OPTION);
        if (earPluginIds != null) {
            deployEarFiles(earPluginIds.split(","), appserverHomeDir);
        }
        String rarPluginIds = commandLine.getOptionValue(CMD_LINE_RAR_PLUGINS_OPTION);
        if (rarPluginIds != null) {
            deployRarFiles(rarPluginIds.split(","), appserverHomeDir);
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
        Option rarPluginsOption = new Option(CMD_LINE_RAR_PLUGINS_OPTION, CMD_LINE_RAR_PLUGINS_OPTION, true, "comma-separated list of rar plugins");
        cmdLineOptions.addOption(rarPluginsOption);
        Option earPluginsOption = new Option(CMD_LINE_EAR_PLUGINS_OPTION, CMD_LINE_EAR_PLUGINS_OPTION, true, "comma-separated list of ear plugins");
        cmdLineOptions.addOption(earPluginsOption);
        Option configOption = new Option(CMD_LINE_CONFIG_OPTION, CMD_LINE_CONFIG_OPTION, false, "glassfish configuration files");
        cmdLineOptions.addOption(configOption);
        return cmdLineOptions;
    }

    protected void deployConfig() throws Exception {
        String appserverHomeDirname = (String) evaluate("#{globalProperty['appserver.home']}", getDescriptor());
        File appserverHomeDir = new File(appserverHomeDirname);
        if (!appserverHomeDir.exists()) {
            throw new RuntimeException("appserver home does not exist at: " + appserverHomeDir.getPath());
        }
        File appserverStageDir = new File(getStageDir(), appserverHomeDir.getName());
        if (!appserverStageDir.exists()) {
            appserverStageDir.mkdirs();
        }
        Collection<File> stageFiles = FileUtils.listFiles(appserverStageDir, null, true);
        for (File stageFile : stageFiles) {
            String relativeStageFilename = stageFile.getPath().substring(appserverStageDir.getPath().length());
            File deployedFile = new File(appserverHomeDir, relativeStageFilename);
            if (deployedFile.exists()) {
                String stageFileDigest = TolvenMessageDigest.checksum(stageFile.toURI().toURL(), MESSAGE_DIGEST_ALGORITHM);
                String deployedFileDigest = TolvenMessageDigest.checksum(deployedFile.toURI().toURL(), MESSAGE_DIGEST_ALGORITHM);
                if (deployedFileDigest.equals(stageFileDigest)) {
                    continue;
                }
            }
            logger.info("Deploy " + stageFile.getPath() + " to " + deployedFile.getPath());
            FileUtils.copyFile(stageFile, deployedFile);
        }
    }

    protected void deployRarFiles(String[] rarPlugins, File appserverHomeDir) throws Exception {
        Collection<File> filesToDeploy = null;
        for (String rarPlugin : rarPlugins) {
            File rarDir = new File(getStageDir(), rarPlugin);
            if(!rarDir.exists()) {
                throw new RuntimeException(rarDir.getPath() + " does not exist");
            }
            filesToDeploy = FileUtils.listFiles(rarDir, null, true);
            deploy(filesToDeploy, rarDir, appserverHomeDir);
        }
    }

    protected void deployEarFiles(String[] earPlugins, File appserverHomeDir) throws Exception {
        Collection<File> filesToDeploy = null;
        for (String earPlugin : earPlugins) {
            File earDir = new File(getStageDir(), earPlugin);
            if(!earDir.exists()) {
                throw new RuntimeException(earDir.getPath() + " does not exist");
            }
            filesToDeploy = FileUtils.listFiles(earDir, null, true);
            deploy(filesToDeploy, earDir, appserverHomeDir);
        }
    }

    protected void deploy(Collection<File> filesToDeploy, File sourceDir, File appserverHomeDir) throws Exception {
        String relativeDeployDirPath = getDescriptor().getAttribute(ATTRIBUTE_DEPLOY_DIR).getValue();
        File deployDir = new File(appserverHomeDir, relativeDeployDirPath);
        for (File stageFile : filesToDeploy) {
            String relativeStageFilename = stageFile.getPath().substring(1 + sourceDir.getPath().length());
            File deployedFile = new File(deployDir, relativeStageFilename);
            if (deployedFile.exists()) {
                String stageFileDigest = TolvenMessageDigest.checksum(stageFile.toURI().toURL(), MESSAGE_DIGEST_ALGORITHM);
                String deployedFileDigest = TolvenMessageDigest.checksum(deployedFile.toURI().toURL(), MESSAGE_DIGEST_ALGORITHM);
                if (deployedFileDigest.equals(stageFileDigest)) {
                    continue;
                }
            }
            logger.debug("Deploy " + stageFile.getPath() + " to " + deployedFile.getPath());
            FileUtils.copyFile(stageFile, deployedFile);
        }
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
