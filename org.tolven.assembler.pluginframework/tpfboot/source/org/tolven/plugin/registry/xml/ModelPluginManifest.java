/*****************************************************************************
 * Java Plug-in Framework (JPF)
 * Copyright (C) 2004-2007 Dmitry Olshansky
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *****************************************************************************/
package org.tolven.plugin.registry.xml;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.java.plugin.registry.ExtensionMultiplicity;
import org.java.plugin.registry.MatchingRule;
import org.java.plugin.registry.ParameterMultiplicity;
import org.java.plugin.registry.ParameterType;
import org.java.plugin.registry.Version;

public abstract class ModelPluginManifest {
    private URL location;
    private String id;
    private Version version;
    private String vendor;
    private String docsPath;
    private ModelDocumentation documentation;
    private LinkedList<ModelAttribute> attributes = new LinkedList<ModelAttribute>();
    private LinkedList<ModelPrerequisite> prerequisites = new LinkedList<ModelPrerequisite>();
    private LinkedList<ModelLibrary> libraries = new LinkedList<ModelLibrary>();
    private LinkedList<ModelExtensionPoint> extensionPoints = new LinkedList<ModelExtensionPoint>();
    private LinkedList<ModelExtension> extensions = new LinkedList<ModelExtension>();

    public URL getLocation() {
        return location;
    }

    public void setLocation(URL value) {
        location = value;
    }

    public String getDocsPath() {
        return docsPath;
    }

    public void setDocsPath(String value) {
        docsPath = value;
    }

    public ModelDocumentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ModelDocumentation value) {
        documentation = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String value) {
        vendor = value;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(String value) {
        version = Version.parse(value);
    }

    public List<ModelAttribute> getAttributes() {
        return attributes;
    }

    public List<ModelExtensionPoint> getExtensionPoints() {
        return extensionPoints;
    }

    public List<ModelExtension> getExtensions() {
        return extensions;
    }

    public List<ModelLibrary> getLibraries() {
        return libraries;
    }

    public List<ModelPrerequisite> getPrerequisites() {
        return prerequisites;
    }
}

class ModelPluginDescriptor extends ModelPluginManifest {
    private String className;

    public ModelPluginDescriptor() {
        // no-op
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String value) {
        className = value;
    }
}

class ModelDocumentation {
    private LinkedList<ModelDocumentationReference> references = new LinkedList<ModelDocumentationReference>();
    private String caption;
    private String text;

    public ModelDocumentation() {
        // no-op
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String value) {
        caption = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }

    public List<ModelDocumentationReference> getReferences() {
        return references;
    }
}

class ModelDocumentationReference {
    private String path;
    private String caption;

    public ModelDocumentationReference() {
        // no-op
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String value) {
        caption = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String value) {
        path = value;
    }
}

class ModelAttribute {
    private String id;
    private String value;
    private ModelDocumentation documentation;
    private LinkedList<ModelAttribute> attributes = new LinkedList<ModelAttribute>();

    public ModelAttribute() {
        // no-op
    }

    public ModelDocumentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ModelDocumentation aValue) {
        documentation = aValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String aValue) {
        id = aValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String aValue) {
        value = aValue;
    }

    public List<ModelAttribute> getAttributes() {
        return attributes;
    }
}

class ModelLibrary {
    private String id;
    private String path;
    private boolean isCodeLibrary;
    private ModelDocumentation documentation;
    private LinkedList<String> exports = new LinkedList<String>();
    private Version version;

    public ModelLibrary() {
        // no-op
    }

    public ModelDocumentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ModelDocumentation value) {
        documentation = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public boolean isCodeLibrary() {
        return isCodeLibrary;
    }

    public void setCodeLibrary(String value) {
        isCodeLibrary = "code".equals(value);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String value) {
        path = value;
    }

    public List<String> getExports() {
        return exports;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(String value) {
        version = Version.parse(value);
    }
}

class ModelExtensionPoint {
    private String id;
    private String parentPluginId;
    private String parentPointId;
    private ExtensionMultiplicity extensionMultiplicity = ExtensionMultiplicity.ONE;
    private ModelDocumentation documentation;
    private LinkedList<ModelParameterDef> paramDefs = new LinkedList<ModelParameterDef>();

    public ModelExtensionPoint() {
        // no-op
    }

    public ModelDocumentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ModelDocumentation value) {
        documentation = value;
    }

    public ExtensionMultiplicity getExtensionMultiplicity() {
        return extensionMultiplicity;
    }

    public void setExtensionMultiplicity(ExtensionMultiplicity value) {
        extensionMultiplicity = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getParentPluginId() {
        return parentPluginId;
    }

    public void setParentPluginId(String value) {
        parentPluginId = value;
    }

    public String getParentPointId() {
        return parentPointId;
    }

    public void setParentPointId(String value) {
        parentPointId = value;
    }

    public List<ModelParameterDef> getParamDefs() {
        return paramDefs;
    }
}

class ModelParameterDef {
    private String id;
    private ParameterMultiplicity multiplicity = ParameterMultiplicity.ONE;
    private ParameterType type = ParameterType.STRING;
    private String customData;
    private ModelDocumentation documentation;
    private LinkedList<ModelParameterDef> paramDefs = new LinkedList<ModelParameterDef>();
    private String defaultValue;

    public ModelParameterDef() {
        // no-op
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String value) {
        customData = value;
    }

    public ModelDocumentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ModelDocumentation value) {
        documentation = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public ParameterMultiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(ParameterMultiplicity value) {
        multiplicity = value;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType value) {
        type = value;
    }

    public List<ModelParameterDef> getParamDefs() {
        return paramDefs;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String value) {
        defaultValue = value;
    }
}

class ModelExtension {
    private String id;
    private String pluginId;
    private String pointId;
    private ModelDocumentation documentation;
    private LinkedList<ModelParameter> params = new LinkedList<ModelParameter>();

    public ModelExtension() {
        // no-op
    }

    public ModelDocumentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ModelDocumentation value) {
        documentation = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String value) {
        pluginId = value;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String value) {
        pointId = value;
    }

    public List<ModelParameter> getParams() {
        return params;
    }
}

class ModelParameter {
    private String id;
    private String value;
    private ModelDocumentation documentation;
    private LinkedList<ModelParameter> params = new LinkedList<ModelParameter>();

    public ModelParameter() {
        // no-op
    }

    public ModelDocumentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ModelDocumentation aValue) {
        documentation = aValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String aValue) {
        id = aValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String aValue) {
        value = aValue;
    }

    public List<ModelParameter> getParams() {
        return params;
    }
}

class ModelManifestInfo {
    private String id;
    private Version version;
    private String vendor;
    private String pluginId;
    private Version pluginVersion;
    private MatchingRule matchingRule = MatchingRule.COMPATIBLE;

    public ModelManifestInfo() {
        // no-op
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String value) {
        vendor = value;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(String value) {
        version = Version.parse(value);
    }

    public MatchingRule getMatchRule() {
        return matchingRule;
    }

    public void setMatchingRule(MatchingRule value) {
        matchingRule = value;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String value) {
        pluginId = value;
    }

    public Version getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String value) {
        pluginVersion = Version.parse(value);
    }
}
