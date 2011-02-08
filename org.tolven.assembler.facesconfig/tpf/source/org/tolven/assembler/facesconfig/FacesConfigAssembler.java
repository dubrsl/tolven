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
package org.tolven.assembler.facesconfig;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin assmebles a faces-config.xml for a war file
 * 
 * @author Joseph Isaac
 *
 */
public class FacesConfigAssembler extends TolvenCommandPlugin {

    private static final String ATTRIBUTE_TEMPLATE_FACESCONFIGXML = "template-faces-config.xml";
    private static final String FACES_EXTENSIONPOINT = "faces";
    private static final String TAGLIB_EXTENSIONPOINT = "taglib";

    public static final String CMD_LINE_WAR_PLUGIN_OPTION = "warPlugin";
    public static final String CMD_LINE_DESTDIR_OPTION = "destDir";

    private Logger logger = Logger.getLogger(FacesConfigAssembler.class);

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
        String destDirname = commandLine.getOptionValue(CMD_LINE_DESTDIR_OPTION);
        PluginDescriptor warPluginDescriptor = getManager().getRegistry().getPluginDescriptor(warPluginId);
        File facesConfigXMLFile = new File(destDirname, "faces-config.xml");
        if (!facesConfigXMLFile.exists()) {
            logger.debug(facesConfigXMLFile.getPath() + " does not exist");
            String templateFilename = getDescriptor().getAttribute(ATTRIBUTE_TEMPLATE_FACESCONFIGXML).getValue();
            File templateFile = getFilePath(templateFilename);
            if (!templateFile.exists()) {
                throw new RuntimeException("Could not locate: '" + templateFile.getPath() + "' in " + getDescriptor().getId());
            }
            facesConfigXMLFile.getParentFile().mkdirs();
            logger.debug("Copy " + templateFile.getPath() + " to " + facesConfigXMLFile.getPath());
            FileUtils.copyFile(templateFile, facesConfigXMLFile);
        }
        StringBuffer originalXML = new StringBuffer();
        originalXML.append(FileUtils.readFileToString(facesConfigXMLFile));
        String xslt = getXSLT(warPluginDescriptor);
        if (xslt == null) {
            facesConfigXMLFile.delete();
        } else {
            File facesConfigXSLT = new File(getPluginTmpDir(), "faces-configxml-xslt.xml");
            logger.debug("Write faces-config XSLT to " + facesConfigXSLT.getPath());
            FileUtils.writeStringToFile(facesConfigXSLT, xslt);
            String translatedXMLString = getTranslatedXML(originalXML.toString(), xslt);
            facesConfigXMLFile.getParentFile().mkdirs();
            logger.debug("Write translated faces-config.xml to " + facesConfigXMLFile.getPath());
            FileUtils.writeStringToFile(facesConfigXMLFile, translatedXMLString);
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
        Option warPluginOption = new Option(CMD_LINE_WAR_PLUGIN_OPTION, CMD_LINE_WAR_PLUGIN_OPTION, true, "war plugin");
        warPluginOption.setRequired(true);
        cmdLineOptions.addOption(warPluginOption);
        Option destDirPluginOption = new Option(CMD_LINE_DESTDIR_OPTION, CMD_LINE_DESTDIR_OPTION, true, "destination directory");
        destDirPluginOption.setRequired(true);
        cmdLineOptions.addOption(destDirPluginOption);
        return cmdLineOptions;
    }

    protected PluginDescriptor getFacesPluginDescriptor() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(FACES_EXTENSIONPOINT);
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

    protected String getXSLT(PluginDescriptor pluginDescriptor) throws XMLStreamException {
        StringWriter xslt = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = null;
        boolean added = false;
        try {
            xmlStreamWriter = factory.createXMLStreamWriter(xslt);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("xsl:stylesheet");
            xmlStreamWriter.writeAttribute("version", "2.0");
            xmlStreamWriter.writeNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");
            xmlStreamWriter.writeNamespace("tp", "http://java.sun.com/xml/ns/javaee");
            xmlStreamWriter.writeAttribute("exclude-result-prefixes", "tp");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("xsl:output");
            xmlStreamWriter.writeAttribute("method", "xml");
            xmlStreamWriter.writeAttribute("indent", "yes");
            xmlStreamWriter.writeAttribute("encoding", "UTF-8");
            xmlStreamWriter.writeAttribute("omit-xml-declaration", "no");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            addMainTemplate(xmlStreamWriter);
            addRootTemplate(xmlStreamWriter);
            added = added | addApplicationChildren(pluginDescriptor, xmlStreamWriter);
            added = added | addGlobalValidatorTemplates(pluginDescriptor, xmlStreamWriter);
            added = added | addGlobalConverterTemplates(pluginDescriptor, xmlStreamWriter);
            added = added | addLifeCycleTemplates(pluginDescriptor, xmlStreamWriter);
            added = added | addNavigationRuleTemplates(pluginDescriptor, xmlStreamWriter);
            added = added | addManagedBeanTemplates(pluginDescriptor, xmlStreamWriter);
            xmlStreamWriter.writeEndDocument();
        } finally {
            if (xmlStreamWriter != null) {
                xmlStreamWriter.close();
            }
        }
        if (added) {
            return xslt.toString();
        } else {
            return null;
        }
    }

