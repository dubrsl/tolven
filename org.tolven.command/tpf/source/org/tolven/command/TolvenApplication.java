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
package org.tolven.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.PluginManager;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.ApplicationPlugin;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.PluginPrerequisite;
import org.java.plugin.registry.PluginRegistry;
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;
import org.java.plugin.util.ExtendedProperties;
import org.tolven.plugin.boot.TPFBoot;
import org.tolven.security.hash.TolvenMessageDigest;

/**
 * This plugin is an ApplicationPlugin called by the JPF framework on startup.
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenApplication extends ApplicationPlugin implements Application {

    public static final String CMD_LINE_PLUGIN_OPTION = "plugin";
    public static final String EXTENSION_POINT_RUNTIME_LIB = "runtimeLib";
    public static final String EXTENSION_POINT_DOWNLOAD_WEB = "downLoadWeb";
    public static final String COMMAND_PLUGIN_ID = "org.tolven.command";
    public static final String MESSAGE_DIGEST_ALGORITHM = "md5";

    private static String[] initArgs;
    private static Method method;

    private static Logger logger = Logger.getLogger(TolvenApplication.class);

    @Override
    protected Application initApplication(ExtendedProperties extendedProperties, String[] initArgs) throws Exception {
        TolvenApplication.initArgs = initArgs;
        return this;
    }

    @Override
    public void startApplication() throws Exception {
        CommandLine commandLine = getCommandLine(initArgs);
        String[] pluginArgs = commandLine.getArgs();
        /*
         * The initArgs are used to determine which plugins to execute, while pluginArgs are passed to those plugins
         */
        String pluginsString = commandLine.getOptionValue(CMD_LINE_PLUGIN_OPTION);
        loadLibraries(pluginsString);
        List<String> plugins = getPlugins(pluginsString);
        String adminPluginId = getDescriptor().getAttribute("adminPluginId").getValue();
        execute(adminPluginId, getManager(), pluginArgs);
        plugins.remove(adminPluginId);
        startPlugins(plugins);
        String[] pluginIdsWithoutAdminId = new String[plugins.size()];
        for (int i = 0; i < plugins.size(); i++) {
            pluginIdsWithoutAdminId[i] = plugins.get(i);
        }
        for (String pluginId : plugins) {
            execute(pluginId, getManager(), pluginArgs);
        }
        logger.info("Execution of: " + pluginsString + " completed");
    }

    private CommandLine getCommandLine(String[] args) {
        Option pluginOption = new Option(CMD_LINE_PLUGIN_OPTION, "plugin", true, "plugin id");
        pluginOption.setRequired(true);
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption(pluginOption);
        try {
            GnuParser parser = new GnuParser();
            CommandLine commandLine = parser.parse(cmdLineOptions, args, true);
            String pluginsString = commandLine.getOptionValue(CMD_LINE_PLUGIN_OPTION);
            if (pluginsString.length() == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(getClass().getName(), cmdLineOptions);
                throw new RuntimeException("No plugins were supplied at the command line");
            }
            return commandLine;
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(getClass().getName(), cmdLineOptions);
            throw new RuntimeException("Could not parse command line for: " + getClass().getName(), ex);
        }
    }

    private void loadLibraries(String pluginsString) throws Exception {
        List<PluginDescriptor> pluginDescriptors = new ArrayList<PluginDescriptor>();
        pluginDescriptors.add(getDescriptor());
        String adminPluginId = getDescriptor().getAttribute("adminPluginId").getValue();
        PluginDescriptor adminPluginDescriptor = getManager().getRegistry().getPluginDescriptor(adminPluginId);
        pluginDescriptors.add(adminPluginDescriptor);
        PluginRegistry registry = getManager().getRegistry();
        for (String pluginId : pluginsString.split(",")) {
            PluginDescriptor pluginDescriptor = registry.getPluginDescriptor(pluginId);
            pluginDescriptors.add(pluginDescriptor);
        }
        Set<PluginDescriptor> processedPluginDescriptors = new HashSet<PluginDescriptor>();
        List<URL> libraryPaths = new ArrayList<URL>();
        for (PluginDescriptor pluginDescriptor : pluginDescriptors) {
            loadLibraries(pluginDescriptor, getManager(), processedPluginDescriptors, libraryPaths);
        }
        StringBuffer buff = new StringBuffer();
        String pathSeparator = System.getProperty("path.separator");
        for (URL url : libraryPaths) {
            buff.append(url.getFile());
            buff.append(pathSeparator);
        }
        logger.debug("Plugin library path=" + buff.toString());
    }

    public static void execute(String pluginId, PluginManager pluginManager, String[] args) {
        PluginDescriptor pluginDescriptor = pluginManager.getRegistry().getPluginDescriptor(pluginId);
        execute(pluginDescriptor, pluginManager, args);
    }

    public static void execute(PluginDescriptor pluginDescriptor, PluginManager pluginManager, String[] args) {
        try {
            Plugin plugin = getPlugin(pluginDescriptor, pluginManager);
            pluginManager.activatePlugin(plugin.getDescriptor().getId());
            Method method = null;
            try {
                method = plugin.getClass().getDeclaredMethod("execute", new Class[] { String[].class });
                method.invoke(plugin, new Object[] { args });
            } catch (NoSuchMethodException ex) {
                // does not support execute() method, and that is fine, it was activated
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not execute: " + pluginDescriptor.getId(), ex);
        }
    }

    public static Plugin getPlugin(PluginDescriptor pluginDescriptor, PluginManager pluginManager) {
        loadLibraries(pluginDescriptor, pluginManager);
        try {
            Plugin plugin = pluginManager.getPlugin(pluginDescriptor.getId());
            return plugin;
        } catch (PluginLifecycleException ex) {
            throw new RuntimeException("Could not get plugin: " + pluginDescriptor.getId(), ex);
        }
    }

    private static List<URL> loadLibraries(PluginDescriptor pluginDescriptor, PluginManager pluginManager) {
        Set<PluginDescriptor> processedPluginDescriptors = new HashSet<PluginDescriptor>();
        List<URL> libraryPaths = new ArrayList<URL>();
        loadLibraries(pluginDescriptor, pluginManager, processedPluginDescriptors, libraryPaths);
        return libraryPaths;
    }

    private static List<URL> loadLibraries(PluginDescriptor pluginDescriptor, PluginManager pluginManager, Set<PluginDescriptor> processedPluginDescriptors, List<URL> libraryPaths) {
        Set<PluginDescriptor> unprocessedPluginDescriptors = new HashSet<PluginDescriptor>();
        if (!processedPluginDescriptors.contains(pluginDescriptor)) {
            unprocessedPluginDescriptors.add(pluginDescriptor);
        }
        for (PluginPrerequisite prereq : pluginDescriptor.getPrerequisites()) {
            PluginDescriptor prereqPluginDescriptor = pluginManager.getRegistry().getPluginDescriptor(prereq.getPluginId());
            if (!processedPluginDescriptors.contains(prereqPluginDescriptor)) {
                unprocessedPluginDescriptors.add(prereqPluginDescriptor);
            }
        }
        if (!unprocessedPluginDescriptors.isEmpty()) {
            for (PluginDescriptor unprocessedPluginDescriptor : unprocessedPluginDescriptors) {
                loadLibraries(unprocessedPluginDescriptor, pluginManager, libraryPaths);
                processedPluginDescriptors.add(unprocessedPluginDescriptor);
                loadLibraries(unprocessedPluginDescriptor, pluginManager, processedPluginDescriptors, libraryPaths);
            }
        }
        return libraryPaths;
    }

    private static void loadLibraries(PluginDescriptor descr, PluginManager pluginManager, List<URL> libraryPaths) {
        try {
            URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
            for (ExtensionPoint extensionPoint : descr.getExtensionPoints()) {
                if (EXTENSION_POINT_RUNTIME_LIB.equals(extensionPoint.getParentExtensionPointId())) {
                    String parentExtensionPointId = extensionPoint.getParentExtensionPointId();
                    String parentPluginId = extensionPoint.getParentPluginId();
                    if (parentPluginId == null || parentExtensionPointId == null) {
                        throw new RuntimeException("Cannot locate parent for extension point: " + extensionPoint.getUniqueId());
                    }
                    ExtensionPoint libraryExtensionPoint = descr.getRegistry().getExtensionPoint(parentPluginId, parentExtensionPointId);
                    PluginDescriptor libraryPluginDescriptor = libraryExtensionPoint.getDeclaringPluginDescriptor();
                    for (ParameterDefinition parameterDefinition : extensionPoint.getParameterDefinitions()) {
                        String parameterId = parameterDefinition.getId();
                        ParameterDefinition parentParamterDefinition = libraryExtensionPoint.getParameterDefinition(parameterId);
                        if (parentParamterDefinition == null) {
                            throw new RuntimeException(libraryExtensionPoint.getUniqueId() + " does not have the parameter definition: " + parameterId);
                        }
                        if (parentParamterDefinition.getDefaultValue() == null) {
                            continue;
                        }
                        String library = TPFBoot.pluginsWrapper.evaluate(parentParamterDefinition.getDefaultValue(), libraryPluginDescriptor.getId());
                        ParameterDefinition remoteDefinition = null;
                        try {
                            remoteDefinition = parentParamterDefinition.getSubDefinition("remote");
                        } catch (Exception ex) {
                            // The underlying JPF did not follow its own pattern for allowing a call to handle whether a parameter definition exists or not
                        }
                        URL url = null;
                        if (remoteDefinition == null) {
                            url = loadLocalLibrary(library, libraryPluginDescriptor, pluginManager, urlClassLoader);
                        } else {
                            try {
                                url = loadRemoteLibrary(library, pluginManager, urlClassLoader);
                            } catch (Exception ex) {
                                logger.warn("Remote library unavailable: " + library + " because " + ex.getMessage());
                            }
                        }
                        if (url != null) {
                            libraryPaths.add(url);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not load runtime libraries for plugin: " + descr.getId(), ex);
        }
    }

    private static URL loadLocalLibrary(String library, PluginDescriptor pluginDescriptor, PluginManager pluginManager, URLClassLoader urlClassLoader) throws Exception {
        File libraryFile = null;
        if (new File(library).getPath().equals(new File(library).getAbsolutePath())) {
            libraryFile = new File(library);
        } else {
            URL url = new URL(pluginManager.getPathResolver().resolvePath(pluginDescriptor, "") + library);
            libraryFile = new File(url.getFile());
        }
        if (!libraryFile.exists()) {
            throw new RuntimeException(pluginDescriptor.getUniqueId() + " could not find runtime library using: " + library + " which leads to: " + libraryFile.getPath());
        }
        URL url = libraryFile.toURI().toURL();
        updateLibraryPath(url, urlClassLoader);
        return url;
    }

    private static URL loadRemoteLibrary(String library, PluginManager pluginManager, URLClassLoader urlClassLoader) {
        ExtensionPoint extensionPoint = pluginManager.getRegistry().getExtensionPoint(COMMAND_PLUGIN_ID, EXTENSION_POINT_DOWNLOAD_WEB);
        String hostname = TPFBoot.pluginsWrapper.evaluate(extensionPoint.getParameterDefinition("download.web.hostname").getDefaultValue(), COMMAND_PLUGIN_ID);
        if (hostname == null) {
            throw new RuntimeException("The property download.web.hostname for " + COMMAND_PLUGIN_ID + " evaluated to null");
        }
        String port = TPFBoot.pluginsWrapper.evaluate(extensionPoint.getParameterDefinition("download.web.http.port").getDefaultValue(), COMMAND_PLUGIN_ID);
        if (port == null) {
            throw new RuntimeException("The property download.web.http.port for " + COMMAND_PLUGIN_ID + " evaluated to null");
        }
        String downloadURLString = "http://" + hostname + ":" + port + "/Tolven/download";
        String installDirname = TPFBoot.pluginsWrapper.evaluate("#{globalProperty['installation.dir']}");
        try {
            File installRemoteLibDir = new File(installDirname, "remoteLib");
            if (!installRemoteLibDir.exists()) {
                installRemoteLibDir.mkdirs();
            }
            File destFile = new File(installRemoteLibDir, library);
            URL localURL = destFile.toURI().toURL();
            if (Arrays.asList(urlClassLoader.getURLs()).contains(localURL)) {
                return null;
            }
            String urlString = downloadURLString + "/" + library;
            URL url = new URL(urlString);
            if (destFile.exists()) {
                String md5sum = getRemoteChecksum(urlString + ".md5");
                if (md5sum.equals(TolvenMessageDigest.checksum(localURL, MESSAGE_DIGEST_ALGORITHM))) {
                    updateLibraryPath(localURL, urlClassLoader);
                    return localURL;
                }
            }
            logger.debug("Downloading remote library from: " + downloadURLString + " to " + installDirname);
            downloadFile(url, destFile);
            updateLibraryPath(localURL, urlClassLoader);
            return localURL;
        } catch (Exception ex) {
            throw new RuntimeException("Could not retrieve library: " + library, ex);
        }
    }

    private static String getRemoteChecksum(String urlString) {
        try {
            String md5sum = null;
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("remoteMD5_", null);
                URL url = new URL(urlString);
                downloadFile(url, tmpFile);
                md5sum = FileUtils.readFileToString(tmpFile);
            } finally {
                if (tmpFile != null) {
                    tmpFile.delete();
                }
            }
            return md5sum;
        } catch (Exception ex) {
            throw new RuntimeException("Could not download remote MD5 URL: " + urlString, ex);
        }
    }

    private static void downloadFile(URL url, File destFile) throws Exception {
        try {
            URLConnection urlConnection = url.openConnection();
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("remoteLib_", null);
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    in = urlConnection.getInputStream();
                    out = new FileOutputStream(tmpFile);
                    IOUtils.copy(in, out);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }
                FileUtils.copyFile(tmpFile, destFile);
            } finally {
                if (tmpFile != null) {
                    tmpFile.delete();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not download URL: " + url, ex);
        }
    }

    private static Method getMethod() throws SecurityException, NoSuchMethodException {
        if (method == null) {
            Class<?> sysclass = URLClassLoader.class;
            method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
            method.setAccessible(true);
        }
        return method;
    }

    private static void updateLibraryPath(URL url, URLClassLoader urlClassLoader) throws Exception {
        getMethod().invoke(urlClassLoader, new Object[] { url });
    }

    private List<String> getPlugins(String pluginsString) {
        List<String> plugins = new ArrayList<String>();
        for (String plugin : pluginsString.split(",")) {
            plugins.add(plugin);
        }
        return plugins;
    }

    private List<Plugin> startPlugins(List<String> plugins) throws PluginLifecycleException {
        // Activate (doStart) all plugins
        List<Plugin> startedPlugins = new ArrayList<Plugin>();
        for (String pluginId : plugins) {
            getManager().activatePlugin(pluginId);
            Plugin plugin = getManager().getPlugin(pluginId);
            startedPlugins.add(plugin);
        }
        return startedPlugins;
    }

    @Override
    protected void doStart() throws Exception {
    }

    @Override
    protected void doStop() throws Exception {
    }

}
