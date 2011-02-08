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
package org.tolven.assembler.webxml;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin assemblers a web.xml for a war file.
 * 
 * @author Joseph Isaac
 *
 */
public class WebXMLAssembler extends TolvenCommandPlugin {

    public static final String ATTRIBUTE_TEMPLATE_WEBXML = "template-webxml";
    public static final String EXTENSIONPOINT_WAR = "warExtensionPoint";
    public static final String EXTENSIONPOINT_ABSTRACT_WAR = "abstractWAR";

    public static final String CMD_LINE_WAR_PLUGIN_OPTION = "warPlugin";
    public static final String CMD_LINE_DESTDIR_OPTION = "destDir";

    private Logger logger = Logger.getLogger(WebXMLAssembler.class);

    protected PluginDescriptor getAbstractWARPluginDescriptor() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_ABSTRACT_WAR);
        String parentPluginId = extensionPoint.getParentPluginId();
        PluginDescriptor pluginDescriptor = getManager().getRegistry().getPluginDescriptor(parentPluginId);
        return pluginDescriptor;
    }

    protected Extension getTransactionFilter(PluginDescriptor warPluginDescriptor) {
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint transactionFilterExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("transaction-filter");
        if (transactionFilterExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a transaction-filter extension point");
        }
        Extension transactionFilterExtension = null;
        for (Extension extension : transactionFilterExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            Parameter filterNameParameter = extension.getParameter("filter-name");
            if (filterNameParameter == null || filterNameParameter.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a filter-name parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                transactionFilterExtension = extension;
                break;
            }
        }
        return transactionFilterExtension;
    }

    protected Extension getSecurityFilter(PluginDescriptor warPluginDescriptor) {
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint securityFilterExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("security-filter");
        if (securityFilterExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a security-filter extension point");
        }
        Extension securityFilterExtension = null;
        for (Extension extension : securityFilterExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            Parameter filterNameParameter = extension.getParameter("filter-name");
            if (filterNameParameter == null || filterNameParameter.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a filter-name parameter value");
            }
            Parameter webResourceNameParameter = extension.getParameter("web-resource-name");
            if (webResourceNameParameter == null || webResourceNameParameter.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a web-resource-name parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                securityFilterExtension = extension;
                break;
            }
        }
        /*
        if (securityFilterExtension == null) {
            throw new RuntimeException(securityFilterExtensionPoint.getUniqueId() + " does not have a connected security filter");
        }
        */
        return securityFilterExtension;
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
        String destDirname = commandLine.getOptionValue(CMD_LINE_DESTDIR_OPTION);
        PluginDescriptor warPluginDescriptor = getManager().getRegistry().getPluginDescriptor(warPluginId);
        File webXMLFile = new File(destDirname, "web.xml");
        logger.debug(webXMLFile.getPath() + " does not exist, so processing is required.");
        if (!webXMLFile.exists()) {
            logger.debug(webXMLFile.getPath() + " does not exist");
            String templateFilename = getDescriptor().getAttribute(ATTRIBUTE_TEMPLATE_WEBXML).getValue();
            File templateFile = getFilePath(templateFilename);
            if (!templateFile.exists()) {
                throw new RuntimeException("Could not locate: '" + templateFile.getPath());
            }
            logger.debug("Copy " + templateFile + " to " + webXMLFile.getPath());
            FileUtils.copyFile(templateFile, webXMLFile);
        }
        StringBuffer originalXML = new StringBuffer();
        logger.debug("Read " + webXMLFile.getPath());
        originalXML.append(FileUtils.readFileToString(webXMLFile));
        String xslt = getXSLT(warPluginDescriptor);
        File xsltFile = new File(getPluginTmpDir(), "webxml-xslt.xml");
        logger.debug("Write web.xml xslt to " + xsltFile.getPath());
        FileUtils.writeStringToFile(new File(getPluginTmpDir(), "webxml-xslt.xml"), xslt);
        String translatedXMLString = getTranslatedXML(originalXML.toString(), xslt);
        webXMLFile.getParentFile().mkdirs();
        logger.debug("Write translated web.xml file to " + webXMLFile.getPath());
        FileUtils.writeStringToFile(webXMLFile, translatedXMLString);
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

    protected String getXSLT(PluginDescriptor pluginDescriptor) throws XMLStreamException {
        StringWriter xslt = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = null;
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
            addContextParameterTemplate(xmlStreamWriter);
            addContextParameterCallTemplates(pluginDescriptor, xmlStreamWriter);
            addFilterTemplates(pluginDescriptor, xmlStreamWriter);
            addListenerTemplates(pluginDescriptor, xmlStreamWriter);
            addServletTemplates(pluginDescriptor, xmlStreamWriter);
            addEJBLocalRefTemplates(pluginDescriptor, xmlStreamWriter);
            addSessionConfigTemplates(pluginDescriptor, xmlStreamWriter);
            addWelcomeFileListTemplates(pluginDescriptor, xmlStreamWriter);
            addWebSecurityConstraintTemplates(pluginDescriptor, xmlStreamWriter);
            addLoginConfigTemplates(pluginDescriptor, xmlStreamWriter);
            addSecurityRoleTemplates(pluginDescriptor, xmlStreamWriter);
            addErrorPageTemplates(pluginDescriptor, xmlStreamWriter);
            xmlStreamWriter.writeEndDocument();
        } finally {
            if (xmlStreamWriter != null) {
                xmlStreamWriter.close();
            }
        }
        return xslt.toString();
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
        xmlStreamWriter.writeAttribute("match", "tp:web-app");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("web-app");
        xmlStreamWriter.writeAttribute("version", "{@version}");
        xmlStreamWriter.writeAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
        xmlStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlStreamWriter.writeAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd");
        xmlStreamWriter.writeCharacters("\n");
        addRootContextParameterSelects(xmlStreamWriter);
        addRootListenerSelects(xmlStreamWriter);
        addRootFilterSelects(xmlStreamWriter);
        addRootServletSelects(xmlStreamWriter);
        addRootEJBLocalRefSelects(xmlStreamWriter);
        addRootSessionConfigSelects(xmlStreamWriter);
        addRootWelcomeFileListSelects(xmlStreamWriter);
        addRootWebSecurityConstraintSelects(xmlStreamWriter);
        addRootLoginConfigSelects(xmlStreamWriter);
        addRootSecurityRoleSelects(xmlStreamWriter);
        addRootErrorPageSelects(xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootContextParameterSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "initTagContextParameter");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:context-param");
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
        xmlStreamWriter.writeAttribute("name", "addContextParameters");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootFilterSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:filter");
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
        xmlStreamWriter.writeStartElement("xsl:variable");
        xmlStreamWriter.writeAttribute("name", "filter-name");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "tp:filter-name");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", "../tp:filter-mapping[tp:filter-name = $filter-name]");
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
        xmlStreamWriter.writeAttribute("name", "addFilters");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootListenerSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:listener");
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
        xmlStreamWriter.writeAttribute("name", "addListeners");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootServletSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:servlet");
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
        xmlStreamWriter.writeStartElement("xsl:variable");
        xmlStreamWriter.writeAttribute("name", "servlet-name");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "tp:servlet-name");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", "../tp:servlet-mapping[tp:servlet-name = $servlet-name]");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "addServlets");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootEJBLocalRefSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:ejb-local-ref");
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
        xmlStreamWriter.writeStartElement("xsl:variable");
        xmlStreamWriter.writeAttribute("name", "ejb-ref-name");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "tp:ejb-ref-name");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "addEJBLocalRefs");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootSessionConfigSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:session-config");
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
        xmlStreamWriter.writeAttribute("name", "addSessionConfigs");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootWelcomeFileListSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:welcome-file-list");
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
        xmlStreamWriter.writeAttribute("name", "addWelcomeFileLists");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootWebSecurityConstraintSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:security-constraint");
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
        xmlStreamWriter.writeAttribute("name", "addWebSecurityConstraints");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootSecurityRoleSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:security-role");
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
        xmlStreamWriter.writeAttribute("name", "addSecurityRoles");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootErrorPageSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:error-page");
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
        xmlStreamWriter.writeAttribute("name", "addErrorPages");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootLoginConfigSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:login-config");
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
        xmlStreamWriter.writeAttribute("name", "addLoginConfigs");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addContextParameterTemplate(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "context-param");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:param");
        xmlStreamWriter.writeAttribute("name", "param-name");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:param");
        xmlStreamWriter.writeAttribute("name", "param-value");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("context-param");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("param-name");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "$param-name");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("param-value");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "$param-value");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addContextParameterCallTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addContextParameters");
        xmlStreamWriter.writeCharacters("\n");
        List<Extension> contextParams = new ArrayList<Extension>();
        contextParams.addAll(getExtensions(pluginDescriptor, "context-param"));
        Comparator<Object> comparator = new Comparator<Object>() {
            public int compare(Object obj1, Object obj2) {
                Extension e1 = (Extension) obj1;
                Extension e2 = (Extension) obj2;
                return e1.getParameter("param-name").valueAsString().compareTo(e2.getParameter("param-name").valueAsString());
            };
        };
        Collections.sort(contextParams, comparator);
        for (Extension extension : contextParams) {
            String paramValue = evaluate(extension.getParameter("param-value").valueAsString(), pluginDescriptor);
            addContextParameterCallTemplate(extension.getParameter("param-name").valueAsString(), paramValue, xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        List<Extension> relevantExtensions = new ArrayList<Extension>();
        for (Extension extension : getExtensions(pluginDescriptor, "taglib")) {
            if (pluginDescriptor.getId().equals(extension.getParameter("target-plugin-id").valueAsString())) {
                relevantExtensions.add(extension);
            }
        }
        boolean semicolonSeparator = false;
        if (relevantExtensions.isEmpty()) {
            addInitTagLibContextParameterCallTemplate(null, xmlStreamWriter);
        } else {
            for (int i = 0; i < relevantExtensions.size(); i++) {
                Extension extension = relevantExtensions.get(i);
                String tagLib = "/META-INF/tags/" + extension.getParameter("tag-filename").valueAsString();
                String templateName = tagLib.replace("/", "");
                if (i == 0) {
                    addInitTagLibContextParameterCallTemplate(templateName, xmlStreamWriter);
                }
                if (i > 0) {
                    semicolonSeparator = true;
                }
                if (i == relevantExtensions.size() - 1) {
                    addTagLibContextParameterCallTemplate(tagLib, semicolonSeparator, templateName, "context-param", xmlStreamWriter);
                } else {
                    String tagLib2 = "/META-INF/tags/" + relevantExtensions.get(i + 1).getParameter("tag-filename").valueAsString();
                    String templateName2 = tagLib2.replace("/", "");
                    addTagLibContextParameterCallTemplate(tagLib, semicolonSeparator, templateName, templateName2, xmlStreamWriter);
                }
            }
        }
    }

    protected void addContextParameterCallTemplate(String paramName, String paramValue, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:context-param[tp:param-name = '" + paramName + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "context-param");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:with-param");
        xmlStreamWriter.writeAttribute("name", "param-name");
        xmlStreamWriter.writeCharacters(paramName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:with-param");
        xmlStreamWriter.writeAttribute("name", "param-value");
        xmlStreamWriter.writeCharacters(paramValue);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addInitTagLibContextParameterCallTemplate(String initialTagTemplate, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "initTagContextParameter");
        xmlStreamWriter.writeCharacters("\n");
        if (initialTagTemplate != null) {
            xmlStreamWriter.writeStartElement("xsl:call-template");
            xmlStreamWriter.writeAttribute("name", initialTagTemplate);
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("xsl:with-param");
            xmlStreamWriter.writeAttribute("name", "param-name");
            xmlStreamWriter.writeCharacters("javax.faces.FACELETS_LIBRARIES");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("xsl:with-param");
            xmlStreamWriter.writeAttribute("name", "param-value");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addTagLibContextParameterCallTemplate(String tagLib, boolean semicolonSeparator, String templateName1, String templateName2, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", templateName1);
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:param");
        xmlStreamWriter.writeAttribute("name", "param-name");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:param");
        xmlStreamWriter.writeAttribute("name", "param-value");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:choose");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:when");
        xmlStreamWriter.writeAttribute("test", "contains($param-value, '" + tagLib + "')");
        xmlStreamWriter.writeCharacters("\n");
        addTagLibContextParameterWhenCallTemplate(templateName2, xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:otherwise");
        xmlStreamWriter.writeCharacters("\n");
        addTagLibContextParameterOtherwiseCallTemplate(templateName2, tagLib, semicolonSeparator, xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addTagLibContextParameterWhenCallTemplate(String templateName, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", templateName);
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:with-param");
        xmlStreamWriter.writeAttribute("name", "param-name");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "$param-name");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:with-param");
        xmlStreamWriter.writeAttribute("name", "param-value");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "$param-value");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addTagLibContextParameterOtherwiseCallTemplate(String templateName, String tabLib, boolean semicolonSeparator, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", templateName);
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:with-param");
        xmlStreamWriter.writeAttribute("name", "param-name");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "$param-name");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:with-param");
        xmlStreamWriter.writeAttribute("name", "param-value");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        if (semicolonSeparator) {
            xmlStreamWriter.writeAttribute("select", "concat($param-value,'" + ";" + tabLib + "')");
        } else {
            xmlStreamWriter.writeAttribute("select", "concat($param-value,'" + tabLib + "')");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addFilterTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addFilters");
        xmlStreamWriter.writeCharacters("\n");
        List<Extension> filterExtensions = getFilterExtensions(warPluginDescriptor);
        Extension transactionFilterExtension = getTransactionFilter(warPluginDescriptor);
        Extension securityFilterExtension = getSecurityFilter(warPluginDescriptor);
        Map<String, Map<String, Object>> filterMappings = getFilterMappings(warPluginDescriptor, filterExtensions);
        for (Extension extension : filterExtensions) {
            String filterName = extension.getParameter("filter-name").valueAsString();
            addFilter(extension, xmlStreamWriter);
            if (extension.equals(transactionFilterExtension)) {
                addTransactionFilterMapping(extension, filterMappings, xmlStreamWriter);
            } else if (securityFilterExtension != null && securityFilterExtension.equals(extension)) {
                addSecurityFilterMapping(extension, filterMappings, warPluginDescriptor, xmlStreamWriter);
            } else {
                Set<String> urlPatterns = new HashSet<String>();
                String[] dispatchers = null;
                Map<String, Object> map = filterMappings.get(filterName);
                List<Extension> mappingExtensions = (List<Extension>) map.get("mappings");
                for (Extension mappingExtension : mappingExtensions) {
                    for (Parameter parameter : mappingExtension.getParameters("url-pattern")) {
                        urlPatterns.add(parameter.valueAsString());
                    }
                    Parameter dispatcherParameter = mappingExtension.getParameter("dispatchers");
                    if (dispatcherParameter != null && dispatcherParameter.valueAsString().length() > 0) {
                        dispatchers = dispatcherParameter.valueAsString().split(",");
                    }
                }
                List<String> sortedURLPatterns = new ArrayList<String>(urlPatterns);
                Collections.sort(sortedURLPatterns);
                for (String urlPattern : sortedURLPatterns) {
                    addFilterMapping(filterName, urlPattern, dispatchers, xmlStreamWriter);
                }
            }
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addTransactionFilterMapping(Extension transactionFilterExtension, Map<String, Map<String, Object>> filterMappings, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        Set<String> urlPatterns = new HashSet<String>();
        String filterName = transactionFilterExtension.getParameter("filter-name").valueAsString();
        Map<String, Object> map = filterMappings.get(filterName);
        List<Extension> mappingExtensions = (List<Extension>) map.get("mappings");
        for (Extension mappingExtension : mappingExtensions) {
            for (Parameter parameter : mappingExtension.getParameters("url-pattern")) {
                urlPatterns.add(parameter.valueAsString());
            }
        }
        List<String> sortedURLPatterns = new ArrayList<String>(urlPatterns);
        Collections.sort(sortedURLPatterns);
        for (String urlPattern : sortedURLPatterns) {
            addFilterMapping(filterName, urlPattern, null, xmlStreamWriter);
        }
    }

    protected void addSecurityFilterMapping(Extension securityFilterExtension, Map<String, Map<String, Object>> filterMappings, PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        Set<String> urlPatterns = new HashSet<String>();
        String filterName = securityFilterExtension.getParameter("filter-name").valueAsString();
        Map<String, Object> map = filterMappings.get(filterName);
        List<Extension> mappingExtensions = (List<Extension>) map.get("mappings");
        for (Extension mappingExtension : mappingExtensions) {
            for (Parameter parameter : mappingExtension.getParameters("url-pattern")) {
                urlPatterns.add(parameter.valueAsString());
            }
        }
        urlPatterns.addAll(getSecurityURLPatterns(filterMappings, securityFilterExtension));
        List<Extension> servletExtensions = getServletExtensions(warPluginDescriptor);
        Map<String, Map<String, Object>> servletMappings = getServletMappings(warPluginDescriptor, servletExtensions);
        urlPatterns.addAll(getSecurityURLPatterns(servletMappings, securityFilterExtension));
        String securityWebResourceName = securityFilterExtension.getParameter("web-resource-name").valueAsString();
        urlPatterns.addAll(getWebDirectoryURLPatterns(warPluginDescriptor, securityWebResourceName));
        Parameter urlPatternExcludesParameter = securityFilterExtension.getParameter("url-pattern-excludes");
        if (urlPatternExcludesParameter != null) {
            List<String> excludes = Arrays.asList(urlPatternExcludesParameter.valueAsString().split(","));
            urlPatterns.removeAll(excludes);
        }
        List<String> sortedURLPatterns = new ArrayList<String>(urlPatterns);
        Collections.sort(sortedURLPatterns);
        for (String urlPattern : sortedURLPatterns) {
            addFilterMapping(filterName, urlPattern, null, xmlStreamWriter);
        }
    }

    protected List<Extension> getFilterExtensions(PluginDescriptor warPluginDescriptor) {
        List<Extension> filterExtensions = new ArrayList<Extension>();
        Extension transactionFilterExtension = getTransactionFilter(warPluginDescriptor);
        if (transactionFilterExtension != null) {
            filterExtensions.add(transactionFilterExtension);
        }
        Extension securityFilterExtension = getSecurityFilter(warPluginDescriptor);
        if (securityFilterExtension != null) {
            filterExtensions.add(securityFilterExtension);
        }
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint filterExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("filter");
        if (filterExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a filter extension point");
        }
        for (Extension extension : filterExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                Parameter filterNameParameter = extension.getParameter("filter-name");
                if (filterNameParameter == null || filterNameParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a filter-name parameter value");
                }
                Parameter filterClassParameter = extension.getParameter("filter-class");
                if (filterClassParameter == null || filterClassParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a filter-class parameter value");
                }
                filterExtensions.add(extension);
            }
        }
        Comparator<Extension> comparator = new Comparator<Extension>() {
            public int compare(Extension filterExtension1, Extension filterExtension2) {
                Number num1 = filterExtension1.getParameter("filter-sequence").valueAsNumber();
                Number num2 = filterExtension2.getParameter("filter-sequence").valueAsNumber();
                if (num1.doubleValue() < num2.doubleValue()) {
                    return -1;
                } else if (num1.doubleValue() > num2.doubleValue()) {
                    return 1;
                } else {
                    return 0;
                }
            };
        };
        Collections.sort(filterExtensions, comparator);
        return filterExtensions;
    }

    protected Map<String, Map<String, Object>> getFilterMappings(PluginDescriptor warPluginDescriptor, List<Extension> filterExtensions) {
        Map<String, Map<String, Object>> filterMappings = new HashMap<String, Map<String, Object>>();
        List<String> filterNames = new ArrayList<String>();
        for (Extension filterExtension : filterExtensions) {
            Parameter targetPluginId = filterExtension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(filterExtension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                Map<String, Object> map = new HashMap<String, Object>();
                String filterName = filterExtension.getParameter("filter-name").valueAsString();
                filterNames.add(filterName);
                map.put("extension", filterExtension);
                map.put("mappings", new ArrayList<Extension>());
                filterMappings.put(filterName, map);
            }
        }
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint filterMappingExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("filter-mapping-contribution");
        if (filterMappingExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a filter-mapping-contribution extension point");
        }
        for (Extension extension : filterMappingExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                Parameter filterNameParameter = extension.getParameter("filter-name");
                if (filterNameParameter == null || filterNameParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a filter-name parameter value");
                }
                Parameter optionalParameter = extension.getParameter("optional");
                if (!filterNames.contains(filterNameParameter.valueAsString())) {
                    if (optionalParameter != null && optionalParameter.valueAsBoolean()) {
                        continue;
                    } else {
                        throw new RuntimeException(extension.getUniqueId() + " is not optional yet has a filter-name which does not exist");
                    }
                }
                if (filterNameParameter == null || filterNameParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a filter-name parameter value");
                }
                String filterName = filterNameParameter.valueAsString();
                Map<String, Object> map = filterMappings.get(filterName);
                List<Extension> extensions = (List<Extension>) map.get("mappings");
                extensions.add(extension);
            }
        }
        return filterMappings;
    }

    protected Set<String> getSecurityURLPatterns(Map<String, Map<String, Object>> mappings, Extension securityFilterExtension) {
        Parameter securityWebResourceNameParameter = securityFilterExtension.getParameter("web-resource-name");
        if (securityWebResourceNameParameter == null || securityWebResourceNameParameter.valueAsString().trim().length() == 0) {
            throw new RuntimeException(securityFilterExtension.getUniqueId() + " does not have a web-resource-name parameter value");
        }
        String securityWebResourceName = securityWebResourceNameParameter.valueAsString();
        Set<String> urlPatterns = new HashSet<String>();
        for (String filterName : mappings.keySet()) {
            Map<String, Object> map = (Map<String, Object>) mappings.get(filterName);
            Extension extension = (Extension) map.get("extension");
            List<Extension> mappingExtensions = (List<Extension>) map.get("mappings");
            for (Extension mappingExtension : mappingExtensions) {
                Parameter mappingWebResourceNameParameter = extension.getParameter("web-resource-name");
                if (mappingWebResourceNameParameter != null && securityWebResourceName.equals(mappingWebResourceNameParameter.valueAsString())) {
                    for (Parameter parameter : mappingExtension.getParameters("url-pattern")) {
                        urlPatterns.add(parameter.valueAsString());
                    }
                }
            }
        }
        return urlPatterns;

    }

    protected void addFilter(Extension filterExtension, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        String filterName = filterExtension.getParameter("filter-name").valueAsString();
        String filterClass = filterExtension.getParameter("filter-class").valueAsString();
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:filter[tp:filter-name = '" + filterName + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("filter");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("filter-name");
        xmlStreamWriter.writeCharacters(filterName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("filter-class");
        xmlStreamWriter.writeCharacters(filterClass);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        for (Parameter parameter : filterExtension.getParameters("init-param")) {
            xmlStreamWriter.writeStartElement("init-param");
            String paramName = parameter.getSubParameter("param-name").valueAsString();
            xmlStreamWriter.writeStartElement("param-name");
            xmlStreamWriter.writeCharacters(paramName);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            String paramValue = parameter.getSubParameter("param-value").valueAsString();
            xmlStreamWriter.writeStartElement("param-value");
            xmlStreamWriter.writeCharacters(paramValue);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addFilterMapping(String filterName, String urlPattern, String[] dispatchers, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:filter-mapping[tp:filter-name = '" + filterName + "' and tp:url-pattern = '" + urlPattern + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("filter-mapping");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("filter-name");
        xmlStreamWriter.writeCharacters(filterName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("url-pattern");
        xmlStreamWriter.writeCharacters(urlPattern);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        if (dispatchers != null) {
            for (String dispatcher : dispatchers) {
                xmlStreamWriter.writeStartElement("dispatcher");
                xmlStreamWriter.writeCharacters(dispatcher);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addListenerTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addListeners");
        xmlStreamWriter.writeCharacters("\n");
        for (Extension extension : getExtensions(pluginDescriptor, "listener")) {
            addListener(extension.getParameter("listener-class").valueAsString(), xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addListener(String listenerClass, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:listener[tp:listener-class = '" + listenerClass + "']) = 0");
        xmlStreamWriter.writeStartElement("listener");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("listener-class");
        xmlStreamWriter.writeCharacters(listenerClass);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addServletTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addServlets");
        xmlStreamWriter.writeCharacters("\n");
        List<Extension> servletExtensions = getServletExtensions(warPluginDescriptor);
        Map<String, Map<String, Object>> servletMappings = getServletMappings(warPluginDescriptor, servletExtensions);
        for (Extension extension : servletExtensions) {
            String servletName = extension.getParameter("servlet-name").valueAsString();
            addServlet(extension, xmlStreamWriter);
            Set<String> urlPatterns = new HashSet<String>();
            Map<String, Object> map = servletMappings.get(servletName);
            if (map != null) {
                List<Extension> mappingExtensions = (List<Extension>) map.get("mappings");
                for (Extension mappingExtension : mappingExtensions) {
                    for (Parameter parameter : mappingExtension.getParameters("url-pattern")) {
                        urlPatterns.add(parameter.valueAsString());
                    }
                }
            }
            for (String urlPattern : urlPatterns) {
                addServletMapping(servletName, urlPattern, xmlStreamWriter);
            }
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected List<Extension> getServletExtensions(PluginDescriptor warPluginDescriptor) {
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint servletExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("servlet");
        if (servletExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a servlet extension point");
        }
        List<Extension> servletExtensions = new ArrayList<Extension>();
        for (Extension extension : servletExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                Parameter servletNameParameter = extension.getParameter("servlet-name");
                if (servletNameParameter == null || servletNameParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a servlet-name parameter value");
                }
                Parameter servletClassParameter = extension.getParameter("servlet-class");
                if (servletClassParameter == null || servletClassParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a servlet-class parameter value");
                }
                servletExtensions.add(extension);
            }
        }
        return servletExtensions;
    }

    protected Map<String, Map<String, Object>> getServletMappings(PluginDescriptor warPluginDescriptor, List<Extension> servletExtensions) {
        Map<String, Extension> servletMap = new HashMap<String, Extension>();
        for (Extension servletExtension : servletExtensions) {
            Parameter targetPluginId = servletExtension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(servletExtension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                servletMap.put(servletExtension.getParameter("servlet-name").valueAsString(), servletExtension);
            }
        }
        List<String> servletNames = new ArrayList<String>(servletMap.keySet());
        Map<String, Map<String, Object>> servletMappings = new HashMap<String, Map<String, Object>>();
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint servletMappingExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("servlet-mapping-contribution");
        if (servletMappingExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a servlet-mapping-contribution extension point");
        }
        for (Extension extension : servletMappingExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                Parameter servletNameParameter = extension.getParameter("servlet-name");
                if (servletNameParameter == null || servletNameParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a servlet-name parameter value");
                }
                Parameter optionalParameter = extension.getParameter("optional");
                if (!servletNames.contains(servletNameParameter.valueAsString())) {
                    if (optionalParameter != null && optionalParameter.valueAsBoolean()) {
                        continue;
                    } else {
                        throw new RuntimeException(extension.getUniqueId() + " is not optional yet has a servlet-name which does not exist");
                    }
                }
                if (servletNameParameter == null || servletNameParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a servlet-name parameter value");
                }
                String servletName = servletNameParameter.valueAsString();
                Map<String, Object> map = servletMappings.get(servletName);
                if (map == null) {
                    map = new HashMap<String, Object>();
                    map.put("extension", servletMap.get(servletName));
                    map.put("mappings", new ArrayList<Extension>());
                    servletMappings.put(servletName, map);
                }
                List<Extension> extensions = (List<Extension>) map.get("mappings");
                extensions.add(extension);
            }
        }
        return servletMappings;
    }

    protected void addServlet(Extension servletExtension, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        String servletName = servletExtension.getParameter("servlet-name").valueAsString();
        String servletClass = servletExtension.getParameter("servlet-class").valueAsString();
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:servlet[tp:servlet-name = '" + servletName + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("servlet");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("servlet-name");
        xmlStreamWriter.writeCharacters(servletName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("servlet-class");
        xmlStreamWriter.writeCharacters(servletClass);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        for (Parameter parameter : servletExtension.getParameters("init-param")) {
            xmlStreamWriter.writeStartElement("init-param");
            String paramName = parameter.getSubParameter("param-name").valueAsString();
            xmlStreamWriter.writeStartElement("param-name");
            xmlStreamWriter.writeCharacters(paramName);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            String paramValue = parameter.getSubParameter("param-value").valueAsString();
            xmlStreamWriter.writeStartElement("param-value");
            xmlStreamWriter.writeCharacters(paramValue);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        if (servletExtension.getParameter("load-on-startup") != null) {
            Number loadOnStartUp = servletExtension.getParameter("load-on-startup").valueAsNumber();
            xmlStreamWriter.writeStartElement("load-on-startup");
            xmlStreamWriter.writeCharacters(loadOnStartUp.toString());
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addServletMapping(String servletName, String urlPattern, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:servlet-mapping[tp:servlet-name = '" + servletName + "' and tp:url-pattern = '" + urlPattern + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("servlet-mapping");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("servlet-name");
        xmlStreamWriter.writeCharacters(servletName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("url-pattern");
        xmlStreamWriter.writeCharacters(urlPattern);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addEJBLocalRefTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addEJBLocalRefs");
        xmlStreamWriter.writeCharacters("\n");
        List<Extension> ejbLocalRefExtensions = getEJBLocalRefExtensions(warPluginDescriptor);
        for (Extension extension : ejbLocalRefExtensions) {
            String ejbRefName = null;
            if (extension.getParameter("ejb-ref-name") != null) {
                ejbRefName = extension.getParameter("ejb-ref-name").valueAsString();
            }
            String ejbRefType = null;
            if (extension.getParameter("ejb-ref-type") != null) {
                ejbRefType = extension.getParameter("ejb-ref-type").valueAsString();
            }
            String localHome = null;
            if (extension.getParameter("local-home") != null) {
                localHome = extension.getParameter("local-home").valueAsString();
            }
            String local = null;
            if (extension.getParameter("local") != null) {
                local = extension.getParameter("local").valueAsString();
            }
            String ejbLink = null;
            if (extension.getParameter("ejb-link") != null) {
                ejbLink = extension.getParameter("ejb-link").valueAsString();
            }
            addEJBLocalRef(ejbRefName, ejbRefType, localHome, local, ejbLink, xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected List<Extension> getEJBLocalRefExtensions(PluginDescriptor warPluginDescriptor) {
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint ejbLocalRefExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("ejb-local-ref");
        if (ejbLocalRefExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have a ejb-local-ref extension point");
        }
        List<Extension> ejbLocalRefExtensions = new ArrayList<Extension>();
        for (Extension extension : ejbLocalRefExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                Parameter ejbLocalRefNameParameter = extension.getParameter("ejb-ref-name");
                if (ejbLocalRefNameParameter == null || ejbLocalRefNameParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " does not have a ejb-ref-name parameter value");
                }
                ejbLocalRefExtensions.add(extension);
            }
        }
        return ejbLocalRefExtensions;
    }

    protected void addEJBLocalRef(String ejbRefName, String ejbRefType, String localHome, String local, String ejbLink, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:ejb-local-ref[tp:ejb-ref-name = '" + ejbRefName + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("ejb-local-ref");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("ejb-ref-name");
        xmlStreamWriter.writeCharacters(ejbRefName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        if (ejbRefType != null) {
            xmlStreamWriter.writeStartElement("ejb-ref-type");
            xmlStreamWriter.writeCharacters(ejbRefType);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        if (localHome != null) {
            xmlStreamWriter.writeStartElement("local-home");
            xmlStreamWriter.writeCharacters(localHome);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        if (local != null) {
            xmlStreamWriter.writeStartElement("local");
            xmlStreamWriter.writeCharacters(local);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        if (ejbLink != null) {
            xmlStreamWriter.writeStartElement("ejb-link");
            xmlStreamWriter.writeCharacters(ejbLink);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addSessionConfigTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addSessionConfigs");
        xmlStreamWriter.writeCharacters("\n");
        for (Extension extension : getExtensions(pluginDescriptor, "session-config")) {
            addSessionConfig(extension.getParameter("session-timeout").valueAsNumber(), xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addSessionConfig(Number sessionTimeout, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:session-config[tp:session-timeout = '" + sessionTimeout + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("session-config");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("session-timeout");
        xmlStreamWriter.writeCharacters(sessionTimeout.toString());
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addWelcomeFileListTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addWelcomeFileLists");
        xmlStreamWriter.writeCharacters("\n");
        for (Extension extension : getExtensions(pluginDescriptor, "welcome-file-list")) {
            addWelcomeFileList(extension.getParameter("welcome-file").valueAsString(), xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addWelcomeFileList(String welcomeFile, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:welcome-file-list[tp:welcome-file = '" + welcomeFile + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("welcome-file-list");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("welcome-file");
        xmlStreamWriter.writeCharacters(welcomeFile);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addWebSecurityConstraintTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addWebSecurityConstraints");
        xmlStreamWriter.writeCharacters("\n");
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint securityConstraintExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("security-constraint");
        for (Extension extension : securityConstraintExtensionPoint.getConnectedExtensions()) {
            Parameter targetPluginId = extension.getParameter("target-plugin-id");
            if (targetPluginId == null || targetPluginId.valueAsString().trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " does not have a target-plugin-id parameter value");
            }
            if (warPluginDescriptor.getId().equals(targetPluginId.valueAsString())) {
                addWebSecurityConstraint(warPluginDescriptor, extension, xmlStreamWriter);
            }
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected Set<String> getFilterMappingURLPatterns(PluginDescriptor warPluginDescriptor, String webResourceName) {
        Set<String> urlPatterns = new HashSet<String>();
        List<Extension> filterExtensions = getFilterExtensions(warPluginDescriptor);
        Map<String, Map<String, Object>> filterMappings = getFilterMappings(warPluginDescriptor, filterExtensions);
        for (Extension filterExtension : filterExtensions) {
            Parameter mappingWebResourceNameParameter = filterExtension.getParameter("web-resource-name");
            if (mappingWebResourceNameParameter != null && mappingWebResourceNameParameter.valueAsString().equals(webResourceName)) {
                Map<String, Object> map = filterMappings.get(filterExtension.getParameter("filter-name").valueAsString());
                if (map != null) {
                    List<Extension> mappingExtensions = (List<Extension>) map.get("mappings");
                    for (Extension mappingExtension : mappingExtensions) {
                        for (Parameter urlPatternParameter : mappingExtension.getParameters("url-pattern")) {
                            if (urlPatternParameter.valueAsString().trim().length() == 0) {
                                throw new RuntimeException(mappingExtension.getUniqueId() + " must have values for the url-pattern");
                            }
                            urlPatterns.add(urlPatternParameter.valueAsString());
                        }
                    }
                }
            }
        }
        return urlPatterns;
    }

    protected Set<String> getServletMappingURLPatterns(PluginDescriptor warPluginDescriptor, String webResourceName) {
        Set<String> urlPatterns = new HashSet<String>();
        List<Extension> servletExtensions = getServletExtensions(warPluginDescriptor);
        Map<String, Map<String, Object>> servletMappings = getServletMappings(warPluginDescriptor, servletExtensions);
        for (Extension servletExtension : servletExtensions) {
            Parameter mappingWebResourceNameParameter = servletExtension.getParameter("web-resource-name");
            if (mappingWebResourceNameParameter != null && mappingWebResourceNameParameter.valueAsString().equals(webResourceName)) {
                Map<String, Object> map = servletMappings.get(servletExtension.getParameter("servlet-name").valueAsString());
                if (map != null) {
                    List<Extension> mappingExtensions = (List<Extension>) map.get("mappings");
                    for (Extension mappingExtension : mappingExtensions) {
                        for (Parameter urlPatternParameter : mappingExtension.getParameters("url-pattern")) {
                            if (urlPatternParameter.valueAsString().trim().length() == 0) {
                                throw new RuntimeException(mappingExtension.getUniqueId() + " must have values for the url-pattern");
                            }
                            urlPatterns.add(urlPatternParameter.valueAsString());
                        }
                    }
                }
            }
        }
        return urlPatterns;
    }

    protected Set<String> getWebDirectoryURLPatterns(PluginDescriptor warPluginDescriptor, String webResourceName) {
        Set<String> urlPatterns = new HashSet<String>();
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
                throw new RuntimeException(webDirectoryExtension.getUniqueId() + " does not have a parameter target-plugin-id value");
            }
            Parameter webResourceNameParameter = webDirectoryExtension.getParameter("web-resource-name");
            if (webResourceNameParameter == null || webResourceNameParameter.valueAsString() == null || webResourceNameParameter.valueAsString().trim().length() == 0) {
                // No entry in the security constraints
                continue;
            }
            if (targetPluginIdParameter.valueAsString().equals(warPluginDescriptor.getId()) && webResourceNameParameter.valueAsString().equals(webResourceName)) {
                Parameter functionParameter = webDirectoryExtension.getParameter("function");
                if (functionParameter == null || functionParameter.valueAsString() == null || functionParameter.valueAsString().trim().length() == 0) {
                    throw new RuntimeException(webDirectoryExtension.getUniqueId() + " does not have a parameter function value");
                }
                urlPatterns.add("/" + functionParameter.valueAsString() + "/*");
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
            ParameterDefinition webResourceNameParameterDef = webDirectoryDescendentExtensionPoint.getParameterDefinition("web-resource-name");
            if (webResourceNameParameterDef == null || webResourceNameParameterDef.getDefaultValue() == null || webResourceNameParameterDef.getDefaultValue().trim().length() == 0) {
                // No entry in the security constraints
                continue;
            }
            if (targetPluginIdParameterDef.getDefaultValue().equals(warPluginDescriptor.getId()) && webResourceNameParameterDef.getDefaultValue().equals(webResourceName)) {
                ParameterDefinition functionParameterDef = webDirectoryDescendentExtensionPoint.getParameterDefinition("function");
                if (functionParameterDef == null || functionParameterDef.getDefaultValue() == null || functionParameterDef.getDefaultValue().trim().length() == 0) {
                    throw new RuntimeException(webDirectoryDescendentExtensionPoint.getUniqueId() + " does not have a default parameter-def function value");
                }
                urlPatterns.add("/" + functionParameterDef.getDefaultValue() + "/*");
            }
        }
        return urlPatterns;
    }

    protected void addWebSecurityConstraint(PluginDescriptor warPluginDescriptor, Extension securityConstraintExtension, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("security-constraint");
        xmlStreamWriter.writeCharacters("\n");
        for (Parameter webResourceCollectionParameter : securityConstraintExtension.getParameters("web-resource-collection")) {
            Parameter webResourceNameParameter = webResourceCollectionParameter.getSubParameter("web-resource-name");
            if (webResourceNameParameter == null || webResourceNameParameter.valueAsString().trim().length() == 0) {
                throw new RuntimeException(securityConstraintExtension.getUniqueId() + " does not have a web-resource-name parameter value");
            }
            String webResourceName = webResourceNameParameter.valueAsString();
            Set<String> urlPatterns = new HashSet<String>();
            urlPatterns.addAll(getFilterMappingURLPatterns(warPluginDescriptor, webResourceName));
            urlPatterns.addAll(getServletMappingURLPatterns(warPluginDescriptor, webResourceName));
            urlPatterns.addAll(getWebDirectoryURLPatterns(warPluginDescriptor, webResourceName));
            for (Parameter urlPattern : webResourceCollectionParameter.getSubParameters("url-pattern")) {
                urlPatterns.add(urlPattern.valueAsString());
            }
            Set<String> httpMethods = new HashSet<String>();
            for (Parameter httpMethod : webResourceCollectionParameter.getSubParameters("http-method")) {
                httpMethods.add(httpMethod.valueAsString());
            }
            addWebResourceCollection(webResourceName, urlPatterns, httpMethods, xmlStreamWriter);
        }
        Set<String> roleNames = new HashSet<String>();
        for (Parameter parameter : securityConstraintExtension.getParameters("role-name")) {
            if (parameter.valueAsString().trim().length() == 0) {
                throw new RuntimeException(securityConstraintExtension.getUniqueId() + " must have values for the role-name");
            }
            roleNames.add(parameter.valueAsString());
        }
        if (!roleNames.isEmpty()) {
            xmlStreamWriter.writeStartElement("auth-constraint");
            for (String roleName : roleNames) {
                xmlStreamWriter.writeStartElement("role-name");
                xmlStreamWriter.writeCharacters(roleName);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        Parameter transportGuaranteeParameter = securityConstraintExtension.getParameter("transport-guarantee");
        if (transportGuaranteeParameter != null) {
            xmlStreamWriter.writeStartElement("user-data-constraint");
            String transportGuarantee = transportGuaranteeParameter.valueAsString();
            xmlStreamWriter.writeStartElement("transport-guarantee");
            xmlStreamWriter.writeCharacters(transportGuarantee);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addWebResourceCollection(String webResourceName, Set<String> urlPatterns, Set<String> httpMethods, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:security-constraint[tp:web-resource-collection/tp:web-resource-name = '" + webResourceName + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("web-resource-collection");
        xmlStreamWriter.writeStartElement("web-resource-name");
        xmlStreamWriter.writeCharacters(webResourceName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        List<String> sortedURLPatterns = new ArrayList<String>(urlPatterns);
        Collections.sort(sortedURLPatterns);
        for (String urlPattern : sortedURLPatterns) {
            xmlStreamWriter.writeStartElement("url-pattern");
            xmlStreamWriter.writeCharacters(urlPattern);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        List<String> sortedHttpMethods = new ArrayList<String>(httpMethods);
        Collections.sort(sortedHttpMethods);
        for (String httpMethod : sortedHttpMethods) {
            xmlStreamWriter.writeStartElement("http-method");
            xmlStreamWriter.writeCharacters(httpMethod);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addSecurityRoleTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addSecurityRoles");
        xmlStreamWriter.writeCharacters("\n");
        for (Extension extension : getExtensions(pluginDescriptor, "security-role")) {
            addSecurityRole(extension.getParameter("role-name").valueAsString(), xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addSecurityRole(String roleName, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:security-role[tp:role-name = '" + roleName + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("security-role");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("role-name");
        xmlStreamWriter.writeCharacters(roleName);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addErrorPageTemplates(PluginDescriptor warPluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addErrorPages");
        xmlStreamWriter.writeCharacters("\n");
        Map<String, Extension> errorPageMap = new HashMap<String, Extension>();
        for (Extension defaultExtension : getErrorPageExtensions(warPluginDescriptor, true)) {
            String errorCode = defaultExtension.getParameter("error-code").valueAsString();
            errorPageMap.put(errorCode, defaultExtension);
        }
        for (Extension nonDefaultExtension : getErrorPageExtensions(warPluginDescriptor, false)) {
            String errorCode = nonDefaultExtension.getParameter("error-code").valueAsString();
            Extension errorPageExtension = errorPageMap.get(errorCode);
            if (errorPageExtension != null) {
                if (nonDefaultExtension.getParameter("override") == null) {
                    throw new RuntimeException(nonDefaultExtension.getUniqueId() + " requires an override parameter to override " + errorPageExtension.getUniqueId());
                }
                logger.debug(nonDefaultExtension.getUniqueId() + " overrides " + errorPageExtension.getUniqueId());
            }
            errorPageMap.put(errorCode, nonDefaultExtension);
        }
        for (Extension extension : errorPageMap.values()) {
            Parameter errorCode = extension.getParameter("error-code");
            String errorCodeString = "";
            if (errorCode != null) {
                errorCodeString = errorCode.valueAsString();
            }
            String exceptionTypeString = "";
            Parameter exceptionType = extension.getParameter("exception-type");
            if (exceptionType != null) {
                exceptionTypeString = exceptionType.valueAsString();
            }
            Parameter location = extension.getParameter("location");
            String locationString = "";
            if (location != null) {
                locationString = location.valueAsString();
            }
            addErrorPage(errorCodeString, exceptionTypeString, locationString, xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected Set<Extension> getErrorPageExtensions(PluginDescriptor warPluginDescriptor, boolean defaults) {
        Set<Extension> extensions = new HashSet<Extension>();
        PluginDescriptor abstractWARPluginDescriptor = getAbstractWARPluginDescriptor();
        ExtensionPoint errorPageExtensionPoint = abstractWARPluginDescriptor.getExtensionPoint("error-page");
        if (errorPageExtensionPoint == null) {
            throw new RuntimeException(abstractWARPluginDescriptor.getUniqueId() + " does not have an error-page extension point");
        }
        for (Extension extension : errorPageExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor errorPagePluginDescriptor = extension.getDeclaringPluginDescriptor();
            String targetPluginId = extension.getParameter("target-plugin-id").valueAsString();
            if (targetPluginId == null) {
                throw new RuntimeException("A value for target-plugin-id on " + extension.getUniqueId() + " must be supplied");
            }
            if (defaults) {
                if (errorPagePluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                    extensions.add(extension);
                }
            } else {
                if (!errorPagePluginDescriptor.getId().equals(warPluginDescriptor.getId()) && targetPluginId.equals(warPluginDescriptor.getId())) {
                    extensions.add(extension);
                }
            }
        }
        List<Extension> duplicates = getDuplicateErrorPages(extensions);
        if (!duplicates.isEmpty()) {
            StringBuffer buff = new StringBuffer();
            for (Extension extension : duplicates) {
                buff.append(extension.getUniqueId() + " ");
            }
            throw new RuntimeException("ErrorPages with duplicate codes detected: " + buff.toString());
        }
        return extensions;
    }

    protected List<Extension> getDuplicateErrorPages(Set<Extension> errorExtensions) {
        List<Extension> duplicates = new ArrayList<Extension>();
        Map<String, List<Extension>> extensionMap = new HashMap<String, List<Extension>>();
        for (Extension extension : errorExtensions) {
            Parameter errorCodeParameter = extension.getParameter("error-code");
            if (errorCodeParameter == null || errorCodeParameter.valueAsString() == null || errorCodeParameter.valueAsString().trim().isEmpty()) {
                throw new RuntimeException("A value for error-code on " + extension.getUniqueId() + " must be supplied");
            }
            Parameter locationParameter = extension.getParameter("location");
            if (locationParameter == null || locationParameter.valueAsString() == null || locationParameter.valueAsString().trim().isEmpty()) {
                throw new RuntimeException("A value for location on " + extension.getUniqueId() + " must be supplied");
            }
            String errorCode = errorCodeParameter.valueAsString();
            List<Extension> extensions = extensionMap.get(errorCode);
            if (extensions == null) {
                extensions = new ArrayList<Extension>();
                extensionMap.put(errorCode, extensions);
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

    protected void addErrorPage(String errorCode, String exceptionType, String location, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:error-page[tp:error-code = '" + errorCode + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("error-page");
        xmlStreamWriter.writeCharacters("\n");
        if (errorCode.length() > 0) {
            xmlStreamWriter.writeStartElement("error-code");
            xmlStreamWriter.writeCharacters(errorCode);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        if (exceptionType.length() > 0) {
            xmlStreamWriter.writeStartElement("exception-type");
            xmlStreamWriter.writeCharacters(exceptionType);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        if (location.length() > 0) {
            xmlStreamWriter.writeStartElement("location");
            xmlStreamWriter.writeCharacters(location);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addLoginConfigTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addLoginConfigs");
        xmlStreamWriter.writeCharacters("\n");
        for (Extension extension : getExtensions(pluginDescriptor, "login-config")) {
            Parameter authMethod = extension.getParameter("auth-method");
            String authMethodString = "";
            if (authMethod != null) {
                authMethodString = authMethod.valueAsString();
            }
            addLoginConfig(authMethodString, extension.getParameter("form-login-page").valueAsString(), extension.getParameter("form-error-page").valueAsString(), xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addLoginConfig(String authMethod, String formLoginPage, String formErrorPage, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(tp:login-config[tp:form-login-config/tp:form-login-page = '" + formLoginPage + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("login-config");
        xmlStreamWriter.writeCharacters("\n");
        if (authMethod.length() > 0) {
            xmlStreamWriter.writeStartElement("auth-method");
            xmlStreamWriter.writeCharacters(authMethod);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeStartElement("form-login-config");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("form-login-page");
        xmlStreamWriter.writeCharacters(formLoginPage);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("form-error-page");
        xmlStreamWriter.writeCharacters(formErrorPage);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
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
