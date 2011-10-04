//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.07.22 at 03:20:53 PM PDT 
//


package org.tolven.config.model.config.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ApplicationDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApplicationDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="authRestfulURL" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="appRestfulURL" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationDetail")
public class ApplicationDetail {

    @XmlAttribute(required = true)
    protected String authRestfulURL;
    @XmlAttribute(required = true)
    protected String appRestfulURL;

    /**
     * Gets the value of the authRestfulURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthRestfulURL() {
        return authRestfulURL;
    }

    /**
     * Sets the value of the authRestfulURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthRestfulURL(String value) {
        this.authRestfulURL = value;
    }

    /**
     * Gets the value of the appRestfulURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppRestfulURL() {
        return appRestfulURL;
    }

    /**
     * Sets the value of the appRestfulURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppRestfulURL(String value) {
        this.appRestfulURL = value;
    }

}