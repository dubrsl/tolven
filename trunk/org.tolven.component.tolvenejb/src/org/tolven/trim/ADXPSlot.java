
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ADXPSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ADXPSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;sequence>
 *         &lt;element name="type" type="{urn:tolven-org:trim:4.0}AddressPartType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *           &lt;element name="ST" type="{urn:tolven-org:trim:4.0}ST" minOccurs="0"/>
 *           &lt;element name="ED" type="{urn:tolven-org:trim:4.0}ED" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ADXPSlot", propOrder = {
    "type",
    "ed",
    "st",
    "_null"
})
public class ADXPSlot
    extends Slot
    implements Serializable
{

    protected AddressPartType type;
    @XmlElement(name = "ED")
    protected ED ed;
    @XmlElement(name = "ST")
    protected ST st;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link AddressPartType }
     *     
     */
    public AddressPartType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressPartType }
     *     
     */
    public void setType(AddressPartType value) {
        this.type = value;
    }

    /**
     * Gets the value of the ed property.
     * 
     * @return
     *     possible object is
     *     {@link ED }
     *     
     */
    public ED getED() {
        return ed;
    }

    /**
     * Sets the value of the ed property.
     * 
     * @param value
     *     allowed object is
     *     {@link ED }
     *     
     */
    public void setED(ED value) {
        this.ed = value;
    }

    /**
     * Gets the value of the st property.
     * 
     * @return
     *     possible object is
     *     {@link ST }
     *     
     */
    public ST getST() {
        return st;
    }

    /**
     * Sets the value of the st property.
     * 
     * @param value
     *     allowed object is
     *     {@link ST }
     *     
     */
    public void setST(ST value) {
        this.st = value;
    }

    /**
     * Gets the value of the null property.
     * 
     * @return
     *     possible object is
     *     {@link NullFlavor }
     *     
     */
    public NullFlavor getNull() {
        return _null;
    }

    /**
     * Sets the value of the null property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullFlavor }
     *     
     */
    public void setNull(NullFlavor value) {
        this._null = value;
    }

}
