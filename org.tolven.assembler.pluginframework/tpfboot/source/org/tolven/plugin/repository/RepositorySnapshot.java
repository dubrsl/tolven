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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tolven.plugin.boot.TPFBoot;
import org.tolven.plugin.repository.bean.PluginPropertyDetail;
import org.tolven.plugin.repository.bean.Plugins;

/**
 * The class creates a snapshot metadata for repositories
 * 
 * @author Joseph Isaac
 *
 */
public class RepositorySnapshot {

    private Logger logger = Logger.getLogger(RepositorySnapshot.class);

    public RepositorySnapshot() {
    }

    public ConfigPluginsWrapper getConfigPluginsWrapper() {
        return TPFBoot.pluginsWrapper;
    }

    public void initialize() {
        logger.debug("*** start ***");
        PluginPropertyDetail repositoryLibraryGroupProperty = getConfigPluginsWrapper().getRepositoryLibrary();
        Map<File, File> fileMap = new HashMap<File, File>();
        for (PluginPropertyDetail repositoryLibraryProperty : repositoryLibraryGroupProperty.getProperty()) {
            boolean overwriteSnapshot = getOverwriteSnapshot(repositoryLibraryProperty);
            String snapshotMetadata = getProperty(ConfigPluginsWrapper.REPOSITORY_SNAPSHOT_METADATA, repositoryLibraryProperty.getProperty());
            if (snapshotMetadata == null) {
                continue;
            }
            String evaluatedSnapshotMetadata = getConfigPluginsWrapper().evaluate(snapshotMetadata);
            if (evaluatedSnapshotMetadata == null) {
                throw new RuntimeException("Library URL: " + evaluatedSnapshotMetadata + " evaluated to null");
            }
            URL snapshotMetadataURL = null;
            try {
                snapshotMetadataURL = new URL(evaluatedSnapshotMetadata);
            } catch (MalformedURLException ex) {
                throw new RuntimeException("Could not convert to URL: '" + evaluatedSnapshotMetadata + "'", ex);
            }
            File snapshotMetadataFile = null;
            try {
                snapshotMetadataFile = new File(snapshotMetadataURL.toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Could not convert to file: " + snapshotMetadataURL.toExternalForm(), ex);
            }
            if (!snapshotMetadataFile.exists() || overwriteSnapshot) {
                File tmpFile = getTmpFile(repositoryLibraryProperty);
                if (tmpFile != null) {
                    fileMap.put(tmpFile, snapshotMetadataFile);
                }
            }
        }
        copyTmpFiles(fileMap);
    }

    private File getTmpFile(PluginPropertyDetail repositoryLibraryProperty) {
        String trunkMetadata = getProperty(ConfigPluginsWrapper.REPOSITORY_TRUNK_METADATA, repositoryLibraryProperty.getProperty());
        if (trunkMetadata == null) {
            throw new RuntimeException("Property undefined: " + ConfigPluginsWrapper.REPOSITORY_TRUNK_METADATA);
        }
        String evaluatedTrunkMetadata = getConfigPluginsWrapper().evaluate(trunkMetadata);
        if (evaluatedTrunkMetadata == null) {
            throw new RuntimeException("Library URL: " + evaluatedTrunkMetadata + " evaluated to null");
        }
        URL trunkMetadataURL = null;
        try {
            trunkMetadataURL = new URL(evaluatedTrunkMetadata);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not convert to URL: '" + evaluatedTrunkMetadata + "'", ex);
        }
        Plugins libraryPlugins = getPlugins(trunkMetadataURL);
        String pluginsXML = RepositoryMetadata.getPluginsXML(libraryPlugins);
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("snapshotMetadata-", ".tmp");
            tmpFile.deleteOnExit();
        } catch (Exception ex) {
            throw new RuntimeException("Could not create temporary file", ex);
        }
        try {
            FileUtils.writeStringToFile(tmpFile, pluginsXML);
            return tmpFile;
        } catch (Exception ex) {
            throw new RuntimeException("Could not write snapshot metadata to: " + tmpFile.getPath(), ex);
        }
    }

    private Plugins getPlugins(URL metadataURL) {
        try {
            InputStream in = null;
            try {
                in = metadataURL.openStream();
                Plugins libraryPlugins = RepositoryMetadata.getPlugins(in);
                return libraryPlugins;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load plugins metadata from: " + metadataURL.toExternalForm(), ex);
        }
    }

    private String getProperty(String name, List<PluginPropertyDetail> properties) {
        for (PluginPropertyDetail property : properties) {
            if (property.getName().equals(name)) {
                return property.getValue();
            }
        }
        return null;
    }

    private boolean getOverwriteSnapshot(PluginPropertyDetail repositoryLibraryProperty) {
        String overwriteSnapshotValue = getProperty(ConfigPluginsWrapper.OVERWRITE_SNAPSHOT, repositoryLibraryProperty.getProperty());
        boolean overwriteSnapshot = false;
        if (overwriteSnapshotValue == null) {
            overwriteSnapshot = false;
        } else if (Boolean.TRUE.toString().equals(overwriteSnapshotValue)) {
            overwriteSnapshot = true;
        } else if (Boolean.FALSE.toString().equals(overwriteSnapshotValue)) {
            overwriteSnapshot = false;
        } else {
            throw new RuntimeException("Unrecognized property value for: " + ConfigPluginsWrapper.OVERWRITE_SNAPSHOT + ": " + overwriteSnapshotValue);
        }
        return overwriteSnapshot;
    }

    private void copyTmpFiles(Map<File, File> fileMap) {
        for (File tmpFile : fileMap.keySet()) {
            File snapshotMetadataFile = null;
            try {
                snapshotMetadataFile = fileMap.get(tmpFile);
                snapshotMetadataFile.getParentFile().mkdirs();
                logger.info("Write Snapshot metadata to " + snapshotMetadataFile);
                FileUtils.copyFile(tmpFile, snapshotMetadataFile);
            } catch (Exception ex) {
                throw new RuntimeException("Could not write snapshot metadata to: " + snapshotMetadataFile.getPath(), ex);
            }
        }
    }

    public static void main(String[] args) {
        new RepositorySnapshot().initialize();
    }

}
