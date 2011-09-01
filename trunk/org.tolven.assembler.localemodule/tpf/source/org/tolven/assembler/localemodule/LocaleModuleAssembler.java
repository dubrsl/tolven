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
package org.tolven.assembler.localemodule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.tools.ant.TolvenDependSet;
import org.tolven.tools.ant.TolvenJar;

/**
 * This plugin assmebles locale jar files for an ear file
 * 
 * @author Joseph Isaac
 *
 */
public class LocaleModuleAssembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_COMPONENT = "component";

    private Logger logger = Logger.getLogger(LocaleModuleAssembler.class);

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        assembleLocaleModuleContributions();
    }

    protected void assembleLocaleModuleContributions() throws IOException {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_COMPONENT);
        for (Extension localeModuleExtension : extensionPoint.getConnectedExtensions()) {
            PluginDescriptor localeModulePluginDescriptor = localeModuleExtension.getDeclaringPluginDescriptor();
            File localeModulePluginTmpDir = getPluginTmpDir(localeModulePluginDescriptor);
            String jarFilename = localeModuleExtension.getParameter("jar").valueAsString();
            File destinationJARFile = new File(localeModulePluginTmpDir, jarFilename);
            String earPluginId = localeModuleExtension.getParameter("target-plugin-id").valueAsString();
            String localeModulePluginId = localeModulePluginDescriptor.getId();
            if (isProcessingRequired(localeModulePluginDescriptor, localeModulePluginId, destinationJARFile)) {
                File earPluginDir = new File(getPluginTmpDir(), earPluginId);
                File localeModuleDir = new File(earPluginDir, jarFilename);
                File sourceJarFile = getFilePath(localeModulePluginDescriptor, jarFilename);
                logger.debug("Unjar " + sourceJarFile.getPath() + " to " + localeModuleDir.getPath());
                FileUtils.deleteDirectory(localeModuleDir);
                localeModuleDir.mkdirs();
                TolvenJar.unjar(sourceJarFile, localeModuleDir);
                String languageCode = null;
                Parameter parameter = localeModuleExtension.getParameter("language-code");
                if (parameter == null) {
                    languageCode = "";
                } else {
                    languageCode = parameter.valueAsString();
                }
                ExtensionPoint applicationLocaleExtensionPoint = localeModulePluginDescriptor.getExtensionPoint("application-locale");
                if (applicationLocaleExtensionPoint != null) {
                    for (Extension extension : applicationLocaleExtensionPoint.getConnectedExtensions()) {
                        String applicationName = extension.getParameter("application-name").valueAsString();
                        File applicationLocaleFile = getLocalePropertiesFile(localeModuleDir, applicationName, "AppBundle", languageCode);
                        updateProperties(extension, applicationLocaleFile);
                    }
                }
                ExtensionPoint applicationGlobalLocaleExtensionPoint = localeModulePluginDescriptor.getExtensionPoint("application-global-locale");
                if (applicationGlobalLocaleExtensionPoint != null) {
                    for (Extension extension : applicationGlobalLocaleExtensionPoint.getConnectedExtensions()) {
                        String applicationName = extension.getParameter("application-name").valueAsString();
                        File applicationLocaleFile = getLocalePropertiesFile(localeModuleDir, applicationName, "GlobalBundle", languageCode);
                        updateProperties(extension, applicationLocaleFile);
                    }
                }
                ExtensionPoint globalLocaleExtensionPoint = localeModulePluginDescriptor.getExtensionPoint("global-locale");
                if (globalLocaleExtensionPoint != null) {
                    for (Extension extension : globalLocaleExtensionPoint.getConnectedExtensions()) {
                        File globalLocaleFile = getLocalePropertiesFile(localeModuleDir, "messages", "GlobalBundle", languageCode);
                        updateProperties(extension, globalLocaleFile);
                    }
                }
                destinationJARFile.getParentFile().mkdirs();
                logger.debug("Jar " + localeModuleDir.getPath() + " to " + destinationJARFile.getPath());
                TolvenJar.jar(localeModuleDir, destinationJARFile);
            }
        }
    }

    protected boolean isProcessingRequired(PluginDescriptor pluginDescriptor, String earPluginId, File productFile) {
        if (!productFile.exists()) {
            logger.debug(productFile.getPath() + " does not exist, so it will be created");
            return true;
        }
        Set<File> pluginFiles = new HashSet<File>();
        pluginFiles.add(getPluginZip(pluginDescriptor));
        pluginFiles.add(getPluginTmpDir(pluginDescriptor));
        for (Extension extension : getExtensions(pluginDescriptor, "application-locale")) {
            if (earPluginId.equals(extension.getParameter("target-plugin-id").valueAsString())) {
                pluginFiles.add(getPluginZip(extension.getDeclaringPluginDescriptor()));
                pluginFiles.add(getPluginTmpDir(extension.getDeclaringPluginDescriptor()));
            }
        }
        for (Extension extension : getExtensions(pluginDescriptor, "application-global-locale")) {
            if (earPluginId.equals(extension.getParameter("target-plugin-id").valueAsString())) {
                pluginFiles.add(getPluginZip(extension.getDeclaringPluginDescriptor()));
                pluginFiles.add(getPluginTmpDir(extension.getDeclaringPluginDescriptor()));
            }
        }
        for (Extension extension : getExtensions(pluginDescriptor, "global-locale")) {
            if (earPluginId.equals(extension.getParameter("target-plugin-id").valueAsString())) {
                pluginFiles.add(getPluginZip(extension.getDeclaringPluginDescriptor()));
                pluginFiles.add(getPluginTmpDir(extension.getDeclaringPluginDescriptor()));
            }
        }
        if (!pluginFiles.isEmpty()) {
            TolvenDependSet.process(pluginFiles, productFile);
        }
        if (productFile.exists()) {
            logger.debug(productFile.getPath() + " is more recent than any of its source files");
            return false;
        } else {
            logger.debug(productFile.getPath() + " was removed since its source files are more recent");
            return true;
        }
    }

    protected void updateProperties(Extension extension, File localeFile) throws IOException {
        String contributedPropertiesFilename = extension.getParameter("properties-file").valueAsString();
        File contributedPropertiesFile = getFilePath(extension.getDeclaringPluginDescriptor(), contributedPropertiesFilename);
        Properties localeProperties = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(localeFile);
            localeProperties.load(in);
        } finally {
            in.close();
        }
        Properties contributedProperties = load(contributedPropertiesFile.getPath());
        String key;
        for (Object obj : contributedProperties.keySet()) {
            key = (String) obj;
            localeProperties.setProperty(key, (String) contributedProperties.get(key));
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(localeFile);
            logger.debug("Store properties in  " + localeFile.getPath());
            localeProperties.store(out, null);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected File getLocalePropertiesFile(File tmpLocaleDir, String applicationName, String bundleName, String languageCode) {
        String bundleFilename = null;
        if (languageCode == null || languageCode.length() == 0) {
            bundleFilename = bundleName + ".properties";
        } else {
            bundleFilename = bundleName + "_" + languageCode + ".properties";
        }
        return new File(tmpLocaleDir, applicationName + "/" + bundleFilename);
    }

    public Properties load(String filename) throws IOException {
        Properties properties = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(filename);
            logger.debug("Load properties from  " + filename);
            properties.load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return properties;
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
