
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PQSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PQSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="PQ" type="{urn:tolven-org:trim:4.0}PQ" minOccurs="0"/>
 *         &lt;element name="REAL" type="{urn:tolven-org:trim:4.0}REAL" minOccurs="0"/>
 *         &lt;element name="INT" type="{urn:tolven-org:trim:4.0}INT" minOccurs="0"/>
 *         &lt;element name="URG_PQ" type="{urn:tolven-org:trim:4.0}URG_PQ" minOccurs="0"/>
 *         &lt;element name="URG_REAL" type="{urn:tolven-org:trim:4.0}URG_REAL" minOccurs="0"/>
 *         &lt;element name="URG_INT" type="{urn:tolven-org:trim:4.0}URG_INT" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PQSlot", propOrder = {
    "urgint",
    "urgreal",
    "urgpq",
    "_int",
    "real",
    "pq",
    "_null"
})
public class PQSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "URG_INT")
    protected URGINT urgint;
    @XmlElement(name = "URG_REAL")
    protected URGREAL urgreal;
    @XmlElement(name = "URG_PQ")
    protected URGPQ urgpq;
    @XmlElement(name = "INT")
    protected INT _int;
    @XmlElement(name = "REAL")
    protected REAL real;
    @XmlElement(name = "PQ")
    protected PQ pq;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the urgint property.
     * 
     * @return
     *     possible object is
     *     {@link URGINT }
     *     
     */
    public URGINT getURGINT() {
        return urgint;
    }

    /**
     * Sets the value of the urgint property.
     * 
     * @param value
     *     allowed object is
     *     {@link URGINT }
     *     
     */
    public void setURGINT(URGINT value) {
        this.urgint = value;
    }

    /**
     * Gets the value of the urgreal property.
     * 
     * @return
     *     possible object is
     *     {@link URGREAL }
     *     
     */
    public URGREAL getURGREAL() {
        return urgreal;
    }

    /**
     * Sets the value of the urgreal property.
     * 
     * @param value
     *     allowed object is
     *     {@link URGREAL }
     *     
     */
    public void setURGREAL(URGREAL value) {
        this.urgreal = value;
    }

    /**
     * Gets the value of the urgpq property.
     * 
     * @return
     *     possible object is
     *     {@link URGPQ }
     *     
     */
    public URGPQ getURGPQ() {
        return urgpq;
    }

    /**
     * Sets the value of the urgpq property.
     * 
     * @param value
     *     allowed object is
     *     {@link URGPQ }
     *     
     */
    public void setURGPQ(URGPQ value) {
        this.urgpq = value;
    }

    /**
     * Gets the value of the int property.
     * 
     * @return
     *     possible object is
     *     {@link INT }
     *     
     */
    public INT getINT() {
        return _int;
    }

    /**
     * Sets the value of the int property.
     * 
     * @param value
     *     allowed object is
     *     {@link INT }
     *     
     */
    public void setINT(INT value) {
        this._int = value;
    }

    /**
     * Gets the value of the real property.
     * 
     * @return
     *     possible object is
     *     {@link REAL }
     *     
     */
    public REAL getREAL() {
        return real;
    }

    /**
     * Sets the value of the real property.
     * 
     * @param value
     *     allowed object is
     *     {@link REAL }
     *     
     */
    public void setREAL(REAL value) {
        this.real = value;
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
