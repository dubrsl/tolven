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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.plugin.repository.bean.PluginDetail;
import org.tolven.plugin.repository.bean.PluginPropertyDetail;
import org.tolven.plugin.repository.bean.Plugins;

/**
 * This class acts as a wrapper for the JAXB Plugins class.
 * 
 * @author Joseph Isaac
 *
 */
public class ConfigPluginsWrapper {

    public static final String CONFIG_DIR = "config.dir";
    public static final String BUILD_DIR = "build";
    public static final String REPOSITORY_LIBRARY = "repositoryLibrary";
    public static final String REPOSITORY_TRUNK_METADATA = "trunkMetadata";
    public static final String REPOSITORY_SNAPSHOT_METADATA = "snapshotMetadata";
    public static final String USE_SNAPSHOT = "useSnapshot";
    public static final String OVERWRITE_SNAPSHOT = "overwriteSnapshot";
    public static final String REPOSITORY_DEVLIB = "repositoryDevLib";
    public static final String REPOSITORY_RUNTIME = "repositoryRuntime";
    public static final String REPOSITORY_RUNTIME_UNPACKED = "repositoryRuntimeUnpacked";
    public static final String REPOSITORY_STAGE = "repositoryStage";
    public static final String REPOSITORY_TMP = "repositoryTmp";
    public static final String LOG4JCONFIGURATION = "log4JConfiguration";
    public static final String LOGFILE = "logFile";
    public static final String LIBRARY_PLUGS_DIR = "plugins";
    public static final String INSTALL_DIR = "installation.dir";

    private File pluginsFile;
    private Plugins plugins;
    private ExpressionEvaluator ee;
    private Logger logger = Logger.getLogger(ConfigPluginsWrapper.class);
    private Map<String, Map<String, String>> pluginPropertyMap = new HashMap<String, Map<String, String>>();

    public ConfigPluginsWrapper() {
    }

    public File getPluginsFile() {
        return pluginsFile;
    }

    public void setPluginsFile(File pluginsFile) {
        this.pluginsFile = pluginsFile;
    }

    public Plugins getPlugins() {
        return plugins;
    }

    public void setPlugins(Plugins plugins) {
        this.plugins = plugins;
    }

    private String getProperty(String name) {
        return getProperty(name, null);
    }

    private String getProperty(String name, String defaultValue) {
        for (PluginPropertyDetail pluginProperty : getPlugins().getProperty()) {
            if (pluginProperty.getName().equals(name)) {
                return pluginProperty.getValue();
            }
        }
        return defaultValue;
    }

    public PluginPropertyDetail getPluginProperty(String name) {
        for (PluginPropertyDetail pluginProperty : getPlugins().getProperty()) {
            if (pluginProperty.getName().equals(name)) {
                return pluginProperty;
            }
        }
        return null;
    }

