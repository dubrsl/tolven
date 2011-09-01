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
 * @version $Id: Application.java,v 1.4.2.6 2010/09/10 07:31:17 joseph_isaac Exp $
 */
package org.tolven.component.application;

import java.io.File;

import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.tolven.client.load.LoadAccountTypes;
import org.tolven.client.load.LoadReports;
import org.tolven.client.load.LoadRules;
import org.tolven.client.load.LoadTRIM;
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin configures the application, by interacting with the server e.g. loading applications, trims etc
 * 
 * @author Joseph Isaac
 *
 */
public class Application extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_APPLICATIONS = "applications";
    public static final String EXTENSIONPOINT_RULES = "rules";
    public static final String EXTENSIONPOINT_TRIMS = "trims";
    public static final String EXTENSIONPOINT_REPORTS = "reports";

    private Logger logger = Logger.getLogger(Application.class);

    private String getAdminId() {
        return getTolvenConfigWrapper().getAdminId();
    }

    private char[] getAdminPassword() {
        return getTolvenConfigWrapper().getAdminPassword();
    }

    private String getAppRestfulRootURL() {
        return getTolvenConfigWrapper().getApplication().getAppRestfulURL();
    }

    private String getAuthRestfulRootURL() {
        return getTolvenConfigWrapper().getApplication().getAuthRestfulURL();
    }

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    /**
     * Find all specified .application.xml files and send them to the ApplicationMetadata session bean on the server for processing.
     * @throws Exception
     */
    public void loadAccountTypes() throws Exception {
        logger.info("Load accountTypes...");
        LoadAccountTypes loadAccountTypes = new LoadAccountTypes(getAdminId(), getAdminPassword(), getAppRestfulRootURL(), getAuthRestfulRootURL());
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_APPLICATIONS);
        for (Extension extension : extensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
            Parameter dirParam = extension.getParameter("dir");
            if (dirParam==null || dirParam.valueAsString()==null) {
            	throw new RuntimeException( extension.getUniqueId() + " must have dir parameter value" );
            }
            String dirname = (String) evaluate(dirParam.valueAsString(), pluginDescriptor);
            File dir = new File(dirname);
            File sourceDir = null;
            if (dir.getPath().equals(dir.getAbsolutePath())) {
                sourceDir = dir;
            } else {
                sourceDir = getFilePath(pluginDescriptor, dirname);
            }
            if (!sourceDir.exists()) {
                throw new RuntimeException(sourceDir.getPath() + " does not exist");
            }
            loadAccountTypes.addDirectory( sourceDir );
            logger.info("Source directory is " + sourceDir.getPath());
        }
        loadAccountTypes.uploadApplications(getAdminId());
        logger.info("...Load accountTypes completed");
    }
    private void loadRules( ExtensionPoint extensionPoint ) throws Exception {
        for (Extension extension : extensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
            String dirname = (String) evaluate(extension.getParameter("dir").valueAsString(), pluginDescriptor);
            File dir = new File(dirname);
            File sourceDir = null;
            if (dir.getPath().equals(dir.getAbsolutePath())) {
                sourceDir = dir;
            } else {
                sourceDir = getFilePath(pluginDescriptor, dirname);
            }
            if (!sourceDir.exists()) {
                throw new RuntimeException(sourceDir.getPath() + " does not exist");
            }
            logger.info("Source directory is " + sourceDir.getPath());
            LoadRules loadRules = new LoadRules(getAdminId(), getAdminPassword(), getAppRestfulRootURL(), getAuthRestfulRootURL());
            logger.info("Load rules...");
            loadRules.uploadFromDirectory(sourceDir.getPath());
            logger.info("Load rules completed");
        }
    }
    /**
     * Load Rules looking for rules in the application directory and in the rules directory.
     * It is not usually a good idea to put them in both places, but we do this for legacy purposes.
     * @throws Exception
     */
    public void loadRules() throws Exception {
        loadRules( getDescriptor().getExtensionPoint(EXTENSIONPOINT_APPLICATIONS));
        loadRules( getDescriptor().getExtensionPoint(EXTENSIONPOINT_RULES));
    }

    public void loadTrims() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_TRIMS);
        logger.info("Load trims...");
        LoadTRIM loadTrim = new LoadTRIM(getAdminId(), getAdminPassword(), getAppRestfulRootURL(), getAuthRestfulRootURL());
        for (Extension extension : extensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
            String dirname = (String) evaluate(extension.getParameter("dir").valueAsString(), pluginDescriptor);
            File dir = new File(dirname);
            File sourceDir = null;
            if (dir.getPath().equals(dir.getAbsolutePath())) {
                sourceDir = dir;
            } else {
                sourceDir = getFilePath(pluginDescriptor, dirname);
            }
            if (!sourceDir.exists()) {
                throw new RuntimeException(sourceDir.getPath() + " does not exist");
            }
            logger.info("Source directory is " + sourceDir.getPath());
            loadTrim.uploadFromDirectory(sourceDir.getPath());
        }
        loadTrim.activate();
        logger.info("Load trims completed");

    }

    public void loadReports() throws Exception {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_REPORTS);
        for (Extension extension : extensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
            String dirname = (String) evaluate(extension.getParameter("dir").valueAsString(), pluginDescriptor);
            File dir = new File(dirname);
            File sourceDir = null;
            if (dir.getPath().equals(dir.getAbsolutePath())) {
                sourceDir = dir;
            } else {
                sourceDir = getFilePath(pluginDescriptor, dirname);
            }
            if (!sourceDir.exists()) {
                throw new RuntimeException(sourceDir.getPath() + " does not exist");
            }
            logger.info("Source directory is " + sourceDir.getPath());
            LoadReports loadReports = new LoadReports(getAdminId(), getAdminPassword(), getAppRestfulRootURL(), getAuthRestfulRootURL(), sourceDir.getPath());
            logger.info("load reports...");
            loadReports.uploadFromDirectory();
            logger.info("load reports completed");
        }
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** end ***");
    }

	@Override
	public void execute(String[] args) throws Exception {
        logger.info("*** execute ***");
        logger.info("Starting for user: " + getAdminId());
//        updateMDBUser();
        loadAccountTypes();
        loadRules();
        loadTrims();
        loadReports();
	}

}
