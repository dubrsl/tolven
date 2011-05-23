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
package org.tolven.assembler.glassfish3;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.tools.ant.TolvenJar;

/**
 * This plugin assembles all of the tolven specific configuration files for the GlassFish appserver
 * 
 * @author Joseph Isaac
 *
 */
public class GlassFish3Assembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_DB_PLUGIN_COMPONENT = "databasePlugin";
    public static final String EXTENSIONPOINT_LIBJAR_COMPONENT = "libJar";
    public static final String EXTENSIONPOINT_CONFIG_FILE_COMPONENT = "configFile";
    public static final String EXTENSIONPOINT_LIB_CLASSES = "classes";

    public static final String ATTRIBUTE_STAGE_LIB = "libDir";
    public static final String ATTRIBUTE_STAGE_CONFIG_DIR = "configDir";

    public static final String CMD_LINE_DESTDIR_OPTION = "destDir";

    private String appServerDirname;

    private Logger logger = Logger.getLogger(GlassFish3Assembler.class);

    protected String getAppServerDirname() {
        if (appServerDirname == null) {
            String appserverPathname = (String) evaluate("#{globalProperty['appserver.home']}", getDescriptor());
            File appserverDir = new File(appserverPathname);
            appServerDirname = appserverDir.getName();
        }
        return appServerDirname;
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
        File[] tmpFiles = getPluginTmpDir().listFiles();
        if (tmpFiles != null && tmpFiles.length > 0) {
            return;
        }
        executeRequiredPlugins(args);
        assembleLibClasses();
        assembleLibJars();
        assembleConfigFiles();
        copyToStageDir();
    }

    protected void executeRequiredPlugins(String[] args) throws Exception {
        ExtensionPoint dbPluginExtensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_DB_PLUGIN_COMPONENT);
        Extension dbPluginExtension = getSingleConnectedExtension(dbPluginExtensionPoint);
        String dbPluginDescriptor = dbPluginExtension.getDeclaringPluginDescriptor().getId();
        execute(dbPluginDescriptor, args);
    }

    protected void assembleLibClasses() {
        String relativeLibDirPath = getDescriptor().getAttribute(ATTRIBUTE_STAGE_LIB).getValue();
        File tmpStageLibDir = new File(getPluginTmpDir(), getAppServerDirname() + "/" + relativeLibDirPath);
        File jar = new File(tmpStageLibDir, getDescriptor().getId() + ".jar");
        jar.delete();
        jar.getParentFile().mkdirs();
        ExtensionPoint classesExtensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_LIB_CLASSES);
        for (Extension classesExtension : classesExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDescriptor = classesExtension.getDeclaringPluginDescriptor();
            String dirname = classesExtension.getParameter("dir").valueAsString();
            String eval_dirname = (String) evaluate(dirname, pluginDescriptor);
            if (eval_dirname == null) {
                throw new RuntimeException("plugin property: dir '" + dirname + "'evaluated to: null for: " + pluginDescriptor);
            }
            File dir = getFilePath(pluginDescriptor, dirname);
            TolvenJar.jarDir(dir, jar, true);
        }
    }

    protected void assembleLibJars() throws IOException {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_LIBJAR_COMPONENT);
        ExtensionPoint appServerExtensionPoint = getParentExtensionPoint(extensionPoint);
        String relativeLibExtDirPath = getDescriptor().getAttribute(ATTRIBUTE_STAGE_LIB).getValue();
        File tmpStageLibExtDir = new File(getPluginTmpDir(), getAppServerDirname() + "/" + relativeLibExtDirPath);
        for (Extension extension : appServerExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
            for (Parameter parameter : extension.getParameters("jar")) {
                File sourceJar = getFilePath(pluginDescriptor, parameter.valueAsString());
                File destJar = new File(tmpStageLibExtDir, sourceJar.getName());
                FileUtils.copyFile(sourceJar, destJar);
            }
        }
    }

    protected void assembleConfigFiles() throws IOException {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_CONFIG_FILE_COMPONENT);
        ExtensionPoint appServerExtensionPoint = getParentExtensionPoint(extensionPoint);
        String relativeConfigExtDirPath = getDescriptor().getAttribute(ATTRIBUTE_STAGE_CONFIG_DIR).getValue();
        File tmpStageConfigExtDir = new File(getPluginTmpDir(), getAppServerDirname() + "/" + relativeConfigExtDirPath);
        for (Extension extension : appServerExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
            for (Parameter parameter : extension.getParameters("file")) {
                File sourceJar = getFilePath(pluginDescriptor, parameter.valueAsString());
                File destJar = new File(tmpStageConfigExtDir, sourceJar.getName());
                FileUtils.copyFile(sourceJar, destJar);
            }
        }
    }

    protected void copyToStageDir() throws IOException {
        File tmpAppServerStageDir = new File(getPluginTmpDir(), getAppServerDirname());
        File stageAppServerDir = new File(getStageDir(), getAppServerDirname());
        logger.debug("Copy " + tmpAppServerStageDir.getPath() + " to " + stageAppServerDir.getPath());
        stageAppServerDir.mkdirs();
        FileUtils.copyDirectory(tmpAppServerStageDir, stageAppServerDir);
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
