//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.07.03 at 03:14:40 AM PDT 
//


package org.tolven.plugin.repository.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PluginDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PluginDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="version" type="{urn:tolven-org:plugins:1.0}PluginVersionDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dependent" type="{urn:tolven-org:plugins:1.0}DependentPluginDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="root" type="{urn:tolven-org:plugins:1.0}RootPluginDetail" minOccurs="0"/>
 *         &lt;element name="property" type="{urn:tolven-org:plugins:1.0}PluginPropertyDetail" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="useVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PluginDetail", propOrder = {
    "version",
    "dependent",
    "root",
    "property"
})
public class PluginDetail {

    protected List<PluginVersionDetail> version;
    protected List<DependentPluginDetail> dependent;
    protected RootPluginDetail root;
    protected List<PluginPropertyDetail> property;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute
    protected String useVersion;

    /**
     * Gets the value of the version property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the version property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PluginVersionDetail }
     * 
     * 
     */
    public List<PluginVersionDetail> getVersion() {
        if (version == null) {
            version = new ArrayList<PluginVersionDetail>();
        }
        return this.version;
    }

    /**
     * Gets the value of the dependent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dependent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDependent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DependentPluginDetail }
     * 
     * 
     */
    public List<DependentPluginDetail> getDependent() {
        if (dependent == null) {
            dependent = new ArrayList<DependentPluginDetail>();
        }
        return this.dependent;
    }

    /**
     * Gets the value of the root property.
     * 
     * @return
     *     possible object is
     *     {@link RootPluginDetail }
     *     
     */
    public RootPluginDetail getRoot() {
        return root;
    }

    /**
     * Sets the value of the root property.
     * 
     * @param value
     *     allowed object is
     *     {@link RootPluginDetail }
     *     
     */
    public void setRoot(RootPluginDetail value) {
        this.root = value;
    }

    /**
     * Gets the value of the property property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PluginPropertyDetail }
     * 
     * 
     */
    public List<PluginPropertyDetail> getProperty() {
        if (property == null) {
            property = new ArrayList<PluginPropertyDetail>();
        }
        return this.property;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the useVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUseVersion() {
        return useVersion;
    }

    /**
     * Sets the value of the useVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUseVersion(String value) {
        this.useVersion = value;
    }

}