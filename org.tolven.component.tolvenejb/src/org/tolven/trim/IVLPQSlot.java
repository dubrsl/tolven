
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IVL_PQSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IVL_PQSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="PQ" type="{urn:tolven-org:trim:4.0}PQ" minOccurs="0"/>
 *         &lt;element name="IVL_PQ" type="{urn:tolven-org:trim:4.0}IVL_PQ" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IVL_PQSlot", propOrder = {
    "ivlpq",
    "pq",
    "_null"
})
public class IVLPQSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "IVL_PQ")
    protected IVLPQ ivlpq;
    @XmlElement(name = "PQ")
    protected PQ pq;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the ivlpq property.
     * 
     * @return
     *     possible object is
     *     {@link IVLPQ }
     *     
     */
    public IVLPQ getIVLPQ() {
        return ivlpq;
    }

    /**
     * Sets the value of the ivlpq property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLPQ }
     *     
     */
    public void setIVLPQ(IVLPQ value) {
        this.ivlpq = value;
    }

    /**
     * Gets the value of the pq property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getPQ() {
        return pq;
    }

    /**
     * Sets the value of the pq property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setPQ(PQ value) {
        this.pq = value;
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