    public File getConfigDir() {
        String value = getProperty(CONFIG_DIR);
        if (value == null) {
            throw new RuntimeException("Plugin property " + CONFIG_DIR + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public File getInstallDir() {
        String value = getProperty(INSTALL_DIR);
        if (value == null) {
            throw new RuntimeException("Plugin property " + INSTALL_DIR + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public File getBuildDir() {
        return new File(getConfigDir(), BUILD_DIR);
    }

    public File getRepositoryRuntimeDir() {
        String value = getProperty(REPOSITORY_RUNTIME);
        if (value == null) {
            throw new RuntimeException("Plugin property " + REPOSITORY_RUNTIME + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public File getRepositoryRuntimePluginsDir() {
        String value = getProperty(REPOSITORY_RUNTIME);
        if (value == null) {
            throw new RuntimeException("Plugin property " + REPOSITORY_RUNTIME + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(getRepositoryRuntimeDir(), LIBRARY_PLUGS_DIR);
    }

    public File getRepositoryRuntimeUnpackedDir() {
        String value = getProperty(REPOSITORY_RUNTIME_UNPACKED);
        if (value == null) {
            throw new RuntimeException("Plugin property " + REPOSITORY_RUNTIME_UNPACKED + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public File getRepositoryTmpDir() {
        String value = getProperty(REPOSITORY_TMP);
        if (value == null) {
            throw new RuntimeException("Plugin property " + REPOSITORY_TMP + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public File getRepositoryStageDir() {
        String value = getProperty(REPOSITORY_STAGE);
        if (value == null) {
            throw new RuntimeException("Plugin property " + REPOSITORY_STAGE + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public File getRepositoryDevLibDir() {
        String value = getProperty(REPOSITORY_DEVLIB);
        if (value == null) {
            throw new RuntimeException("Plugin property " + REPOSITORY_DEVLIB + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public PluginPropertyDetail getRepositoryLibrary() {
        PluginPropertyDetail pluginProperty = getPluginProperty(REPOSITORY_LIBRARY);
        if (pluginProperty == null) {
            throw new RuntimeException("Plugin property " + REPOSITORY_LIBRARY + " is not defined");
        } else {
            return pluginProperty;
        }
    }

    public File getLog4JConfiguration() {
        String value = getProperty(LOG4JCONFIGURATION);
        if (value == null) {
            throw new RuntimeException("Plugin property " + LOG4JCONFIGURATION + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public File getLogFile() {
        String value = getProperty(LOGFILE);
        if (value == null) {
            throw new RuntimeException("Plugin property " + LOGFILE + " is not defined");
        }
        value = (String) evaluate(value);
        return new File(value);
    }

    public String evaluate(String aString) {
        return (String) ee.evaluate(aString);
    }

    public String evaluate(String aString, String pluginId) {
        ee.pushContext();
        ee.addVariable("pluginProperty", pluginPropertyMap.get(pluginId));
        String value = aString;
        int count = 0;
        do {
            value = (String) ee.evaluate(value);
            //TODO cheap loop detection
            if (value == null || count++ > 10) {
                break;
            }
        } while (value.contains("#{"));
        ee.popContext();
        return value;
    }

    public void loadConfigDir(File configDir) {
        if (!configDir.exists()) {
            throw new RuntimeException("Could not find the configuration directory: " + configDir.getPath());
        }
        setPluginsFile(new File(configDir, RepositoryMetadata.METADATA_XML));
        load(getPluginsFile());
        if (!configDir.equals(getConfigDir())) {
            throw new RuntimeException(CONFIG_DIR + " plugins file property " + getConfigDir().getPath() + " does not match load directory: " + getConfigDir().getPath());
        }
        File log4JConfiguration = getLog4JConfiguration();
        if (!log4JConfiguration.getPath().equals(log4JConfiguration.getAbsolutePath())) {
            log4JConfiguration = new File(System.getProperty("user.dir"), log4JConfiguration.getName());
        }
        File logFile = getLogFile();
        if (!logFile.getPath().equals(logFile.getAbsolutePath())) {
            logFile = new File(System.getProperty("user.dir"), logFile.getName());
        }
        TolvenLogger.initialize(log4JConfiguration.getPath(), logFile);
    }

    public void storeMetadata(File pluginsXMLFile) {
        logger.info("Write Runtime plugins metadata to " + pluginsXMLFile);
        try {
            FileUtils.writeStringToFile(pluginsXMLFile, RepositoryMetadata.getPluginsXML(getPlugins()));
        } catch (IOException ex) {
            throw new RuntimeException("Could not write runtime plugins to: " + pluginsXMLFile, ex);
        }
    }

    public void load(File pluginsFile) {
        //Do not add any logger statements at this point, because the pluginsFile contains information
        //about the log4JConfiguration and logFile, so the log4j is still uninitialized
        FileInputStream fis = null;
        try {
            try {
                fis = new FileInputStream(pluginsFile);
                loadPlugins(fis);
            } finally {
                if (fis != null)
                    fis.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load plugins from " + pluginsFile.getPath(), ex);
        }
    }

    /**
     * Unmarshall a Plugins from an InputStream
     * @param xsdStream
     * @return
     */
    public Plugins loadPlugins(InputStream xsdStream) {
        try {
            JAXBContext jc = JAXBContext.newInstance(RepositoryMetadata.PLUGINS_PACKAGE, ClassLoader.getSystemClassLoader());
            Unmarshaller u = jc.createUnmarshaller();
            setPlugins((Plugins) u.unmarshal(xsdStream));
            validate();
            ee = new ExpressionEvaluator();
            Map<String, String> globalPropertyMap = new HashMap<String, String>();
            for (PluginPropertyDetail globalProperty : getPlugins().getProperty()) {
                globalPropertyMap.put(globalProperty.getName(), globalProperty.getValue());
            }
            ee.addVariable("globalProperty", globalPropertyMap);
            for (PluginDetail plugin : getPlugins().getPlugin()) {
                Map<String, String> pMap = new HashMap<String, String>();
                for (PluginPropertyDetail pluginProperty : plugin.getProperty()) {
                    updatePropertyMap(pMap, pluginProperty);
                }
                pluginPropertyMap.put(plugin.getId(), pMap);
            }
            return getPlugins();
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not load plugins inputstream", ex);
        }
    }

    /**
     * Update properties map for property and any child properties
     * 
     * @param pMap
     * @param property
     */
    private void updatePropertyMap(Map<String, String> pMap, PluginPropertyDetail property) {
        pMap.put(property.getName(), property.getValue());
        Collection<PluginPropertyDetail> childProperties = property.getProperty();
        if (childProperties.isEmpty()) {
            return;
        }
        for (PluginPropertyDetail pluginProperty : childProperties) {
            updatePropertyMap(pMap, pluginProperty);
        }
    }

    private void validate() {
        //Check for duplicates
        Set<String> pluginIds = new HashSet<String>();
        for (PluginDetail plugin : getPlugins().getPlugin()) {
            if (pluginIds.contains(plugin.getId())) {
                throw new RuntimeException(plugin.getId() + " has a duplicate entry in " + getPluginsFile().getPath());
            } else {
                pluginIds.add(plugin.getId());
            }
        }
    }

}
