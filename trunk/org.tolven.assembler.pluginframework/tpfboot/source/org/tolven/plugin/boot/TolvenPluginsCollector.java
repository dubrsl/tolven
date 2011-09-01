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
package org.tolven.plugin.boot;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.boot.DefaultPluginsCollector;
import org.tolven.plugin.standard.TolvenPluginLocation;

/**
 * A class to collect plugins from a repository, based on whether they contain tolven-plugin.xml or tolven-plugin-fragment.xml
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenPluginsCollector extends DefaultPluginsCollector {

    @Override
    protected void processFolder(File folder, List<PluginLocation> result) {
        List<PluginLocation> pluginLocations = new ArrayList<PluginLocation>();
        try {
            pluginLocations = TolvenPluginLocation.createDirectoryLocations(folder);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not process: '" + folder.getPath() + "'", ex);
        }
        for (PluginLocation pluginLocation : pluginLocations) {
            result.add(pluginLocation);
        }
        if (pluginLocations.isEmpty()) {
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile()) {
                    processFile(file, result);
                } else if (file.isDirectory()) {
                    processFolder(file, result);
                }
            }
        }
    }

    @Override
    protected void processFile(File file, List<PluginLocation> result) {
        List<PluginLocation> pluginLocations = new ArrayList<PluginLocation>();
        try {
            pluginLocations = TolvenPluginLocation.createZipLocations(file);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not process: '" + file.getPath() + "'", ex);
        }
        for (PluginLocation pluginLocation : pluginLocations) {
            result.add(pluginLocation);
        }
    }

}
