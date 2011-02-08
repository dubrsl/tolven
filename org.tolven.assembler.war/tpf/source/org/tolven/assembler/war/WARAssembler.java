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
package org.tolven.assembler.war;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.security.hash.TolvenMessageDigest;
import org.tolven.tools.ant.TolvenJar;
import org.tolven.tools.ant.TolvenReplaceRegExp;
import org.tolven.tools.ant.TolvenZip;

/**
 * This plugin assembles a war file.
 * 
 * @author Joseph Isaac
 *
 */
public class WARAssembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_WEBDIRECTORY = "webDirectory";
    public static final String EXTENSIONPOINT_WAR_PROPERTY = "property";
    public static final String EXTENSIONPOINT_WAR_PROPERTY_SEQUENCE = "propertySequence";
    public static final String EXTENSIONPOINT_ABSTRACT_WAR = "abstractWAR";
    public static final String EXTENSION_WEBXML_ASSEMBLER = "webXMLAssemblerExtension";
    public static final String EXTENSION_FACES_CONFIG_ASSEMBLER = "facesConfigAssemblerExtension";
    public static final String EXTENSIONPOINT_CLASSES = "classes";
    public static final String EXTENSIONPOINT_METAINF = "META-INF";
    public static final String EXTENSIONPOINT_WEBINFLIB = "WEB-INF-LIB";
    public static final String EXTENSIONPOINT_FILE_INCLUDE = "fileInclude";
    public static final String TAGLIB_EXTENSIONPOINT = "taglib";
    public static final String EXTENSIONPOINT_REMOTE_PRODUCT_DIR = "remoteProductDir";
    public static final String EXTENSIONPOINT_REMOTE_INCLUDES = "remoteIncludes";
    public static final String EXTENSIONPOINT_REMOTE_FILE = "remoteFile";
    public static final String MESSAGE_DIGEST_ALGORITHM = "md5";

    public static final String CMD_LINE_WAR_PLUGIN_OPTION = "warPlugin";
    public static final String CMD_LINE_WEB_URI_OPTION = "webURI";
    public static final String CMD_LINE_DESTDIR_OPTION = "destDir";

    private Logger logger = Logger.getLogger(WARAssembler.class);

    protected PluginDescriptor getAbstractWARPluginDescriptor() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_ABSTRACT_WAR);
        String parentPluginId = extensionPoint.getParentPluginId();
        PluginDescriptor pluginDescriptor = getManager().getRegistry().getPluginDescriptor(parentPluginId);
        return pluginDescriptor;
    }

    protected PluginDescriptor getTagLibPluginDescriptor() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(TAGLIB_EXTENSIONPOINT);
        String parentPluginId = extensionPoint.getParentPluginId();
        PluginDescriptor pluginDescriptor = getManager().getRegistry().getPluginDescriptor(parentPluginId);
        return pluginDescriptor;
    }

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
        logger.debug("deleting: " + getPluginTmpDir());
        FileUtils.deleteDirectory(getPluginTmpDir());
        getPluginTmpDir().mkdirs();
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        String warPluginId = commandLine.getOptionValue(CMD_LINE_WAR_PLUGIN_OPTION);
        String webURI = commandLine.getOptionValue(CMD_LINE_WEB_URI_OPTION);
        String destDirname = commandLine.getOptionValue(CMD_LINE_DESTDIR_OPTION);
        if (destDirname == null) {
            destDirname = new File(getStageDir(), warPluginId).getPath();
        }
        PluginDescriptor warPluginDescriptor = getManager().getRegistry().getPluginDescriptor(warPluginId);
        File myPluginTmpDir = getPluginTmpDir();
        File myWARPluginDir = new File(myPluginTmpDir, warPluginDescriptor.getId());
        File sourceWARFile = getFilePath(warPluginDescriptor, webURI);
        if (sourceWARFile.exists()) {
            logger.debug("Unjar " + sourceWARFile.getPath() + " to " + myWARPluginDir.getPath());
            FileUtils.deleteDirectory(myWARPluginDir);
            myWARPluginDir.mkdirs();
            TolvenJar.unjar(sourceWARFile, myWARPluginDir);
        } else {
            logger.debug(sourceWARFile + " does not exist");
        }
        executeRequiredPlugins(warPluginId);
        assembleWebDirectory(warPluginDescriptor);
        assembleScriptIncludes(warPluginDescriptor);
        assembleTagLibs(warPluginDescriptor);
        assembleMetaInf(warPluginDescriptor);
        assembleWebInfLibraries(warPluginDescriptor);
        assembleClasses(warPluginDescriptor);
        assembleProperties(warPluginDescriptor);
        assembleRemoteContributions(warPluginDescriptor);
        //addRemoteJars(warPluginDescriptor);
        File destinationWARFile = new File(destDirname, webURI);
        destinationWARFile.getParentFile().mkdirs();
        logger.debug("Jar " + myWARPluginDir.getPath() + " to " + destinationWARFile.getPath());
        TolvenJar.jar(myWARPluginDir, destinationWARFile);
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
        Option warPluginOption = new Option(CMD_LINE_WAR_PLUGIN_OPTION, CMD_LINE_WAR_PLUGIN_OPTION, true, "war plugin");
        warPluginOption.setRequired(true);
        cmdLineOptions.addOption(warPluginOption);
        Option webURIPluginOption = new Option(CMD_LINE_WEB_URI_OPTION, CMD_LINE_WEB_URI_OPTION, true, "web-uri");
        webURIPluginOption.setRequired(true);
        cmdLineOptions.addOption(webURIPluginOption);
        Option destDirPluginOption = new Option(CMD_LINE_DESTDIR_OPTION, CMD_LINE_DESTDIR_OPTION, true, "destination directory");
        cmdLineOptions.addOption(destDirPluginOption);
        return cmdLineOptions;
    }

    protected void executeRequiredPlugins(String warPluginId) throws Exception {
        File warPluginTmpDir = new File(getPluginTmpDir(), warPluginId);
        File destDir = new File(warPluginTmpDir, "WEB-INF");
        destDir.mkdirs();
        String argString = "-warPlugin " + warPluginId + " -destDir " + destDir;
        execute("org.tolven.assembler.webxml", argString.split(" "));
        execute("org.tolven.assembler.facesconfig", argString.split(" "));
    }

    protected void assembleMetaInf(PluginDescriptor pluginDescriptor) throws IOException {
        File myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        for (ExtensionPoint extensionPoint : pluginDescriptor.getExtensionPoints()) {
            if (EXTENSIONPOINT_METAINF.equals(extensionPoint.getParentExtensionPointId()) && extensionPoint.getParentPluginId().equals(getDescriptor().getId())) {
                myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
                for (Extension metaInfExtension : extensionPoint.getConnectedExtensions()) {
                    String dirname = metaInfExtension.getParameter("dir").valueAsString();
                    File dir = getFilePath(metaInfExtension.getDeclaringPluginDescriptor(), dirname);
                    File destDir = new File(myWARPluginDir + "/META-INF");
                    logger.debug("Copy " + dir.getPath() + " to " + destDir.getPath());
                    FileUtils.copyDirectory(dir, destDir);
                }
            }
        }
    }

    protected void assembleWebInfLibraries(PluginDescriptor pluginDescriptor) throws IOException {
        File myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        for (ExtensionPoint extensionPoint : pluginDescriptor.getExtensionPoints()) {
            if (EXTENSIONPOINT_WEBINFLIB.equals(extensionPoint.getParentExtensionPointId()) && extensionPoint.getParentPluginId().equals(getDescriptor().getId())) {
                myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
                for (Extension webInfExtension : extensionPoint.getConnectedExtensions()) {
                    String jarname = webInfExtension.getParameter("jar").valueAsString();
                    File jar = getFilePath(webInfExtension.getDeclaringPluginDescriptor(), jarname);
                    File destDir = new File(myWARPluginDir + "/WEB-INF/lib");
                    logger.debug("Copy " + jar.getPath() + " to " + destDir.getPath());
                    FileUtils.copyFileToDirectory(jar, destDir);
                }
            }
        }
    }

    protected void assembleClasses(PluginDescriptor pluginDescriptor) throws IOException {
        File myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        //The following loop is where class contributions are not directly attached to pluginDescriptor.getId()
        for (Extension extension : getExtensions(pluginDescriptor, EXTENSIONPOINT_CLASSES)) {
            assembleWARClasses(extension, myWARPluginDir);
        }
        for (ExtensionPoint classesExtensionPoint : pluginDescriptor.getExtensionPoints()) {
            if (EXTENSIONPOINT_CLASSES.equals(classesExtensionPoint.getParentExtensionPointId()) && classesExtensionPoint.getParentPluginId().equals(getDescriptor().getId())) {
                myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
                for (Extension extension : classesExtensionPoint.getConnectedExtensions()) {
                    assembleWARClasses(extension, myWARPluginDir);
                }
            }
        }
    }

    protected void assembleProperties(PluginDescriptor pluginDescriptor) {
        File myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        Map<String, Map<String, Set<String>>> propertyClassMapping = new HashMap<String, Map<String, Set<String>>>();
        Map<String, List<String>> propertySequenceMapping = propertySequenceMapping(pluginDescriptor);
        Set<Extension> classesExtensions = new HashSet<Extension>();
        classesExtensions.addAll(getChildExtensions(EXTENSIONPOINT_WAR_PROPERTY, pluginDescriptor));
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint propertyExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint(EXTENSIONPOINT_WAR_PROPERTY);
        for (Extension extension : propertyExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (pluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                classesExtensions.add(extension);
            }
        }
        for (Extension classesExtension : classesExtensions) {
            String classname = classesExtension.getParameter("class").valueAsString();
            File classFile = new File(myWARPluginDir, "WEB-INF/classes/" + classname.replace(".", "/") + ".class");
            if (!classFile.exists()) {
                throw new RuntimeException("Plugin " + classesExtension.getUniqueId() + " requires properties for non-existent class: " + classname + " (looked in: " + classFile.getPath());
            }
            Map<String, Set<String>> propertySetMap = propertyClassMapping.get(classname);
            if (propertySetMap == null) {
                propertySetMap = new HashMap<String, Set<String>>();
                String propertiesFilename = classname.replace(".", "/") + ".properties";
                File propertiesFile = new File(propertiesFilename);
                File previousPropertiesFile = new File(myWARPluginDir, propertiesFile.getPath());
                if (previousPropertiesFile.exists()) {
                    Properties props = loadProperties(previousPropertiesFile);
                    for (Object key : props.keySet()) {
                        Set<String> valueSet = new HashSet<String>();
                        String[] values = ((String) props.get(key)).split(",");
                        for (String v : values) {
                            valueSet.add(v);
                        }
                        propertySetMap.put((String) key, valueSet);
                    }
                }
                propertyClassMapping.put(classname, propertySetMap);
            }
            String name = classesExtension.getParameter("name").valueAsString();
            Set<String> valueSet = propertySetMap.get(name);
            if (valueSet == null) {
                valueSet = new HashSet<String>();
                propertySetMap.put(name, valueSet);
            }
            valueSet.add(classesExtension.getParameter("value").valueAsString());
        }
        for (String classname : propertyClassMapping.keySet()) {
            Properties props = new Properties();
            Map<String, Set<String>> propertySetMap = propertyClassMapping.get(classname);
            for (String name : propertySetMap.keySet()) {
                final List<String> sequence = propertySequenceMapping.get(classname + "." + name);
                List<String> orderedValues = new ArrayList<String>();
                List<String> unorderedValues = new ArrayList<String>();
                for (String value : propertySetMap.get(name)) {
                    if (sequence != null && sequence.contains(value)) {
                        orderedValues.add(value);
                    } else {
                        unorderedValues.add(value);
                    }
                }
                if (sequence != null) {
                    Comparator<String> comparator = new Comparator<String>() {
                        public int compare(String string1, String string2) {
                            return new Integer(sequence.indexOf(string1)).compareTo(new Integer(sequence.indexOf(string2)));
                        };
                    };
                    Collections.sort(orderedValues, comparator);
                }
                List<String> allValues = new ArrayList<String>();
                allValues.addAll(orderedValues);
                allValues.addAll(unorderedValues);
                Iterator<String> it = allValues.iterator();
                StringBuffer buff = new StringBuffer();
                while (it.hasNext()) {
                    buff.append(it.next());
                    if (it.hasNext()) {
                        buff.append(",");
                    }
                }
                String value = buff.toString();
                props.put(name, value);
            }
            String propertiesFilename = classname.replace(".", "/") + ".properties";
            File propertiesFile = new File(myWARPluginDir, "WEB-INF/classes/" + propertiesFilename);
            logger.debug("Write " + propertiesFile.getPath());
            propertiesFile.getParentFile().mkdirs();
            storeProperties(props, propertiesFile);
        }
    }

    private Map<String, List<String>> propertySequenceMapping(PluginDescriptor pluginDescriptor) {
        Map<String, List<String>> propertySequenceMapping = new HashMap<String, List<String>>();
        Set<Extension> classesExtensions = new HashSet<Extension>();
        classesExtensions.addAll(getChildExtensions(EXTENSIONPOINT_WAR_PROPERTY_SEQUENCE, pluginDescriptor));
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint propertyExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint(EXTENSIONPOINT_WAR_PROPERTY_SEQUENCE);
        classesExtensions.addAll(propertyExtensionPoint.getConnectedExtensions());
        for (Extension classesExtension : classesExtensions) {
            Parameter targetPluginId = classesExtension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(classesExtension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (pluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                String classname = classesExtension.getParameter("class").valueAsString();
                String name = classesExtension.getParameter("name").valueAsString();
                String key = classname + "." + name;
                if (propertySequenceMapping.get(key) == null) {
                    String sequence = null;
                    if (classesExtension.getParameter("sequence") != null) {
                        sequence = classesExtension.getParameter("sequence").valueAsString();
                    }
                    String evaluatedSequence = null;
                    if (sequence != null) {
                        evaluatedSequence = evaluate(sequence, classesExtension.getDeclaringPluginDescriptor());
                    }
                    if (evaluatedSequence == null && classesExtension.getParameter("defaultSequence") != null) {
                        String defaultSequence = classesExtension.getParameter("defaultSequence").valueAsString();
                        evaluatedSequence = evaluate(defaultSequence, classesExtension.getDeclaringPluginDescriptor());
                    }
                    if (evaluatedSequence != null) {
                        List<String> list = new ArrayList<String>();
                        for (String string : evaluatedSequence.split(",")) {
                            list.add(string);
                        }
                        propertySequenceMapping.put(key, list);
                    }
                } else {
                    throw new RuntimeException(classesExtension.getUniqueId() + " is redefining a propertySequence for: " + classname + " when one exists for: " + key);
                }
            }
        }
        return propertySequenceMapping;
    }

    private void assembleWARClasses(Extension extension, File myWARDir) throws IOException {
        String dirname = extension.getParameter("dir").valueAsString();
        File dir = getFilePath(extension.getDeclaringPluginDescriptor(), dirname);
        File destDir = new File(myWARDir + "/WEB-INF/classes");
        logger.debug("Copy " + dir.getPath() + " to " + destDir.getPath());
        FileUtils.copyDirectory(dir, destDir);
    }

    protected void assembleWebDirectory(PluginDescriptor warPluginDescriptor) throws IOException {
        File myWARPluginDir = new File(getPluginTmpDir(), warPluginDescriptor.getId());
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint webDirectoryExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("webDirectory");
        if (webDirectoryExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a webDirectory extension point");
        }
        /*
         * Extensions directly connected to the webDirectory extension-point
         * These contribute a new webDirectory to the war file
         */
        for (Extension webDirectoryExtension : webDirectoryExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginIdParameter = webDirectoryExtension.getParameter("target-plugin-id");
            if (targetPluginIdParameter == null || targetPluginIdParameter.valueAsString() == null || targetPluginIdParameter.valueAsString().trim().length() == 0) {
                throw new RuntimeException(webDirectoryExtension.getUniqueId() + " does not have a default parameter target-plugin-id value");
            }
            if (targetPluginIdParameter.valueAsString().equals(warPluginDescriptor.getId())) {
                Parameter functionParameter = webDirectoryExtension.getParameter("function");
                if (functionParameter == null || functionParameter.valueAsString() == null || functionParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(webDirectoryExtension.getUniqueId() + " does not have a parameter function value");
                }
                File destinationDir = new File(myWARPluginDir, functionParameter.valueAsString());
                assembleWebDirectory(webDirectoryExtension, destinationDir);
            }
        }
        /*
         * Extensions directly connected to the descendent of the webDirectory extension-point
         * These are webDirectories offered directly by the war file
         */
        for (ExtensionPoint webDirectoryDescendentExtensionPoint : webDirectoryExtensionPoint.getDescendants()) {
            ParameterDefinition targetPluginIdParameterDef = webDirectoryDescendentExtensionPoint.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParameterDef == null || targetPluginIdParameterDef.getDefaultValue() == null || targetPluginIdParameterDef.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(webDirectoryDescendentExtensionPoint.getUniqueId() + " does not have a default parameter-def target-plugin-id value");
            }
            if (targetPluginIdParameterDef.getDefaultValue().equals(warPluginDescriptor.getId())) {
                ParameterDefinition functionParameterDef = webDirectoryDescendentExtensionPoint.getParameterDefinition("function");
                if (functionParameterDef == null || functionParameterDef.getDefaultValue() == null || functionParameterDef.getDefaultValue().trim().length() == 0) {
                    throw new RuntimeException(webDirectoryDescendentExtensionPoint.getUniqueId() + " does not have a default parameter-def function value");
                }
                String defaultWebDirname = functionParameterDef.getDefaultValue();
                File destinationDir = new File(myWARPluginDir, defaultWebDirname);
                for (Extension webDirectoryExtension : webDirectoryDescendentExtensionPoint.getConnectedExtensions()) {
                    assembleWebDirectory(webDirectoryExtension, destinationDir);
                }
            }
        }
    }

    protected void assembleWebDirectory(Extension webDirectoryExtension, File destinationDir) throws IOException {
        Parameter sourceDirnameParameter = webDirectoryExtension.getParameter("sourceDirectory");
        if (sourceDirnameParameter == null || sourceDirnameParameter.valueAsString().trim().length() == 0) {
            throw new RuntimeException(webDirectoryExtension.getUniqueId() + " does not have a sourceDirectory parameter value");
        }
        String sourceDirname = sourceDirnameParameter.valueAsString();
        File sourceDir = getFilePath(webDirectoryExtension.getDeclaringPluginDescriptor(), sourceDirname);
        logger.debug("Copy " + sourceDir.getPath() + " to " + destinationDir.getPath());
        FileUtils.copyDirectory(sourceDir, destinationDir);
    }

    /**
     * An inclusion tags for java scripts
     * 
     * @param warPluginDescriptor
     * @throws IOException
     */
    protected void assembleScriptIncludes(PluginDescriptor warPluginDescriptor) throws IOException {
        File myWARPluginDir = new File(getPluginTmpDir(), warPluginDescriptor.getId());
        Map<File, Map<String, Object>> fileIncludesMap = new HashMap<File, Map<String, Object>>();
        for (ExtensionPoint fileIncludeExtensionPoint : warPluginDescriptor.getExtensionPoints()) {
            if (EXTENSIONPOINT_FILE_INCLUDE.equals(fileIncludeExtensionPoint.getParentExtensionPointId()) && fileIncludeExtensionPoint.getParentPluginId().equals(getDescriptor().getId())) {
                String matchExp = fileIncludeExtensionPoint.getParameterDefinition("matchExp").getDefaultValue();
                if (matchExp == null) {
                    throw new RuntimeException(fileIncludeExtensionPoint.getUniqueId() + " must supply a default value for matchExp");
                }
                String targetFilename = fileIncludeExtensionPoint.getParameterDefinition("targetFile").getDefaultValue();
                if (targetFilename == null) {
                    throw new RuntimeException(fileIncludeExtensionPoint.getUniqueId() + " must supply a default value for targetFile");
                }
                File targetFile = new File(myWARPluginDir, targetFilename);
                if (!targetFile.exists()) {
                    throw new RuntimeException(targetFilename + " is not available for the war file");
                }
                for (Extension extension : fileIncludeExtensionPoint.getConnectedExtensions()) {
                    String includeFilename = extension.getParameter("includeFile").valueAsString();
                    if (includeFilename == null) {
                        throw new RuntimeException(extension.getUniqueId() + " must supply a value for includeFile");
                    }
                    File includeFile = getFilePath(extension.getDeclaringPluginDescriptor(), includeFilename);
                    if (!includeFile.exists()) {
                        throw new RuntimeException("Could not locate: " + includeFile.getPath());
                    }
                    Map<String, Object> info = fileIncludesMap.get(targetFile);
                    if (info == null) {
                        info = new HashMap<String, Object>();
                        info.put("matchExp", matchExp);
                        List<File> includeFiles = new ArrayList<File>();
                        info.put("includeFiles", includeFiles);
                        fileIncludesMap.put(targetFile, info);
                    }
                    List<File> includeFiles = (List<File>) info.get("includeFiles");
                    includeFiles.add(includeFile);
                }
            }
        }
        for (File targetFile : fileIncludesMap.keySet()) {
            StringBuffer buff = new StringBuffer();
            Map<String, Object> info = fileIncludesMap.get(targetFile);
            List<File> includeFiles = (List<File>) info.get("includeFiles");
            Collections.sort(includeFiles);
            for (File includeFile : includeFiles) {
                buff.append(FileUtils.readFileToString(includeFile) + "\n");
            }
            if (buff.toString().length() > 0) {
                TolvenReplaceRegExp.replaceregexp(targetFile, (String) info.get("matchExp"), buff.toString());
            }
        }
    }

    protected void assembleTagLibs(PluginDescriptor pluginDescriptor) throws IOException, XMLStreamException {
        File myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        PluginDescriptor tagLibPluginDescriptor = getTagLibPluginDescriptor();
        ExtensionPoint tagLibExtensionPoint = tagLibPluginDescriptor.getExtensionPoint("taglib");
        if (tagLibExtensionPoint == null) {
            throw new RuntimeException(tagLibPluginDescriptor.getUniqueId() + " must have a taglib extension point");
        }
        for (Extension taglibExtension : tagLibExtensionPoint.getConnectedExtensions()) {
            if (pluginDescriptor.getId().equals(taglibExtension.getParameter("target-plugin-id").valueAsString())) {
                addTagInfo(taglibExtension, myWARPluginDir);
            }
        }
    }

    protected void addTagInfo(Extension taglibExtension, File myWARPluginDir) throws IOException, XMLStreamException {
        File metaInfTagDir = new File(myWARPluginDir.getPath() + "/META-INF/tags");
        metaInfTagDir.mkdirs();
        StringWriter tagLibWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = null;
        try {
            xmlStreamWriter = factory.createXMLStreamWriter(tagLibWriter);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeDTD("<!DOCTYPE facelet-taglib PUBLIC \"-//Sun Microsystems, Inc.//DTD Facelet Taglib 1.0//EN\" \"http://java.sun.com/dtd/facelet-taglib_1_0.dtd\">");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("facelet-taglib");
            xmlStreamWriter.writeCharacters("\n");
            String namespace = taglibExtension.getParameter("namespace").valueAsString();
            xmlStreamWriter.writeStartElement("namespace");
            xmlStreamWriter.writeCharacters(namespace);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            PluginDescriptor taglibPluginDescriptor = taglibExtension.getDeclaringPluginDescriptor();
            addTagSource(taglibPluginDescriptor, metaInfTagDir, xmlStreamWriter);
            addTagValidator(taglibPluginDescriptor, xmlStreamWriter);
            addTagConverter(taglibPluginDescriptor, xmlStreamWriter);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndDocument();
        } finally {
            if (xmlStreamWriter != null) {
                xmlStreamWriter.close();
            }
        }
        String tagFilename = taglibExtension.getParameter("tag-filename").valueAsString();
        File metaInfTagFile = new File(metaInfTagDir, tagFilename);
        logger.debug("Write tagLib string to " + metaInfTagFile.getPath());
        FileUtils.writeStringToFile(metaInfTagFile, tagLibWriter.toString());
    }

    protected void addTagSource(PluginDescriptor pluginDescriptor, File metaInfTagDir, XMLStreamWriter xmlStreamWriter) throws IOException, XMLStreamException {
        ExtensionPoint tagSourceDirectoryExtensionPoint = pluginDescriptor.getExtensionPoint("tagSourceDirectory");
        if (tagSourceDirectoryExtensionPoint != null) {
            for (Extension tagSourceDirectoryExtension : tagSourceDirectoryExtensionPoint.getConnectedExtensions()) {
                String dirname = tagSourceDirectoryExtension.getParameter("source-directory").valueAsString();
                File dir = getFilePath(tagSourceDirectoryExtension.getDeclaringPluginDescriptor(), dirname);
                logger.debug("Copy " + dir.getPath() + " to " + metaInfTagDir.getPath());
                FileUtils.copyDirectoryToDirectory(dir, metaInfTagDir);
                addTagSource(dir, xmlStreamWriter);
            }
        }
    }

    protected void addTagSource(File dir, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        for (File file : dir.listFiles()) {
            String source = dir.getName() + "/" + file.getName();
            xmlStreamWriter.writeStartElement("tag");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("tag-name");
            int index = file.getName().lastIndexOf(".");
            if (index == -1) {
                xmlStreamWriter.writeCharacters(file.getName());
            } else {
                xmlStreamWriter.writeCharacters(file.getName().substring(0, index));
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("source");
            xmlStreamWriter.writeCharacters(source);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
    }

    protected void addTagValidator(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint validatorExtensionPoint = pluginDescriptor.getExtensionPoint("tagValidator");
        if (validatorExtensionPoint != null) {
            for (Extension extension : validatorExtensionPoint.getConnectedExtensions()) {
                xmlStreamWriter.writeStartElement("tag");
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeStartElement("tag-name");
                xmlStreamWriter.writeCharacters(extension.getParameter("tag-name").valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeStartElement("validator");
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeStartElement("validator-id");
                xmlStreamWriter.writeCharacters(extension.getParameter("validator-id").valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                Parameter handlerClass = extension.getParameter("handler-class");
                if (handlerClass != null) {
                    xmlStreamWriter.writeStartElement("handler-class");
                    xmlStreamWriter.writeCharacters(handlerClass.valueAsString());
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
        }
    }

    protected void addTagConverter(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint converterExtensionPoint = pluginDescriptor.getExtensionPoint("tagConverter");
        if (converterExtensionPoint != null) {
            for (Extension extension : converterExtensionPoint.getConnectedExtensions()) {
                xmlStreamWriter.writeStartElement("tag");
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeStartElement("tag-name");
                xmlStreamWriter.writeCharacters(extension.getParameter("tag-name").valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeStartElement("converter");
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeStartElement("converter-id");
                xmlStreamWriter.writeCharacters(extension.getParameter("converter-id").valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                Parameter handlerClass = extension.getParameter("handler-class");
                if (handlerClass != null) {
                    xmlStreamWriter.writeStartElement("handler-class");
                    xmlStreamWriter.writeCharacters(handlerClass.valueAsString());
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
        }
    }

    protected void addRemoteJars(PluginDescriptor warPluginDescriptor) {
        File myWARPluginDir = new File(getPluginTmpDir(), warPluginDescriptor.getId());
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint remoteProductDirExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint(EXTENSIONPOINT_REMOTE_PRODUCT_DIR);
        if (remoteProductDirExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a " + EXTENSIONPOINT_REMOTE_PRODUCT_DIR + " extension point");
        }
        for (ExtensionPoint remoteProductDirDescendentExtensionPoint : remoteProductDirExtensionPoint.getDescendants()) {
            ParameterDefinition targetPluginIdParameterDef = remoteProductDirDescendentExtensionPoint.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParameterDef == null || targetPluginIdParameterDef.getDefaultValue() == null || targetPluginIdParameterDef.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(remoteProductDirDescendentExtensionPoint.getUniqueId() + " does not have a default parameter-def target-plugin-id value");
            }
            if (targetPluginIdParameterDef.getDefaultValue().equals(warPluginDescriptor.getId())) {
                ParameterDefinition destinationParameterDef = remoteProductDirDescendentExtensionPoint.getParameterDefinition("destination");
                if (destinationParameterDef == null || destinationParameterDef.getDefaultValue() == null || destinationParameterDef.getDefaultValue().trim().length() == 0) {
                    throw new RuntimeException(remoteProductDirDescendentExtensionPoint.getUniqueId() + " does not have a default parameter-def destination value");
                }
                String destination = destinationParameterDef.getDefaultValue();
                for (Extension remoteProductDirExtension : remoteProductDirDescendentExtensionPoint.getConnectedExtensions()) {
                    String sourcePluginId = remoteProductDirExtension.getParameter("source-plugin-id").valueAsString();
                    if (sourcePluginId.trim().length() == 0) {
                        throw new RuntimeException(remoteProductDirExtension.getUniqueId() + " must have a source-plugin-id parameter value");
                    }
                    PluginDescriptor sourcePluginDescriptor = getManager().getRegistry().getPluginDescriptor(sourcePluginId);
                    String sourceExtensionPointId = remoteProductDirExtension.getParameter("source-extension-point-id").valueAsString();
                    if (sourceExtensionPointId.trim().length() == 0) {
                        throw new RuntimeException(remoteProductDirExtension.getUniqueId() + " must have a source-extension-point-id parameter value");
                    }
                    ExtensionPoint sourceExtensionPoint = sourcePluginDescriptor.getExtensionPoint(sourceExtensionPointId);
                    String sourceDirParameter = remoteProductDirExtension.getParameter("source-dir-parameter").valueAsString();
                    if (sourceDirParameter.trim().length() == 0) {
                        throw new RuntimeException(sourceExtensionPoint.getUniqueId() + " must have a source-dir-parameter parameter value");
                    }
                    String dir = sourceExtensionPoint.getParameterDefinition(sourceDirParameter).getDefaultValue();
                    if (dir.trim().length() == 0) {
                        throw new RuntimeException(sourceExtensionPoint.getUniqueId() + " must have a " + sourceDirParameter + " parameter value");
                    }
                    String evaluatedDir = evaluate(dir, sourcePluginDescriptor);
                    File productDir = new File(evaluatedDir);
                    File destinationDir = new File(myWARPluginDir, destination);
                    destinationDir.mkdirs();
                    try {
                        FileUtils.copyDirectory(productDir, destinationDir);
                    } catch (IOException ex) {
                        throw new RuntimeException("Could not copy the stage file " + productDir + " to " + myWARPluginDir, ex);
                    }
                }
            }
        }
    }

    protected void assembleRemoteContributions(PluginDescriptor pluginDescriptor) {
        ExtensionPoint remoteProductDirExtensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_REMOTE_PRODUCT_DIR);
        if (remoteProductDirExtensionPoint != null && getAbstractWARPluginDescriptor().getId().equals(remoteProductDirExtensionPoint.getParentPluginId())) {
            String dirname = remoteProductDirExtensionPoint.getParameterDefinition("dir").getDefaultValue();
            String evaluatedDirname = evaluate(dirname, pluginDescriptor);
            if (evaluatedDirname == null) {
                throw new RuntimeException(remoteProductDirExtensionPoint.getUniqueId() + " default dir parameter evaluated to null");
            }
            File myWARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
            File dir = new File(myWARPluginDir, evaluatedDirname);
            dir.mkdirs();
            assembleRemoteFiles(pluginDescriptor, dir);
            assembleRemoteZips(pluginDescriptor, dir);
        }
    }

    protected void assembleRemoteIncludes(PluginDescriptor pluginDescriptor, File remoteProductDir) {
        ExtensionPoint remoteIncludesExtensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_REMOTE_INCLUDES);
        if (remoteIncludesExtensionPoint != null && getDescriptor().getId().equals(remoteIncludesExtensionPoint.getParentPluginId())) {
            if (remoteIncludesExtensionPoint.getParameterDefinition("jar") == null) {
                throw new RuntimeException(remoteIncludesExtensionPoint.getUniqueId() + " must have a jar parameter");
            }
            String jarname = remoteIncludesExtensionPoint.getParameterDefinition("jar").getDefaultValue();
            String evaluatedJarname = evaluate(jarname);
            File remoteFile = new File(remoteProductDir, evaluatedJarname);
            if (remoteFile.exists()) {
                remoteFile.delete();
            }
            remoteFile.getParentFile().mkdirs();
            for (Extension extension : remoteIncludesExtensionPoint.getConnectedExtensions()) {
                String dirname = extension.getParameter("sourceDir").valueAsString();
                if (dirname == null) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a dir parameter value");
                }
                File sourceDir = getFilePath(extension.getDeclaringPluginDescriptor(), dirname);
                String includes = null;
                if (extension.getParameter("includes") != null) {
                    includes = extension.getParameter("includes").valueAsString();
                }
                List<String> include = new ArrayList<String>();
                for (Parameter includeParameter : extension.getParameters("include")) {
                    String inc = includeParameter.valueAsString();
                    if (inc == null) {
                        throw new RuntimeException(extension.getUniqueId() + " requires an include parameter value");
                    }
                    include.add(inc);
                }
                String excludes = null;
                if (extension.getParameter("excludes") != null) {
                    excludes = extension.getParameter("excludes").valueAsString();
                }
                List<String> exclude = new ArrayList<String>();
                for (Parameter excludeParameter : extension.getParameters("exclude")) {
                    String exc = excludeParameter.valueAsString();
                    if (exc == null) {
                        throw new RuntimeException(extension.getUniqueId() + " requires an exclude parameter value");
                    }
                    exclude.add(exc);
                }
                TolvenJar.jarDir(sourceDir, remoteFile, includes, include, excludes, exclude, true);
            }
            if (remoteFile.exists() && remoteFile.length() > 0) {
                colocateChecksum(remoteFile);
            } else if (remoteFile.exists() && remoteFile.length() == 0) {
                remoteFile.delete();
            }
        }
    }

    private void colocateChecksum(File file) {
        String md5sum;
        try {
            md5sum = TolvenMessageDigest.checksum(file.toURI().toURL(), MESSAGE_DIGEST_ALGORITHM);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not create URL for file: " + file.getPath(), ex);
        }
        File checksumFile = new File(file.getParentFile(), file.getName() + ".md5");
        try {
            FileUtils.writeStringToFile(checksumFile, md5sum);
        } catch (IOException ex) {
            throw new RuntimeException("Could not write checksum file: " + file.getPath(), ex);
        }
    }

    protected void assembleRemoteFiles(PluginDescriptor pluginDescriptor, File remoteProductDir) {
        ExtensionPoint remoteFileExtensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_REMOTE_FILE);
        if (remoteFileExtensionPoint != null && getAbstractWARPluginDescriptor().getId().equals(remoteFileExtensionPoint.getParentPluginId())) {
            for (Extension extension : remoteFileExtensionPoint.getConnectedExtensions()) {
                for (Parameter fileParameter : extension.getParameters("file")) {
                    String filePath = fileParameter.valueAsString();
                    if (filePath == null) {
                        throw new RuntimeException(extension.getUniqueId() + " requires a file parameter value");
                    }
                    File jar = getFilePath(extension.getDeclaringPluginDescriptor(), filePath);
                    try {
                        FileUtils.copyFileToDirectory(jar, remoteProductDir);
                        colocateChecksum(new File(remoteProductDir, jar.getName()));
                    } catch (IOException ex) {
                        throw new RuntimeException("Could not copy the remote file " + jar.getPath() + " to: " + remoteProductDir.getPath(), ex);
                    }
                }
            }
        }
    }

    protected void assembleRemoteZips(PluginDescriptor pluginDescriptor, File remoteProductDir) {
        ExtensionPoint remoteFileExtensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_REMOTE_FILE);
        if (remoteFileExtensionPoint != null && getAbstractWARPluginDescriptor().getId().equals(remoteFileExtensionPoint.getParentPluginId())) {
            for (Extension extension : remoteFileExtensionPoint.getConnectedExtensions()) {
                for (Parameter zipParameter : extension.getParameters("zip")) {
                    String sourceDirname = zipParameter.getSubParameter("sourceDir").valueAsString();
                    File sourceDir = getFilePath(extension.getDeclaringPluginDescriptor(), sourceDirname);
                    if (sourceDir == null) {
                        throw new RuntimeException(extension.getUniqueId() + " requires a sourceDir parameter value");
                    }
                    String includes = null;
                    if (zipParameter.getSubParameter("includes") != null) {
                        includes = zipParameter.getSubParameter("includes").valueAsString();
                    }
                    List<String> include = new ArrayList<String>();
                    for (Parameter includeParameter : zipParameter.getSubParameters("include")) {
                        String inc = includeParameter.valueAsString();
                        if (inc == null) {
                            throw new RuntimeException(extension.getUniqueId() + " requires an include parameter value");
                        }
                        include.add(inc);
                    }
                    String excludes = null;
                    if (zipParameter.getSubParameter("excludes") != null) {
                        excludes = zipParameter.getSubParameter("excludes").valueAsString();
                    }
                    List<String> exclude = new ArrayList<String>();
                    for (Parameter excludeParameter : zipParameter.getSubParameters("exclude")) {
                        String exc = excludeParameter.valueAsString();
                        if (exc == null) {
                            throw new RuntimeException(extension.getUniqueId() + " requires an exclude parameter value");
                        }
                        exclude.add(exc);
                    }
                    String destFilename = zipParameter.getSubParameter("destFile").valueAsString();
                    if (destFilename == null) {
                        throw new RuntimeException(extension.getUniqueId() + " requires a file parameter value");
                    }
                    File destFile = new File(remoteProductDir, destFilename);
                    TolvenZip.zip(sourceDir, destFile, includes, include, excludes, exclude);
                    colocateChecksum(destFile);
                }
            }
        }
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