    protected void addMainTemplate(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("match", "/ | * | @* | text() | comment()");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy");
        xmlStreamWriter.writeAttribute("select", ".");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootTemplate(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("match", "tp:faces-config");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("faces-config");
        xmlStreamWriter.writeAttribute("version", "{@version}");
        xmlStreamWriter.writeAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
        xmlStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlStreamWriter.writeAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-facesconfig_2_0.xsd");
        xmlStreamWriter.writeCharacters("\n");
        addRootApplicationSelects(xmlStreamWriter);
        addRootValidatorSelects(xmlStreamWriter);
        addRootConverterSelects(xmlStreamWriter);
        addRootLifeCycleSelects(xmlStreamWriter);
        addRootNavigationRuleSelects(xmlStreamWriter);
        addRootManagedBeanSelects(xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootApplicationSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:application");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", ".");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "addApplicationChildren");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootValidatorSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:validator");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", ".");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "addValidators");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootConverterSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:converter");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", ".");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "addConverters");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootLifeCycleSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:lifecycle");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", ".");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "addLifeCycles");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootNavigationRuleSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:navigation-rule");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", ".");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "addNavigationRules");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootManagedBeanSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:managed-bean");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", ".");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "addManagedBeans");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected boolean addApplicationChildren(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        boolean added = false;
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addApplicationChildren");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("application");
        xmlStreamWriter.writeCharacters("\n");
        Set<String> viewHandlerStringSet = new HashSet<String>();
        for (Extension extension : getExtensions(pluginDescriptor, "view-handler")) {
            for (Parameter parameter : extension.getParameters("view-handler")) {
                viewHandlerStringSet.add(parameter.valueAsString());
                added = true;
            }
        }
        Comparator<String> comparator = new Comparator<String>() {
            public int compare(String string1, String string2) {
                return string1.compareTo(string2);
            };
        };
        List<String> viewHandlerStringList = new ArrayList<String>();
        viewHandlerStringList.addAll(viewHandlerStringSet);
        Collections.sort(viewHandlerStringList, comparator);
        for (String viewHandler : viewHandlerStringList) {
            xmlStreamWriter.writeStartElement("view-handler");
            xmlStreamWriter.writeCharacters(viewHandler);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            added = true;
        }
        Set<String> elResolverStringSet = new HashSet<String>();
        for (Extension extension : getExtensions(pluginDescriptor, "el-resolver")) {
            for (Parameter parameter : extension.getParameters("el-resolver")) {
                elResolverStringSet.add(parameter.valueAsString());
                added = true;
            }
        }
        List<String> elResolverStringList = new ArrayList<String>();
        elResolverStringList.addAll(elResolverStringSet);
        Collections.sort(elResolverStringList, comparator);
        for (String elResolver : elResolverStringList) {
            xmlStreamWriter.writeStartElement("el-resolver");
            xmlStreamWriter.writeCharacters(elResolver);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            added = true;
        }
        for (Extension extension : getExtensions(pluginDescriptor, "localeConfig")) {
            xmlStreamWriter.writeStartElement("locale-config");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("default-locale");
            xmlStreamWriter.writeCharacters(extension.getParameter("default-locale").valueAsString());
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            for (Parameter parameter : extension.getParameters("supported-locale")) {
                xmlStreamWriter.writeStartElement("supported-locale");
                xmlStreamWriter.writeCharacters(parameter.valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            added = true;
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        return added;
    }

    /**
     * Add a global validator templates
     * 
     * @param warPluginDescriptor
     * @param xmlStreamWriter
     * @throws XMLStreamException
     */
    protected boolean addGlobalValidatorTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        boolean added = false;
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addValidators");
        xmlStreamWriter.writeCharacters("\n");
        Map<String, Extension> validatorMap = new HashMap<String, Extension>();
        for (Extension nonDefaultExtension : getGlobalValidatorExtensions(warPluginDescriptor, true)) {
            String validatorId = nonDefaultExtension.getParameter("validator-id").valueAsString();
            validatorMap.put(validatorId, nonDefaultExtension);
            added = true;
        }
        for (Extension nonDefaultExtension : getGlobalValidatorExtensions(warPluginDescriptor, false)) {
            String validatorId = nonDefaultExtension.getParameter("validator-id").valueAsString();
            Extension validatorExtension = validatorMap.get(validatorId);
            if (validatorExtension != null) {
                logger.debug(nonDefaultExtension.getUniqueId() + " overrides " + validatorExtension.getUniqueId());
            }
            validatorMap.put(validatorId, nonDefaultExtension);
            added = true;
        }
        List<Extension> extensions = new ArrayList<Extension>();
        extensions.addAll(validatorMap.values());
        Comparator<Object> comparator = new Comparator<Object>() {
            public int compare(Object obj1, Object obj2) {
                Extension e1 = (Extension) obj1;
                Extension e2 = (Extension) obj2;
                return e1.getParameter("validator-id").valueAsString().compareTo(e2.getParameter("validator-id").valueAsString());
            };
        };
        Collections.sort(extensions, comparator);
        for (Extension extension : extensions) {
            Parameter handlerClass = extension.getParameter("validator-class");
            String validatorClass = null;
            if (handlerClass != null) {
                validatorClass = handlerClass.valueAsString();
            }
            addGlobalValidator(extension.getParameter("validator-id").valueAsString(), validatorClass, xmlStreamWriter);
            added = true;
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        return added;
    }

    /**
     * Return the validators whose extensions exist on the plugin given by the target-plugin-id if defaultValidators is true,
     * otherwise return validators defined on any other plugins.
     * 
     * @param warPluginDescriptor
     * @param defaultValidators
     * @return
     */
    protected Set<Extension> getGlobalValidatorExtensions(PluginDescriptor warPluginDescriptor, boolean defaultValidators) {
        Set<Extension> extensions = new HashSet<Extension>();
        PluginDescriptor facesPluginDescriptor = getFacesPluginDescriptor();
        ExtensionPoint validatorExtensionPoint = facesPluginDescriptor.getExtensionPoint("globalValidator");
        if (validatorExtensionPoint == null) {
            throw new RuntimeException(facesPluginDescriptor.getUniqueId() + " does not have a globalValidator extension point");
        }
        for (Extension extension : validatorExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor validatorPluginDescriptor = extension.getDeclaringPluginDescriptor();
            String targetPluginId = extension.getParameter("target-plugin-id").valueAsString();
            if (targetPluginId == null) {
                throw new RuntimeException("A value for target-plugin-id on " + extension.getUniqueId() + " must be supplied");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId)) {
                if (defaultValidators) {
                    if (validatorPluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                        extensions.add(extension);
                    }
                } else {
                    if (!validatorPluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                        extensions.add(extension);
                    }
                }
            }
        }
        if (defaultValidators) {
            PluginDescriptor tagLibPluginDescriptor = getTagLibPluginDescriptor();
            ExtensionPoint tagLibExtensionPoint = tagLibPluginDescriptor.getExtensionPoint("taglib");
            if (tagLibExtensionPoint == null) {
                throw new RuntimeException(tagLibPluginDescriptor.getUniqueId() + " must have a taglib extension point");
            }
            for (Extension tagLibExtension : tagLibExtensionPoint.getConnectedExtensions()) {
                Parameter targetPluginIdParameter = tagLibExtension.getParameter("target-plugin-id");
                if (targetPluginIdParameter == null || targetPluginIdParameter.valueAsString() == null) {
                    throw new RuntimeException(tagLibExtension.getUniqueId() + " must have a target-plugin-id parameter value");
                }
                if (warPluginDescriptor.getId().equals(targetPluginIdParameter.valueAsString())) {
                    PluginDescriptor tagLibExtensionPluginDescriptor = tagLibExtension.getDeclaringPluginDescriptor();
                    ExtensionPoint tagValidatorExtensionPoint = tagLibExtensionPluginDescriptor.getExtensionPoint("tagValidator");
                    if (tagValidatorExtensionPoint != null) {
                        for (Extension extension : tagValidatorExtensionPoint.getConnectedExtensions()) {
                            if (extension.getParameter("validator-class") != null) {
                                extensions.add(extension);
                            }
                        }
                    }
                }
            }
        }
        List<Extension> duplicates = getDuplicateValidators(extensions);
        if (!duplicates.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            for (Extension extension : duplicates) {
                buff.append(extension.getUniqueId() + " ");
            }
            throw new RuntimeException("Validators with duplicate Ids detected: " + buff.toString());
        }
        return extensions;
    }

    /**
     * Return any validator extensions which have the same validator-id
     * 
     * @param validatorExtensions
     * @return
     */
    protected List<Extension> getDuplicateValidators(Set<Extension> validatorExtensions) {
        List<Extension> duplicates = new ArrayList<Extension>();
        Map<String, List<Extension>> extensionMap = new HashMap<String, List<Extension>>();
        for (Extension extension : validatorExtensions) {
            Parameter validatorIdParameter = extension.getParameter("validator-id");
            if (validatorIdParameter == null) {
                throw new RuntimeException("A value for validator-id on " + extension.getUniqueId() + " must be supplied");
            }
            String validatorId = validatorIdParameter.valueAsString();
            List<Extension> extensions = extensionMap.get(validatorId);
            if (extensions == null) {
                extensions = new ArrayList<Extension>();
                extensionMap.put(validatorId, extensions);
            }
            extensions.add(extension);
        }
        for (String key : extensionMap.keySet()) {
            if (extensionMap.get(key).size() > 1) {
                duplicates.addAll(extensionMap.get(key));
            }
        }
        return duplicates;
    }

    /**
     * Add the global template if-clause for validatorId
     * 
     * @param validatorId
     * @param validatorClass
     * @param xmlStreamWriter
     * @throws XMLStreamException
     */
    protected void addGlobalValidator(String validatorId, String validatorClass, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:validator[tp:validator-id = '" + validatorId + "']) = 0");
        xmlStreamWriter.writeStartElement("validator");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("validator-id");
        xmlStreamWriter.writeCharacters(validatorId);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        if (validatorClass != null) {
            xmlStreamWriter.writeStartElement("validator-class");
            xmlStreamWriter.writeCharacters(validatorClass);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    /**
     * Add a global converter templates
     * 
     * @param warPluginDescriptor
     * @param xmlStreamWriter
     * @throws XMLStreamException
     */
    protected boolean addGlobalConverterTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        boolean added = false;
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addConverters");
        xmlStreamWriter.writeCharacters("\n");
        Map<String, Extension> converterMap = new HashMap<String, Extension>();
        for (Extension nonDefaultExtension : getGlobalConverterExtensions(warPluginDescriptor, true)) {
            String converterId = nonDefaultExtension.getParameter("converter-id").valueAsString();
            converterMap.put(converterId, nonDefaultExtension);
            added = true;
        }
        for (Extension nonDefaultExtension : getGlobalConverterExtensions(warPluginDescriptor, false)) {
            String converterId = nonDefaultExtension.getParameter("converter-id").valueAsString();
            Extension converterExtension = converterMap.get(converterId);
            if (converterExtension != null) {
                logger.debug(nonDefaultExtension.getUniqueId() + " overrides " + converterExtension.getUniqueId());
            }
            converterMap.put(converterId, nonDefaultExtension);
            added = true;
        }
        List<Extension> extensions = new ArrayList<Extension>();
        extensions.addAll(converterMap.values());
        Comparator<Object> comparator = new Comparator<Object>() {
            public int compare(Object obj1, Object obj2) {
                Extension e1 = (Extension) obj1;
                Extension e2 = (Extension) obj2;
                return e1.getParameter("converter-id").valueAsString().compareTo(e2.getParameter("converter-id").valueAsString());
            };
        };
        Collections.sort(extensions, comparator);
        for (Extension extension : extensions) {
            Parameter handlerClass = extension.getParameter("converter-class");
            String converterClass = null;
            if (handlerClass != null) {
                converterClass = handlerClass.valueAsString();
            }
            addGlobalConverter(extension.getParameter("converter-id").valueAsString(), converterClass, xmlStreamWriter);
            added = true;
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        return added;
    }

    /**
     * Return the converters whose extensions exist on the plugin given by the target-plugin-id if defaultConverters is true,
     * otherwise return converters defined on any other plugins.
     * 
     * @param warPluginDescriptor
     * @param defaultConverters
     * @return
     */
    protected Set<Extension> getGlobalConverterExtensions(PluginDescriptor warPluginDescriptor, boolean defaultConverters) {
        Set<Extension> extensions = new HashSet<Extension>();
        PluginDescriptor facesPluginDescriptor = getFacesPluginDescriptor();
        ExtensionPoint converterExtensionPoint = facesPluginDescriptor.getExtensionPoint("globalConverter");
        if (converterExtensionPoint == null) {
            throw new RuntimeException(facesPluginDescriptor.getUniqueId() + " does not have a globalConverter extension point");
        }
        for (Extension extension : converterExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor converterPluginDescriptor = extension.getDeclaringPluginDescriptor();
            String targetPluginId = extension.getParameter("target-plugin-id").valueAsString();
            if (targetPluginId == null) {
                throw new RuntimeException("A value for target-plugin-id on " + extension.getUniqueId() + " must be supplied");
            }
            if (defaultConverters) {
                if (converterPluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                    extensions.add(extension);
                }
            } else {
                if (!converterPluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                    extensions.add(extension);
                }
            }
        }
        if (defaultConverters) {
            PluginDescriptor tagLibPluginDescriptor = getTagLibPluginDescriptor();
            ExtensionPoint tagLibExtensionPoint = tagLibPluginDescriptor.getExtensionPoint("taglib");
            if (tagLibExtensionPoint == null) {
                throw new RuntimeException(tagLibPluginDescriptor.getUniqueId() + " must have a taglib extension point");
            }
            for (Extension tagLibExtension : tagLibExtensionPoint.getConnectedExtensions()) {
                Parameter targetPluginIdParameter = tagLibExtension.getParameter("target-plugin-id");
                if (targetPluginIdParameter == null || targetPluginIdParameter.valueAsString() == null) {
                    throw new RuntimeException(tagLibExtension.getUniqueId() + " must have a target-plugin-id parameter value");
                }
                if (warPluginDescriptor.getId().equals(targetPluginIdParameter.valueAsString())) {
                    PluginDescriptor tabLibExtensionPluginDescriptor = tagLibExtension.getDeclaringPluginDescriptor();
                    ExtensionPoint tagConverterExtensionPoint = tabLibExtensionPluginDescriptor.getExtensionPoint("tagConverter");
                    if (tagConverterExtensionPoint != null) {
                        for (Extension extension : tagConverterExtensionPoint.getConnectedExtensions()) {
                            if (extension.getParameter("converter-class") != null) {
                                extensions.add(extension);
                            }
                        }
                    }
                }
            }
        }
        List<Extension> duplicates = getDuplicateConverters(extensions);
        if (!duplicates.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            for (Extension extension : duplicates) {
                buff.append(extension.getUniqueId() + " ");
            }
            throw new RuntimeException("Converters with duplicate Ids detected: " + buff.toString());
        }
        return extensions;
    }

    /**
     * Return any converter extensions which have the same converter-id
     * 
     * @param converterExtensions
     * @return
     */
    protected List<Extension> getDuplicateConverters(Set<Extension> converterExtensions) {
        List<Extension> duplicates = new ArrayList<Extension>();
        Map<String, List<Extension>> extensionMap = new HashMap<String, List<Extension>>();
        for (Extension extension : converterExtensions) {
            Parameter converterIdParameter = extension.getParameter("converter-id");
            if (converterIdParameter == null) {
                throw new RuntimeException("A value for converter-id on " + extension.getUniqueId() + " must be supplied");
            }
            String converterId = converterIdParameter.valueAsString();
            List<Extension> extensions = extensionMap.get(converterId);
            if (extensions == null) {
                extensions = new ArrayList<Extension>();
                extensionMap.put(converterId, extensions);
            }
            extensions.add(extension);
        }
        for (String key : extensionMap.keySet()) {
            if (extensionMap.get(key).size() > 1) {
                duplicates.addAll(extensionMap.get(key));
            }
        }
        return duplicates;
    }

    /**
     * Add the global template if-clause for converterId
     * 
     * @param converterId
     * @param converterClass
     * @param xmlStreamWriter
     * @throws XMLStreamException
     */
    protected void addGlobalConverter(String converterId, String converterClass, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:converter[tp:converter-id = '" + converterId + "']) = 0");
        xmlStreamWriter.writeStartElement("converter");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("converter-id");
        xmlStreamWriter.writeCharacters(converterId);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        if (converterClass != null) {
            xmlStreamWriter.writeStartElement("converter-class");
            xmlStreamWriter.writeCharacters(converterClass);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected boolean addLifeCycleTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        boolean added = false;
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addLifeCycles");
        xmlStreamWriter.writeCharacters("\n");
        for (Extension extension : getExtensions(pluginDescriptor, "lifecycle")) {
            Parameter phaseListener = extension.getParameter("phase-listener");
            String phaseListenerString = null;
            if (phaseListener != null) {
                phaseListenerString = phaseListener.valueAsString();
            }
            addLifeCycle(phaseListenerString, xmlStreamWriter);
            added = true;
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        return added;
    }

    protected void addLifeCycle(String phaseListener, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:lifecycle[tp:phase-listener = '" + phaseListener + "']) = 0");
        xmlStreamWriter.writeStartElement("lifecycle");
        if (phaseListener != null) {
            xmlStreamWriter.writeStartElement("phase-listener");
            xmlStreamWriter.writeCharacters(phaseListener);
            xmlStreamWriter.writeEndElement();
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected boolean addNavigationRuleTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        boolean added = false;
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addNavigationRules");
        xmlStreamWriter.writeCharacters("\n");
        Map<String, Map<String, Object>> navigationRuleMap = new HashMap<String, Map<String, Object>>();
        for (Extension defaultExtension : getNavigationRuleExtensions(warPluginDescriptor, true)) {
            String fromViewId = defaultExtension.getParameter("from-view-id").valueAsString();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("navigationRuleExtension", defaultExtension);
            navigationRuleMap.put(fromViewId, map);
            added = true;
        }
        for (Extension nonDefaultExtension : getNavigationRuleExtensions(warPluginDescriptor, false)) {
            String fromViewId = nonDefaultExtension.getParameter("from-view-id").valueAsString();
            Extension navigationRuleExtension = null;
            Map<String, Object> map = (Map<String, Object>) navigationRuleMap.get(fromViewId);
            if (map == null) {
                map = new HashMap<String, Object>();
                navigationRuleMap.put(fromViewId, map);
            } else {
                navigationRuleExtension = (Extension) map.get("navigationRuleExtension");
                if (nonDefaultExtension.getParameter("override") == null) {
                    throw new RuntimeException(nonDefaultExtension.getUniqueId() + " requires an override parameter to override " + navigationRuleExtension.getUniqueId());
                }
                logger.debug(nonDefaultExtension.getUniqueId() + " overrides " + navigationRuleExtension.getUniqueId());
            }
            navigationRuleMap.get(fromViewId).put("navigationRuleExtension", nonDefaultExtension);
            added = true;
        }
        for (String fromViewId : navigationRuleMap.keySet()) {
            Map<String, Object> map = navigationRuleMap.get(fromViewId);
            Extension navigationRuleExtension = (Extension) map.get("navigationRuleExtension");
            Map<String, Parameter> caseParameterMap = new HashMap<String, Parameter>();
            for (Parameter parameter : navigationRuleExtension.getParameters("navigation-case")) {
                String fromAction = null;
                Parameter fromActionParameter = parameter.getSubParameter("from-action");
                if (fromActionParameter == null) {
                    fromAction = null;
                } else {
                    fromAction = fromActionParameter.valueAsString();
                }
                String fromOutcome = null;
                Parameter fromOutcomeParameter = parameter.getSubParameter("from-outcome");
                if (fromOutcomeParameter == null) {
                    fromOutcome = null;
                } else {
                    fromOutcome = fromOutcomeParameter.valueAsString();
                }
                Parameter toViewIdParameter = parameter.getSubParameter("to-view-id");
                //caseParameter map has key which is concatentation of fromAction, fromOutcomeParameter and toViewIdParameter
                caseParameterMap.put(fromAction + fromOutcome + toViewIdParameter.valueAsString(), parameter);
            }
            map.put("caseParameterMap", caseParameterMap);
            added = true;
        }
        for (Extension navigationRuleContributionExtension : getNavigationRuleContributionExtensions(warPluginDescriptor)) {
            String fromViewId = navigationRuleContributionExtension.getParameter("from-view-id").valueAsString();
            Map<String, Object> map = navigationRuleMap.get(fromViewId);
            Extension navigationRuleExtension = (Extension) map.get("navigationRuleExtension");
            Map<String, Parameter> caseParameterMap = (Map<String, Parameter>) map.get("caseParameterMap");
            if (navigationRuleExtension == null) {
                throw new RuntimeException(navigationRuleContributionExtension.getUniqueId() + " refers to a navigation-rule that does not exist with from-view-id: " + fromViewId);
            }
            Parameter contributingCaseParameter = navigationRuleContributionExtension.getParameter("navigation-case");
            Parameter contributingFromActionParameter = contributingCaseParameter.getSubParameter("from-action");
            String contributingFromAction = null;
            if (contributingFromActionParameter == null) {
                contributingFromAction = "";
            } else {
                contributingFromAction = contributingFromActionParameter.valueAsString();
            }
            Parameter contributingFromOutcomeParameter = contributingCaseParameter.getSubParameter("from-outcome");
            String contributingFromOutcome = null;
            if (contributingFromOutcomeParameter == null) {
                contributingFromOutcome = "";
            } else {
                contributingFromOutcome = contributingFromOutcomeParameter.valueAsString();
            }
            String key = contributingFromAction + fromViewId + contributingFromOutcome;
            Parameter caseParameter = caseParameterMap.get(key);
            if (caseParameter != null) {
                if (navigationRuleContributionExtension.getParameter("override") == null) {
                    throw new RuntimeException(navigationRuleContributionExtension.getUniqueId() + " requires an override parameter to override navigation-case in: " + navigationRuleExtension.getUniqueId());
                }
                logger.debug(navigationRuleContributionExtension.getUniqueId() + " overrides navigation-case in: " + navigationRuleExtension.getUniqueId());
            }
            caseParameterMap.put(key, contributingCaseParameter);
            added = true;
        }
        Comparator<String> navigationRuleComparator = new Comparator<String>() {
            public int compare(String string1, String string2) {
                return string1.compareTo(string2);
            };
        };
        List<String> fromViewIdList = new ArrayList<String>();
        fromViewIdList.addAll(navigationRuleMap.keySet());
        Collections.sort(fromViewIdList, navigationRuleComparator);
        for (String fromViewId : fromViewIdList) {
            Map<String, Object> map = navigationRuleMap.get(fromViewId);
            Extension navigationRuleExtension = (Extension) map.get("navigationRuleExtension");
            xmlStreamWriter.writeStartElement("xsl:if");
            xmlStreamWriter.writeAttribute("test", "count(tp:navigation-rule[tp:from-view-id = '" + fromViewId + "']) = 0");
            xmlStreamWriter.writeStartElement("navigation-rule");
            xmlStreamWriter.writeCharacters("\n");
            Parameter descriptionParameter = navigationRuleExtension.getParameter("description");
            if (descriptionParameter != null) {
                xmlStreamWriter.writeStartElement("description");
                xmlStreamWriter.writeCharacters(descriptionParameter.valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            xmlStreamWriter.writeStartElement("from-view-id");
            xmlStreamWriter.writeCharacters(fromViewId);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            Map<String, Parameter> caseParameterMap = (Map<String, Parameter>) map.get("caseParameterMap");
            List<Parameter> caseParameterList = new ArrayList<Parameter>();
            caseParameterList.addAll(caseParameterMap.values());
            Comparator<Parameter> caseParameterComparator = new Comparator<Parameter>() {
                public int compare(Parameter p1, Parameter p2) {
                    Parameter p1FromActionParameter = p1.getSubParameter("from-action");
                    Parameter p1FromOutcomeParameter = p1.getSubParameter("from-outcome");
                    Parameter p1RedirectParameter = p1.getSubParameter("redirect");
                    Parameter p2FromActionParameter = p2.getSubParameter("from-action");
                    Parameter p2FromOutcomeParameter = p2.getSubParameter("from-outcome");
                    Parameter p2RedirectParameter = p2.getSubParameter("redirect");
                    String s1 = (p1FromActionParameter == null ? "null" : p1FromActionParameter.valueAsString()) + (p1FromOutcomeParameter == null ? "null" : p1FromOutcomeParameter.valueAsString()) + (p1RedirectParameter == null ? "null" : p1RedirectParameter.valueAsString());
                    String s2 = (p2FromActionParameter == null ? "null" : p2FromActionParameter.valueAsString()) + (p2FromOutcomeParameter == null ? "null" : p2FromOutcomeParameter.valueAsString()) + (p2RedirectParameter == null ? "null" : p2RedirectParameter.valueAsString());
                    return s1.compareTo(s2);
                };
            };
            Collections.sort(caseParameterList, caseParameterComparator);
            for (Parameter parameter : caseParameterList) {
                xmlStreamWriter.writeStartElement("navigation-case");
                xmlStreamWriter.writeCharacters("\n");
                Parameter fromActionParameter = parameter.getSubParameter("from-action");
                if (fromActionParameter != null) {
                    xmlStreamWriter.writeStartElement("from-action");
                    xmlStreamWriter.writeCharacters(fromActionParameter.valueAsString());
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                Parameter fromOutcomeParameter = parameter.getSubParameter("from-outcome");
                if (fromOutcomeParameter != null) {
                    xmlStreamWriter.writeStartElement("from-outcome");
                    xmlStreamWriter.writeCharacters(fromOutcomeParameter.valueAsString());
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                xmlStreamWriter.writeStartElement("to-view-id");
                xmlStreamWriter.writeCharacters(parameter.getSubParameter("to-view-id").valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                Parameter redirectParameter = parameter.getSubParameter("redirect");
                if (redirectParameter != null) {
                    xmlStreamWriter.writeEmptyElement("redirect");
                    xmlStreamWriter.writeCharacters("\n");
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            added = true;
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        return added;
    }

    /**
     * Return the navigation rules whose extensions exist on the plugin given by the target-plugin-id if defaultNavigationRules is true,
     * otherwise return navigation rules defined on any other plugins.
     * 
     * @param warPluginDescriptor
     * @param defaultNavigationRules
     * @return
     */
    protected Set<Extension> getNavigationRuleExtensions(PluginDescriptor warPluginDescriptor, boolean defaultNavigationRules) {
        Set<Extension> extensions = new HashSet<Extension>();
        PluginDescriptor facesPluginDescriptor = getFacesPluginDescriptor();
        ExtensionPoint navigationRuleExtensionPoint = facesPluginDescriptor.getExtensionPoint("navigation-rule");
        if (navigationRuleExtensionPoint == null) {
            throw new RuntimeException(facesPluginDescriptor.getUniqueId() + " does not have a navigation-rule extension point");
        }
        for (Extension extension : navigationRuleExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor navigationRulePluginDescriptor = extension.getDeclaringPluginDescriptor();
            String targetPluginId = extension.getParameter("target-plugin-id").valueAsString();
            if (targetPluginId == null) {
                throw new RuntimeException("A value for target-plugin-id on " + extension.getUniqueId() + " must be supplied");
            }
            if (defaultNavigationRules) {
                if (navigationRulePluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                    extensions.add(extension);
                }
            } else {
                if (!navigationRulePluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                    extensions.add(extension);
                }
            }
        }
        List<Extension> duplicates = getDuplicateNavigationRules(extensions);
        if (!duplicates.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            for (Extension extension : duplicates) {
                buff.append(extension.getUniqueId() + " ");
            }
            throw new RuntimeException("NavigationRules with duplicate navigation-rule to-view-id detected: " + buff.toString());
        }
        return extensions;
    }

    /**
     * Return any navigation rule extensions which have the same to-view-id
     * 
     * @param navigationRuleExtensions
     * @return
     */
    protected List<Extension> getDuplicateNavigationRules(Set<Extension> navigationRuleExtensions) {
        List<Extension> duplicates = new ArrayList<Extension>();
        Map<String, List<Extension>> extensionMap = new HashMap<String, List<Extension>>();
        for (Extension extension : navigationRuleExtensions) {
            Parameter fromViewIdParameter = extension.getParameter("from-view-id");
            if (fromViewIdParameter == null) {
                throw new RuntimeException("A value for from-view-id on " + extension.getUniqueId() + " must be supplied");
            }
            String navigationRule = fromViewIdParameter.valueAsString();
            List<Extension> extensions = extensionMap.get(navigationRule);
            if (extensions == null) {
                extensions = new ArrayList<Extension>();
                extensionMap.put(navigationRule, extensions);
            }
            extensions.add(extension);
        }
        for (String key : extensionMap.keySet()) {
            if (extensionMap.get(key).size() > 1) {
                duplicates.addAll(extensionMap.get(key));
            }
        }
        return duplicates;
    }

    /**
     * Return the navigation cases whose extensions exist on the plugin given by the target-plugin-id..
     * 
     * @param warPluginDescriptor
     * @param defaultNavigationCases
     * @return
     */
    protected Set<Extension> getNavigationRuleContributionExtensions(PluginDescriptor warPluginDescriptor) {
        Set<Extension> extensions = new HashSet<Extension>();
        PluginDescriptor facesPluginDescriptor = getFacesPluginDescriptor();
        ExtensionPoint navigationRuleContributionExtensionPoint = facesPluginDescriptor.getExtensionPoint("navigation-rule-contribution");
        if (navigationRuleContributionExtensionPoint == null) {
            throw new RuntimeException(facesPluginDescriptor.getUniqueId() + " does not have a navigation-rule-contribution extension point");
        }
        for (Extension extension : navigationRuleContributionExtensionPoint.getConnectedExtensions()) {
            String targetPluginId = extension.getParameter("target-plugin-id").valueAsString();
            if (targetPluginId == null) {
                throw new RuntimeException("A value for target-plugin-id on " + extension.getUniqueId() + " must be supplied");
            }
            if (targetPluginId.equals(warPluginDescriptor.getId())) {
                extensions.add(extension);
            }
        }
        List<Extension> duplicates = getDuplicateNavigationRuleContributions(extensions);
        if (!duplicates.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            for (Extension extension : duplicates) {
                buff.append(extension.getUniqueId() + " ");
            }
            throw new RuntimeException("NavigationCases with duplicate outcome and to-view-id detected: " + buff.toString());
        }
        return extensions;
    }

    /**
     * Return any contributed navigation rule contributions extensions which have the same outcome and to-view-id within a rule
     * 
     * @param navigationRuleContributionExtensions
     * @return
     */
    protected List<Extension> getDuplicateNavigationRuleContributions(Set<Extension> navigationRuleContributionExtensions) {
        List<Extension> duplicates = new ArrayList<Extension>();
        Map<String, List<Extension>> extensionMap = new HashMap<String, List<Extension>>();
        for (Extension extension : navigationRuleContributionExtensions) {
            Parameter fromViewIdParameter = extension.getParameter("from-view-id");
            if (fromViewIdParameter == null || fromViewIdParameter.valueAsString() == null) {
                throw new RuntimeException(extension.getUniqueId() + " must be supplied with a from-view-id parameter and its value");
            }
            Parameter caseParameter = extension.getParameter("navigation-case");
            String fromOutcome = null;
            Parameter fromOutcomeParameter = caseParameter.getSubParameter("from-outcome");
            if (fromOutcomeParameter == null || fromOutcomeParameter.valueAsString() == null) {
                fromOutcome = "";
            } else {
                fromOutcome = fromOutcomeParameter.valueAsString();
            }
            Parameter toViewIdParameter = caseParameter.getSubParameter("to-view-id");
            if (toViewIdParameter == null || toViewIdParameter.valueAsString() == null) {
                throw new RuntimeException("A value for to-view-id on the navigation-case of " + extension.getUniqueId() + " must be supplied");
            }
            String key = fromViewIdParameter.valueAsString() + fromOutcome + toViewIdParameter.valueAsString();
            List<Extension> extensions = extensionMap.get(key);
            if (extensions == null) {
                extensions = new ArrayList<Extension>();
                extensionMap.put(key, extensions);
            }
            extensions.add(extension);
        }
        for (String key : extensionMap.keySet()) {
            if (extensionMap.get(key).size() > 1) {
                duplicates.addAll(extensionMap.get(key));
            }
        }
        return duplicates;
    }

    /**
     * Add managed bean templates.
     * 
     * @param warPluginDescriptor
     * @param xmlStreamWriter
     * @throws XMLStreamException
     */
    protected boolean addManagedBeanTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        boolean added = false;
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addManagedBeans");
        xmlStreamWriter.writeCharacters("\n");
        Map<String, Extension> managedBeanMap = new HashMap<String, Extension>();
        for (Extension defaultExtension : getManagedBeanExtensions(warPluginDescriptor, true)) {
            String beanName = defaultExtension.getParameter("managed-bean-name").valueAsString();
            managedBeanMap.put(beanName, defaultExtension);
            added = true;
        }
        for (Extension nonDefaultExtension : getManagedBeanExtensions(warPluginDescriptor, false)) {
            String beanName = nonDefaultExtension.getParameter("managed-bean-name").valueAsString();
            Extension managedBeanExtension = managedBeanMap.get(beanName);
            if (managedBeanExtension != null) {
                if (nonDefaultExtension.getParameter("override") == null) {
                    throw new RuntimeException(nonDefaultExtension.getUniqueId() + " requires an override parameter to override " + managedBeanExtension.getUniqueId());
                }
                logger.debug(nonDefaultExtension.getUniqueId() + " overrides " + managedBeanExtension.getUniqueId());
            }
            managedBeanMap.put(beanName, nonDefaultExtension);
            added = true;
        }
        for (Extension extension : managedBeanMap.values()) {
            Parameter descriptionParameter = extension.getParameter("description");
            String descsription = null;
            if (descriptionParameter != null) {
                descsription = descriptionParameter.valueAsString();
            }
            String managedBeanName = extension.getParameter("managed-bean-name").valueAsString();
            String managedBeanClass = extension.getParameter("managed-bean-class").valueAsString();
            String managedBeanScope = extension.getParameter("managed-bean-scope").valueAsString();
            Collection<Parameter> managedBeanProperties = extension.getParameters("managed-property");
            addManagedBean(descsription, managedBeanName, managedBeanClass, managedBeanScope, managedBeanProperties, xmlStreamWriter);
            added = true;
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        return added;
    }

    /**
     * Return the managed beans whose extensions exist on the plugin given by the target-plugin-id if defaultManagedBeans is true,
     * otherwise return managed beans defined on any other plugins.
     * 
     * @param warPluginDescriptor
     * @param defaultManagedBeans
     * @return
     */
    protected Set<Extension> getManagedBeanExtensions(PluginDescriptor warPluginDescriptor, boolean defaultManagedBeans) {
        Set<Extension> extensions = new HashSet<Extension>();
        PluginDescriptor facesPluginDescriptor = getFacesPluginDescriptor();
        ExtensionPoint managedBeanExtensionPoint = facesPluginDescriptor.getExtensionPoint("managed-bean");
        if (managedBeanExtensionPoint == null) {
            throw new RuntimeException(facesPluginDescriptor.getUniqueId() + " does not have a managed-bean extension point");
        }
        for (Extension extension : managedBeanExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor managedBeanPluginDescriptor = extension.getDeclaringPluginDescriptor();
            String targetPluginId = extension.getParameter("target-plugin-id").valueAsString();
            if (targetPluginId == null) {
                throw new RuntimeException("A value for target-plugin-id on " + extension.getUniqueId() + " must be supplied");
            }
            if (defaultManagedBeans) {
                if (managedBeanPluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                    extensions.add(extension);
                }
            } else {
                if (!managedBeanPluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                    extensions.add(extension);
                }
            }
        }
        List<Extension> duplicates = getDuplicateManagedBeans(extensions);
        if (!duplicates.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            for (Extension extension : duplicates) {
                buff.append(extension.getUniqueId() + " ");
            }
            throw new RuntimeException("ManagedBeans with duplicate bean names detected: " + buff.toString());
        }
        return extensions;
    }

    /**
     * Return any managed bean extensions which have the same managed-bean-name
     * 
     * @param managedBeanExtensions
     * @return
     */
    protected List<Extension> getDuplicateManagedBeans(Set<Extension> managedBeanExtensions) {
        List<Extension> duplicates = new ArrayList<Extension>();
        Map<String, List<Extension>> extensionMap = new HashMap<String, List<Extension>>();
        for (Extension extension : managedBeanExtensions) {
            Parameter beanNameParameter = extension.getParameter("managed-bean-name");
            if (beanNameParameter == null) {
                throw new RuntimeException("A value for managed-bean-name on " + extension.getUniqueId() + " must be supplied");
            }
            String beanName = beanNameParameter.valueAsString();
            List<Extension> extensions = extensionMap.get(beanName);
            if (extensions == null) {
                extensions = new ArrayList<Extension>();
                extensionMap.put(beanName, extensions);
            }
            extensions.add(extension);
        }
        for (String key : extensionMap.keySet()) {
            if (extensionMap.get(key).size() > 1) {
                duplicates.addAll(extensionMap.get(key));
            }
        }
        return duplicates;
    }

    /**
     * Add a managed bean template if-clause for managedBeanName
     * 
     * @param description
     * @param managedBeanName
     * @param managedBeanClass
     * @param managedBeanScope
     * @param xmlStreamWriter
     * @throws XMLStreamException
     */
    protected void addManagedBean(String description, String managedBeanName, String managedBeanClass, String managedBeanScope, Collection<Parameter> managedBeanProperties, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:managed-bean[tp:managed-bean-name = '" + managedBeanName + "']) = 0");
        xmlStreamWriter.writeStartElement("managed-bean");
        xmlStreamWriter.writeCharacters("\n");
        if (description != null) {
            xmlStreamWriter.writeStartElement("description");
            xmlStreamWriter.writeCharacters(description);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeStartElement("managed-bean-name");
        xmlStreamWriter.writeCharacters(managedBeanName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("managed-bean-class");
        xmlStreamWriter.writeCharacters(managedBeanClass);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("managed-bean-scope");
        xmlStreamWriter.writeCharacters(managedBeanScope);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        for (Parameter managedProperty : managedBeanProperties) {
            xmlStreamWriter.writeStartElement("managed-property");
            String propertyName = managedProperty.getSubParameter("property-name").valueAsString();
            if (propertyName == null || propertyName.trim().length() == 0) {
                throw new RuntimeException("property-name parameter of a managed-property requires a value");
            }
            xmlStreamWriter.writeStartElement("property-name");
            xmlStreamWriter.writeCharacters(propertyName);
            xmlStreamWriter.writeEndElement();
            if (managedProperty.getSubParameter("value") != null) {
                String propertyClass = managedProperty.getSubParameter("property-class").valueAsString();
                xmlStreamWriter.writeStartElement("property-class");
                xmlStreamWriter.writeCharacters(propertyClass);
                xmlStreamWriter.writeEndElement();
            }
            String value = managedProperty.getSubParameter("value").valueAsString();
            xmlStreamWriter.writeStartElement("value");
            xmlStreamWriter.writeCharacters(value);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
