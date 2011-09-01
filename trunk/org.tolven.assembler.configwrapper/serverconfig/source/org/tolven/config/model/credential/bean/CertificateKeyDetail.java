//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.07.22 at 03:20:48 PM PDT 
//


package org.tolven.config.model.credential.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CertificateKeyDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CertificateKeyDetail">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:credentials:1.0}Credential">
 *       &lt;attribute name="passwordProtected" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="format" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="pem"/>
 *             &lt;enumeration value="der"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="commercial" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateKeyDetail")
public class CertificateKeyDetail
    extends Credential
{

    @XmlAttribute
    protected Boolean passwordProtected;
    @XmlAttribute(required = true)
    protected String format;
    @XmlAttribute(required = true)
    protected boolean commercial;

    /**
     * Gets the value of the passwordProtected property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isPasswordProtected() {
        if (passwordProtected == null) {
            return true;
        } else {
            return passwordProtected;
        }
    }

    /**
     * Sets the value of the passwordProtected property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPasswordProtected(Boolean value) {
        this.passwordProtected = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the commercial property.
     * 
     */
    public boolean isCommercial() {
        return commercial;
    }

    /**
     * Sets the value of the commercial property.
     * 
     */
    public void setCommercial(boolean value) {
        this.commercial = value;
    }

}
