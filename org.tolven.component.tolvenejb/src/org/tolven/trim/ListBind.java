
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListBind complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListBind">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="cardinality" type="{urn:tolven-org:trim:4.0}BindCardinality" default="single" />
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListBind", propOrder = {
    "path"
})
public class ListBind
    implements Serializable
{

    @XmlElement(required = true)
    protected String path;
    @XmlAttribute
    protected BindCardinality cardinality;
    @XmlAttribute
    protected Boolean optional;

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the cardinality property.
     * 
     * @return
     *     possible object is
     *     {@link BindCardinality }
     *     
     */
    public BindCardinality getCardinality() {
        if (cardinality == null) {
            return BindCardinality.SINGLE;
        } else {
            return cardinality;
        }
    }

    /**
     * Sets the value of the cardinality property.
     * 
     * @param value
     *     allowed object is
     *     {@link BindCardinality }
     *     
     */
    public void setCardinality(BindCardinality value) {
        this.cardinality = value;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isOptional() {
        if (optional == null) {
            return true;
        } else {
            return optional;
        }
    }

    /**
     * Sets the value of the optional property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptional(Boolean value) {
        this.optional = value;
    }

}
