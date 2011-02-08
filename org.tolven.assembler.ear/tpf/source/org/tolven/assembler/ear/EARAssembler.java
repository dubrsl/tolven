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
package org.tolven.assembler.ear;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

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
import org.tolven.tools.ant.TolvenJar;

/**
 * This plugin is the main EAR assembler. It uses a number of subassemblers to complete its task
 * 
 * @author Joseph Isaac
 *
 */
public class EARAssembler extends TolvenCommandPlugin {

    public static final String ATTRIBUTE_TEMPLATE_APPLICATIONXML = "template-applicationxml";
    public static final String EXTENSIONPOINT_JAVAMODULE = "javaModule";
    public static final String EXTENSIONPOINT_JAVAMODULE_PRODUCT = "javaModuleProduct";
    public static final String EXTENSIONPOINT_JAVAMODULE_PRODUCT_PLUGIN = "javaModuleProductPlugin";
    public static final String EXTENSIONPOINT_CONNECTORMODULE_PRODUCT = "connectorModuleProduct";
    public static final String EXTENSIONPOINT_EJBMODULE = "ejbModule";
    public static final String EXTENSIONPOINT_EJBMODULE_PRODUCT = "ejbModuleProduct";
    public static final String EXTENSIONPOINT_WARMODULE = "warModule";
    public static final String EXTENSIONPOINT_WARMODULE_PRODUCT = "warModuleProduct";
    public static final String EXTENSIONPOINT_LOCALEMODULE = "localeModule";
    public static final String EXTENSIONPOINT_LOCALEMODULE_PRODUCT = "localeModuleProduct";
    public static final String EXTENSIONPOINT_SECURITY_ROLE = "security-role";
    public static final String EXTENSIONPOINT_METAINF = "META-INF";
    public static final String EXTENSIONPOINT_ABSTRACT_EAR = "abstractEAR";

    public static final String CMD_LINE_EAR_PLUGIN_OPTION = "earPlugin";
    public static final String CMD_LINE_EAR_FILE_OPTION = "earFile";
    public static final String CMD_LINE_DESTDIR_OPTION = "destDir";

    private Logger logger = Logger.getLogger(EARAssembler.class);

