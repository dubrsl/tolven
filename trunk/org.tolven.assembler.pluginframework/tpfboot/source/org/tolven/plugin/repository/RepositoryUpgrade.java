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
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.tolven.plugin.boot.TPFBoot;
import org.tolven.plugin.registry.xml.ManifestParser;
import org.tolven.plugin.registry.xml.ModelPluginManifest;
import org.tolven.plugin.repository.bean.DependentPluginDetail;
import org.tolven.plugin.repository.bean.InfoDetail;
import org.tolven.plugin.repository.bean.MessageDigestDetail;
import org.tolven.plugin.repository.bean.ObjectFactory;
import org.tolven.plugin.repository.bean.PluginDetail;
import org.tolven.plugin.repository.bean.PluginPropertyDetail;
import org.tolven.plugin.repository.bean.PluginVersionDetail;
import org.tolven.plugin.repository.bean.Plugins;
import org.tolven.plugin.repository.bean.RootPluginDetail;
import org.tolven.security.hash.TolvenMessageDigest;

/**
 * The class initializes and/or upgrades the runtime repository
 * 
 * @author Joseph Isaac
 *
 */
public class RepositoryUpgrade {

    public static final String REPOSITORY_LIBRARY_URL = "repositoryLibraryURL";
    public static final String CMD_LINE_CONF_OPTION = "conf";
    public static final String ENV_CONF = "TOLVEN_CONFIG_DIR";
    public static final String PLUGINS_PACKAGE = "org.tolven.plugin.repository.bean";
    public static final String INFO_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";

    public static final String CMD_LINE_NOOP_OPTION = "noop";
    public static final String NOOP = "NOOP";

    private ObjectFactory objectFactory;
    private boolean noop = false;

    private Logger logger = Logger.getLogger(RepositoryUpgrade.class);

    public RepositoryUpgrade() {
        setObjectFactory(new ObjectFactory());
    }

    public ConfigPluginsWrapper getConfigPluginsWrapper() {
        return TPFBoot.pluginsWrapper;
    }

    private File getConfigDir() {
        return getConfigPluginsWrapper().getConfigDir();
    }

    private File getRepositoryRuntimeDir() {
        return getConfigPluginsWrapper().getRepositoryRuntimeDir();
    }

    private File getRepositoryRuntimePluginsDir() {
        return getConfigPluginsWrapper().getRepositoryRuntimePluginsDir();
    }

    private ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    private void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    private boolean isNoop() {
        return noop;
    }

    private void setNoop(boolean noop) {
        this.noop = noop;
    }

