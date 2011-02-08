
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SCSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SCSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="ST" type="{urn:tolven-org:trim:4.0}ST" minOccurs="0"/>
 *         &lt;element name="SC" type="{urn:tolven-org:trim:4.0}SC" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SCSlot", propOrder = {
    "sc",
    "st",
    "_null"
})
public class SCSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "SC")
    protected SC sc;
    @XmlElement(name = "ST")
    protected ST st;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the sc property.
     * 
     * @return
     *     possible object is
     *     {@link SC }
     *     
     */
    public SC getSC() {
        return sc;
    }

    /**
     * Sets the value of the sc property.
     * 
     * @param value
     *     allowed object is
     *     {@link SC }
     *     
     */
    public void setSC(SC value) {
        this.sc = value;
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
