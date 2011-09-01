
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RTOSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RTOSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="RTO" type="{urn:tolven-org:trim:4.0}RTO" minOccurs="0"/>
 *         &lt;element name="REAL" type="{urn:tolven-org:trim:4.0}REAL" minOccurs="0"/>
 *         &lt;element name="INT" type="{urn:tolven-org:trim:4.0}INT" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RTOSlot", propOrder = {
    "_int",
    "real",
    "rto",
    "_null"
})
public class RTOSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "INT")
    protected INT _int;
    @XmlElement(name = "REAL")
    protected REAL real;
    @XmlElement(name = "RTO")
    protected RTO rto;
    @XmlElement(name = "null")
    protected NullFlavor _null;

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
     * Gets the value of the rto property.
     * 
     * @return
     *     possible object is
     *     {@link RTO }
     *     
     */
    public RTO getRTO() {
        return rto;
    }

    /**
     * Sets the value of the rto property.
     * 
     * @param value
     *     allowed object is
     *     {@link RTO }
     *     
     */
    public void setRTO(RTO value) {
        this.rto = value;
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
