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
 * @version $Id: TolvenPlugin.java 1837 2011-07-26 10:38:02Z joe.isaac $
 */
package org.tolven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.java.plugin.Plugin;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.tolven.plugin.repository.ConfigPluginsWrapper;
import org.tolven.plugin.repository.bean.PluginPropertyDetail;

/**
 * An abstract class for all tolven plugins. The methods in this class may not be supported in future versions.
 * 
 * @author Joseph Isaac
 *
 */
public abstract class TolvenPlugin extends Plugin {

    public static final String JAAS_LOGIN_CONFIG_PROPERTY = "java.security.auth.login.config";
    public static final String TOLVEN_LOGINCONTEXT = "tolvenLDAP";

    private static String[] initArgs;
    private static ConfigPluginsWrapper pluginsWrapper;

    public static String[] getInitArgs() {
        return initArgs;
    }

    public static void setInitArgs(String[] initArgs) {
        TolvenPlugin.initArgs = initArgs;
    }

    public static ConfigPluginsWrapper getPluginsWrapper() {
        return pluginsWrapper;
    }

    public static void setPluginsWrapper(ConfigPluginsWrapper pluginsWrapper) {
        TolvenPlugin.pluginsWrapper = pluginsWrapper;
    }

    public static File getConfigDir() {
        return getPluginsWrapper().getConfigDir();
    }

    public static File getTolvenConfigDir() {
        return getPluginsWrapper().getConfigDir();
    }

    public static File getTmpDir() {
        return getPluginsWrapper().getRepositoryTmpDir();
    }

    public static File getStageDir() {
        return getPluginsWrapper().getRepositoryStageDir();
    }

    public static File getDevLib() {
        return getPluginsWrapper().getRepositoryDevLibDir();
    }

    protected File getPluginZip() {
        return getPluginZip(getDescriptor());
    }

    protected File getPluginZip(PluginDescriptor pluginDescriptor) {
        String manifestFile = pluginDescriptor.getLocation().getFile();
        int index = manifestFile.lastIndexOf("!/");
        if (index == -1) {
            index = manifestFile.length() - 1;
        }
        try {
            URL zipURL = new URL(manifestFile.substring(0, index));
            return new File(zipURL.getFile());
        } catch (IOException ex) {
            throw new RuntimeException("Error occurred while retrieving URL for plugin: " + pluginDescriptor, ex);
        }
    }

    protected File getPluginTmpDir() {
        return getPluginTmpDir(getDescriptor());
    }

    protected File getPluginTmpDir(PluginDescriptor pluginDescriptor) {
        File pluginTmpDir = new File(getTmpDir(), pluginDescriptor.getId());
        if (!pluginTmpDir.exists()) {
            pluginTmpDir.mkdirs();
        }
        return pluginTmpDir;
    }

    protected File getFilePath(String path) {
        return getFilePath(getDescriptor(), path);
    }

    protected File getFilePath(PluginDescriptor pluginDescriptor) {
        return getFilePath(pluginDescriptor, "");
    }

    protected File getFilePath(PluginDescriptor pluginDescriptor, String path) {
        try {
            URL url = new URL(getManager().getPathResolver().resolvePath(pluginDescriptor, "") + path);
            return new File(url.getFile());
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not create URL for pluginDescriptor " + pluginDescriptor.getUniqueId() + " with suffix '" + path + "'", ex);
        }
    }

    protected boolean resourceExists(URL url) throws IOException {
        return url.openConnection().getContentLength() != -1;
    }

    /**
     * Escape {} because XSLT complains if this is not done before it begins translating
     * Note that although many properties could be expanded to remove the braces, some file
     * require the braces to remain intact
     * 
     * @param aString
     * @return
     */
    protected String escapeCurlyBraces(String aString) {
        return aString.replace("{", "{{").replace("}", "}}");
    }

    protected boolean isAbsoluteFilePath(File file) {
        return file.getPath().equals(file.getAbsolutePath());
    }

    protected Extension getMyExtension(String extensionId) {
        Extension extension = getDescriptor().getExtension(extensionId);
        if (extension == null) {
            throw new RuntimeException(getDescriptor().getId() + " does not have extension: '" + extensionId + "'");
        }
        return extension;
    }

    protected ExtensionPoint getMyExtensionPoint(String extensionPointId) {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(extensionPointId);
        if (extensionPoint == null) {
            throw new RuntimeException(getDescriptor().getId() + " does not have extension: '" + extensionPointId + "'");
        }
        return extensionPoint;
    }

    protected Extension getSingleConnectedExtension(ExtensionPoint extensionPoint) {
        Collection<Extension> extensions = extensionPoint.getConnectedExtensions();
        if (extensions.size() != 1) {
            throw new RuntimeException("ExtensionPoint" + extensionPoint + " does not have a only one extension");
        }
        return extensions.iterator().next();
    }

