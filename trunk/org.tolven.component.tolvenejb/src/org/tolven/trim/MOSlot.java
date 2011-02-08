
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MOSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MOSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="MO" type="{urn:tolven-org:trim:4.0}PQ" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MOSlot", propOrder = {
    "mo",
    "_null"
})
public class MOSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "MO")
    protected PQ mo;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the mo property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getMO() {
        return mo;
    }

    /**
     * Sets the value of the mo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setMO(PQ value) {
        this.mo = value;
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
