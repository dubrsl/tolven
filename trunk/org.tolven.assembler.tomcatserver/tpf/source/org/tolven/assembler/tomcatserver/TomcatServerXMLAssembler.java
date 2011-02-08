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
package org.tolven.assembler.tomcatserver;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

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
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin assmebles the tomcat server.xml by collecting information about the connectors.
 * 
 * @author Joseph Isaac
 *
 */
public class TomcatServerXMLAssembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_CONNECTOR = "connector";
    public static final String EXTENSION_TOMCATSERVERXML_ASSEMBLY = "tomcatserverxmlAssembly";

    public static final String CMD_LINE_SOURCE_SERVERXML_FILE_OPTION = "source";
    public static final String CMD_LINE_DEST_SERVERXML_FILE_OPTION = "dest";

    private Logger logger = Logger.getLogger(TomcatServerXMLAssembler.class);

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
        String sourceFilename = commandLine.getOptionValue(CMD_LINE_SOURCE_SERVERXML_FILE_OPTION);
        File sourceFile = new File(sourceFilename);
        if (!sourceFile.exists()) {
            throw new RuntimeException("server.xml source file: " + sourceFile.getPath() + " does not exist");
        }
        File[] tmpFiles = getPluginTmpDir().listFiles();
        if (tmpFiles != null && tmpFiles.length > 0) {
            return;
        }
        Extension extension = getDescriptor().getExtension(EXTENSION_TOMCATSERVERXML_ASSEMBLY);
        String connectorPluginId = extension.getExtendedPluginId();
        PluginDescriptor connectorPluginDescriptor = getManager().getRegistry().getPluginDescriptor(connectorPluginId);
        ExtensionPoint tomcatserverxmlAssemblyExtensionPoint = getManager().getRegistry().getExtensionPoint(connectorPluginDescriptor.getId(), extension.getExtendedPointId());
        String connectorExtensionPointId = tomcatserverxmlAssemblyExtensionPoint.getParameterDefinition("connectorDefinition").getDefaultValue();
        ExtensionPoint connectorExtensionPoint = connectorPluginDescriptor.getExtensionPoint(connectorExtensionPointId);
        String destFilename = commandLine.getOptionValue(CMD_LINE_DEST_SERVERXML_FILE_OPTION);
        File destFile = new File(destFilename);
        assembleServerxml(connectorExtensionPoint, sourceFile, destFile);
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
        Option option = new Option(CMD_LINE_SOURCE_SERVERXML_FILE_OPTION, CMD_LINE_SOURCE_SERVERXML_FILE_OPTION, true, "source server.xml file");
        option.setRequired(true);
        cmdLineOptions.addOption(option);
        option = new Option(CMD_LINE_DEST_SERVERXML_FILE_OPTION, CMD_LINE_DEST_SERVERXML_FILE_OPTION, true, "destination server.xml file");
        option.setRequired(true);
        cmdLineOptions.addOption(option);
        return cmdLineOptions;
    }

    protected void assembleServerxml(ExtensionPoint connectorExtensionPoint, File sourceFile, File destFile) throws IOException, XMLStreamException {
        StringBuffer originalXML = new StringBuffer();
        logger.debug("Read " + sourceFile.getPath());
        originalXML.append(FileUtils.readFileToString(sourceFile));
        String xslt = getXSLT(connectorExtensionPoint);
        File xsltFile = new File(getPluginTmpDir(), "serverxml-xslt.xml");
        logger.debug("Write xslt file " + xsltFile.getPath());
        FileUtils.writeStringToFile(xsltFile, xslt);
        String translatedXMLString = getTranslatedXML(originalXML.toString(), xslt);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        logger.debug("Write translated server.xml file to " + destFile);
        FileUtils.writeStringToFile(destFile, translatedXMLString);
    }

    protected String getXSLT(ExtensionPoint connectorExtensionPoint) throws XMLStreamException, IOException {
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
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("xsl:output");
            xmlStreamWriter.writeAttribute("method", "xml");
            xmlStreamWriter.writeAttribute("indent", "yes");
            xmlStreamWriter.writeAttribute("encoding", "UTF-8");
            xmlStreamWriter.writeAttribute("omit-xml-declaration", "no");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            addMainTemplate(xmlStreamWriter);
            addServiceTemplate(connectorExtensionPoint, xmlStreamWriter);
            xmlStreamWriter.writeEndDocument();
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

    protected void addServiceTemplate(ExtensionPoint connectorExtensionPoint, XMLStreamWriter xmlStreamWriter) throws XMLStreamException, IOException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("match", "Service");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:element");
        xmlStreamWriter.writeAttribute("name", "{name()}");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "@*");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:attribute");
        xmlStreamWriter.writeAttribute("name", "{name()}");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", ".");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:value-of");
        xmlStreamWriter.writeAttribute("select", "text()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        Extension extension = getDescriptor().getExtension(EXTENSION_TOMCATSERVERXML_ASSEMBLY);
        String connectorPluginId = extension.getExtendedPluginId();
        PluginDescriptor connectorPluginDescriptor = getManager().getRegistry().getPluginDescriptor(connectorPluginId);
        ExtensionPoint tomcatserverxmlAssemblyExtensionPoint = getManager().getRegistry().getExtensionPoint(connectorPluginDescriptor.getId(), extension.getExtendedPointId());
        String webserverCredentialDirname = (String) evaluate(tomcatserverxmlAssemblyExtensionPoint.getParameterDefinition("webserverCredentialDirectory").getDefaultValue(), tomcatserverxmlAssemblyExtensionPoint.getDeclaringPluginDescriptor());
        File webserverCredentialDir = new File(webserverCredentialDirname);
        if (!isAbsoluteFilePath(webserverCredentialDir)) {
            throw new RuntimeException("The webserver credential must be an absolute path " + webserverCredentialDir.getPath());
        }
        webserverCredentialDir.mkdirs();
        logger.debug("WebServer credential directory will be: " + webserverCredentialDir.getPath());
        String webserverDeploymentDirname = (String) evaluate(tomcatserverxmlAssemblyExtensionPoint.getParameterDefinition("webserverDeploymentDirectory").getDefaultValue(), tomcatserverxmlAssemblyExtensionPoint.getDeclaringPluginDescriptor());
        File webserverDeploymentDir = new File(webserverDeploymentDirname);
        if (!isAbsoluteFilePath(webserverDeploymentDir)) {
            throw new RuntimeException("The webserver deployment directory must be an absolute path " + webserverDeploymentDir.getPath());
        }
        logger.debug("WebServer deployment directory in the server.xml will be: " + webserverDeploymentDir.getPath());
        for (Extension connectorExtension : connectorExtensionPoint.getConnectedExtensions()) {
            addConnectorTemplate(connectorExtension, webserverCredentialDir, webserverDeploymentDir, xmlStreamWriter);
        }
        xmlStreamWriter.writeStartElement("xsl:copy-of");
        xmlStreamWriter.writeAttribute("select", "*");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:apply-templates");
        xmlStreamWriter.writeAttribute("select", "* | @* | text() | comment()");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addConnectorTemplate(Extension connectorExtension, File webserverCredentialDir, File webserverDeploymentDir, XMLStreamWriter xmlStreamWriter) throws XMLStreamException, IOException {
        Properties props = getConnectorProperties(connectorExtension, webserverCredentialDir, webserverDeploymentDir);
        if (!props.isEmpty()) {
            List<Object> objs = new ArrayList<Object>();
            objs.addAll(props.keySet());
            Comparator<Object> comparator = new Comparator<Object>() {
                public int compare(Object obj1, Object obj2) {
                    return (obj1.toString().compareTo(obj2.toString()));
                };
            };
            Collections.sort(objs, comparator);
            xmlStreamWriter.writeStartElement("Connector");
            for (Object obj : objs) {
                String key = (String) obj;
                xmlStreamWriter.writeAttribute(key, props.getProperty(key));
            }
            xmlStreamWriter.writeEndElement();
        }
    }

    protected Properties getConnectorProperties(Extension connectorExtension, File webserverCredentialDir, File webserverDeploymentDir) throws IOException {
        Properties props = new Properties();
        PluginDescriptor pluginDescriptor = connectorExtension.getDeclaringPluginDescriptor();
        String evaluatedProperty = null;
        if (connectorExtension.getParameter("port") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("port").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("port", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("address") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("address").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("address", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("maxThreads") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("maxThreads").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("maxThreads", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("maxHttpHeaderSize") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("maxHttpHeaderSize").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("maxHttpHeaderSize", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("emptySessionPath") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("emptySessionPath").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("emptySessionPath", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("protocol") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("protocol").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("protocol", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("enableLookups") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("enableLookups").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("enableLookups", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("redirectPort") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("redirectPort").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("redirectPort", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("acceptCount") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("acceptCount").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("acceptCount", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("connectionTimeout") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("connectionTimeout").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("connectionTimeout", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("disableUploadTimeout") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("disableUploadTimeout").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("disableUploadTimeout", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("SSLEnabled") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("SSLEnabled").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("SSLEnabled", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("scheme") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("scheme").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("scheme", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("secure") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("secure").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("secure", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("clientAuth") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("clientAuth").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("clientAuth", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("sslProtocol") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("sslProtocol").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("sslProtocol", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("keystoreFile") != null) {
            PluginDescriptor connectorPluginDescriptor = connectorExtension.getDeclaringPluginDescriptor();
            String sourceKeyStoreFilename = (String) evaluate(connectorExtension.getParameter("keystoreFile").valueAsString(), pluginDescriptor);
            if (sourceKeyStoreFilename != null) {
                File sourceKeyStoreFile = null;
                if (new File(sourceKeyStoreFilename).getPath().equals(new File(sourceKeyStoreFilename).getAbsolutePath())) {
                    sourceKeyStoreFile = new File(sourceKeyStoreFilename);
                } else {
                    sourceKeyStoreFile = getFilePath(connectorPluginDescriptor, sourceKeyStoreFilename);
                }
                logger.debug("Copy " + sourceKeyStoreFile.getPath() + " to " + webserverCredentialDir.getPath());
                FileUtils.copyFileToDirectory(sourceKeyStoreFile, webserverCredentialDir);
                File finalDeployedKeyStoreFile = new File(webserverDeploymentDir, sourceKeyStoreFile.getName());
                props.put("keystoreFile", escapeCurlyBraces(finalDeployedKeyStoreFile.getPath().replace("\\", "/")));
            }
        }
        if (connectorExtension.getParameter("keystoreType") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("keystoreType").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("keystoreType", escapeCurlyBraces(evaluatedProperty));
            }
        }
        if (connectorExtension.getParameter("keystorePass") != null) {
            evaluatedProperty = (String) evaluate(connectorExtension.getParameter("keystorePass").valueAsString(), pluginDescriptor);
            if (evaluatedProperty != null) {
                props.put("keystorePass", escapeCurlyBraces(evaluatedProperty));
            }
        }
        return props;
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