    protected List<Extension> getExtensions(PluginDescriptor pluginDescriptor, String extensionPointId) {
        Set<Extension> extensions = new HashSet<Extension>();
        for (Extension localExtension : pluginDescriptor.getExtensions()) {
            Parameter targetPluginIdParameter = localExtension.getParameter("target-plugin-id");
            if (localExtension.getExtendedPointId().equals(extensionPointId) && targetPluginIdParameter != null && pluginDescriptor.getId().equals(targetPluginIdParameter.valueAsString())) {
                extensions.add(localExtension);
            }
        }
        for (Extension localExtension : pluginDescriptor.getExtensions()) {
            if (localExtension.getExtendedPointId().equals("compositeExtensionPoint")) {
                PluginDescriptor extendedPluginDescriptor = getManager().getRegistry().getPluginDescriptor(localExtension.getExtendedPluginId());
                ExtensionPoint remoteExtensionPoint = extendedPluginDescriptor.getExtensionPoint(extensionPointId);
                if (remoteExtensionPoint != null) {
                    for (Extension remoteExtension : remoteExtensionPoint.getConnectedExtensions()) {
                        Parameter targetPluginIdParameter = remoteExtension.getParameter("target-plugin-id");
                        if (targetPluginIdParameter != null && pluginDescriptor.getId().equals(targetPluginIdParameter.valueAsString())) {
                            extensions.add(remoteExtension);
                        }
                    }
                }
            }
        }
        return new ArrayList<Extension>(extensions);
    }

    protected List<Extension> getOrderedExtensions(Collection<Extension> extensions) {
        List<Extension> orderedExtensions = new ArrayList<Extension>();
        orderedExtensions.addAll(extensions);
        return getOrderedExtensions(orderedExtensions);
    }

    protected List<Extension> getOrderedExtensions(List<Extension> extensions) {
        List<Extension> orderedExtensions = new ArrayList<Extension>();
        orderedExtensions.addAll(extensions);
        Comparator<Extension> comparator = new Comparator<Extension>() {
            public int compare(Extension extension1, Extension extension2) {
                return (extension1.getId().compareTo(extension2.getId()));
            };
        };
        Collections.sort(orderedExtensions, comparator);
        return orderedExtensions;
    }

    protected List<ExtensionPoint> getOrderedExtensionPoints(Collection<ExtensionPoint> extensionPoints) {
        List<ExtensionPoint> orderedExtensionPoints = new ArrayList<ExtensionPoint>();
        orderedExtensionPoints.addAll(extensionPoints);
        return getOrderedExtensionPoints(orderedExtensionPoints);
    }

    protected List<ExtensionPoint> getOrderedExtensionPoints(List<ExtensionPoint> extensionPoints) {
        List<ExtensionPoint> orderedExtensionPoints = new ArrayList<ExtensionPoint>();
        orderedExtensionPoints.addAll(extensionPoints);
        Comparator<ExtensionPoint> comparator = new Comparator<ExtensionPoint>() {
            public int compare(ExtensionPoint extension1, ExtensionPoint extension2) {
                return (extension1.getId().compareTo(extension2.getId()));
            };
        };
        Collections.sort(orderedExtensionPoints, comparator);
        return orderedExtensionPoints;
    }

    protected ExtensionPoint getParentExtensionPoint(ExtensionPoint extensionPoint) {
        String parentPluginId = extensionPoint.getParentPluginId();
        String parentExtensionPointId = extensionPoint.getParentExtensionPointId();
        if (parentPluginId == null || parentExtensionPointId == null) {
            throw new RuntimeException("Cannot locate parent for extension point: " + extensionPoint.getUniqueId());
        }
        return getManager().getRegistry().getExtensionPoint(parentPluginId, parentExtensionPointId);
    }

    protected List<Extension> getChildExtensions(String extensionPointId, PluginDescriptor pluginDescriptor) {
        List<Extension> extensions = new ArrayList<Extension>();
        for (ExtensionPoint extensionPoint : pluginDescriptor.getExtensionPoints()) {
            if (getDescriptor().getId().equals(extensionPoint.getParentPluginId()) && extensionPointId.equals(extensionPoint.getParentExtensionPointId())) {
                extensions.addAll(extensionPoint.getConnectedExtensions());
            }
        }
        return extensions;
    }

    protected Extension getSingleExtension(ExtensionPoint extensionPoint) {
        Collection<Extension> extensions = extensionPoint.getConnectedExtensions();
        if (extensions.size() != 1) {
            throw new RuntimeException("Expected one and only one extension connected to: " + extensionPoint.getUniqueId());
        }
        return extensions.iterator().next();
    }

    protected String getTranslatedXML(String xml, String xslt) {
        StringWriter writer = new StringWriter();
        StringReader stringReader = new StringReader(xml);
        Source xmlSource = new StreamSource(stringReader);
        Source xsltSource = new StreamSource(new StringReader(xslt));
        Result result = new StreamResult(writer);
        TransformerFactory transFact = TransformerFactory.newInstance();
        try {
            Transformer trans = transFact.newTransformer(xsltSource);
            trans.transform(xmlSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            throw new RuntimeException("Could not carry out XSLT transform", ex);
        }
    }

    public String evaluate(String aString) {
        return evaluate(aString, getDescriptor());
    }

    public String evaluate(String aString, PluginDescriptor pluginDescriptor) {
        return getPluginsWrapper().evaluate(aString, pluginDescriptor.getId());
    }

    public static PluginPropertyDetail getPluginProperty(String key) {
        return getPluginsWrapper().getPluginProperty(key);
    }

    @Override
    protected abstract void doStart() throws Exception;

    @Override
    protected abstract void doStop() throws Exception;

    protected void copy(URL url, File file) throws IOException {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = url.openStream();
            out = new FileOutputStream(file);
            IOUtils.copy(in, out);
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    protected Properties loadProperties(File propertiesFile) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(propertiesFile);
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException ex) {
            throw new RuntimeException("Could not load properties file: " + propertiesFile.getPath(), ex);
        }
    }

    protected Properties storeProperties(Properties properties, File file) {
        try {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                properties.store(out, "Created by TPF");
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            return properties;
        } catch (Exception ex) {
            throw new RuntimeException("Could not store properties to " + file.getPath(), ex);
        }
    }

}
