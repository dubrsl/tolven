
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PlaceholderBind complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PlaceholderBind">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="code" type="{urn:tolven-org:trim:4.0}CDSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="bindAction" type="{urn:tolven-org:trim:4.0}BindAction" default="exist" />
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlaceholderBind", propOrder = {
    "path",
    "code"
})
public class PlaceholderBind
    implements Serializable
{

    @XmlElement(required = true)
    protected String path;
    protected CDSlot code;
    @XmlAttribute
    protected BindAction bindAction;
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
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link CDSlot }
     *     
     */
    public CDSlot getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link CDSlot }
     *     
     */
    public void setCode(CDSlot value) {
        this.code = value;
    }

    /**
     * Gets the value of the bindAction property.
     * 
     * @return
     *     possible object is
     *     {@link BindAction }
     *     
     */
    public BindAction getBindAction() {
        if (bindAction == null) {
            return BindAction.EXIST;
        } else {
            return bindAction;
        }
    }

    /**
     * Sets the value of the bindAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link BindAction }
     *     
     */
    public void setBindAction(BindAction value) {
        this.bindAction = value;
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
            return false;
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
