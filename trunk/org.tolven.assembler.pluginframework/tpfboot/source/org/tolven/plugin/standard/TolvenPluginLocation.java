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
package org.tolven.plugin.standard;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.standard.StandardPluginLocation;
import org.java.plugin.util.IoUtil;

/**
 * This class collects all of the known plugins based on the repository locations in the boot.properties file.
 * It looks for plugin manifests called tolven-plugin.xml and tolven-plugin-fragment.xml, which it expects to find
 * either in the directory of the plugin or the top of the zip file, if the plugin is zipped.
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenPluginLocation extends StandardPluginLocation {

    public TolvenPluginLocation(File file, String manifestPath) throws MalformedURLException {
        super(file, manifestPath);
    }

    public TolvenPluginLocation(URL context, URL manifest) {
        super(context, manifest);
    }

    public static List<PluginLocation> createDirectoryLocations(File dir) throws MalformedURLException {
        List<PluginLocation> pluginLocations = new ArrayList<PluginLocation>();
        if (dir.isDirectory()) {
            for (URL manifestUrl : getDirectoryManifestUrls(dir)) {
                pluginLocations.add(new StandardPluginLocation(IoUtil.file2url(dir), manifestUrl));
            }
        }
        return pluginLocations;
    }

    protected static List<URL> getDirectoryManifestUrls(File dir) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();
        File tolvenPluginXML = new File(dir, "tolven-plugin.xml");
        if (tolvenPluginXML.exists()) {
            urls.add(IoUtil.file2url(tolvenPluginXML));
        }
        File tolvenPluginFragment = new File(dir, "tolven-plugin-fragment.xml");
        if (tolvenPluginFragment.exists()) {
            urls.add(IoUtil.file2url(tolvenPluginFragment));
        }
        return urls;
    }

    public static List<PluginLocation> createZipLocations(File file) throws MalformedURLException {
        List<PluginLocation> pluginLocations = new ArrayList<PluginLocation>();
        if (file.isFile() && file.getPath().endsWith(".zip")) {
            for (URL manifestUrl : getZipManifestUrls(file)) {
                pluginLocations.add(new StandardPluginLocation(IoUtil.file2url(file), manifestUrl));
            }
        }
        return pluginLocations;
    }

    protected static List<URL> getZipManifestUrls(File file) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();
        URL tolvenPluginXML = new URL("jar:" + IoUtil.file2url(file).toExternalForm() + "!/tolven-plugin.xml");
        try {
            if (tolvenPluginXML.openConnection().getContentLength() != -1) {
                urls.add(tolvenPluginXML);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not read tolven-plugin.xml in : " + file.getPath(), ex);
        }
        URL tolvenPluginFragment = new URL("jar:" + IoUtil.file2url(file).toExternalForm() + "!/tolven-plugin-fragment.xml");
        try {
            if (tolvenPluginFragment.openConnection().getContentLength() != -1) {
                urls.add(tolvenPluginFragment);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not read tolven-plugin-fragment.xml in : " + file.getPath(), ex);
        }
        return urls;
    }

}
