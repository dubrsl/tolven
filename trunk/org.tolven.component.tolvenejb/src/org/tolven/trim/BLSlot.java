
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BLSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BLSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="BL" type="{urn:tolven-org:trim:4.0}BL" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BLSlot", propOrder = {
    "bl",
    "_null"
})
public class BLSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "BL")
    protected BL bl;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the bl property.
     * 
     * @return
     *     possible object is
     *     {@link BL }
     *     
     */
    public BL getBL() {
        return bl;
    }

    /**
     * Sets the value of the bl property.
     * 
     * @param value
     *     allowed object is
     *     {@link BL }
     *     
     */
    public void setBL(BL value) {
        this.bl = value;
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
