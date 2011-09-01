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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;
import org.java.plugin.registry.PluginDescriptor;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.tools.ant.TolvenJar;

/**
 * This plugin assmebles ejb modules for ear files
 * 
 * @author Joseph Isaac
 *
 */
public class EJBModuleAssembler extends TolvenCommandPlugin {

    public static final String CMD_DESTDIR_OPT = "destDir";
    public static final String CMD_EJB_FILE_OPT = "ejbFile";
    public static final String CMD_EJB_PLUGIN_OPT = "ejbPlugin";

    public static final String EXNPT_ABSTRACT_EJB = "abstractEJB";
    public static final String EXNPT_CLASSES = "classes";
    public static final String EXNPT_CLASSES_ADPTR = "classes-adaptor";
    public static final String EXNPT_COMPONENT = "component";
    public static final String EXNPT_CONTEXT_ID = "context-id";
    public static final String EXNPT_EJB_PROPERTY = "property";
    public static final String EXNPT_EJBMODULE_DECL = "ejbModule-declaration";
    public static final String EXNPT_EMBEDDABLE = "embeddable";
    public static final String EXNPT_ENTITY = "entity";
    public static final String EXNPT_ID = "extension-point";
    public static final String EXNPT_LDAP_PROPERTY_ACCESSOR = "ldapPropertyAccessor";
    public static final String EXNPT_MAPPED_SUPERCLASS = "mapped-superclass";
    public static final String EXNPT_METAINF = "META-INF";
    public static final String EXNPT_METAINF_ADPTR = "META-INF-adaptor";
    public static final String EXNPT_ORM = "orm";
    public static final String EXNPT_PERSISTENCE_UNIT = "persistence-unit";
    public static final String EXNPT_PERSISTENCE_UNIT_METADATA = "persistence-unit-metadata";
    public static final String EXNPT_SRC_PLUGIN_ID = "source-plugin-id";
    public static final String EXNPT_TABLE_GENERATOR = "table-generator";

    private Logger logger = Logger.getLogger(EJBModuleAssembler.class);

