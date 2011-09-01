
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IVL_TSSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IVL_TSSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="TS" type="{urn:tolven-org:trim:4.0}TS" minOccurs="0"/>
 *         &lt;element name="IVL_TS" type="{urn:tolven-org:trim:4.0}IVL_TS" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IVL_TSSlot", propOrder = {
    "ivlts",
    "ts",
    "_null"
})
public class IVLTSSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "IVL_TS")
    protected IVLTS ivlts;
    @XmlElement(name = "TS")
    protected TS ts;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the ivlts property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTS }
     *     
     */
    public IVLTS getIVLTS() {
        return ivlts;
    }

    /**
     * Sets the value of the ivlts property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTS }
     *     
     */
    public void setIVLTS(IVLTS value) {
        this.ivlts = value;
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
