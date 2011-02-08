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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.tolven.plugin.registry.xml.ManifestParser;
import org.tolven.plugin.registry.xml.ModelPluginFragment;
import org.tolven.plugin.registry.xml.ModelPluginManifest;
import org.tolven.plugin.registry.xml.ModelPrerequisite;
import org.tolven.plugin.registry.xml.bean.Plugin;
import org.tolven.plugin.registry.xml.bean.PluginFragment;
import org.tolven.plugin.repository.bean.ConstraintDetail;
import org.tolven.plugin.repository.bean.DependentPluginDetail;
import org.tolven.plugin.repository.bean.InfoDetail;
import org.tolven.plugin.repository.bean.MessageDigestDetail;
import org.tolven.plugin.repository.bean.ObjectFactory;
import org.tolven.plugin.repository.bean.PluginDetail;
import org.tolven.plugin.repository.bean.PluginPropertyDetail;
import org.tolven.plugin.repository.bean.PluginVersionDetail;
import org.tolven.plugin.repository.bean.Plugins;
import org.tolven.security.hash.TolvenMessageDigest;
import org.tolven.util.VersionNumber;

/**
 * The class reads information in all of the plugin manifests in a repository and compiles a repository plugins.xml file.
 * 
 * @author Joseph Isaac
 *
 */
public class RepositoryMetadata {

    public static final String PLUGINS_PACKAGE = "org.tolven.plugin.repository.bean";
    public static final String PLUGIN_PACKAGE = "org.tolven.plugin.registry.xml.bean";
    public static final String LIBRARY_PLUGS_DIR = "plugins";
    public static final String METADATA_XML = "plugins.xml";
    public static final String PLUGINS_XSD = "plugins.xsd";
    public static final String INFO_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    public static final String MESSAGE_DIGEST_ALGORITHM = "md5";
    private static final String INTEGER_MAX_STRING = String.valueOf(Integer.MAX_VALUE);

    private File outDir;
    private ObjectFactory objectFactory;
    private Logger logger = Logger.getLogger(RepositoryMetadata.class);
    private static JAXBContext jaxbContext;

    public RepositoryMetadata() {
    }

    public File getXmlOutputDir() {
        return outDir;
    }

    public void setXmlOutputDir(File outDir) {
        this.outDir = outDir;
    }

