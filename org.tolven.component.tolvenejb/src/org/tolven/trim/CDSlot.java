
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CDSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CDSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="flavor" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="CD" type="{urn:tolven-org:trim:4.0}CD" minOccurs="0"/>
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
@XmlType(name = "CDSlot", propOrder = {
    "ce",
    "cd",
    "flavor"
})
public class CDSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "CE")
    protected CE ce;
    @XmlElement(name = "CD")
    protected CD cd;
    protected NullFlavor flavor;

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
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCD() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCD(CD value) {
        this.cd = value;
    }

    /**
     * Gets the value of the flavor property.
     * 
     * @return
     *     possible object is
     *     {@link NullFlavor }
     *     
     */
    public NullFlavor getFlavor() {
        return flavor;
    }

    /**
     * Sets the value of the flavor property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullFlavor }
     *     
     */
    public void setFlavor(NullFlavor value) {
        this.flavor = value;
    }

}
