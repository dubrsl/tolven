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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.tools.ant.TolvenJar;

/**
 * This plugin assembles all of the tolven specific configuration files for the GlassFish appserver
 * 
 * @author Joseph Isaac
 *
 */
public class GlassFish3Assembler extends TolvenCommandPlugin {

    public static final String EXNPT_DB_PLUGIN = "databasePlugin";
    public static final String EXNPT_LIBPROD_ADPTR = "libProduct-adaptor";
    public static final String EXNPT_LIBJAR = "libJar";
    public static final String EXNPT_CONFIG = "config";
    public static final String EXNPT_DEPLOY = "deploy";
    public static final String EXNPT_LIB_CLASSES = "classes";

    public static final String ATTR_STAGE_LIB = "libDir";
    public static final String ATTR_STAGE_CONFIG_DIR = "configDir";
    public static final String ATTR_STAGE_DEPLOY_DIR = "deployDir";

    public static final String CMD_LINE_DESTDIR_OPTION = "destDir";

    public static final String SRC_PLUGIN_ID = "source-plugin-id";
    public static final String EXNPT_ID = "extension-point";

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
        assembleLibProductAdaptors();
        assembleLibClasses();
        assembleConfigFiles();
        assembleDeployFiles();
        copyToStageDir();
    }

    protected void executeRequiredPlugins(String[] args) throws Exception {
        ExtensionPoint dbPluginExnPt = getDescriptor().getExtensionPoint(EXNPT_DB_PLUGIN);
        Extension dbPluginExn = getSingleConnectedExtension(dbPluginExnPt);
        String dbPD = dbPluginExn.getDeclaringPluginDescriptor().getId();
        execute(dbPD, args);
    }

    protected void assembleLibClasses() {
        String relativeLibDirPath = getDescriptor().getAttribute(ATTR_STAGE_LIB).getValue();
        File tmpStageLibDir = new File(getPluginTmpDir(), getAppServerDirname() + "/" + relativeLibDirPath);
        File destJar = new File(tmpStageLibDir, getDescriptor().getId() + ".jar");
        destJar.delete();
        destJar.getParentFile().mkdirs();
        ExtensionPoint classesExnPt = getDescriptor().getExtensionPoint(EXNPT_LIB_CLASSES);
        for (Extension classesExn : classesExnPt.getConnectedExtensions()) {
            PluginDescriptor pd = classesExn.getDeclaringPluginDescriptor();
            String srcDirname = classesExn.getParameter("dir").valueAsString();
            String eval_srcDirname = (String) evaluate(srcDirname, pd);
            if (eval_srcDirname == null) {
                throw new RuntimeException("plugin property: dir '" + srcDirname + "'evaluated to: null for: " + pd);
            }
            File srcDir = getFilePath(pd, srcDirname);
            TolvenJar.jarDir(srcDir, destJar, true);
        }
    }

    /**
     * Add libraries located by extension-point libProduct-adaptor to the appserver lib directory
     * 
     * @param pd
     * @throws IOException
     */
    protected void assembleLibProductAdaptors() throws IOException {
        ExtensionPoint exnPt = getDescriptor().getExtensionPoint(EXNPT_LIBPROD_ADPTR);
        ExtensionPoint appServerExnPt = getParentExtensionPoint(exnPt);
        String relativeLibExtDirPath = getDescriptor().getAttribute(ATTR_STAGE_LIB).getValue();
        File destDir = new File(getPluginTmpDir(), getAppServerDirname() + "/" + relativeLibExtDirPath);
        for (Extension exn : appServerExnPt.getConnectedExtensions()) {
            for (File src : getAdaptorFiles(exn)) {
                FileUtils.copyFileToDirectory(src, destDir);
            }
        }
    }

    protected void assembleConfigFiles() throws IOException {
        ExtensionPoint exnPt = getDescriptor().getExtensionPoint(EXNPT_CONFIG);
        ExtensionPoint appServerExnPt = getParentExtensionPoint(exnPt);
        String relativeConfigExtDirPath = getDescriptor().getAttribute(ATTR_STAGE_CONFIG_DIR).getValue();
        File destDir = new File(getPluginTmpDir(), getAppServerDirname() + "/" + relativeConfigExtDirPath);
        for (Extension exn : appServerExnPt.getConnectedExtensions()) {
            PluginDescriptor pd = exn.getDeclaringPluginDescriptor();
            for (Parameter param : exn.getParameters("file")) {
                File src = getFilePath(pd, param.valueAsString());
                FileUtils.copyFileToDirectory(src, destDir);
            }
            for (Parameter param : exn.getParameters("dir")) {
                File srcDir = getFilePath(pd, param.valueAsString());
                FileUtils.copyDirectory(srcDir, destDir);
            }
        }
    }

    protected void assembleDeployFiles() throws IOException {
        ExtensionPoint exnPt = getDescriptor().getExtensionPoint(EXNPT_DEPLOY);
        ExtensionPoint appServerExnPt = getParentExtensionPoint(exnPt);
        String relativeConfigExtDirPath = getDescriptor().getAttribute(ATTR_STAGE_DEPLOY_DIR).getValue();
        File destDir = new File(getPluginTmpDir(), getAppServerDirname() + "/" + relativeConfigExtDirPath);
        for (Extension exn : appServerExnPt.getConnectedExtensions()) {
            PluginDescriptor pd = exn.getDeclaringPluginDescriptor();
            for (Parameter param : exn.getParameters("file")) {
                File src = getFilePath(pd, param.valueAsString());
                FileUtils.copyFileToDirectory(src, destDir);
            }
            for (Parameter param : exn.getParameters("dir")) {
                File srcDir = getFilePath(pd, param.valueAsString());
                FileUtils.copyDirectoryToDirectory(srcDir, destDir);
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

    private List<File> getAdaptorFiles(Extension exn) {
        String pluginId = exn.getParameter(SRC_PLUGIN_ID).valueAsString();
        if (pluginId == null || pluginId.trim().length() == 0) {
            throw new RuntimeException("No parameter value for " + SRC_PLUGIN_ID + " found in " + exn.getUniqueId());
        }
        String exnPtId = exn.getParameter(EXNPT_ID).valueAsString();
        if (exnPtId == null || exnPtId.trim().length() == 0) {
            throw new RuntimeException("No parameter value for " + EXNPT_ID + " found in " + exn.getUniqueId());
        }
        ExtensionPoint exnPt = getManager().getRegistry().getExtensionPoint(pluginId + "@" + exnPtId);
        List<File> files = new ArrayList<File>();
        for (ParameterDefinition paramDef : exnPt.getParameterDefinitions()) {
            String filename = paramDef.getDefaultValue();
            if (filename == null || filename.trim().length() == 0) {
                throw new RuntimeException("No default-value for parameter-def found in " + exnPt.getUniqueId());
            }
            File src = getFilePath(exnPt.getDeclaringPluginDescriptor(), filename);
            files.add(src);
        }
        return files;
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
