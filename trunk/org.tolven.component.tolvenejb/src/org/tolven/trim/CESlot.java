
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CESlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CESlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="CE" type="{urn:tolven-org:trim:4.0}CE" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CESlot", propOrder = {
    "ce",
    "_null"
})
public class CESlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "CE")
    protected CE ce;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the ce property.
     * 
     * @return
     *     possible object is
     *     {@link CE }
     *     
     */
    public CE getCE() {
        return ce;
    }

    /**
     * Sets the value of the ce property.
     * 
     * @param value
     *     allowed object is
     *     {@link CE }
     *     
     */
    public void setCE(CE value) {
        this.ce = value;
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