    public void initialize(String[] args) {
        CommandLine commandLine = getCommandLine(args);
        setNoop(commandLine.hasOption(CMD_LINE_NOOP_OPTION));
        debug("*** start ***");
        Plugins libraryPlugins = getLibraryPlugins();
        Plugins runtimePlugins = getRuntimePlugins();
        Plugins rootPlugins = getRootPlugins(getConfigPluginsWrapper().getPlugins());
        Plugins mergePlugins = mergePlugins(libraryPlugins, runtimePlugins, rootPlugins);
        Plugins upgradePlugins = getUpgradePlugins(mergePlugins);
        upgrade(upgradePlugins, runtimePlugins);
        if (pluginsChanged()) {
            deleteBuildDir();
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
        Option noopOption = new Option(CMD_LINE_NOOP_OPTION, CMD_LINE_NOOP_OPTION, false, "noop i.e. do nothing but produce what the repositoryRuntime would look like");
        cmdLineOptions.addOption(noopOption);
        return cmdLineOptions;
    }

    private Plugins getLibraryPlugins() {
        Plugins mergedLibraryPlugins = getObjectFactory().createPlugins();
        PluginPropertyDetail repositoryLibraryGroupProperty = getConfigPluginsWrapper().getRepositoryLibrary();
        StringBuffer buff = new StringBuffer();
        Iterator<PluginPropertyDetail> it = repositoryLibraryGroupProperty.getProperty().iterator();
        while (it.hasNext()) {
            PluginPropertyDetail repositoryLibraryProperty = it.next();
            boolean useSnapshotDefault = getUseSnapshotDefault(repositoryLibraryProperty);
            String libraryURL = null;
            if (useSnapshotDefault) {
                libraryURL = getProperty(ConfigPluginsWrapper.REPOSITORY_SNAPSHOT_METADATA, repositoryLibraryProperty.getProperty());
                if (libraryURL == null) {
                    throw new RuntimeException("Property undefined: " + ConfigPluginsWrapper.REPOSITORY_SNAPSHOT_METADATA);
                }
            } else {
                libraryURL = getProperty(ConfigPluginsWrapper.REPOSITORY_TRUNK_METADATA, repositoryLibraryProperty.getProperty());
                if (libraryURL == null) {
                    throw new RuntimeException("Property undefined: " + ConfigPluginsWrapper.REPOSITORY_TRUNK_METADATA);
                }
            }
            String evaluatedURL = getConfigPluginsWrapper().evaluate(libraryURL);
            if (evaluatedURL == null) {
                throw new RuntimeException("Library URL: " + libraryURL + " evaluated to null");
            }
            buff.append(evaluatedURL);
            if (it.hasNext()) {
                buff.append(",");
            }
            URL metadataURL = null;
            try {
                metadataURL = new URL(evaluatedURL);
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert to URL: '" + evaluatedURL + "'", ex);
            }
            try {
                InputStream in = null;
                try {
                    in = metadataURL.openStream();
                    Plugins libraryPlugins = RepositoryMetadata.getPlugins(in);
                    merge(libraryPlugins, mergedLibraryPlugins);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException("Could not load plugins metadata from: " + metadataURL.toExternalForm(), ex);
            }
        }
        String evaluatedRepositoryURLString = buff.toString();
        info(ConfigPluginsWrapper.REPOSITORY_LIBRARY + "=" + evaluatedRepositoryURLString);
        return mergedLibraryPlugins;
    }

    private boolean getUseSnapshotDefault(PluginPropertyDetail repositoryLibraryProperty) {
        String useSnapshotDefaultValue = getProperty(ConfigPluginsWrapper.USE_SNAPSHOT, repositoryLibraryProperty.getProperty());
        boolean useSnapshotDefault = false;
        if (useSnapshotDefaultValue == null) {
            useSnapshotDefault = false;
        } else if (Boolean.TRUE.toString().equals(useSnapshotDefaultValue)) {
            useSnapshotDefault = true;
        } else if (Boolean.FALSE.toString().equals(useSnapshotDefaultValue)) {
            useSnapshotDefault = false;
        } else {
            throw new RuntimeException("Unrecognized property value for: " + ConfigPluginsWrapper.USE_SNAPSHOT + ": " + useSnapshotDefaultValue);
        }
        return useSnapshotDefault;
    }

    private String getProperty(String name, List<PluginPropertyDetail> properties) {
        for (PluginPropertyDetail property : properties) {
            if (property.getName().equals(name)) {
                return property.getValue();
            }
        }
        return null;
    }

    private void merge(Plugins source, Plugins destination) {
        List<PluginDetail> newPlugins = new ArrayList<PluginDetail>();
        for (PluginDetail sourcePlugin : source.getPlugin()) {
            PluginDetail destPlugin = getPlugin(sourcePlugin.getId(), destination);
            if (destPlugin == null) {
                newPlugins.add(sourcePlugin);
            } else {
                mergeVersions(sourcePlugin, destPlugin);
                mergeDependents(sourcePlugin, destPlugin);
            }
        }
        destination.getPlugin().addAll(newPlugins);
    }

    private void mergeVersions(PluginDetail source, PluginDetail destination) {
        List<PluginVersionDetail> newPluginVersions = new ArrayList<PluginVersionDetail>();
        for (PluginVersionDetail sourcePluginVersion : source.getVersion()) {
            PluginVersionDetail destPluginVersion = getPluginVersion(sourcePluginVersion.getId(), destination);
            if (destPluginVersion == null) {
                newPluginVersions.add(sourcePluginVersion);
            }
        }
        destination.getVersion().addAll(newPluginVersions);
    }

    private void mergeDependents(PluginDetail source, PluginDetail destination) {
        List<DependentPluginDetail> newDependentPlugins = new ArrayList<DependentPluginDetail>();
        for (DependentPluginDetail sourceDependentPlugin : source.getDependent()) {
            DependentPluginDetail destDependentPlugin = getDependentPlugin(sourceDependentPlugin, destination);
            if (destDependentPlugin == null) {
                newDependentPlugins.add(sourceDependentPlugin);
            }
        }
        destination.getDependent().addAll(newDependentPlugins);
    }

    private Plugins getRuntimePlugins() {
        File repositoryDir = getRepositoryRuntimePluginsDir();
        if (!repositoryDir.getPath().equals(repositoryDir.getAbsolutePath())) {
            repositoryDir = new File(getConfigDir(), repositoryDir.getPath());
        }
        if (!isNoop()) {
            repositoryDir.mkdirs();
        }
        URL repositoryURL;
        try {
            repositoryURL = repositoryDir.toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not convert repository directory " + repositoryDir.getPath() + " to a URL: ", ex);
        }
        info("Runtime repository: " + repositoryURL.toExternalForm());
        String repositoryLibraryURLString = repositoryURL.toExternalForm();
        Plugins plugins = getObjectFactory().createPlugins();
        String[] repositoryDirList = repositoryDir.list();
        List<String> pluginZipList = null;
        if (repositoryDirList == null) {
            pluginZipList = new ArrayList<String>();
        } else {
            pluginZipList = Arrays.asList(repositoryDir.list());
        }
        ManifestParser manifestParser = new ManifestParser(false);
        for (String pluginZip : pluginZipList) {
            URL pluginURL;
            String pluginURLString = repositoryLibraryURLString + "/" + pluginZip;
            try {
                pluginURL = new URL(pluginURLString);
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert plugin " + pluginURLString + " to a URL: ", ex);
            }
            URL pluginManifestURL = null;
            String pluginManifestURLString = "jar:" + repositoryLibraryURLString + "/" + pluginZip + "!/tolven-plugin.xml";
            try {
                pluginManifestURL = new URL(pluginManifestURLString);
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert plugin manifest " + pluginManifestURLString + " to a URL: ", ex);
            }
            ModelPluginManifest pluginManifest;
            try {
                pluginManifest = manifestParser.parseManifest(pluginManifestURL.toURI().toURL());
            } catch (Exception ex) {
                throw new RuntimeException("Could not parse plugin manifest: " + pluginManifestURL.toExternalForm(), ex);
            }
            String pluginId = pluginManifest.getId();
            PluginDetail plugin = getPlugin(pluginId, plugins);
            if (plugin == null) {
                plugin = createPlugin(pluginId);
                plugins.getPlugin().add(plugin);
            }
            String pluginVersionString = null;
            if (pluginManifest.getVersion() == null) {
                pluginVersionString = "";
            } else {
                pluginVersionString = pluginManifest.getVersion().toString();
            }
            if (pluginVersionString.length() == 0) {
                throw new RuntimeException(pluginManifestURL.toExternalForm() + " must have a version ");
            }
            PluginVersionDetail pluginVersion = getObjectFactory().createPluginVersionDetail();
            pluginVersion.setId(pluginVersionString);
            pluginVersion.setUri(pluginURL.toExternalForm());
            MessageDigestDetail messageDigest = getMessageDigest(pluginURL);
            pluginVersion.setMessageDigest(messageDigest);
            pluginVersion.setRuntime("true");
            plugin.getVersion().add(pluginVersion);
        }
        return plugins;
    }

    private Plugins getRootPlugins(Plugins plugins) {
        Plugins rootPlugins = getObjectFactory().createPlugins();
        for (PluginDetail plugin : plugins.getPlugin()) {
            if (plugin.getRoot() != null) {
                rootPlugins.getPlugin().add(plugin);
            }
        }
        return rootPlugins;
    }

    private MessageDigestDetail getMessageDigest(URL pluginURL) {
        MessageDigestDetail messageDetail = getObjectFactory().createMessageDigestDetail();
        String digest = TolvenMessageDigest.checksum(pluginURL, RepositoryMetadata.MESSAGE_DIGEST_ALGORITHM);
        messageDetail.setType(RepositoryMetadata.MESSAGE_DIGEST_ALGORITHM);
        messageDetail.setValue(digest);
        return messageDetail;
    }

    private PluginDetail getPlugin(String pluginId, Plugins plugins) {
        for (PluginDetail pluginDetail : plugins.getPlugin()) {
            if (pluginDetail.getId().equals(pluginId)) {
                return pluginDetail;
            }
        }
        return null;
    }

    private PluginDetail createPlugin(String pluginId) {
        PluginDetail pluginDetail = getObjectFactory().createPluginDetail();
        pluginDetail.setId(pluginId);
        return pluginDetail;
    }

    private PluginVersionDetail getSinglePluginVersion(PluginDetail plugin) {
        if (plugin.getVersion().size() != 1) {
            throw new RuntimeException("Expected to find one and only one version for plugin: " + plugin);
        }
        return plugin.getVersion().get(0);
    }

    private PluginVersionDetail getPluginVersion(String version, PluginDetail plugin) {
        for (PluginVersionDetail pluginDetailVersion : plugin.getVersion()) {
            if (pluginDetailVersion.getId().equals(version)) {
                return pluginDetailVersion;
            }
        }
        return null;
    }

    private DependentPluginDetail getDependentPlugin(DependentPluginDetail dependentPlugin, PluginDetail plugin) {
        for (DependentPluginDetail dp : plugin.getDependent()) {
            if (dp.getId().equals(dependentPlugin.getId()) && dp.getVersion().equals(dependentPlugin.getVersion())) {
                if (dp.getRequiresMinVersion() == null && dependentPlugin.getRequiresMinVersion() != null) {
                    return null;
                }
                if (dp.getRequiresMinVersion() != null && !dp.getRequiresMinVersion().equals(dependentPlugin.getRequiresMinVersion())) {
                    return null;
                }
                if (dp.getRequiresMaxVersion() == null && dependentPlugin.getRequiresMaxVersion() != null) {
                    return null;
                }
                if (dp.getRequiresMaxVersion() != null && !dp.getRequiresMaxVersion().equals(dependentPlugin.getRequiresMaxVersion())) {
                    return null;
                }
                return dp;
            }
        }
        return null;
    }

    private Plugins mergePlugins(Plugins libraryPlugins, Plugins runtimePlugins, Plugins rootPlugins) {
        List<PluginDetail> missingRootPlugins = new ArrayList<PluginDetail>();
        for (PluginDetail plugin : rootPlugins.getPlugin()) {
            PluginDetail libraryPlugin = getPlugin(plugin.getId(), libraryPlugins);
            if (libraryPlugin == null) {
                missingRootPlugins.add(plugin);
            }
        }
        if (!missingRootPlugins.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            for (PluginDetail rootPlugin : missingRootPlugins) {
                buff.append(rootPlugin.getId() + ",");
            }
            throw new RuntimeException("Repository does not contain the following root plugins: " + buff.toString() + "\n");
        }
        Plugins mergePlugins = RepositoryMetadata.copyPlugins(libraryPlugins);
        for (PluginDetail mergePlugin : mergePlugins.getPlugin()) {
            PluginDetail runtimePlugin = getPlugin(mergePlugin.getId(), runtimePlugins);
            if (runtimePlugin != null) {
                if (runtimePlugin.getVersion().size() != 1) {
                    throw new RuntimeException("The runtime plugin id: " + runtimePlugin.getId() + " should have only one version");
                }
                String runtimeVersion = runtimePlugin.getVersion().get(0).getId();
                for (PluginVersionDetail pluginDetailVersion : mergePlugin.getVersion()) {
                    if (runtimeVersion.equals(pluginDetailVersion.getId())) {
                        pluginDetailVersion.setRuntime("true");
                        break;
                    }
                }
            }
            PluginDetail rootPlugin = getPlugin(mergePlugin.getId(), rootPlugins);
            if (rootPlugin != null) {
                RootPluginDetail root = getObjectFactory().createRootPluginDetail();
                root.setMinVersion(rootPlugin.getRoot().getMinVersion());
                root.setMaxVersion(rootPlugin.getRoot().getMaxVersion());
                mergePlugin.setRoot(root);
            }
        }
        return mergePlugins;
    }

    private Plugins getUpgradePlugins(Plugins mergePlugins) {
        Plugins upgradePlugins = RepositoryMetadata.copyPlugins(mergePlugins);
        List<PluginDetail> pluginsWithVersionErrors = new ArrayList<PluginDetail>();
        List<PluginDetail> pluginsWithVersionMatchErrors = new ArrayList<PluginDetail>();
        for (PluginDetail plugin : upgradePlugins.getPlugin()) {
            if (plugin.getRoot() != null) {
                try {
                    addUpgrade(plugin, upgradePlugins);
                } catch (NoVersionException ex) {
                    pluginsWithVersionErrors.add(ex.getPlugin());
                } catch (NoVersionMatchException ex) {
                    pluginsWithVersionMatchErrors.add(ex.getPlugin());
                }
            }
        }
        if (!pluginsWithVersionErrors.isEmpty() || !pluginsWithVersionMatchErrors.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            if (!pluginsWithVersionErrors.isEmpty()) {
                buff.append("\n\nPlugins with no known version:\n\n");
                for (PluginDetail plugin : pluginsWithVersionErrors) {
                    buff.append(plugin.getId() + "\n");
                }
            }
            if (!pluginsWithVersionMatchErrors.isEmpty()) {
                buff.append("\n\nPlugins with no matching version:\n\n");
                for (PluginDetail plugin : pluginsWithVersionMatchErrors) {
                    buff.append(plugin.getId() + "\n");
                }
            }
            throw new RuntimeException("Plugin version errors: " + buff.toString());
        }
        return upgradePlugins;
    }

    private void addUpgrade(PluginDetail sourcePlugin, Plugins upgradePlugins) {
        if (sourcePlugin.getUseVersion() != null) {
            return;
        }
        PluginVersionDetail latestPluginVersion = RepositoryMetadata.getLatestVersion(sourcePlugin, getConfigPluginsWrapper().getPlugins().getConstraint());
        sourcePlugin.setUseVersion(latestPluginVersion.getId());
        List<PluginDetail> requiredPlugins = getRequiredPlugins(sourcePlugin, upgradePlugins);
        if (requiredPlugins.isEmpty()) {
            return;
        } else {
            for (PluginDetail requiredPlugin : requiredPlugins) {
                addUpgrade(requiredPlugin, upgradePlugins);
            }
        }
    }

    private List<PluginDetail> getRequiredPlugins(PluginDetail sourcePlugin, Plugins upgradePlugins) {
        List<PluginDetail> requiredPlugins = new ArrayList<PluginDetail>();
        for (PluginDetail plugin : upgradePlugins.getPlugin()) {
            for (DependentPluginDetail dependent : plugin.getDependent()) {
                if (dependent.getId().equals(sourcePlugin.getId()) && sourcePlugin.getUseVersion().equals(dependent.getVersion())) {
                    requiredPlugins.add(plugin);
                }
            }
        }
        return requiredPlugins;
    }

    private void upgrade(Plugins upgradePlugins, Plugins runtimePlugins) {
        File repositoryPluginsDir = getRepositoryRuntimePluginsDir();
        if (!repositoryPluginsDir.getPath().equals(repositoryPluginsDir.getAbsolutePath())) {
            repositoryPluginsDir = new File(getConfigDir(), repositoryPluginsDir.getPath());
        }
        boolean runtimeChanged = false;
        for (PluginDetail plugin : upgradePlugins.getPlugin()) {
            if (plugin.getUseVersion() != null) {
                PluginVersionDetail usePluginVersion = getPluginVersion(plugin.getUseVersion(), plugin);
                PluginDetail runtimePlugin = getPlugin(plugin.getId(), runtimePlugins);
                PluginVersionDetail runtimePluginVersion = null;
                if (runtimePlugin != null) {
                    runtimePluginVersion = getSinglePluginVersion(runtimePlugin);
                }
                if (runtimePluginVersion != null && !runtimePluginVersion.getId().equals(plugin.getUseVersion())) {
                    File[] oldPluginVersionFiles = getPluginZips(plugin.getId(), repositoryPluginsDir);
                    for (File oldPluginVersionFile : oldPluginVersionFiles) {
                        info("Deleting: " + oldPluginVersionFile.getPath());
                        if (!isNoop()) {
                            if (!oldPluginVersionFile.delete()) {
                                throw new RuntimeException("Could not delete old plugin version: " + oldPluginVersionFile.getPath());
                            }
                        }
                    }
                    File oldPluginDir = new File(repositoryPluginsDir, plugin.getId());
                    if (oldPluginDir.exists()) {
                        if (!new File(oldPluginDir, "tolven-plugin.xml").exists()) {
                            throw new RuntimeException(oldPluginDir + " is not a plugin directory");
                        }
                    }
                }
                if (runtimePluginVersion == null) {
                    info("INSTALLING...: " + usePluginVersion.getUri());
                    String downloadInfo = copy(usePluginVersion, repositoryPluginsDir);
                    info(".............. Download Info : " + downloadInfo);
                    runtimeChanged = true;
                } else if (!runtimePluginVersion.getId().equals(usePluginVersion.getId())) {
                    info("UPDATING...: " + usePluginVersion.getUri());
                    String downloadInfo = copy(usePluginVersion, repositoryPluginsDir);
                    info(".............. Download Info : " + downloadInfo);
                    runtimeChanged = true;
                } else if (!runtimePluginVersion.getMessageDigest().getValue().equals(usePluginVersion.getMessageDigest().getValue())) {
                    info("UPDATING: " + usePluginVersion.getUri());
                    String downloadInfo = copy(usePluginVersion, repositoryPluginsDir);
                    info(".............. Download Info : " + downloadInfo);
                    runtimeChanged = true;
                }
            }
        }
        if (deleteUnknownRuntimePlugins(upgradePlugins, runtimePlugins)) {
            runtimeChanged = true;
        }
        File runtimePluginsFile = new File(getRepositoryRuntimeDir(), RepositoryMetadata.METADATA_XML);
        if (!runtimePluginsFile.exists() || runtimeChanged) {
            updateRuntimePluginsXML(upgradePlugins, runtimePluginsFile);
        }
    }

    private boolean deleteUnknownRuntimePlugins(Plugins upgradePlugins, Plugins runtimePlugins) {
        boolean runtimeChanged = false;
        for (PluginDetail runtimePlugin : runtimePlugins.getPlugin()) {
            PluginVersionDetail runtimePluginVersion = getSinglePluginVersion(runtimePlugin);
            PluginDetail upgradePlugin = getPlugin(runtimePlugin.getId(), upgradePlugins);
            File runtimeFile = null;
            try {
                runtimeFile = new File(new URL(runtimePluginVersion.getUri()).getFile());
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert " + runtimePluginVersion.getUri() + " to a File", ex);
            }
            String deleteMessage = null;
            if (upgradePlugin == null) {
                deleteMessage = "REMOVED " + runtimeFile.getPath();
                runtimeChanged = true;
            } else if (upgradePlugin.getUseVersion() == null || !upgradePlugin.getUseVersion().equals(runtimePluginVersion.getId())) {
                deleteMessage = "REMOVED " + runtimeFile.getPath();
                runtimeChanged = true;
            }
            if (deleteMessage != null && runtimeFile.exists()) {
                if (!isNoop()) {
                    runtimeFile.delete();
                }
                info(deleteMessage);
            }
        }
        return runtimeChanged;
    }

    private void updateRuntimePluginsXML(Plugins upgradePlugins, File runtimePluginsFile) {
        Iterator<PluginDetail> pluginIt = upgradePlugins.getPlugin().iterator();
        while (pluginIt.hasNext()) {
            PluginDetail plugin = pluginIt.next();
            PluginVersionDetail usePluginVersion = getPluginVersion(plugin.getUseVersion(), plugin);
            if (usePluginVersion == null) {
                pluginIt.remove();
            } else {
                Iterator<PluginVersionDetail> pluginVersionIt = plugin.getVersion().iterator();
                while (pluginVersionIt.hasNext()) {
                    PluginVersionDetail pluginVersion = pluginVersionIt.next();
                    if (!usePluginVersion.getId().equals(pluginVersion.getId())) {
                        pluginVersionIt.remove();
                    }
                }
            }
        }
        info("Write Library plugins to " + runtimePluginsFile);
        try {
            upgradePlugins.setInfo(getInfo());
            String runtimeRepositoryPluginsXML = RepositoryMetadata.getPluginsXML(upgradePlugins);
            if (isNoop()) {
                System.out.println(runtimeRepositoryPluginsXML);
            } else {
                FileUtils.writeStringToFile(runtimePluginsFile, runtimeRepositoryPluginsXML);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the repositoryRuntime plugins.xml", ex);
        }
    }

    private InfoDetail getInfo() {
        InfoDetail info = getObjectFactory().createInfoDetail();
        SimpleDateFormat dateFormat = new SimpleDateFormat(INFO_TIMESTAMP_FORMAT);
        String timestamp = dateFormat.format(new Date());
        info.setTimestamp(timestamp);
        return info;
    }

    private File[] getPluginZips(String pluginId, File repositoryDir) {
        final Pattern pattern = Pattern.compile(pluginId + "-\\d*.\\d*.\\d*.zip");
        return repositoryDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && pattern.matcher(file.getName()).matches();
            }
        });
    }