    private ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    private void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    private static JAXBContext getJaxbContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(PLUGINS_PACKAGE, ClassLoader.getSystemClassLoader());
            } catch (Exception ex) {
                throw new RuntimeException("Could not create JAXBContext", ex);
            }
        }
        return jaxbContext;
    }

    public void generate(String pluginsDirname, boolean merge, String libraryURLString, File outDir) {
        logger.debug("*** start ***");
        setXmlOutputDir(outDir);
        setObjectFactory(new ObjectFactory());
        File sourcePluginsDir = new File(pluginsDirname);
        if (!sourcePluginsDir.exists()) {
            throw new RuntimeException(sourcePluginsDir.getPath() + " does not exist");
        }
        URL sourceLibraryPluginsURL;
        try {
            sourceLibraryPluginsURL = sourcePluginsDir.toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not convert repository directory " + sourcePluginsDir.getPath() + " to a URL: ", ex);
        }
        logger.info("Reading Library plugins from: " + sourceLibraryPluginsURL.toExternalForm());
        URL libraryPluginsURL;
        try {
            libraryPluginsURL = new URL(libraryURLString);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not convert plugin URL " + libraryURLString + " to a URL: ", ex);
        }
        logger.info("URIs will generated from: " + libraryPluginsURL.toExternalForm());
        List<String> plugins = getLibraryPluginsList(sourcePluginsDir);
        Plugins libraryPlugins = getPlugins(plugins, merge, sourceLibraryPluginsURL.toExternalForm(), libraryPluginsURL.toExternalForm());
        libraryPlugins.setInfo(getInfo());
        if (getXmlOutputDir() == null) {
            logger.info(getPluginsXML(libraryPlugins));
        } else {
            File libraryPluginsFile = new File(getXmlOutputDir(), METADATA_XML);
            logger.info("Write Library plugins to " + libraryPluginsFile);
            libraryPluginsFile.getParentFile().mkdirs();
            try {
                FileUtils.writeStringToFile(libraryPluginsFile, getPluginsXML(libraryPlugins));
            } catch (IOException ex) {
                throw new RuntimeException("Could not write library plugins.xml to: " + libraryPluginsFile.getPath(), ex);
            }
        }
        logger.debug("*** end ***");
    }

    private InfoDetail getInfo() {
        InfoDetail info = getObjectFactory().createInfoDetail();
        SimpleDateFormat dateFormat = new SimpleDateFormat(INFO_TIMESTAMP_FORMAT);
        String timestamp = dateFormat.format(new Date());
        info.setTimestamp(timestamp);
        return info;
    }

    public List<String> getLibraryPluginsList(File pluginsDir) {
        List<String> plugins = new ArrayList<String>();
        File[] zipFiles = pluginsDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                try {
                    String urlString = "jar:" + file.toURI().toURL() + "!/tolven-plugin.xml";
                    URL url = null;
                    url = new URL(urlString);
                    return file.isFile() && url.openConnection().getContentLength() != -1;
                } catch (IOException e) {
                    throw new RuntimeException("Could not access manifest for: " + file.getPath(), e);
                }
            }
        });
        for (File zipFile : zipFiles) {
            plugins.add(zipFile.getName());
        }
        return plugins;
    }

    private Plugins getPlugins(List<String> pluginList, boolean merge, String sourcePluginsURLString, String libraryURLString) {
        Plugins plugins = null;
        if (merge) {
            InputStream in = null;
            try {
                in = new URL(libraryURLString + "/" + METADATA_XML).openStream();
                String pluginsXML = IOUtils.toString(in);
                plugins = getPlugins(pluginsXML);
            } catch (Exception ex) {
                throw new RuntimeException("Could not read the " + METADATA_XML + " from: " + libraryURLString, ex);
            } finally {

            }
        } else {
            plugins = getObjectFactory().createPlugins();
        }
        ManifestParser manifestParser = new ManifestParser(false);
        for (String pluginString : pluginList) {
            URL sourcePluginZipURL;
            String sourcePluginZipURLString = sourcePluginsURLString + pluginString;
            try {
                sourcePluginZipURL = new URL(sourcePluginZipURLString);
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert plugin zip " + sourcePluginZipURLString + " to a URL: ", ex);
            }
            URL finalZipURL;
            String finalZipURLString = libraryURLString + "/" + LIBRARY_PLUGS_DIR + "/" + pluginString;
            try {
                finalZipURL = new URL(finalZipURLString);
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert plugin zip " + finalZipURLString + " to a URL: ", ex);
            }
            URL pluginManifestURL;
            String pluginManifestURLString = "jar:" + sourcePluginsURLString + "/" + pluginString + "!/tolven-plugin.xml";
            try {
                pluginManifestURL = new URL(pluginManifestURLString);
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert plugin manifest " + pluginManifestURLString + " to a URL: ", ex);
            }
            try {
                if (pluginManifestURL.openConnection().getContentLength() == -1) {
                    /*
                     * There is no manifest, therefore this is not a tolven plugin
                     */
                    continue;
                }
            } catch (IOException ex) {
                throw new RuntimeException("Could not open a connection to " + pluginManifestURL.toExternalForm(), ex);
            }
            ModelPluginManifest pluginManifest;
            try {
                pluginManifest = manifestParser.parseManifest(pluginManifestURL.toURI().toURL());
            } catch (Exception ex) {
                throw new RuntimeException("Could not parse plugin manifest: " + pluginManifestURL.toExternalForm(), ex);
            }
            String pluginId = pluginManifest.getId();
            String pluginVersionString = null;
            if (pluginManifest.getVersion() == null) {
                pluginVersionString = "";
            } else {
                pluginVersionString = pluginManifest.getVersion().toString();
            }
            if (pluginVersionString.length() == 0) {
                throw new RuntimeException(sourcePluginZipURL.toExternalForm() + " must have a version ");
            }
            if (!pluginString.matches(pluginId + "-" + pluginVersionString + ".zip")) {
                throw new RuntimeException(sourcePluginZipURL.toExternalForm() + " has manifest naming mismatch with external plugin name i.e: " + pluginString + " != " + (pluginId + "-" + pluginVersionString + ".zip"));
            }
            processPluginManifest(pluginManifest, sourcePluginZipURL, finalZipURL, merge, plugins);
            URL pluginFragmentURL;
            String pluginFragmentURLString = "jar:" + sourcePluginsURLString + "/" + pluginString + "!/tolven-plugin-fragment.xml";
            try {
                pluginFragmentURL = new URL(pluginFragmentURLString);
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert plugin fragment manifest: " + pluginFragmentURLString + " to a URL", ex);
            }
            try {
                if (pluginFragmentURL.openConnection().getContentLength() != -1) {
                    /*
                     * tolven-plugin-fragment.xml is an optional manifest.
                     */
                    logger.debug("\tFound PluginFragment: " + pluginFragmentURL.toExternalForm());
                    ModelPluginFragment pluginFragment;
                    try {
                        pluginFragment = (ModelPluginFragment) manifestParser.parseManifest(pluginFragmentURL.toURI().toURL());
                    } catch (Exception ex) {
                        throw new RuntimeException("Could not parse plugin fragment: " + pluginFragmentURL.toExternalForm(), ex);
                    }
                    if (pluginFragment.getPrerequisites().size() != 1) {
                        throw new RuntimeException("Plugin Fragment: " + pluginFragmentURL.toExternalForm() + " must have only one <import> tag, which must refer to plugin: " + pluginFragmentURL.toExternalForm());
                    }
                    ModelPrerequisite prerequisite = pluginFragment.getPrerequisites().get(0);
                    String prerequisiteVersion = null;
                    if (prerequisite.getPluginVersion() == null) {
                        prerequisiteVersion = "";
                    } else {
                        prerequisiteVersion = prerequisite.getPluginVersion().toString();
                    }
                    if (!pluginVersionString.equals(prerequisiteVersion)) {
                        throw new RuntimeException("The <imported> plugin version referred to by: " + pluginFragmentURL.toExternalForm() + " does not match the plugin version of: " + pluginManifestURL.toExternalForm());
                    }
                    processPluginFragment(prerequisite, pluginFragment, plugins);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Could not open a connection to " + pluginFragmentURL.toExternalForm(), ex);
            }
        }
        return plugins;
    }

    private void processPluginManifest(ModelPluginManifest pluginManifest, URL sourcePluginZipURL, URL finalZipURL, boolean merge, Plugins processedPlugins) {
        String pluginId = pluginManifest.getId();
        PluginDetail pluginDetail = getPluginDetail(pluginId, processedPlugins);
        if (pluginDetail == null) {
            pluginDetail = createPluginDetail(pluginId);
            processedPlugins.getPlugin().add(pluginDetail);
        }
        String version = pluginManifest.getVersion().toString();
        if (version == null || version.length() == 0) {
            throw new RuntimeException("Plugin: " + pluginId + " at " + sourcePluginZipURL.toExternalForm() + " must have a version ");
        }
        PluginVersionDetail pluginVersion = getPluginVersionDetail(version, pluginDetail);
        if (pluginVersion == null) {
            pluginVersion = createPluginVersionDetail(version);
            pluginVersion.setUri(finalZipURL.toExternalForm());
            MessageDigestDetail messageDigest = getMessageDigest(sourcePluginZipURL);
            pluginVersion.setMessageDigest(messageDigest);
            pluginDetail.getVersion().add(pluginVersion);
        } else {
            if (merge) {
                MessageDigestDetail messageDigest = getMessageDigest(sourcePluginZipURL);
                if (!pluginVersion.getMessageDigest().getValue().equals(messageDigest.getValue())) {
                    pluginVersion.setMessageDigest(messageDigest);
                    logger.debug("Merged manifest for: " + sourcePluginZipURL.toExternalForm());
                }
            }
        }
        for (ModelPrerequisite prerequisite : pluginManifest.getPrerequisites()) {
            String requiredPluginId = prerequisite.getPluginId();
            String requiredVersion = null;
            if (prerequisite.getPluginVersion() == null) {
                requiredVersion = "";
            } else {
                requiredVersion = prerequisite.getPluginVersion().toString();
            }
            String requiredMatch = null;
            if (prerequisite.getMatchingRule() == null) {
                requiredMatch = "";
            } else {
                requiredMatch = prerequisite.getMatchingRule().toCode();
            }
            processPrerequisite(pluginDetail, pluginVersion, requiredPluginId, requiredVersion, requiredMatch, processedPlugins);
        }
    }

    private void processPluginFragment(ModelPrerequisite prerequisite, ModelPluginFragment pluginFragment, Plugins processedPlugins) {
        String prerequisitePluginId = prerequisite.getPluginId();
        PluginDetail pluginDetail = getPluginDetail(prerequisitePluginId, processedPlugins);
        if (pluginDetail == null) {
            pluginDetail = createPluginDetail(prerequisitePluginId);
            processedPlugins.getPlugin().add(pluginDetail);
        }
        String prerequisiteVersionString = prerequisite.getPluginVersion().toString();
        PluginVersionDetail prerequisiteVersion = getPluginVersionDetail(prerequisiteVersionString, pluginDetail);
        if (prerequisiteVersion == null) {
            prerequisiteVersion = createPluginVersionDetail(prerequisiteVersionString);
            pluginDetail.getVersion().add(prerequisiteVersion);
        }
        String requiredPluginId = pluginFragment.getPluginId();
        String requiredPluginVersion = null;
        if (pluginFragment.getPluginVersion() == null) {
            requiredPluginVersion = "";
        } else {
            requiredPluginVersion = pluginFragment.getPluginVersion().toString();
        }
        String requiredPluginMatch = null;
        if (pluginFragment.getMatchingRule() == null) {
            requiredPluginMatch = "";
        } else {
            requiredPluginMatch = pluginFragment.getMatchingRule().toCode();
        }
        processPrerequisite(pluginDetail, prerequisiteVersion, requiredPluginId, requiredPluginVersion, requiredPluginMatch, processedPlugins);
    }

    private void processPrerequisite(PluginDetail plugin, PluginVersionDetail pluginVersion, String requiredPluginId, String requiredVersion, String requiredMatch, Plugins plugins) {
        DependentPluginDetail dependent = getObjectFactory().createDependentPluginDetail();
        dependent = getObjectFactory().createDependentPluginDetail();
        dependent.setId(plugin.getId());
        dependent.setVersion(pluginVersion.getId());
        if (!requiredVersion.isEmpty()) {
            if (requiredVersion.isEmpty()) {
                dependent.setRequiresMinVersion(requiredVersion);
                if (requiredMatch.equals("equals")) {
                    dependent.setRequiresMaxVersion(requiredVersion);
                } else if (requiredMatch.equals("greater-or-equal")) {
                    dependent.setRequiresMaxVersion(null);
                } else {
                    throw new RuntimeException("Not a supported match value: " + requiredMatch);
                }
            }
        }
        PluginDetail requiredPlugin = getPluginDetail(requiredPluginId, plugins);
        if (requiredPlugin == null) {
            requiredPlugin = createPluginDetail(requiredPluginId);
            plugins.getPlugin().add(requiredPlugin);
        }
        if (getDependentPlugin(dependent, requiredPlugin) == null) {
            requiredPlugin.getDependent().add(dependent);
        }
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

    private MessageDigestDetail getMessageDigest(URL pluginURL) {
        MessageDigestDetail messageDetail = getObjectFactory().createMessageDigestDetail();
        String digest = TolvenMessageDigest.checksum(pluginURL, MESSAGE_DIGEST_ALGORITHM);
        messageDetail.setType(MESSAGE_DIGEST_ALGORITHM);
        messageDetail.setValue(digest);
        return messageDetail;
    }

    public static PluginDetail getPluginDetail(String pluginId, Plugins plugins) {
        for (PluginDetail pluginDetail : plugins.getPlugin()) {
            if (pluginDetail.getId().equals(pluginId)) {
                return pluginDetail;
            }
        }
        return null;
    }

    private PluginDetail createPluginDetail(String pluginId) {
        PluginDetail pluginDetail = getObjectFactory().createPluginDetail();
        pluginDetail.setId(pluginId);
        return pluginDetail;
    }

    public static PluginVersionDetail getPluginVersionDetail(String version, PluginDetail plugin) {
        for (PluginVersionDetail pluginDetailVersion : plugin.getVersion()) {
            if (pluginDetailVersion.getId().equals(version)) {
                return pluginDetailVersion;
            }
        }
        return null;
    }

    private PluginVersionDetail createPluginVersionDetail(String version) {
        PluginVersionDetail pluginDetailVersion = getObjectFactory().createPluginVersionDetail();
        pluginDetailVersion.setId(version);
        return pluginDetailVersion;
    }

    public static Plugins getRepositoryPlugins(URL repositoryURL) {
        try {
            Plugins plugins = getPlugins(new URL(repositoryURL.toExternalForm() + "/" + METADATA_XML));
            return plugins;
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Problem appending " + "/" + METADATA_XML + " to URL " + repositoryURL.toExternalForm(), ex);
        }
    }

    public static Plugins getPlugins(URL pluginsXMLURL) {
        Logger.getLogger(RepositoryMetadata.class).info("Plugins URL: '" + pluginsXMLURL.toExternalForm() + "'");
        try {
            InputStream in = null;
            try {
                in = pluginsXMLURL.openStream();
                return getPlugins(in);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load plugins from: " + pluginsXMLURL.toExternalForm(), ex);
        }
    }

    /**
     * Unmarshall a Plugins from a String
     * 
     * @param pluginsXML
     * @return
     */
    public static Plugins getPlugins(String pluginsXML) {
        try {
            StringReader reader = new StringReader(pluginsXML);
            Unmarshaller u = getJaxbContext().createUnmarshaller();
            Plugins plugins = (Plugins) u.unmarshal(reader);
            return plugins;
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not read plugins from: " + pluginsXML, ex);
        }
    }

    public static Plugin getPlugin(InputStream xsdStream) {
        try {
            Unmarshaller u = getJaxbContext().createUnmarshaller();
            return (Plugin) u.unmarshal(xsdStream);
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not load plugins inputstream", ex);
        }
    }

    public static Plugin getPlugin(URL pluginManifest) {
        InputStream in = null;
        try {
            try {
                in = pluginManifest.openStream();
                return getPlugin(in);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not read plugins from: " + pluginManifest.toExternalForm(), ex);
        }

    }

    public static PluginFragment getPluginFragment(InputStream xsdStream) {
        try {
            Unmarshaller u = getJaxbContext().createUnmarshaller();
            return (PluginFragment) u.unmarshal(xsdStream);
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not load plugins inputstream", ex);
        }
    }

    public static PluginFragment getPluginFragment(URL pluginFragManifest) {
        InputStream in = null;
        try {
            try {
                in = pluginFragManifest.openStream();
                return getPluginFragment(in);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not read plugins from: " + pluginFragManifest.toExternalForm(), ex);
        }
    }

    public static String getPluginManifest(Plugin plugin) {
        try {
            StringWriter stringWriter = new StringWriter();
            Marshaller marshaller = getJaxbContext().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "urn:tolven-org:tpf:1.0 http://tolven.org/xsd/tpf.xsd");
            marshaller.marshal(plugin, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not convert plugin manifest to string for plugin: " + plugin.getId(), ex);
        }
    }

    public static String getPluginFragmentManifest(PluginFragment pluginFragment) {
        try {
            StringWriter stringWriter = new StringWriter();
            Marshaller marshaller = getJaxbContext().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(pluginFragment, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not convert plugin fragment manifest to string for plugin: " + pluginFragment.getId(), ex);
        }
    }

    /**
     * Unmarshall a Plugins from an InputStream
     * @param xmlStream
     * @return
     */
    public static Plugins getPlugins(InputStream xmlStream) {
        try {
            Unmarshaller u = getJaxbContext().createUnmarshaller();
            Plugins plugins = (Plugins) u.unmarshal(xmlStream);
            return plugins;
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not load plugins inputstream", ex);
        }
    }

    public static String getPluginsXML(Plugins plugins) {
        try {
            StringWriter stringWriter = new StringWriter();
            Marshaller marshaller = getJaxbContext().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(plugins, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not convert Plugins to a string", ex);
        }
    }

    public static Plugins copyPlugins(Plugins plugins) {
        return getPlugins(getPluginsXML(plugins));
    }

    public static PluginVersionDetail getLatestVersion(final PluginDetail plugin) {
        return getLatestVersion(plugin, new ArrayList<ConstraintDetail>());
    }

    public static PluginVersionDetail getLatestVersion(final PluginDetail plugin, List<ConstraintDetail> constraints) {
        if (plugin.getVersion().isEmpty()) {
            NoVersionException ex = new NoVersionException("No versions exist for plugin: " + plugin.getId());
            ex.setPlugin(plugin);
            throw ex;
        }
        List<PluginVersionDetail> matchingVersions = getMatchingVersions(plugin, constraints);
        if (matchingVersions.isEmpty()) {
            NoVersionMatchException ex = new NoVersionMatchException("No matching versions exist for plugin: " + plugin.getId());
            ex.setPlugin(plugin);
            throw ex;
        }
        PluginVersionDetail latestPluginVersion = null;
        for (PluginVersionDetail pluginVersion : matchingVersions) {
            if (latestPluginVersion == null || VersionNumber.compare(pluginVersion.getId(), latestPluginVersion.getId()) > 0) {
                latestPluginVersion = pluginVersion;
            }
        }
        return latestPluginVersion;
    }

    private static List<PluginVersionDetail> getMatchingVersions(PluginDetail plugin, List<ConstraintDetail> constraints) {
        List<PluginVersionDetail> matchingVersions = new ArrayList<PluginVersionDetail>();
        for (PluginVersionDetail pluginVersion : plugin.getVersion()) {
            boolean pluginIdMatched = false;
            for (ConstraintDetail constraint : constraints) {
                String constraintPluginId = constraint.getPluginId();
                if (plugin.getId().matches(constraintPluginId)) {
                    pluginIdMatched = true;
                    if ((constraint.getMinVersion() == null || constraint.getMinVersion().length() == 0 || VersionNumber.compare(pluginVersion.getId(), constraint.getMinVersion()) >= 0) && (constraint.getMaxVersion() == null || constraint.getMaxVersion().length() == 0 || VersionNumber.compare(pluginVersion.getId(), constraint.getMaxVersion().replace("*", INTEGER_MAX_STRING)) <= 0)) {
                        matchingVersions.add(pluginVersion);
                    }
                }
            }
            if (!pluginIdMatched) {
                matchingVersions.add(pluginVersion);
            }
        }
        return matchingVersions;
    }

    public static String getVersion(URL pluginManifestURL) {
        ModelPluginManifest pluginManifest = null;
        try {
            ManifestParser manifestParser = new ManifestParser(false);
            pluginManifest = manifestParser.parseManifest(pluginManifestURL.toURI().toURL());
        } catch (Exception ex) {
            throw new RuntimeException("Could not parse plugin manifest: " + pluginManifestURL.toExternalForm(), ex);
        }
        if (pluginManifest.getVersion() == null) {
            throw new RuntimeException(pluginManifestURL.toExternalForm() + " has no version ");
        } else {
            return pluginManifest.getVersion().toString();
        }
    }

    public static String format(URL pluginsXMLURL) {
        Plugins plugins = getPlugins(pluginsXMLURL);
        return format(plugins);
    }

    public static String format(Plugins plugins) {
        List<PluginDetail> pluginDetails = plugins.getPlugin();
        Comparator<PluginDetail> pluginComparator = new Comparator<PluginDetail>() {
            public int compare(PluginDetail plugin1, PluginDetail plugin2) {
                return (plugin1.getId().compareTo(plugin2.getId()));
            };
        };
        Collections.sort(pluginDetails, pluginComparator);
        Comparator<PluginPropertyDetail> propertyDetailComparator = new Comparator<PluginPropertyDetail>() {
            public int compare(PluginPropertyDetail property1, PluginPropertyDetail property2) {
                return (property1.getName().compareTo(property2.getName()));
            };
        };
        for (PluginDetail pluginDetail : pluginDetails) {
            List<PluginPropertyDetail> propertyDetails = pluginDetail.getProperty();
            Collections.sort(propertyDetails, propertyDetailComparator);
        }
        List<PluginPropertyDetail> propertyDetails = plugins.getProperty();
        Collections.sort(propertyDetails, propertyDetailComparator);
        return getPluginsXML(plugins);
    }

    public static void main(String[] args) {
        Options cmdLineOptions = new Options();
        Option pluginsDirOption = new Option("plugins", "plugins", true, "plugins directory");
        pluginsDirOption.setRequired(true);
        cmdLineOptions.addOption(pluginsDirOption);
        cmdLineOptions.addOption(new Option("merge", "merge", false, "Merge pluginsDir into metadata"));
        Option libraryURLOption = new Option("liburl", "liburl", true, "library plugins URL");
        libraryURLOption.setRequired(true);
        cmdLineOptions.addOption(libraryURLOption);
        Option outdirDirOption = new Option("outdir", "outdir", true, "metadata output directory");
        outdirDirOption.setRequired(true);
        cmdLineOptions.addOption(outdirDirOption);
        GnuParser parser = new GnuParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(cmdLineOptions, args);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(org.tolven.plugin.repository.RepositoryMetadata.class.getName(), cmdLineOptions);
            throw new RuntimeException("Could not parse command line for: " + org.tolven.plugin.repository.RepositoryMetadata.class.getName(), ex);
        }
        String pluginsDirname = commandLine.getOptionValue("plugins");
        boolean merge = commandLine.hasOption("merge");
        String libraryURLString = commandLine.getOptionValue("liburl");
        String outDirname = commandLine.getOptionValue("outdir");
        File outDir = new File(outDirname);
        new RepositoryMetadata().generate(pluginsDirname, merge, libraryURLString, outDir);
    }

}
