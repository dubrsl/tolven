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

import org.java.plugin.registry.ExtensionMultiplicity;
import org.java.plugin.registry.MatchingRule;
import org.java.plugin.registry.ParameterMultiplicity;
import org.java.plugin.registry.ParameterType;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class ManifestHandler extends BaseHandler {
    private ModelPluginManifest manifest = null;
    private ModelDocumentation documentation = null;
    private ModelPrerequisite prerequisite;
    private ModelLibrary library;
    private ModelExtensionPoint extensionPoint;
    private ModelExtension extension;
    private StringBuilder docText = null;
    private SimpleStack<ModelAttribute> attributeStack = null;
    private ModelAttribute attribute;
    private SimpleStack<ModelParameterDef> paramDefStack = null;
    private ModelParameterDef paramDef;
    private SimpleStack<ModelParameter> paramStack = null;
    private ModelParameter param;
    private StringBuilder paramValue = null;

    public ManifestHandler(final EntityResolver anEntityResolver) {
        super(anEntityResolver);
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (log.isDebugEnabled()) {
            //log.debug("startElement - [" + uri + "]/[" + localName + "]/[" + qName + "]");
        }
        String name = qName;
        if ("plugin".equals(name)) {
            if (manifest != null) {
                throw new SAXException("unexpected [" + name + "] element (manifest already defined)");
            }
            manifest = new ModelPluginDescriptor();
            manifest.setId(attributes.getValue("id"));
            manifest.setVersion(attributes.getValue("version"));
            manifest.setVendor(attributes.getValue("vendor"));
            manifest.setDocsPath(attributes.getValue("docs-path"));
            ((ModelPluginDescriptor) manifest).setClassName(attributes.getValue("class"));
        } else if ("plugin-fragment".equals(name)) {
            if (manifest != null) {
                throw new SAXException("unexpected [" + name + "] element (manifest already defined)");
            }
            manifest = new ModelPluginFragment();
            manifest.setId(attributes.getValue("id"));
            manifest.setVersion(attributes.getValue("version"));
            manifest.setVendor(attributes.getValue("vendor"));
            manifest.setDocsPath(attributes.getValue("docs-path"));
            ((ModelPluginFragment) manifest).setPluginId(attributes.getValue("plugin-id"));
            if (attributes.getValue("plugin-version") != null) {
                ((ModelPluginFragment) manifest).setPluginVersion(attributes.getValue("plugin-version"));
            }
            if (attributes.getValue("match") != null) {
                ((ModelPluginFragment) manifest).setMatchingRule(MatchingRule.fromCode(attributes.getValue("match")));
            } else {
                ((ModelPluginFragment) manifest).setMatchingRule(MatchingRule.COMPATIBLE);
            }
        } else if ("doc".equals(name)) {
            documentation = new ModelDocumentation();
            documentation.setCaption(attributes.getValue("caption"));
        } else if ("doc-ref".equals(name)) {
            if (documentation == null) {
                if (entityResolver != null) {
                    throw new SAXException("[doc-ref] element found " + "outside of [doc] element");
                }
                // ignore this element
                //log.warn("[doc-ref] element found outside of [doc] element");
                return;
            }
            ModelDocumentationReference docRef = new ModelDocumentationReference();
            docRef.setPath(attributes.getValue("path"));
            docRef.setCaption(attributes.getValue("caption"));
            documentation.getReferences().add(docRef);
        } else if ("doc-text".equals(name)) {
            if (documentation == null) {
                if (entityResolver != null) {
                    throw new SAXException("[doc-text] element found " + "outside of [doc] element");
                }
                // ignore this element
                //log.warn("[doc-text] element found outside of [doc] element");
                return;
            }
            docText = new StringBuilder();
        } else if ("attributes".equals(name)) {
            attributeStack = new SimpleStack<ModelAttribute>();
        } else if ("attribute".equals(name)) {
            if (attributeStack == null) {
                if (entityResolver != null) {
                    throw new SAXException("[attribute] element found " + "outside of [attributes] element");
                }
                // ignore this element
                //log.warn("[attribute] element found " + "outside of [attributes] element");
                return;
            }
            if (attribute != null) {
                attributeStack.push(attribute);
            }
            attribute = new ModelAttribute();
            attribute.setId(attributes.getValue("id"));
            attribute.setValue(attributes.getValue("value"));
        } else if ("requires".equals(name)) {
            // no-op
        } else if ("import".equals(name)) {
            prerequisite = new ModelPrerequisite();
            if (attributes.getValue("id") != null) {
                prerequisite.setId(attributes.getValue("id"));
            }
            prerequisite.setPluginId(attributes.getValue("plugin-id"));
            if (attributes.getValue("plugin-version") != null) {
                prerequisite.setPluginVersion(attributes.getValue("plugin-version"));
            }
            if (attributes.getValue("match") != null) {
                prerequisite.setMatchingRule(MatchingRule.fromCode(attributes.getValue("match")));
            } else {
                prerequisite.setMatchingRule(MatchingRule.COMPATIBLE);
            }
            prerequisite.setExported(attributes.getValue("exported"));
            prerequisite.setOptional(attributes.getValue("optional"));
            prerequisite.setReverseLookup(attributes.getValue("reverse-lookup"));
        } else if ("runtime".equals(name)) {
            // no-op
        } else if ("library".equals(name)) {
            library = new ModelLibrary();
            library.setId(attributes.getValue("id"));
            library.setPath(attributes.getValue("path"));
            library.setCodeLibrary(attributes.getValue("type"));
            if (attributes.getValue("version") != null) {
                library.setVersion(attributes.getValue("version"));
            }
        } else if ("export".equals(name)) {
            if (library == null) {
                if (entityResolver != null) {
                    throw new SAXException("[export] element found " + "outside of [library] element");
                }
                // ignore this element
                //log.warn("[export] element found outside of [library] element");
                return;
            }
            library.getExports().add(attributes.getValue("prefix"));
        } else if ("extension-point".equals(name)) {
            extensionPoint = new ModelExtensionPoint();
            extensionPoint.setId(attributes.getValue("id"));
            extensionPoint.setParentPluginId(attributes.getValue("parent-plugin-id"));
            extensionPoint.setParentPointId(attributes.getValue("parent-point-id"));
            if (attributes.getValue("extension-multiplicity") != null) {
                extensionPoint.setExtensionMultiplicity(ExtensionMultiplicity.fromCode(attributes.getValue("extension-multiplicity")));
            } else {
                extensionPoint.setExtensionMultiplicity(ExtensionMultiplicity.ANY);
            }
            paramDefStack = new SimpleStack<ModelParameterDef>();
        } else if ("parameter-def".equals(name)) {
            if (extensionPoint == null) {
                if (entityResolver != null) {
                    throw new SAXException("[parameter-def] element found " + "outside of [extension-point] element");
                }
                // ignore this element
                //log.warn("[parameter-def] element found " + "outside of [extension-point] element");
                return;
            }
            if (paramDef != null) {
                paramDefStack.push(paramDef);
            }
            paramDef = new ModelParameterDef();
            paramDef.setId(attributes.getValue("id"));
            if (attributes.getValue("multiplicity") != null) {
                paramDef.setMultiplicity(ParameterMultiplicity.fromCode(attributes.getValue("multiplicity")));
            } else {
                paramDef.setMultiplicity(ParameterMultiplicity.ONE);
            }
            if (attributes.getValue("type") != null) {
                paramDef.setType(ParameterType.fromCode(attributes.getValue("type")));
            } else {
                paramDef.setType(ParameterType.STRING);
            }
            paramDef.setCustomData(attributes.getValue("custom-data"));
            paramDef.setDefaultValue(attributes.getValue("default-value"));
        } else if ("extension".equals(name)) {
            extension = new ModelExtension();
            extension.setId(attributes.getValue("id"));
            extension.setPluginId(attributes.getValue("plugin-id"));
            extension.setPointId(attributes.getValue("point-id"));
            paramStack = new SimpleStack<ModelParameter>();
        } else if ("parameter".equals(name)) {
            if (extension == null) {
                if (entityResolver != null) {
                    throw new SAXException("[parameter] element found " + "outside of [extension] element");
                }
                // ignore this element
                //log.warn("[parameter] element found " + "outside of [extension] element");
                return;
            }
            if (param != null) {
                paramStack.push(param);
            }
            param = new ModelParameter();
            param.setId(attributes.getValue("id"));
            param.setValue(attributes.getValue("value"));
        } else if ("value".equals(name)) {
            if (param == null) {
                if (entityResolver != null) {
                    throw new SAXException("[value] element found " + "outside of [parameter] element");
                }
                // ignore this element
                //log.warn("[value] element found " + "outside of [parameter] element");
                return;
            }
            paramValue = new StringBuilder();
        } else {
            if (entityResolver != null) {
                throw new SAXException("unexpected manifest element - [" + uri + "]/[" + localName + "]/[" + qName + "]"); //$NON-NLS-3$
            }
            // ignore this element
            //log.warn("unexpected manifest element - [" + uri + "]/[" + localName + "]/[" + qName + "]");
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) {
        if (log.isDebugEnabled()) {
            //log.debug("endElement - [" + uri + "]/[" + localName + "]/[" + qName + "]");
        }
        String name = qName;
        if ("plugin".equals(name)) {
            // no-op
        } else if ("plugin-fragment".equals(name)) {
            // no-op
        } else if ("doc".equals(name)) {
            if (param != null) {
                param.setDocumentation(documentation);
            } else if (extension != null) {
                extension.setDocumentation(documentation);
            } else if (paramDef != null) {
                paramDef.setDocumentation(documentation);
            } else if (extensionPoint != null) {
                extensionPoint.setDocumentation(documentation);
            } else if (library != null) {
                library.setDocumentation(documentation);
            } else if (prerequisite != null) {
                prerequisite.setDocumentation(documentation);
            } else if (attribute != null) {
                attribute.setDocumentation(documentation);
            } else {
                manifest.setDocumentation(documentation);
            }
            documentation = null;
        } else if ("doc-ref".equals(name)) {
            // no-op
        } else if ("doc-text".equals(name)) {
            documentation.setText(docText.toString());
            docText = null;
        } else if ("attributes".equals(name)) {
            attributeStack = null;
        } else if ("attribute".equals(name)) {
            if (attributeStack.size() == 0) {
                manifest.getAttributes().add(attribute);
                attribute = null;
            } else {
                ModelAttribute temp = attribute;
                attribute = attributeStack.pop();
                attribute.getAttributes().add(temp);
                temp = null;
            }
        } else if ("requires".equals(name)) {
            // no-op
        } else if ("import".equals(name)) {
            manifest.getPrerequisites().add(prerequisite);
            prerequisite = null;
        } else if ("runtime".equals(name)) {
            // no-op
        } else if ("library".equals(name)) {
            manifest.getLibraries().add(library);
            library = null;
        } else if ("export".equals(name)) {
            // no-op
        } else if ("extension-point".equals(name)) {
            manifest.getExtensionPoints().add(extensionPoint);
            extensionPoint = null;
            paramDefStack = null;
        } else if ("parameter-def".equals(name)) {
            if (paramDefStack.size() == 0) {
                extensionPoint.getParamDefs().add(paramDef);
                paramDef = null;
            } else {
                ModelParameterDef temp = paramDef;
                paramDef = paramDefStack.pop();
                paramDef.getParamDefs().add(temp);
                temp = null;
            }
        } else if ("extension".equals(name)) {
            manifest.getExtensions().add(extension);
            extension = null;
            paramStack = null;
        } else if ("parameter".equals(name)) {
            if (paramStack.size() == 0) {
                extension.getParams().add(param);
                param = null;
            } else {
                ModelParameter temp = param;
                param = paramStack.pop();
                param.getParams().add(temp);
                temp = null;
            }
        } else if ("value".equals(name)) {
            param.setValue(paramValue.toString());
            paramValue = null;
        } else {
            // ignore any other element
            //log.warn("ignoring manifest element - [" + uri + "]/[" + localName + "]/[" + qName + "]");
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (docText != null) {
            docText.append(ch, start, length);
        } else if (paramValue != null) {
            paramValue.append(ch, start, length);
        } else {
            if (entityResolver != null) {
                throw new SAXException("unexpected character data");
            }
            // ignore these characters
            //log.warn("ignoring character data - [" + new String(ch, start, length) + "]");
        }
    }

    public ModelPluginManifest getResult() {
        return manifest;
    }
}
