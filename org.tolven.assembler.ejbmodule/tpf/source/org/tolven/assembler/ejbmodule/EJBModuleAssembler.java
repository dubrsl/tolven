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
package org.tolven.assembler.ejbmodule;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.tools.ant.TolvenJar;

/**
 * This plugin assmebles ejb modules for ear files
 * 
 * @author Joseph Isaac
 *
 */
public class EJBModuleAssembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_COMPONENT = "component";
    public static final String EXTENSIONPOINT_CLASSES = "classes";
    public static final String EXTENSIONPOINT_EJB_PROPERTY = "property";
    public static final String EXTENSIONPOINT_LDAP_PROPERTY_ACCESSOR = "ldapPropertyAccessor";
    public static final String EXTENSIONPOINT_ORM = "orm";
    public static final String EXTENSIONPOINT_PERSISTENCE_UNIT = "persistence-unit";
    public static final String EXTENSIONPOINT_PERSISTENCE_UNIT_METADATA = "persistence-unit-metadata";
    public static final String EXTENSIONPOINT_TABLE_GENERATOR = "table-generator";
    public static final String EXTENSIONPOINT_MAPPED_SUPERCLASS = "mapped-superclass";
    public static final String EXTENSIONPOINT_ENTITY = "entity";
    public static final String EXTENSIONPOINT_EMBEDDABLE = "embeddable";
    public static final String EXTENSIONPOINT_METAINF = "META-INF";

    private Logger logger = Logger.getLogger(EJBModuleAssembler.class);

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
        File myPluginTmpDir = getPluginTmpDir();
        File[] tmpFiles = getPluginTmpDir().listFiles();
        if (tmpFiles != null && tmpFiles.length > 0) {
            return;
        }
        ExtensionPoint ejbModuleExtensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_COMPONENT);
        if (ejbModuleExtensionPoint == null) {
            throw new RuntimeException(getDescriptor() + " requires extension point: " + EXTENSIONPOINT_COMPONENT);
        }
        for (Extension ejbModuleExtension : ejbModuleExtensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDescriptor = ejbModuleExtension.getDeclaringPluginDescriptor();
            assembleClasses(pluginDescriptor);
            assembleProperties(pluginDescriptor);
            assemblerMetaInf(pluginDescriptor);
            assembleEJBJARXML(pluginDescriptor);
            assembleORM(ejbModuleExtension);
            assemblePersistenceXML(ejbModuleExtension);
            File destinationEJBPluginDir = getPluginTmpDir(pluginDescriptor);
            String ejbFilename = ejbModuleExtension.getParameter("jar").valueAsString();
            File destinationEJBFile = new File(destinationEJBPluginDir, ejbFilename);
            destinationEJBFile.getParentFile().mkdirs();
            File myEJBPluginDir = new File(myPluginTmpDir, destinationEJBPluginDir.getName());
            logger.debug("Jar " + myEJBPluginDir.getPath() + " to " + destinationEJBFile.getPath());
            TolvenJar.jar(myEJBPluginDir, destinationEJBFile);
        }
    }

    protected void assembleClasses(PluginDescriptor pluginDescriptor) throws IOException {
        File myEJBPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        for (Extension classesExtension : getChildExtensions(EXTENSIONPOINT_CLASSES, pluginDescriptor)) {
            String dirname = classesExtension.getParameter("dir").valueAsString();
            File dir = getFilePath(classesExtension.getDeclaringPluginDescriptor(), dirname);
            logger.debug("Copy " + dir.getPath() + " to " + myEJBPluginDir.getPath());
            FileUtils.copyDirectory(dir, myEJBPluginDir, true);
        }
    }

    protected void assemblerMetaInf(PluginDescriptor pluginDescriptor) throws IOException {
        File myEJBMetaInfPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId() + "/" + "META-INF");
        for (Extension metaInfExtension : getChildExtensions(EXTENSIONPOINT_METAINF, pluginDescriptor)) {
            String dirname = metaInfExtension.getParameter("dir").valueAsString();
            File dir = getFilePath(metaInfExtension.getDeclaringPluginDescriptor(), dirname);
            logger.debug("Copy " + dir.getPath() + " to " + myEJBMetaInfPluginDir.getPath());
            FileUtils.copyDirectory(dir, myEJBMetaInfPluginDir, true);
        }
    }

    /**
     * Return true if the specified parameter value is in the override list
     */
    protected boolean isOverridden(PluginDescriptor pluginDescriptor, String classname, String name, String value) {
        for (Extension classesExtension : getChildExtensions(EXTENSIONPOINT_EJB_PROPERTY, pluginDescriptor)) {
            if (classname.equals(classesExtension.getParameter("class").valueAsString())) {
                if (name.equals(classesExtension.getParameter("name").valueAsString())) {
                    Parameter override = classesExtension.getParameter("override");
                    if (override != null && value.equals(override.valueAsString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Create the PropertyMapSet used by assembleProperties
     */
    protected Map<String, Map<String, Set<String>>> buildPropertyFileMapping(PluginDescriptor pluginDescriptor) {
        File myEJBPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        Map<String, Map<String, Set<String>>> propertyFileMapping = new HashMap<String, Map<String, Set<String>>>();
        for (Extension classesExtension : getChildExtensions(EXTENSIONPOINT_EJB_PROPERTY, pluginDescriptor)) {
            String classname = classesExtension.getParameter("class").valueAsString();
            File classFile = new File(myEJBPluginDir, classname.replace(".", "/") + ".class");
            if (!classFile.exists()) {
                throw new RuntimeException("Plugin " + classesExtension.getUniqueId() + " requires properties for non-existent class: " + classname + " (looked in: " + classFile.getPath());
            }
            Map<String, Set<String>> propertySetMap = propertyFileMapping.get(classname);
            if (propertySetMap == null) {
                propertySetMap = new HashMap<String, Set<String>>();
                String propertiesFilename = classname.replace(".", "/") + ".properties";
                File propertiesFile = new File(propertiesFilename);
                File previousPropertiesFile = new File(myEJBPluginDir, propertiesFile.getPath());
                if (previousPropertiesFile.exists()) {
                    Properties props = loadProperties(previousPropertiesFile);
                    for (Object key : props.keySet()) {
                        Set<String> valueSet = new HashSet<String>();
                        String[] values = ((String) props.get(key)).split(",");
                        for (String v : values) {
                            if (v != null && v.length() > 0) {
                                valueSet.add(v);
                            }
                        }
                        propertySetMap.put((String) key, valueSet);
                    }
                }
                propertyFileMapping.put(classname, propertySetMap);
            }
            String name = classesExtension.getParameter("name").valueAsString();
            Set<String> valueSet = propertySetMap.get(name);
            if (valueSet == null) {
                valueSet = new HashSet<String>();
                propertySetMap.put(name, valueSet);
            }
            valueSet.add(classesExtension.getParameter("value").valueAsString());
        }
        return propertyFileMapping;
    }

    protected void assembleProperties(PluginDescriptor pluginDescriptor) {
        File myEJBPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        Map<String, Map<String, Set<String>>> propertyFileMapping = buildPropertyFileMapping(pluginDescriptor);
        // Build the new property files, leaving out the removed values
        for (String classname : propertyFileMapping.keySet()) {
            Properties props = new Properties();
            Map<String, Set<String>> propertySetMap = propertyFileMapping.get(classname);
            for (String name : propertySetMap.keySet()) {
                Set<String> valueSet = propertySetMap.get(name);
                Iterator<String> it = valueSet.iterator();
                StringBuffer buff = new StringBuffer();
                while (it.hasNext()) {
                    String value = it.next();
                    // Verify that the value is not 'removed'
                    if (!isOverridden(pluginDescriptor, classname, name, value)) {
                        if (buff.length() > 0) {
                            buff.append(',');
                        }
                        buff.append(value);
                    }
                }
                String propertyValue = buff.toString();
                props.put(name, propertyValue);
            }
            // Write the property file
            String propertiesFilename = classname.replace(".", "/") + ".properties";
            File propertiesFile = new File(myEJBPluginDir, propertiesFilename);
            logger.debug("Write " + propertiesFile.getPath());
            propertiesFile.getParentFile().mkdirs();
            storeProperties(props, propertiesFile);
        }
    }

    protected void assembleORM(Extension extension) throws IOException {
        PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
        File myPluginTmpDir = getPluginTmpDir();
        File myEJBPluginDir = new File(myPluginTmpDir, pluginDescriptor.getId());
        myEJBPluginDir.mkdirs();
        File destinationORMFile = new File(myEJBPluginDir, "META-INF/orm.xml");
        logger.debug("Write ORM " + " to " + destinationORMFile.getPath());
        FileUtils.writeStringToFile(destinationORMFile, getORM(pluginDescriptor));
    }

    protected String getORM(PluginDescriptor pluginDescriptor) {
        try {
            StringWriter writer = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlStreamWriter = null;
            try {
                xmlStreamWriter = factory.createXMLStreamWriter(writer);
                xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeStartElement("entity-mappings");
                xmlStreamWriter.writeAttribute("xmlns", "http://java.sun.com/xml/ns/persistence/orm");
                xmlStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                xmlStreamWriter.writeAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm/orm_2_0.xsd");
                xmlStreamWriter.writeAttribute("version", "2.0");
                xmlStreamWriter.writeCharacters("\n");
                ExtensionPoint extensionPoint = getMyExtensionPoint(EXTENSIONPOINT_ORM);
                Extension extension = extensionPoint.getConnectedExtensions().iterator().next();
                String ormId = extension.getParameter("ormId").valueAsString();
                if (ormId == null || ormId.length() == 0) {
                    throw new RuntimeException(extensionPoint.getUniqueId() + "@ormId must have a value");
                }
                addPersistenceUnitMetadata(pluginDescriptor, ormId, xmlStreamWriter);
                addTableGenerators(pluginDescriptor, ormId, xmlStreamWriter);
                addMappedSuperclasses(pluginDescriptor, ormId, xmlStreamWriter);
                addEntities(pluginDescriptor, ormId, xmlStreamWriter);
                addEmbeddables(pluginDescriptor, ormId, xmlStreamWriter);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndDocument();
            } finally {
                if (xmlStreamWriter != null) {
                    xmlStreamWriter.close();
                }
            }
            return writer.toString();
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Could not create orm.xml", ex);
        }
    }

    private boolean containsORMId(String ormId, Collection<Parameter> ormParameters) {
        for (Parameter ormParameter : ormParameters) {
            if (ormId.equals(ormParameter.valueAsString())) {
                return true;
            }
        }
        return false;
    }

    protected void addPersistenceUnitMetadata(PluginDescriptor pluginDescriptor, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint extensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_PERSISTENCE_UNIT_METADATA);
        if (extensionPoint != null && getDescriptor().getId().equals(extensionPoint.getParentPluginId())) {
            ParameterDefinition targetPluginIdParameterDefinition = extensionPoint.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParameterDefinition == null || targetPluginIdParameterDefinition.getDefaultValue() == null || targetPluginIdParameterDefinition.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(extensionPoint.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pluginDescriptor.getId().equals(targetPluginIdParameterDefinition.getDefaultValue())) {
                for (Extension extension : extensionPoint.getConnectedExtensions()) {
                    if (containsORMId(ormId, extension.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("persistence-unit-metadata");
                        xmlStreamWriter.writeCharacters("\n");
                        Parameter persistenceUnitDefaultsParameter = extension.getParameter("persistence-unit-defaults");
                        if (persistenceUnitDefaultsParameter != null) {
                            xmlStreamWriter.writeStartElement("persistence-unit-defaults");
                            xmlStreamWriter.writeCharacters("\n");
                            if (persistenceUnitDefaultsParameter.getSubParameter("schema") != null) {
                                String schema = persistenceUnitDefaultsParameter.getSubParameter("schema").valueAsString();
                                if (schema.trim().length() == 0) {
                                    throw new RuntimeException(extension.getUniqueId() + " requires a schema parameter value");
                                }
                                xmlStreamWriter.writeStartElement("schema");
                                xmlStreamWriter.writeCharacters(schema);
                                xmlStreamWriter.writeEndElement();
                                xmlStreamWriter.writeCharacters("\n");
                            }
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    protected void addTableGenerators(PluginDescriptor pluginDescriptor, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint extensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_TABLE_GENERATOR);
        if (extensionPoint != null && getDescriptor().getId().equals(extensionPoint.getParentPluginId())) {
            ParameterDefinition targetPluginIdParameterDefinition = extensionPoint.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParameterDefinition == null || targetPluginIdParameterDefinition.getDefaultValue() == null || targetPluginIdParameterDefinition.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(extensionPoint.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pluginDescriptor.getId().equals(targetPluginIdParameterDefinition.getDefaultValue())) {
                for (Extension extension : extensionPoint.getConnectedExtensions()) {
                    if (containsORMId(ormId, extension.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("table-generator");
                        String name = extension.getParameter("name").valueAsString();
                        if (name == null || name.trim().length() == 0) {
                            throw new RuntimeException(extension.getUniqueId() + " requires a name parameter value");
                        }
                        xmlStreamWriter.writeAttribute("name", name);
                        if (extension.getParameter("table") != null) {
                            String table = extension.getParameter("table").valueAsString();
                            if (table.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a table parameter value");
                            }
                            xmlStreamWriter.writeAttribute("table", table);
                        }
                        if (extension.getParameter("schema") != null) {
                            String schema = extension.getParameter("schema").valueAsString();
                            if (schema.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a schema parameter value");
                            }
                            xmlStreamWriter.writeAttribute("schema", schema);
                        }
                        if (extension.getParameter("pk-column-name") != null) {
                            String pkColumnName = extension.getParameter("pk-column-name").valueAsString();
                            if (pkColumnName.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a pkColumnName parameter value");
                            }
                            xmlStreamWriter.writeAttribute("pk-column-name", pkColumnName);
                        }
                        if (extension.getParameter("value-column-name") != null) {
                            String valueColumnName = extension.getParameter("value-column-name").valueAsString();
                            if (valueColumnName.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a valueColumnName parameter value");
                            }
                            xmlStreamWriter.writeAttribute("value-column-name", valueColumnName);
                        }
                        if (extension.getParameter("pk-column-value") != null) {
                            String pkColumnValue = extension.getParameter("pk-column-value").valueAsString();
                            if (pkColumnValue.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a pkColumnValue parameter value");
                            }
                            xmlStreamWriter.writeAttribute("pk-column-value", pkColumnValue);
                        }
                        if (extension.getParameter("initial-value") != null) {
                            String initialValue = extension.getParameter("initial-value").valueAsString();
                            if (initialValue.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a initialValue parameter value");
                            }
                            xmlStreamWriter.writeAttribute("initial-value", initialValue);
                        }
                        if (extension.getParameter("allocation-size") != null) {
                            String allocationSize = extension.getParameter("allocation-size").valueAsString();
                            if (allocationSize.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a allocationSize parameter value");
                            }
                            xmlStreamWriter.writeAttribute("allocation-size", allocationSize);
                        }
                        xmlStreamWriter.writeCharacters("\n");
                        addUniqueConstraints(extension.getParameters("unique-constraint"), xmlStreamWriter);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    protected void addUniqueConstraints(Collection<Parameter> parameters, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        for (Parameter uniqueConstraintParameter : parameters) {
            xmlStreamWriter.writeStartElement("unique-constraint");
            xmlStreamWriter.writeCharacters("\n");
            for (Parameter parameterColumnName : uniqueConstraintParameter.getSubParameters("column-name")) {
                xmlStreamWriter.writeStartElement("column-name");
                xmlStreamWriter.writeCharacters(parameterColumnName.valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
    }

    protected void addMappedSuperclasses(PluginDescriptor pluginDescriptor, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint extensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_MAPPED_SUPERCLASS);
        if (extensionPoint != null && getDescriptor().getId().equals(extensionPoint.getParentPluginId())) {
            ParameterDefinition targetPluginIdParameterDefinition = extensionPoint.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParameterDefinition == null || targetPluginIdParameterDefinition.getDefaultValue() == null || targetPluginIdParameterDefinition.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(extensionPoint.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pluginDescriptor.getId().equals(targetPluginIdParameterDefinition.getDefaultValue())) {
                for (Extension extension : extensionPoint.getConnectedExtensions()) {
                    if (containsORMId(ormId, extension.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("mapped-superclass");
                        String clazz = extension.getParameter("class").valueAsString();
                        if (clazz == null || clazz.trim().length() == 0) {
                            throw new RuntimeException(extension.getUniqueId() + " requires a class parameter value");
                        }
                        xmlStreamWriter.writeAttribute("class", clazz);
                        xmlStreamWriter.writeCharacters("\n");
                        addAttributes(extension, xmlStreamWriter);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    protected void addEntities(PluginDescriptor pluginDescriptor, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint extensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_ENTITY);
        if (extensionPoint != null && getDescriptor().getId().equals(extensionPoint.getParentPluginId())) {
            ParameterDefinition targetPluginIdParameterDefinition = extensionPoint.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParameterDefinition == null || targetPluginIdParameterDefinition.getDefaultValue() == null || targetPluginIdParameterDefinition.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(extensionPoint.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pluginDescriptor.getId().equals(targetPluginIdParameterDefinition.getDefaultValue())) {
                for (Extension extension : extensionPoint.getConnectedExtensions()) {
                    if (containsORMId(ormId, extension.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("entity");
                        String clazz = extension.getParameter("class").valueAsString();
                        if (clazz == null || clazz.trim().length() == 0) {
                            throw new RuntimeException(extension.getUniqueId() + " requires a class parameter value");
                        }
                        xmlStreamWriter.writeAttribute("class", clazz);
                        xmlStreamWriter.writeCharacters("\n");
                        Parameter tableParameter = extension.getParameter("table");
                        if (tableParameter != null) {
                            xmlStreamWriter.writeStartElement("table");
                            if (tableParameter.getSubParameter("name") != null) {
                                String tableName = tableParameter.getSubParameter("name").valueAsString();
                                if (tableName.trim().length() > 0) {
                                    xmlStreamWriter.writeAttribute("name", tableName);
                                }
                            }
                            if (tableParameter.getSubParameter("schema") != null) {
                                String schema = tableParameter.getSubParameter("schema").valueAsString();
                                if (schema.trim().length() > 0) {
                                    xmlStreamWriter.writeAttribute("schema", schema);
                                }
                            }
                            addUniqueConstraints(tableParameter.getSubParameters("unique-constraint"), xmlStreamWriter);
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        Parameter discriminatorColumnParameter = extension.getParameter("discriminator-column");
                        if (discriminatorColumnParameter != null) {
                            xmlStreamWriter.writeStartElement("discriminator-column");
                            if (discriminatorColumnParameter.getSubParameter("name") != null) {
                                String discriminatorColumnName = discriminatorColumnParameter.getSubParameter("name").valueAsString();
                                if (discriminatorColumnName.trim().length() == 0) {
                                    throw new RuntimeException(extension.getUniqueId() + " requires a discriminator-column name parameter value");
                                }
                                xmlStreamWriter.writeAttribute("name", discriminatorColumnName);
                            }
                            Parameter lengthParameter = discriminatorColumnParameter.getSubParameter("length");
                            if (lengthParameter != null) {
                                int length = lengthParameter.valueAsNumber().intValue();
                                xmlStreamWriter.writeAttribute("length", String.valueOf(length));
                            }
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        addAttributes(extension, xmlStreamWriter);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    protected void addEmbeddables(PluginDescriptor pluginDescriptor, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint extensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_EMBEDDABLE);
        if (extensionPoint != null && getDescriptor().getId().equals(extensionPoint.getParentPluginId())) {
            ParameterDefinition targetPluginIdParameterDefinition = extensionPoint.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParameterDefinition == null || targetPluginIdParameterDefinition.getDefaultValue() == null || targetPluginIdParameterDefinition.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(extensionPoint.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pluginDescriptor.getId().equals(targetPluginIdParameterDefinition.getDefaultValue())) {
                for (Extension extension : extensionPoint.getConnectedExtensions()) {
                    if (containsORMId(ormId, extension.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("embeddable");
                        String clazz = extension.getParameter("class").valueAsString();
                        if (clazz == null || clazz.trim().length() == 0) {
                            throw new RuntimeException(extension.getUniqueId() + " requires a class parameter value");
                        }
                        xmlStreamWriter.writeAttribute("class", clazz);
                        xmlStreamWriter.writeCharacters("\n");
                        addAttributes(extension, xmlStreamWriter);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    private void addAttributes(Extension extension, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        Parameter attributesParameter = extension.getParameter("attributes");
        if (attributesParameter != null) {
            xmlStreamWriter.writeStartElement("attributes");
            xmlStreamWriter.writeCharacters("\n");
            for (Parameter idParameter : attributesParameter.getSubParameters("id")) {
                xmlStreamWriter.writeStartElement("id");
                String idName = idParameter.getSubParameter("name").valueAsString();
                if (idName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a id name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", idName);
                xmlStreamWriter.writeCharacters("\n");
                Parameter columnParamter = idParameter.getSubParameter("column");
                if (columnParamter != null) {
                    addColumn(columnParamter, extension, xmlStreamWriter);
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            for (Parameter basicParameter : attributesParameter.getSubParameters("basic")) {
                xmlStreamWriter.writeStartElement("basic");
                String basicName = basicParameter.getSubParameter("name").valueAsString();
                if (basicName == null || basicName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", basicName);
                xmlStreamWriter.writeCharacters("\n");
                Parameter columnParameter = basicParameter.getSubParameter("column");
                if (columnParameter != null) {
                    addColumn(columnParameter, extension, xmlStreamWriter);
                }
                Parameter lobParameter = basicParameter.getSubParameter("lob");
                if (lobParameter != null) {
                    xmlStreamWriter.writeStartElement("lob");
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                Parameter temporalParameter = basicParameter.getSubParameter("temporal");
                if (temporalParameter != null) {
                    xmlStreamWriter.writeStartElement("temporal");
                    String temporal = temporalParameter.valueAsString();
                    if (temporal.trim().length() == 0) {
                        throw new RuntimeException(extension.getUniqueId() + " requires a temporal parameter value");
                    }
                    xmlStreamWriter.writeCharacters(temporal);
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                Parameter enumeratedParameter = basicParameter.getSubParameter("enumerated");
                if (enumeratedParameter != null) {
                    xmlStreamWriter.writeStartElement("enumerated");
                    String enumerated = enumeratedParameter.valueAsString();
                    if (enumerated.trim().length() == 0) {
                        throw new RuntimeException(extension.getUniqueId() + " requires a enumerated parameter value");
                    }
                    xmlStreamWriter.writeCharacters(enumerated);
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            for (Parameter idParameter : attributesParameter.getSubParameters("version")) {
                xmlStreamWriter.writeStartElement("version");
                String versionName = idParameter.getSubParameter("name").valueAsString();
                if (versionName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a version name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", versionName);
                xmlStreamWriter.writeCharacters("\n");
                Parameter columnParamter = idParameter.getSubParameter("column");
                if (columnParamter != null) {
                    addColumn(columnParamter, extension, xmlStreamWriter);
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            for (Parameter idParameter : attributesParameter.getSubParameters("many-to-one")) {
                xmlStreamWriter.writeStartElement("many-to-one");
                String manyToOneName = idParameter.getSubParameter("name").valueAsString();
                if (manyToOneName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a many-to-one name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", manyToOneName);
                xmlStreamWriter.writeCharacters("\n");
                Parameter joinColumnParamter = idParameter.getSubParameter("join-column");
                if (joinColumnParamter != null) {
                    addJoinColumn(joinColumnParamter, extension, xmlStreamWriter);
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            for (Parameter embeddedParameter : attributesParameter.getSubParameters("embedded")) {
                xmlStreamWriter.writeStartElement("embedded");
                String embeddedName = embeddedParameter.getSubParameter("name").valueAsString();
                if (embeddedName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a embedded name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", embeddedName);
                xmlStreamWriter.writeCharacters("\n");
                for (Parameter attributeOverrideParameter : embeddedParameter.getSubParameters("attribute-override")) {
                    xmlStreamWriter.writeStartElement("attribute-override");
                    String attributeOverrideName = attributeOverrideParameter.getSubParameter("name").valueAsString();
                    if (attributeOverrideName == null || attributeOverrideName.trim().length() == 0) {
                        throw new RuntimeException(extension.getUniqueId() + " requires an attribute-override name parameter value");
                    }
                    xmlStreamWriter.writeAttribute("name", attributeOverrideName);
                    xmlStreamWriter.writeCharacters("\n");
                    Parameter attributeOverrdieColumnParameter = attributeOverrideParameter.getSubParameter("column");
                    addColumn(attributeOverrdieColumnParameter, extension, xmlStreamWriter);
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
    }

    private void addColumn(Parameter columnParameter, Extension extension, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("column");
        Parameter columnNameParameter = columnParameter.getSubParameter("name");
        if (columnNameParameter != null) {
            String columnName = columnNameParameter.valueAsString();
            if (columnName.trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " requires a column name parameter value");
            }
            xmlStreamWriter.writeAttribute("name", columnName);
        }
        Parameter lengthParameter = columnParameter.getSubParameter("length");
        if (lengthParameter != null) {
            int length = lengthParameter.valueAsNumber().intValue();
            xmlStreamWriter.writeAttribute("length", String.valueOf(length));
        }
        Parameter columnDefinitionParameter = columnParameter.getSubParameter("column-definition");
        if (columnDefinitionParameter != null) {
            String columnDefintion = columnDefinitionParameter.valueAsString();
            if (columnDefintion.trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " requires a column column-definition parameter value");
            }
            xmlStreamWriter.writeAttribute("column-definition", columnDefintion);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    private void addJoinColumn(Parameter joinColumnParameter, Extension extension, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("join-column");
        if (joinColumnParameter.getSubParameter("name") != null) {
            String joinColumnName = joinColumnParameter.getSubParameter("name").valueAsString();
            if (joinColumnName.trim().length() == 0) {
                throw new RuntimeException(extension.getUniqueId() + " requires a join-column name parameter value");
            }
            xmlStreamWriter.writeAttribute("name", joinColumnName);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void assemblePersistenceXML(Extension extension) throws IOException {
        PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
        File myPluginTmpDir = getPluginTmpDir();
        File myEJBPluginDir = new File(myPluginTmpDir, pluginDescriptor.getId());
        myEJBPluginDir.mkdirs();
        File destinationPersistenceXMLFile = new File(myEJBPluginDir, "META-INF/persistence.xml");
        logger.debug("Write persistence.xml " + " to " + destinationPersistenceXMLFile.getPath());
        FileUtils.writeStringToFile(destinationPersistenceXMLFile, getPersistenceXML(pluginDescriptor));
    }

    protected String getPersistenceXML(PluginDescriptor pluginDescriptor) {
        try {
            StringWriter writer = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlStreamWriter = null;
            try {
                xmlStreamWriter = factory.createXMLStreamWriter(writer);
                xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeStartElement("persistence");
                xmlStreamWriter.writeAttribute("xmlns", "http://java.sun.com/xml/ns/persistence");
                xmlStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                xmlStreamWriter.writeAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
                xmlStreamWriter.writeAttribute("version", "2.0");
                xmlStreamWriter.writeCharacters("\n");
                ExtensionPoint extensionPoint = getMyExtensionPoint(EXTENSIONPOINT_ORM);
                Extension extension = extensionPoint.getConnectedExtensions().iterator().next();
                String ormId = extension.getParameter("ormId").valueAsString();
                if (ormId == null || ormId.length() == 0) {
                    throw new RuntimeException(extensionPoint.getUniqueId() + "@ormId must have a value");
                }
                addPersistenceUnit(pluginDescriptor, ormId, xmlStreamWriter);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                xmlStreamWriter.writeEndDocument();
            } finally {
                if (xmlStreamWriter != null) {
                    xmlStreamWriter.close();
                }
            }
            return writer.toString();
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Could not create orm.xml", ex);
        }
    }

    protected void addPersistenceUnit(PluginDescriptor pluginDescriptor, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint extensionPoint = pluginDescriptor.getExtensionPoint(EXTENSIONPOINT_PERSISTENCE_UNIT);
        if (extensionPoint != null && getDescriptor().getId().equals(extensionPoint.getParentPluginId())) {
            ParameterDefinition targetPluginIdParameterDefinition = extensionPoint.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParameterDefinition == null || targetPluginIdParameterDefinition.getDefaultValue() == null || targetPluginIdParameterDefinition.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(extensionPoint.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pluginDescriptor.getId().equals(targetPluginIdParameterDefinition.getDefaultValue())) {
                for (Extension extension : extensionPoint.getConnectedExtensions()) {
                    if (containsORMId(ormId, extension.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("persistence-unit");
                        String name = extension.getParameter("name").valueAsString();
                        if (name.trim().length() == 0) {
                            throw new RuntimeException(extension.getUniqueId() + " requires a name parameter value");
                        }
                        xmlStreamWriter.writeAttribute("name", name);
                        String transactionType = extension.getParameter("transaction-type").valueAsString();
                        if (transactionType.trim().length() == 0) {
                            throw new RuntimeException(extension.getUniqueId() + " requires a transaction-type parameter value");
                        }
                        xmlStreamWriter.writeAttribute("transaction-type", transactionType);
                        xmlStreamWriter.writeCharacters("\n");
                        Parameter providerParameter = extension.getParameter("provider");
                        if (providerParameter != null) {
                            xmlStreamWriter.writeStartElement("provider");
                            String provider = providerParameter.valueAsString();
                            if (provider.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a provider parameter value");
                            }
                            xmlStreamWriter.writeCharacters(provider);
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        Parameter jtaDataSourceParameter = extension.getParameter("jta-data-source");
                        if (jtaDataSourceParameter != null) {
                            xmlStreamWriter.writeStartElement("jta-data-source");
                            String jtaDataSource = jtaDataSourceParameter.valueAsString();
                            if (jtaDataSource.trim().length() == 0) {
                                throw new RuntimeException(extension.getUniqueId() + " requires a jta-data-source parameter value");
                            }
                            xmlStreamWriter.writeCharacters(jtaDataSource);
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        xmlStreamWriter.writeStartElement("mapping-file");
                        String mappingFile = extension.getParameter("mapping-file").valueAsString();
                        if (mappingFile.trim().length() == 0) {
                            throw new RuntimeException(extension.getUniqueId() + " requires a mapping-file parameter value");
                        }
                        xmlStreamWriter.writeCharacters(mappingFile);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                        List<Parameter> propertyParameters = new ArrayList<Parameter>();
                        propertyParameters.addAll(extension.getParameters("property"));
                        if (!propertyParameters.isEmpty()) {
                            xmlStreamWriter.writeStartElement("properties");
                            for (Parameter propertyParameter : propertyParameters) {
                                xmlStreamWriter.writeStartElement("property");
                                String propertyName = propertyParameter.getSubParameter("name").valueAsString();
                                if (mappingFile.trim().length() == 0) {
                                    throw new RuntimeException(extension.getUniqueId() + " requires a property name parameter value");
                                }
                                xmlStreamWriter.writeAttribute("name", propertyName);
                                String propertyValue = propertyParameter.getSubParameter("value").valueAsString();
                                if (mappingFile.trim().length() == 0) {
                                    throw new RuntimeException(extension.getUniqueId() + " requires a property value parameter value");
                                }
                                xmlStreamWriter.writeAttribute("value", propertyValue);
                                xmlStreamWriter.writeEndElement();
                                xmlStreamWriter.writeCharacters("\n");
                            }
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    protected void assembleEJBJARXML(PluginDescriptor pluginDescriptor) throws IOException, XMLStreamException {
        File myEJBJARPluginDir = new File(getPluginTmpDir(), pluginDescriptor.getId());
        File sourceXMLFile = new File(myEJBJARPluginDir, "META-INF/ejb-jar.xml");
        if (!sourceXMLFile.exists()) {
            throw new RuntimeException("Could not locate: ejb-jar file: " + sourceXMLFile);
        }
        StringBuffer originalXML = new StringBuffer();
        originalXML.append(FileUtils.readFileToString(sourceXMLFile));
        String xslt = getEJBJARXSLT();
        File ejbjarxmlXSLT = new File(getPluginTmpDir(), "ejbjarxml-xslt.xml");
        logger.debug("Write ejb-jar XSLT to " + ejbjarxmlXSLT.getPath());
        FileUtils.writeStringToFile(ejbjarxmlXSLT, xslt);
        String translatedXMLString = getTranslatedXML(originalXML.toString(), xslt);
        logger.debug("Write translated ejb-jar.xml to " + sourceXMLFile);
        FileUtils.writeStringToFile(sourceXMLFile, translatedXMLString);
    }

    protected String getEJBJARXSLT() throws XMLStreamException {
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
            addEnterpriseBeansChildren(xmlStreamWriter);
            addAssemblyDescriptorChildren(xmlStreamWriter);
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
        xmlStreamWriter.writeAttribute("match", "tp:ejb-jar");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("ejb-jar");
        xmlStreamWriter.writeAttribute("version", "{@version}");
        xmlStreamWriter.writeAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
        xmlStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlStreamWriter.writeAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_3_1.xsd");
        xmlStreamWriter.writeCharacters("\n");
        addRootEnterpriseBeansSelects(xmlStreamWriter);
        addRootAssemblyDescriptorSelects(xmlStreamWriter);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addRootEnterpriseBeansSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:enterprise-beans");
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
        xmlStreamWriter.writeAttribute("name", "addEnterpriseBeansChildren");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "createEnterpriseBeans");
        xmlStreamWriter.writeEndElement();
    }

    protected void addRootAssemblyDescriptorSelects(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:for-each");
        xmlStreamWriter.writeAttribute("select", "tp:assembly-descriptor");
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
        xmlStreamWriter.writeAttribute("name", "addAssemblyDescriptorChildren");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("xsl:call-template");
        xmlStreamWriter.writeAttribute("name", "createAssemblyDescriptor");
        xmlStreamWriter.writeEndElement();
    }

    protected void addEnterpriseBeansCreate(boolean hasEnterpriseBeans, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "createEnterpriseBeans");
        xmlStreamWriter.writeCharacters("\n");
        if (hasEnterpriseBeans) {
            logger.debug("Enterprise beans tag will be created");
            xmlStreamWriter.writeStartElement("enterprise-beans");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("xsl:call-template");
            xmlStreamWriter.writeAttribute("name", "createEnterpriseBeansChildren");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        } else {
            logger.debug("No enterprise beans tag will be created");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addAssemblyDescriptorCreate(boolean hasAssemblyDescriptor, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "createAssemblyDescriptor");
        xmlStreamWriter.writeCharacters("\n");
        if (hasAssemblyDescriptor) {
            logger.debug("AssemblyDescriptor tag will be created");
            xmlStreamWriter.writeStartElement("assembly-descriptor");
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeStartElement("xsl:call-template");
            xmlStreamWriter.writeAttribute("name", "createAssemblyDescriptorChildren");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        } else {
            logger.debug("No assembly-descriptor tag will be created");
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addEnterpriseBeansChildren(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addEnterpriseBeansChildren");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        addEnterpriseBeansCreate(false, xmlStreamWriter);
    }

    protected void addAssemblyDescriptorChildren(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addAssemblyDescriptorChildren");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        addAssemblyDescriptorCreate(false, xmlStreamWriter);
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