    private String copy(PluginVersionDetail usePluginVersion, File runtimeRepository) {
        try {
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("tmpPlugin_", ".zip");
                tmpFile.deleteOnExit();
                URL url = new URL(usePluginVersion.getUri());
                String destFilename = new File(url.getFile()).getName();
                File destFile = new File(runtimeRepository, destFilename);
                InputStream in = null;
                FileOutputStream out = null;
                int bytesDownload = 0;
                long startTime = 0;
                long endTime = 0;
                try {
                    URLConnection urlConnection = url.openConnection();
                    bytesDownload = urlConnection.getContentLength();
                    in = urlConnection.getInputStream();
                    out = new FileOutputStream(tmpFile);
                    startTime = System.currentTimeMillis();
                    IOUtils.copy(in, out);
                    endTime = System.currentTimeMillis();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }
                String downloadSpeedInfo = null;
                long downloadSpeed = 0;
                if ((endTime - startTime) > 0) {
                    downloadSpeed = 1000L * bytesDownload / (endTime - startTime);
                }
                if (downloadSpeed == 0) {
                    downloadSpeedInfo = "? B/s";
                } else if (downloadSpeed < 1000) {
                    downloadSpeedInfo = downloadSpeed + " B/s";
                } else if (downloadSpeed < 1000000) {
                    downloadSpeedInfo = downloadSpeed / 1000 + " KB/s";
                } else if (downloadSpeed < 1000000000) {
                    downloadSpeedInfo = downloadSpeed / 1000000 + " MB/s";
                } else {
                    downloadSpeedInfo = downloadSpeed / 1000000000 + " GB/s";
                }
                String tmpFileMessageDigest = getMessageDigest(tmpFile.toURI().toURL()).getValue();
                if (!tmpFileMessageDigest.equals(usePluginVersion.getMessageDigest().getValue())) {
                    throw new RuntimeException("Downloaded file: " + usePluginVersion.getUri() + " does not have required message digest: " + usePluginVersion.getMessageDigest().getValue());

                }
                if (!isNoop()) {
                    FileUtils.copyFile(tmpFile, destFile);
                }
                return bytesDownload + " Bytes " + downloadSpeedInfo;
            } finally {
                if (tmpFile != null) {
                    tmpFile.delete();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not download " + usePluginVersion.getUri() + " to " + runtimeRepository, ex);
        }
    }

    private boolean pluginsChanged() {
        Plugins runtimePlugins = getRuntimePlugins();
        Long sourceLastModified = null;
        for (PluginDetail runtimePlugin : runtimePlugins.getPlugin()) {
            PluginVersionDetail runtimePluginVersion = null;
            runtimePluginVersion = getSinglePluginVersion(runtimePlugin);
            URL url;
            try {
                url = new URL(runtimePluginVersion.getUri());
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not create URL " + runtimePluginVersion.getUri(), ex);
            }
            File file = new File(url.getFile());
            if (sourceLastModified == null || file.lastModified() > sourceLastModified) {
                sourceLastModified = file.lastModified();
            }
        }
        Long targetLastModified = null;
        if (getConfigPluginsWrapper().getBuildDir().exists()) {
            for (File file : getConfigPluginsWrapper().getBuildDir().listFiles()) {
                if (!new File(file, ".jpf-shadow").exists()) {
                    if (targetLastModified == null || file.lastModified() > targetLastModified.longValue()) {
                        targetLastModified = file.lastModified();
                    }
                }
            }
        }
        if (sourceLastModified != null && targetLastModified != null && targetLastModified > sourceLastModified) {
            return false;
        } else {
            return true;
        }
    }

    private void deleteBuildDir() {
        info("Deleting " + getConfigPluginsWrapper().getBuildDir());
        if (!isNoop()) {
            String tmpDirname = getConfigPluginsWrapper().getBuildDir().getAbsolutePath() + ".tmp";
            File tmpDir = new File(tmpDirname);
            try {
                FileUtils.deleteDirectory(tmpDir);
                if (getConfigPluginsWrapper().getBuildDir().exists()) {
                    FileUtils.moveDirectory(getConfigPluginsWrapper().getBuildDir(), tmpDir);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Could not delete build directory: " + getConfigPluginsWrapper().getBuildDir(), ex);
            } finally {
                try {
                    FileUtils.deleteDirectory(tmpDir);
                } catch (IOException ex) {
                    throw new RuntimeException("Could not delete build directory: " + tmpDir, ex);
                }
            }
        }
    }

    private void info(String aString) {
        if (isNoop()) {
            logger.info(NOOP + ": " + aString);
        } else {
            logger.info(aString);
        }
    }

    private void debug(String aString) {
        if (isNoop()) {
            logger.debug(NOOP + ": " + aString);
        } else {
            logger.debug(aString);
        }
    }

    public static void main(String[] args) {
        new RepositoryUpgrade().initialize(args);
    }

}
