
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 * 				An entry in this list indicates that the Trim be made available as a top-level, 
 * 				free-standing trim to the named application. A trim is not required to be
 * 				top-level. for example, a history and physical may be composed of a number of trim elements
 * 				that never occur stand-alone. A systolic BP might be such an example because it is a component
 * 				of a blood pressure Trim. However, a Blood Pressure trim, which often occurs as part of a 
 * 				Vital Signs Trim may also be instantiated on its own.
 * 			
 * 
 * <p>Java class for Application complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Application">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="instance" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}Name" />
 *       &lt;attribute name="signatureRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Application", propOrder = {
    "instance",
    "wip"
})
public class Application
    implements Serializable
{

    @XmlElement(required = true)
    protected String instance;
    @XmlElement(required = true)
    protected String wip;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "Name")
    protected String name;
    @XmlAttribute
    protected Boolean signatureRequired;

    /**
     * Gets the value of the instance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstance() {
        return instance;
    }

    /**
     * Sets the value of the instance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstance(String value) {
        this.instance = value;
    }

    /**
     * Gets the value of the wip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWip() {
        return wip;
    }

    /**
     * Sets the value of the wip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWip(String value) {
        this.wip = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the signatureRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSignatureRequired() {
        if (signatureRequired == null) {
            return false;
        } else {
            return signatureRequired;
        }
    }

    /**
     * Sets the value of the signatureRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSignatureRequired(Boolean value) {
        this.signatureRequired = value;
    }

}