    protected void addAssemblyDescriptorChildren(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addAssemblyDescriptorChildren");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        addAssemblyDescriptorCreate(false, xmlStreamWriter);
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

    private void addAttributes(Extension extension, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        Parameter attributesParam = extension.getParameter("attributes");
        if (attributesParam != null) {
            xmlStreamWriter.writeStartElement("attributes");
            xmlStreamWriter.writeCharacters("\n");
            for (Parameter idParam : attributesParam.getSubParameters("id")) {
                xmlStreamWriter.writeStartElement("id");
                String idName = idParam.getSubParameter("name").valueAsString();
                if (idName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a id name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", idName);
                xmlStreamWriter.writeCharacters("\n");
                Parameter columnParam = idParam.getSubParameter("column");
                if (columnParam != null) {
                    addColumn(columnParam, extension, xmlStreamWriter);
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            for (Parameter basicParam : attributesParam.getSubParameters("basic")) {
                xmlStreamWriter.writeStartElement("basic");
                String basicName = basicParam.getSubParameter("name").valueAsString();
                if (basicName == null || basicName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", basicName);
                xmlStreamWriter.writeCharacters("\n");
                Parameter columnParameter = basicParam.getSubParameter("column");
                if (columnParameter != null) {
                    addColumn(columnParameter, extension, xmlStreamWriter);
                }
                Parameter lobParam = basicParam.getSubParameter("lob");
                if (lobParam != null) {
                    xmlStreamWriter.writeStartElement("lob");
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                Parameter temporalParam = basicParam.getSubParameter("temporal");
                if (temporalParam != null) {
                    xmlStreamWriter.writeStartElement("temporal");
                    String temporal = temporalParam.valueAsString();
                    if (temporal.trim().length() == 0) {
                        throw new RuntimeException(extension.getUniqueId() + " requires a temporal parameter value");
                    }
                    xmlStreamWriter.writeCharacters(temporal);
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                Parameter enumeratedParam = basicParam.getSubParameter("enumerated");
                if (enumeratedParam != null) {
                    xmlStreamWriter.writeStartElement("enumerated");
                    String enumerated = enumeratedParam.valueAsString();
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
            for (Parameter idParam : attributesParam.getSubParameters("version")) {
                xmlStreamWriter.writeStartElement("version");
                String versionName = idParam.getSubParameter("name").valueAsString();
                if (versionName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a version name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", versionName);
                xmlStreamWriter.writeCharacters("\n");
                Parameter columnParamter = idParam.getSubParameter("column");
                if (columnParamter != null) {
                    addColumn(columnParamter, extension, xmlStreamWriter);
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            for (Parameter idParam : attributesParam.getSubParameters("many-to-one")) {
                xmlStreamWriter.writeStartElement("many-to-one");
                String manyToOneName = idParam.getSubParameter("name").valueAsString();
                if (manyToOneName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a many-to-one name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", manyToOneName);
                xmlStreamWriter.writeCharacters("\n");
                Parameter joinColumnParamter = idParam.getSubParameter("join-column");
                if (joinColumnParamter != null) {
                    addJoinColumn(joinColumnParamter, extension, xmlStreamWriter);
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            for (Parameter embeddedParam : attributesParam.getSubParameters("embedded")) {
                xmlStreamWriter.writeStartElement("embedded");
                String embeddedName = embeddedParam.getSubParameter("name").valueAsString();
                if (embeddedName.trim().length() == 0) {
                    throw new RuntimeException(extension.getUniqueId() + " requires a embedded name parameter value");
                }
                xmlStreamWriter.writeAttribute("name", embeddedName);
                xmlStreamWriter.writeCharacters("\n");
                for (Parameter attributeOverrideParam : embeddedParam.getSubParameters("attribute-override")) {
                    xmlStreamWriter.writeStartElement("attribute-override");
                    String attributeOverrideName = attributeOverrideParam.getSubParameter("name").valueAsString();
                    if (attributeOverrideName == null || attributeOverrideName.trim().length() == 0) {
                        throw new RuntimeException(extension.getUniqueId() + " requires an attribute-override name parameter value");
                    }
                    xmlStreamWriter.writeAttribute("name", attributeOverrideName);
                    xmlStreamWriter.writeCharacters("\n");
                    Parameter attributeOverrdieColumnParameter = attributeOverrideParam.getSubParameter("column");
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

    private void addColumn(Parameter columnParam, Extension exn, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("column");
        Parameter columnNameParam = columnParam.getSubParameter("name");
        if (columnNameParam != null) {
            String columnName = columnNameParam.valueAsString();
            if (columnName.trim().length() == 0) {
                throw new RuntimeException(exn.getUniqueId() + " requires a column name parameter value");
            }
            xmlStreamWriter.writeAttribute("name", columnName);
        }
        Parameter lengthParam = columnParam.getSubParameter("length");
        if (lengthParam != null) {
            int length = lengthParam.valueAsNumber().intValue();
            xmlStreamWriter.writeAttribute("length", String.valueOf(length));
        }
        Parameter columnDefinitionParam = columnParam.getSubParameter("column-definition");
        if (columnDefinitionParam != null) {
            String columnDefintion = columnDefinitionParam.valueAsString();
            if (columnDefintion.trim().length() == 0) {
                throw new RuntimeException(exn.getUniqueId() + " requires a column column-definition parameter value");
            }
            xmlStreamWriter.writeAttribute("column-definition", columnDefintion);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    protected void addEmbeddables(PluginDescriptor pd, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint exnPt = pd.getExtensionPoint(EXNPT_EMBEDDABLE);
        if (exnPt != null && getDescriptor().getId().equals(exnPt.getParentPluginId())) {
            ParameterDefinition targetPluginIdParamDef = exnPt.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParamDef == null || targetPluginIdParamDef.getDefaultValue() == null || targetPluginIdParamDef.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(exnPt.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pd.getId().equals(targetPluginIdParamDef.getDefaultValue())) {
                for (Extension exn : exnPt.getConnectedExtensions()) {
                    if (containsORMId(ormId, exn.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("embeddable");
                        String clazz = exn.getParameter("class").valueAsString();
                        if (clazz == null || clazz.trim().length() == 0) {
                            throw new RuntimeException(exn.getUniqueId() + " requires a class parameter value");
                        }
                        xmlStreamWriter.writeAttribute("class", clazz);
                        xmlStreamWriter.writeCharacters("\n");
                        addAttributes(exn, xmlStreamWriter);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    protected void addEnterpriseBeansChildren(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("xsl:template");
        xmlStreamWriter.writeAttribute("name", "addEnterpriseBeansChildren");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        addEnterpriseBeansCreate(false, xmlStreamWriter);
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

    protected void addEntities(PluginDescriptor pd, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint exnPt = pd.getExtensionPoint(EXNPT_ENTITY);
        if (exnPt != null && getDescriptor().getId().equals(exnPt.getParentPluginId())) {
            ParameterDefinition targetPluginIdParamDef = exnPt.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParamDef == null || targetPluginIdParamDef.getDefaultValue() == null || targetPluginIdParamDef.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(exnPt.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pd.getId().equals(targetPluginIdParamDef.getDefaultValue())) {
                for (Extension exn : exnPt.getConnectedExtensions()) {
                    if (containsORMId(ormId, exn.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("entity");
                        String clazz = exn.getParameter("class").valueAsString();
                        if (clazz == null || clazz.trim().length() == 0) {
                            throw new RuntimeException(exn.getUniqueId() + " requires a class parameter value");
                        }
                        xmlStreamWriter.writeAttribute("class", clazz);
                        xmlStreamWriter.writeCharacters("\n");
                        Parameter tableParam = exn.getParameter("table");
                        if (tableParam != null) {
                            xmlStreamWriter.writeStartElement("table");
                            if (tableParam.getSubParameter("name") != null) {
                                String tableName = tableParam.getSubParameter("name").valueAsString();
                                if (tableName.trim().length() > 0) {
                                    xmlStreamWriter.writeAttribute("name", tableName);
                                }
                            }
                            if (tableParam.getSubParameter("schema") != null) {
                                String schema = tableParam.getSubParameter("schema").valueAsString();
                                if (schema.trim().length() > 0) {
                                    xmlStreamWriter.writeAttribute("schema", schema);
                                }
                            }
                            addUniqueConstraints(tableParam.getSubParameters("unique-constraint"), xmlStreamWriter);
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        Parameter discriminatorColumnParam = exn.getParameter("discriminator-column");
                        if (discriminatorColumnParam != null) {
                            xmlStreamWriter.writeStartElement("discriminator-column");
                            if (discriminatorColumnParam.getSubParameter("name") != null) {
                                String discriminatorColumnName = discriminatorColumnParam.getSubParameter("name").valueAsString();
                                if (discriminatorColumnName.trim().length() == 0) {
                                    throw new RuntimeException(exn.getUniqueId() + " requires a discriminator-column name parameter value");
                                }
                                xmlStreamWriter.writeAttribute("name", discriminatorColumnName);
                            }
                            Parameter lengthParam = discriminatorColumnParam.getSubParameter("length");
                            if (lengthParam != null) {
                                int length = lengthParam.valueAsNumber().intValue();
                                xmlStreamWriter.writeAttribute("length", String.valueOf(length));
                            }
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        addAttributes(exn, xmlStreamWriter);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    private void addJoinColumn(Parameter joinColumnParam, Extension exn, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("join-column");
        if (joinColumnParam.getSubParameter("name") != null) {
            String joinColumnName = joinColumnParam.getSubParameter("name").valueAsString();
            if (joinColumnName.trim().length() == 0) {
                throw new RuntimeException(exn.getUniqueId() + " requires a join-column name parameter value");
            }
            xmlStreamWriter.writeAttribute("name", joinColumnName);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
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

    protected void addMappedSuperclasses(PluginDescriptor pd, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint exnPt = pd.getExtensionPoint(EXNPT_MAPPED_SUPERCLASS);
        if (exnPt != null && getDescriptor().getId().equals(exnPt.getParentPluginId())) {
            ParameterDefinition targetPluginIdParamDef = exnPt.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParamDef == null || targetPluginIdParamDef.getDefaultValue() == null || targetPluginIdParamDef.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(exnPt.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pd.getId().equals(targetPluginIdParamDef.getDefaultValue())) {
                for (Extension exn : exnPt.getConnectedExtensions()) {
                    if (containsORMId(ormId, exn.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("mapped-superclass");
                        String clazz = exn.getParameter("class").valueAsString();
                        if (clazz == null || clazz.trim().length() == 0) {
                            throw new RuntimeException(exn.getUniqueId() + " requires a class parameter value");
                        }
                        xmlStreamWriter.writeAttribute("class", clazz);
                        xmlStreamWriter.writeCharacters("\n");
                        addAttributes(exn, xmlStreamWriter);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    protected void addPersistenceUnit(PluginDescriptor pd, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint exnPt = pd.getExtensionPoint(EXNPT_PERSISTENCE_UNIT);
        if (exnPt != null && getDescriptor().getId().equals(exnPt.getParentPluginId())) {
            ParameterDefinition targetPluginIdParamDef = exnPt.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParamDef == null || targetPluginIdParamDef.getDefaultValue() == null || targetPluginIdParamDef.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(exnPt.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pd.getId().equals(targetPluginIdParamDef.getDefaultValue())) {
                ExtensionPoint ormExnPt = getMyExtensionPoint(EXNPT_ORM);
                Extension ormExn = ormExnPt.getConnectedExtensions().iterator().next();
                String ormId = ormExn.getParameter("ormId").valueAsString();
                if (ormId == null || ormId.length() == 0) {
                    throw new RuntimeException(ormExnPt.getUniqueId() + "@ormId must have a value");
                }
                String name = ormExn.getParameter("name").valueAsString();
                if (name == null || name.length() == 0) {
                    throw new RuntimeException(ormExnPt.getUniqueId() + "@name must have a value");
                }
                String transactionType = ormExn.getParameter("transaction-type").valueAsString();
                if (transactionType == null || transactionType.length() == 0) {
                    throw new RuntimeException(ormExnPt.getUniqueId() + "@transaction-type must have a value");
                }
                String jtaDataSource = ormExn.getParameter("jta-data-source").valueAsString();
                if (jtaDataSource == null || jtaDataSource.length() == 0) {
                    throw new RuntimeException(ormExnPt.getUniqueId() + "@jta-data-source must have a value");
                }
                for (Extension exn : exnPt.getConnectedExtensions()) {

                    if (containsORMId(ormId, exn.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("persistence-unit");
                        if (name.trim().length() == 0) {
                            throw new RuntimeException(exn.getUniqueId() + " requires a name parameter value");
                        }
                        xmlStreamWriter.writeAttribute("name", name);
                        if (transactionType.trim().length() == 0) {
                            throw new RuntimeException(exn.getUniqueId() + " requires a transaction-type parameter value");
                        }
                        xmlStreamWriter.writeAttribute("transaction-type", transactionType);
                        xmlStreamWriter.writeCharacters("\n");
                        Parameter providerParam = exn.getParameter("provider");
                        if (providerParam != null) {
                            xmlStreamWriter.writeStartElement("provider");
                            String provider = providerParam.valueAsString();
                            if (provider.trim().length() == 0) {
                                throw new RuntimeException(exn.getUniqueId() + " requires a provider parameter value");
                            }
                            xmlStreamWriter.writeCharacters(provider);
                            xmlStreamWriter.writeEndElement();
                            xmlStreamWriter.writeCharacters("\n");
                        }
                        xmlStreamWriter.writeStartElement("jta-data-source");
                        if (jtaDataSource.trim().length() == 0) {
                            throw new RuntimeException(exn.getUniqueId() + " requires a jta-data-source parameter value");
                        }
                        xmlStreamWriter.writeCharacters(jtaDataSource);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                        xmlStreamWriter.writeStartElement("mapping-file");
                        String mappingFile = exn.getParameter("mapping-file").valueAsString();
                        if (mappingFile.trim().length() == 0) {
                            throw new RuntimeException(exn.getUniqueId() + " requires a mapping-file parameter value");
                        }
                        xmlStreamWriter.writeCharacters("META-INF/" + exn.getDeclaringPluginDescriptor().getId() + "-" + mappingFile);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                        List<Parameter> propertyParams = new ArrayList<Parameter>();
                        propertyParams.addAll(exn.getParameters("property"));
                        if (!propertyParams.isEmpty()) {
                            xmlStreamWriter.writeStartElement("properties");
                            for (Parameter propertyParameter : propertyParams) {
                                xmlStreamWriter.writeStartElement("property");
                                String propertyName = propertyParameter.getSubParameter("name").valueAsString();
                                if (mappingFile.trim().length() == 0) {
                                    throw new RuntimeException(exn.getUniqueId() + " requires a property name parameter value");
                                }
                                xmlStreamWriter.writeAttribute("name", propertyName);
                                String propertyValue = propertyParameter.getSubParameter("value").valueAsString();
                                if (mappingFile.trim().length() == 0) {
                                    throw new RuntimeException(exn.getUniqueId() + " requires a property value parameter value");
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

    protected void addPersistenceUnitMetadata(PluginDescriptor pd, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint exnPt = pd.getExtensionPoint(EXNPT_PERSISTENCE_UNIT_METADATA);
        if (exnPt != null && getDescriptor().getId().equals(exnPt.getParentPluginId())) {
            ParameterDefinition targetPluginIdParamDef = exnPt.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParamDef == null || targetPluginIdParamDef.getDefaultValue() == null || targetPluginIdParamDef.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(exnPt.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pd.getId().equals(targetPluginIdParamDef.getDefaultValue())) {
                for (Extension exn : exnPt.getConnectedExtensions()) {
                    if (containsORMId(ormId, exn.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("persistence-unit-metadata");
                        xmlStreamWriter.writeCharacters("\n");
                        Parameter persistenceUnitDefaultsParam = exn.getParameter("persistence-unit-defaults");
                        if (persistenceUnitDefaultsParam != null) {
                            xmlStreamWriter.writeStartElement("persistence-unit-defaults");
                            xmlStreamWriter.writeCharacters("\n");
                            if (persistenceUnitDefaultsParam.getSubParameter("schema") != null) {
                                String schema = persistenceUnitDefaultsParam.getSubParameter("schema").valueAsString();
                                if (schema.trim().length() == 0) {
                                    throw new RuntimeException(exn.getUniqueId() + " requires a schema parameter value");
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

    protected void addTableGenerators(PluginDescriptor pd, String ormId, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        ExtensionPoint exnPt = pd.getExtensionPoint(EXNPT_TABLE_GENERATOR);
        if (exnPt != null && getDescriptor().getId().equals(exnPt.getParentPluginId())) {
            ParameterDefinition targetPluginIdParamDef = exnPt.getParameterDefinition("target-plugin-id");
            if (targetPluginIdParamDef == null || targetPluginIdParamDef.getDefaultValue() == null || targetPluginIdParamDef.getDefaultValue().trim().length() == 0) {
                throw new RuntimeException(exnPt.getUniqueId() + " must defined a parameter-def called target-plugin-id with a default value");
            }
            if (pd.getId().equals(targetPluginIdParamDef.getDefaultValue())) {
                for (Extension exn : exnPt.getConnectedExtensions()) {
                    if (containsORMId(ormId, exn.getParameters("ormId"))) {
                        xmlStreamWriter.writeStartElement("table-generator");
                        String name = exn.getParameter("name").valueAsString();
                        if (name == null || name.trim().length() == 0) {
                            throw new RuntimeException(exn.getUniqueId() + " requires a name parameter value");
                        }
                        xmlStreamWriter.writeAttribute("name", name);
                        if (exn.getParameter("table") != null) {
                            String table = exn.getParameter("table").valueAsString();
                            if (table.trim().length() == 0) {
                                throw new RuntimeException(exn.getUniqueId() + " requires a table parameter value");
                            }
                            xmlStreamWriter.writeAttribute("table", table);
                        }
                        if (exn.getParameter("schema") != null) {
                            String schema = exn.getParameter("schema").valueAsString();
                            if (schema.trim().length() == 0) {
                                throw new RuntimeException(exn.getUniqueId() + " requires a schema parameter value");
                            }
                            xmlStreamWriter.writeAttribute("schema", schema);
                        }
                        if (exn.getParameter("pk-column-name") != null) {
                            String pkColumnName = exn.getParameter("pk-column-name").valueAsString();
                            if (pkColumnName.trim().length() == 0) {
                                throw new RuntimeException(exn.getUniqueId() + " requires a pkColumnName parameter value");
                            }
                            xmlStreamWriter.writeAttribute("pk-column-name", pkColumnName);
                        }
                        if (exn.getParameter("value-column-name") != null) {
                            String valueColumnName = exn.getParameter("value-column-name").valueAsString();
                            if (valueColumnName.trim().length() == 0) {
                                throw new RuntimeException(exn.getUniqueId() + " requires a valueColumnName parameter value");
                            }
                            xmlStreamWriter.writeAttribute("value-column-name", valueColumnName);
                        }
                        if (exn.getParameter("pk-column-value") != null) {
                            String pkColumnValue = exn.getParameter("pk-column-value").valueAsString();
                            if (pkColumnValue.trim().length() == 0) {
                                throw new RuntimeException(exn.getUniqueId() + " requires a pkColumnValue parameter value");
                            }
                            xmlStreamWriter.writeAttribute("pk-column-value", pkColumnValue);
                        }
                        if (exn.getParameter("initial-value") != null) {
                            String initialValue = exn.getParameter("initial-value").valueAsString();
                            if (initialValue.trim().length() == 0) {
                                throw new RuntimeException(exn.getUniqueId() + " requires a initialValue parameter value");
                            }
                            xmlStreamWriter.writeAttribute("initial-value", initialValue);
                        }
                        if (exn.getParameter("allocation-size") != null) {
                            String allocationSize = exn.getParameter("allocation-size").valueAsString();
                            if (allocationSize.trim().length() == 0) {
                                throw new RuntimeException(exn.getUniqueId() + " requires a allocationSize parameter value");
                            }
                            xmlStreamWriter.writeAttribute("allocation-size", allocationSize);
                        }
                        xmlStreamWriter.writeCharacters("\n");
                        addUniqueConstraints(exn.getParameters("unique-constraint"), xmlStreamWriter);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                }
            }
        }
    }

    protected void addUniqueConstraints(Collection<Parameter> params, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        for (Parameter uniqueConstraintParameter : params) {
            xmlStreamWriter.writeStartElement("unique-constraint");
            xmlStreamWriter.writeCharacters("\n");
            for (Parameter columnNameParam : uniqueConstraintParameter.getSubParameters("column-name")) {
                xmlStreamWriter.writeStartElement("column-name");
                xmlStreamWriter.writeCharacters(columnNameParam.valueAsString());
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeCharacters("\n");
        }
    }

    protected void assembleClasses(PluginDescriptor pd, File localDestDir) throws IOException {
        for (Extension classesExn : getChildExtensions(EXNPT_CLASSES, pd)) {
            String srcDirname = classesExn.getParameter("dir").valueAsString();
            File srcDir = getFilePath(classesExn.getDeclaringPluginDescriptor(), srcDirname);
            logger.debug("Copy " + srcDir.getPath() + " to " + localDestDir.getPath());
            FileUtils.copyDirectory(srcDir, localDestDir, true);
        }
    }

    protected void assembleClassesAdaptors(PluginDescriptor pd, String contextId, File localDestDir) throws IOException {
        PluginDescriptor abstractEJBPD = getAbstractEJBPluginDescriptor();
        ExtensionPoint abstractExnPt = abstractEJBPD.getExtensionPoint(EXNPT_CLASSES_ADPTR);
        for (ExtensionPoint descendentExPt : abstractExnPt.getDescendants()) {
            if (descendentExPt.getDeclaringPluginDescriptor().getId().equals(pd.getId())) {
                for (Extension exn : descendentExPt.getConnectedExtensions()) {
                    if (exn.getParameter(EXNPT_CONTEXT_ID) == null || contextId.equals(exn.getParameter(EXNPT_CONTEXT_ID).valueAsString())) {
                        for (File srcDir : getAdaptorFiles(exn)) {
                            logger.debug("Copy " + srcDir.getPath() + " to " + localDestDir.getPath());
                            FileUtils.copyDirectory(srcDir, localDestDir, true);
                        }
                    }
                }
            }
        }
    }

    protected void assembleEJB(PluginDescriptor pd, String contextId, String ejbFilename, File destDir) throws IOException, XMLStreamException {
        File localDestDir = new File(getPluginTmpDir(), pd.getId());
        localDestDir.mkdirs();
        assembleClasses(pd, localDestDir);
        assembleClassesAdaptors(pd, contextId, localDestDir);
        assembleProperties(pd, localDestDir);
        assembleMetaInf(pd, localDestDir);
        assembleMetaInfAdaptors(pd, contextId, localDestDir);
        assembleEJBJARXML(localDestDir);
        assembleORM(pd, localDestDir);
        assemblePersistenceXML(pd, localDestDir);
        buildEJB(ejbFilename, localDestDir, destDir);
    }

    protected void assembleEJBJARXML(File localDestDir) throws IOException, XMLStreamException {
        File src = new File(localDestDir, "META-INF/ejb-jar.xml");
        if (!src.exists()) {
            throw new RuntimeException("Could not locate: ejb-jar file: " + src);
        }
        StringBuffer originalXML = new StringBuffer();
        originalXML.append(FileUtils.readFileToString(src));
        String xslt = getEJBJARXSLT();
        File ejbjarxmlXSLT = new File(getPluginTmpDir(), "ejbjarxml-xslt.xml");
        logger.debug("Write ejb-jar XSLT to " + ejbjarxmlXSLT.getPath());
        FileUtils.writeStringToFile(ejbjarxmlXSLT, xslt);
        String translatedXMLString = getTranslatedXML(originalXML.toString(), xslt);
        logger.debug("Write translated ejb-jar.xml to " + src);
        FileUtils.writeStringToFile(src, translatedXMLString);
    }

    protected void assembleMetaInf(PluginDescriptor pd, File localDestDir) throws IOException {
        File myEJBMetaInfPluginDir = new File(localDestDir, "/" + "META-INF");
        for (Extension metaInfExn : getChildExtensions(EXNPT_METAINF, pd)) {
            String dirname = metaInfExn.getParameter("dir").valueAsString();
            File dir = getFilePath(metaInfExn.getDeclaringPluginDescriptor(), dirname);
            logger.debug("Copy " + dir.getPath() + " to " + myEJBMetaInfPluginDir.getPath());
            FileUtils.copyDirectory(dir, myEJBMetaInfPluginDir, true);
        }
    }

    protected void assembleMetaInfAdaptors(PluginDescriptor pd, String contextId, File localDestDir) throws IOException {
        File myEJBMetaInfPluginDir = new File(localDestDir, "/" + "META-INF");
        PluginDescriptor abstractEJBPD = getAbstractEJBPluginDescriptor();
        ExtensionPoint abstractExnPt = abstractEJBPD.getExtensionPoint(EXNPT_METAINF_ADPTR);
        for (ExtensionPoint descendentExPt : abstractExnPt.getDescendants()) {
            if (descendentExPt.getDeclaringPluginDescriptor().getId().equals(pd.getId())) {
                for (Extension exn : descendentExPt.getConnectedExtensions()) {
                    if (exn.getParameter(EXNPT_CONTEXT_ID) == null || contextId.equals(exn.getParameter(EXNPT_CONTEXT_ID).valueAsString())) {
                        for (File srcDir : getAdaptorFiles(exn)) {
                            logger.debug("Copy " + srcDir.getPath() + " to " + myEJBMetaInfPluginDir.getPath());
                            FileUtils.copyDirectory(srcDir, myEJBMetaInfPluginDir, true);
                        }
                    }
                }
            }
        }
    }

    protected void assembleORM(PluginDescriptor pd, File localDestDir) throws IOException {
        File dest = new File(localDestDir, "META-INF/" + pd.getId() + "-orm.xml");
        logger.debug("Write ORM " + " to " + dest.getPath());
        FileUtils.writeStringToFile(dest, getORM(pd));
    }

    protected void assemblePersistenceXML(PluginDescriptor pd, File localDestDir) throws IOException {
        File dest = new File(localDestDir, "META-INF/persistence.xml");
        logger.debug("Write persistence.xml " + " to " + dest.getPath());
        FileUtils.writeStringToFile(dest, getPersistenceXML(pd));
    }

    protected void assembleProperties(PluginDescriptor pd, File localDestDir) {
        Map<String, Map<String, Set<String>>> propertyFileMapping = buildPropertyFileMapping(pd, localDestDir);
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
                    if (!isOverridden(pd, classname, name, value)) {
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
            File propertiesFile = new File(localDestDir, propertiesFilename);
            logger.debug("Write " + propertiesFile.getPath());
            propertiesFile.getParentFile().mkdirs();
            storeProperties(props, propertiesFile);
        }
    }

    protected void buildEJB(String ejbFilename, File localDestDir, File destDir) {
        File ejbFile = new File(destDir, ejbFilename);
        ejbFile.getParentFile().mkdirs();
        logger.debug("Jar " + localDestDir.getPath() + " to " + ejbFile.getPath());
        TolvenJar.jar(localDestDir, ejbFile);
    }

    /**
     * Create the PropertyMapSet used by assembleProperties
     */
    protected Map<String, Map<String, Set<String>>> buildPropertyFileMapping(PluginDescriptor pd, File localDestDir) {
        Map<String, Map<String, Set<String>>> propertyFileMapping = new HashMap<String, Map<String, Set<String>>>();
        for (Extension classesExn : getChildExtensions(EXNPT_EJB_PROPERTY, pd)) {
            String classname = classesExn.getParameter("class").valueAsString();
            File classFile = new File(localDestDir, classname.replace(".", "/") + ".class");
            if (!classFile.exists()) {
                throw new RuntimeException("Plugin " + classesExn.getUniqueId() + " requires properties for non-existent class: " + classname + " (looked in: " + classFile.getPath());
            }
            Map<String, Set<String>> propertySetMap = propertyFileMapping.get(classname);
            if (propertySetMap == null) {
                propertySetMap = new HashMap<String, Set<String>>();
                String propertiesFilename = classname.replace(".", "/") + ".properties";
                File propertiesFile = new File(propertiesFilename);
                File previousPropertiesFile = new File(localDestDir, propertiesFile.getPath());
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
            String name = classesExn.getParameter("name").valueAsString();
            Set<String> valueSet = propertySetMap.get(name);
            if (valueSet == null) {
                valueSet = new HashSet<String>();
                propertySetMap.put(name, valueSet);
            }
            valueSet.add(classesExn.getParameter("value").valueAsString());
        }
        return propertyFileMapping;
    }

    private boolean containsORMId(String ormId, Collection<Parameter> ormParameters) {
        for (Parameter ormParam : ormParameters) {
            if (ormId.equals(ormParam.valueAsString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
        logger.debug("deleting: " + getPluginTmpDir());
        FileUtils.deleteDirectory(getPluginTmpDir());
        getPluginTmpDir().mkdirs();
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        String pluginId = commandLine.getOptionValue(CMD_EJB_PLUGIN_OPT);
        String ejbFilename = commandLine.getOptionValue(CMD_EJB_FILE_OPT);
        String destDirname = commandLine.getOptionValue(CMD_DESTDIR_OPT);
        if (destDirname == null) {
            destDirname = new File(getStageDir(), pluginId).getPath();
        }
        File destDir = new File(destDirname);
        destDir.mkdirs();
        PluginDescriptor pd = getManager().getRegistry().getPluginDescriptor(pluginId);
        ExtensionPoint exnPt = pd.getExtensionPoint(EXNPT_EJBMODULE_DECL);
        if (exnPt == null) {
            throw new RuntimeException(pd.getId() + " does not have extension point: " + EXNPT_EJBMODULE_DECL);
        }
        ParameterDefinition contextIdParamDef = exnPt.getParameterDefinition(EXNPT_CONTEXT_ID);
        if (contextIdParamDef == null || contextIdParamDef.getDefaultValue() == null) {
            throw new RuntimeException(exnPt.getUniqueId() + " does not define parameter: " + EXNPT_CONTEXT_ID);
        }
        String contextId = contextIdParamDef.getDefaultValue();
        assembleEJB(pd, contextId, ejbFilename, destDir);
    }

    protected PluginDescriptor getAbstractEJBPluginDescriptor() {
        ExtensionPoint exnPt = getDescriptor().getExtensionPoint(EXNPT_ABSTRACT_EJB);
        String parentPluginId = exnPt.getParentPluginId();
        PluginDescriptor pd = getManager().getRegistry().getPluginDescriptor(parentPluginId);
        return pd;
    }

    private List<File> getAdaptorFiles(Extension exn) {
        String pluginId = exn.getParameter(EXNPT_SRC_PLUGIN_ID).valueAsString();
        if (pluginId == null || pluginId.trim().length() == 0) {
            throw new RuntimeException("No parameter value for " + EXNPT_SRC_PLUGIN_ID + " found in " + exn.getUniqueId());
        }
        String exnPtId = exn.getParameter(EXNPT_ID).valueAsString();
        if (exnPtId == null || exnPtId.trim().length() == 0) {
            throw new RuntimeException("No parameter value for " + EXNPT_ID + " found in " + exn.getUniqueId());
        }
        ExtensionPoint exnPt = getManager().getRegistry().getExtensionPoint(pluginId + "@" + exnPtId);
        List<File> files = new ArrayList<File>();
        for (ParameterDefinition paramDef : exnPt.getParameterDefinitions()) {
            String filename = paramDef.getDefaultValue();
            if (filename == null || filename.trim().length() == 0) {
                throw new RuntimeException("No default-value for parameter-def found in " + exnPt.getUniqueId());
            }
            File src = getFilePath(exnPt.getDeclaringPluginDescriptor(), filename);
            files.add(src);
        }
        return files;
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
        Option ejbPluginOption = new Option(CMD_EJB_PLUGIN_OPT, CMD_EJB_PLUGIN_OPT, true, "ejb plugin");
        ejbPluginOption.setRequired(true);
        cmdLineOptions.addOption(ejbPluginOption);
        Option ejbFilenamePluginOption = new Option(CMD_EJB_FILE_OPT, CMD_EJB_FILE_OPT, true, "ejb filename");
        ejbFilenamePluginOption.setRequired(true);
        cmdLineOptions.addOption(ejbFilenamePluginOption);
        Option destDirPluginOption = new Option(CMD_DESTDIR_OPT, CMD_DESTDIR_OPT, true, "destination directory");
        cmdLineOptions.addOption(destDirPluginOption);
        return cmdLineOptions;
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

    protected String getORM(PluginDescriptor pd) {
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
                ExtensionPoint exnPt = getMyExtensionPoint(EXNPT_ORM);
                Extension exn = exnPt.getConnectedExtensions().iterator().next();
                String ormId = exn.getParameter("ormId").valueAsString();
                if (ormId == null || ormId.length() == 0) {
                    throw new RuntimeException(exnPt.getUniqueId() + "@ormId must have a value");
                }
                addPersistenceUnitMetadata(pd, ormId, xmlStreamWriter);
                addTableGenerators(pd, ormId, xmlStreamWriter);
                addMappedSuperclasses(pd, ormId, xmlStreamWriter);
                addEntities(pd, ormId, xmlStreamWriter);
                addEmbeddables(pd, ormId, xmlStreamWriter);
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

    protected String getPersistenceXML(PluginDescriptor pd) {
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
                addPersistenceUnit(pd, xmlStreamWriter);
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

    /**
     * Return true if the specified parameter value is in the override list
     */
    protected boolean isOverridden(PluginDescriptor pd, String classname, String name, String value) {
        for (Extension classesExn : getChildExtensions(EXNPT_EJB_PROPERTY, pd)) {
            if (classname.equals(classesExn.getParameter("class").valueAsString())) {
                if (name.equals(classesExn.getParameter("name").valueAsString())) {
                    Parameter override = classesExn.getParameter("override");
                    if (override != null && value.equals(override.valueAsString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