    protected PluginDescriptor getAbstractEARPluginDescriptor() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_ABSTRACT_EAR);
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
        String earPluginId = commandLine.getOptionValue(CMD_LINE_EAR_PLUGIN_OPTION);
        String earFilename = commandLine.getOptionValue(CMD_LINE_EAR_FILE_OPTION);
        String destDirname = commandLine.getOptionValue(CMD_LINE_DESTDIR_OPTION);
        if (destDirname == null) {
            destDirname = new File(getStageDir(), earPluginId).getPath();
        }
        File deployDir = new File(destDirname);
        deployDir.mkdirs();
        PluginDescriptor earPluginDescriptor = getManager().getRegistry().getPluginDescriptor(earPluginId);
        executeRequiredPlugins(earPluginDescriptor);
        assembleEAR(earPluginDescriptor, earFilename, deployDir);
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
        Option warPluginOption = new Option(CMD_LINE_EAR_PLUGIN_OPTION, CMD_LINE_EAR_PLUGIN_OPTION, true, "ear plugin");
        warPluginOption.setRequired(true);
        cmdLineOptions.addOption(warPluginOption);
        Option webURIPluginOption = new Option(CMD_LINE_EAR_FILE_OPTION, CMD_LINE_EAR_FILE_OPTION, true, "ear filename");
        webURIPluginOption.setRequired(true);
        cmdLineOptions.addOption(webURIPluginOption);
        Option destDirPluginOption = new Option(CMD_LINE_DESTDIR_OPTION, CMD_LINE_DESTDIR_OPTION, true, "destination directory");
        cmdLineOptions.addOption(destDirPluginOption);
        return cmdLineOptions;
    }

    protected void executeRequiredPlugins(PluginDescriptor earPluginDescriptor) throws Exception {
        String[] args = new String[0];
        execute("org.tolven.assembler.localemodule", args);
        execute("org.tolven.assembler.javamodule", args);
        executeRequiredRarModulePlugins(earPluginDescriptor);
        execute("org.tolven.assembler.ejbmodule", args);
        executeRequiredWarModulePlugins(earPluginDescriptor);
    }

    protected void executeRequiredRarModulePlugins(PluginDescriptor earPluginDescriptor) throws Exception {
        ExtensionPoint abstractExtensionPoint = getAbstractEARPluginDescriptor().getExtensionPoint(EXTENSIONPOINT_CONNECTORMODULE_PRODUCT);
        for (ExtensionPoint rarModuleExtensionPoint : abstractExtensionPoint.getDescendants()) {
            if (rarModuleExtensionPoint.getDeclaringPluginDescriptor().getId().equals(earPluginDescriptor.getId())) {
                File destDir = new File(getPluginTmpDir(), earPluginDescriptor.getId());
                destDir.mkdirs();
                for (Extension rarModuleExtension : rarModuleExtensionPoint.getConnectedExtensions()) {
                    PluginDescriptor rarPluginDescriptor = rarModuleExtension.getDeclaringPluginDescriptor();
                    String rarFilename = rarModuleExtension.getParameter("rar").valueAsString();
                    File sourceRAR = getFilePath(rarPluginDescriptor, rarFilename);
                    logger.debug("Copy " + sourceRAR.getPath() + " to " + destDir.getPath());
                    FileUtils.copyFileToDirectory(sourceRAR, destDir);
                }
            }
        }
    }

    protected void executeRequiredWarModulePlugins(PluginDescriptor earPluginDescriptor) throws Exception {
        ExtensionPoint abstractExtensionPoint = getAbstractEARPluginDescriptor().getExtensionPoint(EXTENSIONPOINT_WARMODULE);
        File warDestDir = new File(getPluginTmpDir(), earPluginDescriptor.getId());
        warDestDir.mkdirs();
        for (ExtensionPoint warModuleExtensionPoint : abstractExtensionPoint.getDescendants()) {
            if (warModuleExtensionPoint.getDeclaringPluginDescriptor().getId().equals(earPluginDescriptor.getId())) {
                for (Extension warModuleExtension : warModuleExtensionPoint.getConnectedExtensions()) {
                    PluginDescriptor warPluginDescriptor = warModuleExtension.getDeclaringPluginDescriptor();
                    String webURI = warModuleExtension.getParameter("web-uri").valueAsString();
                    String argString = "-warPlugin " + warPluginDescriptor.getId() + " -webURI " + webURI + " -destDir " + warDestDir.getPath();
                    execute("org.tolven.assembler.war", argString.split(" "));
                }
            }
        }
    }

    protected void assembleEAR(PluginDescriptor earPluginDescriptor, String earFilename, File destDir) throws IOException, XMLStreamException {
        File myEARPluginDir = new File(getPluginTmpDir(), earPluginDescriptor.getId());
        File sourceXMLFile = new File(myEARPluginDir, "META-INF/application.xml");
        String templateFilename = getDescriptor().getAttribute(ATTRIBUTE_TEMPLATE_APPLICATIONXML).getValue();
        File templateFile = getFilePath(templateFilename);
        if (!templateFile.exists()) {
            throw new RuntimeException("Could not locate: '" + templateFile.getPath() + "' in " + getDescriptor().getId());
        }
        sourceXMLFile.getParentFile().mkdirs();
        logger.debug("Copy " + templateFile + " to " + sourceXMLFile);
        FileUtils.copyFile(templateFile, sourceXMLFile);
        assemblerMetaInf(earPluginDescriptor);
        StringBuffer originalXML = new StringBuffer();
        originalXML.append(FileUtils.readFileToString(sourceXMLFile));
        String xslt = getXSLT(earPluginDescriptor);
        File applicationxmlXSLT = new File(getPluginTmpDir(), "applicationxml-xslt.xml");
        logger.debug("Write application.xml XSLT to " + applicationxmlXSLT.getPath());
        FileUtils.writeStringToFile(applicationxmlXSLT, xslt);
        String translatedXMLString = getTranslatedXML(originalXML.toString(), xslt);
        sourceXMLFile.getParentFile().mkdirs();
        logger.debug("Write translated application.xml to " + sourceXMLFile.getPath());
        FileUtils.writeStringToFile(sourceXMLFile, translatedXMLString);
        deployToEARConsumers(earPluginDescriptor, earFilename, destDir);
    }

    protected void deployToEARConsumers(PluginDescriptor earPluginDescriptor, String earFilename, File destEARDir) {
        File myEARPluginDir = new File(getPluginTmpDir(), earPluginDescriptor.getId());
        File earFile = new File(destEARDir, earFilename);
        earFile.getParentFile().mkdirs();
        logger.debug("Jar " + myEARPluginDir.getPath() + " to " + earFile.getPath());
        TolvenJar.jar(myEARPluginDir, earFile);
    }

    protected void assemblerMetaInf(PluginDescriptor earPluginDescriptor) {
        for (Extension metaInfExtension : earPluginDescriptor.getExtensionPoint(EXTENSIONPOINT_METAINF).getConnectedExtensions()) {
            Parameter metaInfParameter = metaInfExtension.getParameter("dir");
            if (metaInfParameter == null || metaInfParameter.valueAsString().trim().length() == 0) {
                throw new RuntimeException(metaInfExtension.getUniqueId() + " must supply a value for the dir parameter");
            }
            PluginDescriptor metaInfPluginDescriptor = metaInfExtension.getDeclaringPluginDescriptor();
            File metaInfSourceDir = getFilePath(metaInfPluginDescriptor, metaInfParameter.valueAsString());
            File metaInfDestDir = new File(getPluginTmpDir(), earPluginDescriptor.getId() + "/META-INF");
            logger.debug("Copy " + metaInfSourceDir.getPath() + " to " + metaInfDestDir.getPath());
            try {
                FileUtils.copyDirectory(metaInfSourceDir, metaInfDestDir);
            } catch (IOException ex) {
                throw new RuntimeException("Could not copy meta-inf files from " + metaInfSourceDir.getPath() + " to " + metaInfDestDir.getPath(), ex);
            }
        }
    }

    protected String getXSLT(PluginDescriptor pluginDescriptor) throws XMLStreamException, IOException {
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
            addJavaLibraryTemplates(pluginDescriptor);
            addConnectorModuleTemplates(pluginDescriptor, xmlStreamWriter);
            addEJBModuleTemplates(pluginDescriptor, xmlStreamWriter);
            addWARModuleTemplates(pluginDescriptor, xmlStreamWriter);
            addSecurityRoleTemplates(pluginDescriptor, xmlStreamWriter);
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

    protected void addRootTemplate(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("match", "tp:application");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("application");
        xmlStreamWriter.writeAttribute("version", "{@version}");
        xmlStreamWriter.writeAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
        xmlStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlStreamWriter.writeAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_5.xsd");
        xmlStreamWriter.writeCharacters("\n");
        addRootDisplayName(xmlStreamWriter);
        addRootConnectorModuleSelects(xmlStreamWriter);
        addRootEJBModuleSelects(xmlStreamWriter);
        addRootWEBModuleSelects(xmlStreamWriter);
        addRootSecurityRoleSelects(xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootDisplayName(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(display-name) = 0");
        xmlStreamWriter.writeStartElement("xsl:element");
        xmlStreamWriter.writeAttribute("name", "display-name");
        xmlStreamWriter.writeCharacters("Tolven Application");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootConnectorModuleSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:module/tp:connector");
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
        xmlStreamWriter.writeAttribute("name", "addConnectorModules");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootEJBModuleSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:module/tp:ejb");
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
        xmlStreamWriter.writeAttribute("name", "addEJBModules");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootWEBModuleSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:module/tp:web");
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
        xmlStreamWriter.writeAttribute("name", "addWEBModules");
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

    protected void addJavaLibraryTemplates(PluginDescriptor pluginDescriptor) throws IOException {
        assembleLocaleModules(pluginDescriptor);
        assembleLocaleModuleProducts(pluginDescriptor);
        assembleJavaLibraries(pluginDescriptor);
        assembleJavaLibraryProducts(pluginDescriptor);
        assembleJavaLibraryProductPlugins(pluginDescriptor);
    }

    protected void assembleLocaleModules(PluginDescriptor pluginDescriptor) throws IOException {
        for (Extension localeModuleExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_LOCALEMODULE).getConnectedExtensions()) {
            File localeModulePluginTmpDir = getPluginTmpDir(localeModuleExtension.getDeclaringPluginDescriptor());
            String jarName = localeModuleExtension.getParameter("jar").valueAsString();
            File jarSourceFile = new File(localeModulePluginTmpDir, jarName);
            File jarDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/lib/" + jarSourceFile.getName());
            jarDestinationFile.getParentFile().mkdirs();
            logger.debug("Copy " + jarSourceFile.getPath() + " to " + jarDestinationFile.getPath());
            FileUtils.copyFile(jarSourceFile, jarDestinationFile);
        }
    }

    protected void assembleLocaleModuleProducts(PluginDescriptor pluginDescriptor) throws IOException {
        for (Extension localeModuleProductExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_LOCALEMODULE_PRODUCT).getConnectedExtensions()) {
            String jarName = localeModuleProductExtension.getParameter("jar").valueAsString();
            File jarSourceFile = getFilePath(localeModuleProductExtension.getDeclaringPluginDescriptor(), jarName);
            File jarDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/lib/" + jarSourceFile.getName());
            jarDestinationFile.getParentFile().mkdirs();
            logger.debug("Copy " + jarSourceFile.getPath() + " to " + jarDestinationFile.getPath());
            FileUtils.copyFile(jarSourceFile, jarDestinationFile);
        }
    }

    protected void assembleJavaLibraries(PluginDescriptor pluginDescriptor) throws IOException {
        for (Extension javaModuleExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_JAVAMODULE).getConnectedExtensions()) {
            File javaModulePluginTmpDir = getPluginTmpDir(javaModuleExtension.getDeclaringPluginDescriptor());
            String jarName = javaModuleExtension.getParameter("jar").valueAsString();
            File jarSourceFile = new File(javaModulePluginTmpDir, jarName);
            File jarDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/lib/" + jarSourceFile.getName());
            jarDestinationFile.getParentFile().mkdirs();
            logger.debug("Copy " + jarSourceFile.getPath() + " to " + jarDestinationFile.getPath());
            FileUtils.copyFile(jarSourceFile, jarDestinationFile);
        }
    }

    protected void assembleJavaLibraryProducts(PluginDescriptor pluginDescriptor) throws IOException {
        for (Extension javaModuleProductExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_JAVAMODULE_PRODUCT).getConnectedExtensions()) {
            if (javaModuleProductExtension.getParameter("jar") != null) {
                String jarName = javaModuleProductExtension.getParameter("jar").valueAsString();
                File jarSourceFile = getFilePath(javaModuleProductExtension.getDeclaringPluginDescriptor(), jarName);
                File jarDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/lib/" + jarSourceFile.getName());
                jarDestinationFile.getParentFile().mkdirs();
                logger.debug("Copy " + jarSourceFile.getPath() + " to " + jarDestinationFile.getPath());
                FileUtils.copyFile(jarSourceFile, jarDestinationFile);
            }
            if (javaModuleProductExtension.getParameter("dir") != null) {
                String dirname = javaModuleProductExtension.getParameter("dir").valueAsString();
                File sourceDir = getFilePath(javaModuleProductExtension.getDeclaringPluginDescriptor(), dirname);
                for (Object obj : FileUtils.listFiles(sourceDir, new String[] { "jar" }, false)) {
                    File jarSourceFile = (File) obj;
                    File jarDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/lib/" + jarSourceFile.getName());
                    jarDestinationFile.getParentFile().mkdirs();
                    logger.debug("Copy " + jarSourceFile.getPath() + " to " + jarDestinationFile.getPath());
                    FileUtils.copyFile(jarSourceFile, jarDestinationFile);
                }
            }
        }
    }

    protected void assembleJavaLibraryProductPlugins(PluginDescriptor pluginDescriptor) throws IOException {
        for (Extension javaModuleProductPluginExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_JAVAMODULE_PRODUCT_PLUGIN).getConnectedExtensions()) {
            String targetPluginId = javaModuleProductPluginExtension.getParameter("target-plugin-id").valueAsString();
            PluginDescriptor targetPluginDescriptor = getManager().getRegistry().getPluginDescriptor(targetPluginId);
            Parameter extensionPointParameter = javaModuleProductPluginExtension.getParameter("extension-point");
            if (extensionPointParameter == null || extensionPointParameter.valueAsString() == null) {
                throw new RuntimeException(javaModuleProductPluginExtension + " must have a parameter called extension-point with a value");
            }
            String extensionPointId = extensionPointParameter.valueAsString();
            ExtensionPoint targetExtensionPoint = targetPluginDescriptor.getExtensionPoint(extensionPointId);
            if (targetExtensionPoint == null) {
                throw new RuntimeException(targetPluginDescriptor + " must have an extension point " + extensionPointId);
            }
            for (Parameter libParameter : extensionPointParameter.getSubParameters("name")) {
                String defaultValueParameter = libParameter.valueAsString();
                String defaultValue = targetExtensionPoint.getParameterDefinition(defaultValueParameter).getDefaultValue();
                if (defaultValue == null) {
                    throw new RuntimeException(targetExtensionPoint + " must have a parameter-def " + defaultValueParameter);
                }
                File jarSourceFile = getFilePath(targetPluginDescriptor, defaultValue);
                File jarDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/lib/" + jarSourceFile.getName());
                jarDestinationFile.getParentFile().mkdirs();
                logger.debug("Copy " + jarSourceFile.getPath() + " to " + jarDestinationFile.getPath());
                FileUtils.copyFile(jarSourceFile, jarDestinationFile);
            }
        }
    }

    protected void addConnectorModuleTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addConnectorModules");
        xmlStreamWriter.writeCharacters("\n");
        assembleConnectorModules(pluginDescriptor, xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void assembleConnectorModules(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        for (Extension connectorModuleExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_CONNECTORMODULE_PRODUCT).getConnectedExtensions()) {
            String rarName = connectorModuleExtension.getParameter("rar").valueAsString();
            addConnectorModule(rarName, xmlStreamWriter);
        }
    }

    protected void addConnectorModule(String rarFilename, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(module/connector[text() = '" + rarFilename + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("module");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("connector");
        xmlStreamWriter.writeCharacters(rarFilename);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addEJBModuleTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException, IOException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addEJBModules");
        xmlStreamWriter.writeCharacters("\n");
        assembleEJBModules(pluginDescriptor, xmlStreamWriter);
        assembleEJBModuleProducts(pluginDescriptor, xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void assembleEJBModules(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws IOException, XMLStreamException {
        for (Extension ejbModuleExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_EJBMODULE).getConnectedExtensions()) {
            File ejbModulePluginTmpDir = getPluginTmpDir(ejbModuleExtension.getDeclaringPluginDescriptor());
            String jarName = ejbModuleExtension.getParameter("jar").valueAsString();
            File jarSourceFile = new File(ejbModulePluginTmpDir, jarName);
            File jarDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/" + jarSourceFile.getName());
            jarDestinationFile.getParentFile().mkdirs();
            logger.debug("Copy " + jarSourceFile.getPath() + " to " + jarDestinationFile.getPath());
            FileUtils.copyFile(jarSourceFile, jarDestinationFile);
            addEJBModule(jarName, xmlStreamWriter);
        }
    }

    protected void assembleEJBModuleProducts(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws IOException, XMLStreamException {
        for (Extension ejbModuleProductExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_EJBMODULE_PRODUCT).getConnectedExtensions()) {
            String ejbModuleJARName = ejbModuleProductExtension.getParameter("jar").valueAsString();
            File jarSourceFile = getFilePath(ejbModuleProductExtension.getDeclaringPluginDescriptor(), ejbModuleJARName);
            File jarDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/" + ejbModuleJARName);
            jarDestinationFile.getParentFile().mkdirs();
            logger.debug("Copy " + jarSourceFile.getPath() + " to " + jarDestinationFile.getPath());
            FileUtils.copyFile(jarSourceFile, jarDestinationFile);
            addEJBModule(ejbModuleJARName, xmlStreamWriter);
        }
    }

    protected void addEJBModule(String jarFilename, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(module/ejb[text() = '" + jarFilename + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("module");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("ejb");
        xmlStreamWriter.writeCharacters(jarFilename);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addWARModuleTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException, IOException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addWEBModules");
        xmlStreamWriter.writeCharacters("\n");
        assembleWARModules(pluginDescriptor, xmlStreamWriter);
        assembleWARModuleProducts(pluginDescriptor, xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void assembleWARModules(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        for (Extension warModuleExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_WARMODULE).getConnectedExtensions()) {
            String webURI = warModuleExtension.getParameter("web-uri").valueAsString();
            String contextRoot = warModuleExtension.getParameter("context-root").valueAsString();
            addWebModules(webURI, contextRoot, xmlStreamWriter);
        }
    }

    protected void assembleWARModuleProducts(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws IOException, XMLStreamException {
        for (Extension warModuleProductExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_WARMODULE_PRODUCT).getConnectedExtensions()) {
            String webURI = warModuleProductExtension.getParameter("web-uri").valueAsString();
            File warSourceFile = getFilePath(warModuleProductExtension.getDeclaringPluginDescriptor(), webURI);
            File warDestinationFile = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/" + warSourceFile.getName());
            warDestinationFile.getParentFile().mkdirs();
            logger.debug("Copy " + warSourceFile.getPath() + " to " + warDestinationFile.getPath());
            FileUtils.copyFile(warSourceFile, warDestinationFile);
            String contextRoot = warModuleProductExtension.getParameter("context-root").valueAsString();
            addWebModules(webURI, contextRoot, xmlStreamWriter);
        }
    }

    protected void addWebModules(String webURI, String contextRoot, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(module/web-uri[text() = '" + webURI + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("module");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("web");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("web-uri");
        xmlStreamWriter.writeCharacters(webURI);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("context-root");
        xmlStreamWriter.writeCharacters(contextRoot);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addSecurityRoleTemplates(PluginDescriptor pluginDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addSecurityRoles");
        xmlStreamWriter.writeCharacters("\n");
        for (Extension securityRoleExtension : pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_SECURITY_ROLE).getConnectedExtensions()) {
            String description = null;
            if (securityRoleExtension.getParameter("description") != null) {
                description = securityRoleExtension.getParameter("description").valueAsString();
            }
            String roleName = securityRoleExtension.getParameter("role-name").valueAsString();
            addSecurityRoles(description, roleName, xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addSecurityRoles(String description, String roleName, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:if");
        xmlStreamWriter.writeAttribute("test", "count(security-role/role-name[text() = '" + roleName + "']) = 0");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("security-role");
        xmlStreamWriter.writeCharacters("\n");
        if (description != null) {
            xmlStreamWriter.writeStartElement("description");
            xmlStreamWriter.writeCharacters(description);
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
        xmlStreamWriter.writeStartElement("role-name");
        xmlStreamWriter.writeCharacters(roleName);
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
