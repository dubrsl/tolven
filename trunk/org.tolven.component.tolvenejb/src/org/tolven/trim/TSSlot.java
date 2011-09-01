
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TSSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TSSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="TS" type="{urn:tolven-org:trim:4.0}TS" minOccurs="0"/>
 *         &lt;element name="URG_TS" type="{urn:tolven-org:trim:4.0}URG_TS" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TSSlot", propOrder = {
    "urgts",
    "ts",
    "_null"
})
public class TSSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "URG_TS")
    protected URGTS urgts;
    @XmlElement(name = "TS")
    protected TS ts;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the urgts property.
     * 
     * @return
     *     possible object is
     *     {@link URGTS }
     *     
     */
    public URGTS getURGTS() {
        return urgts;
    }

    /**
     * Sets the value of the urgts property.
     * 
     * @param value
     *     allowed object is
     *     {@link URGTS }
     *     
     */
    public void setURGTS(URGTS value) {
        this.urgts = value;
    }

    /**
     * Gets the value of the ts property.
     * 
     * @return
     *     possible object is
     *     {@link TS }
     *     
     */
    public TS getTS() {
        return ts;
    }

    /**
     * Sets the value of the ts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TS }
     *     
     */
    public void setTS(TS value) {
        this.ts = value;
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
