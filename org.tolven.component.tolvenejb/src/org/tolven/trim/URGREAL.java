
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for URG_REAL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="URG_REAL">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}URG">
 *       &lt;sequence>
 *         &lt;element name="low" type="{urn:tolven-org:trim:4.0}REALSlot" minOccurs="0"/>
 *         &lt;element name="lowInclusive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="high" type="{urn:tolven-org:trim:4.0}REALSlot" minOccurs="0"/>
 *         &lt;element name="highInclusive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URG_REAL", propOrder = {
    "low",
    "lowInclusive",
    "high",
    "highInclusive"
})
public class URGREAL
    extends URG
    implements Serializable
{

    protected REALSlot low;
    protected Boolean lowInclusive;
    protected REALSlot high;
    protected Boolean highInclusive;

    /**
     * Gets the value of the low property.
     * 
     * @return
     *     possible object is
     *     {@link REALSlot }
     *     
     */
    public REALSlot getLow() {
        return low;
    }

    /**
     * Sets the value of the low property.
     * 
     * @param value
     *     allowed object is
     *     {@link REALSlot }
     *     
     */
    public void setLow(REALSlot value) {
        this.low = value;
    }

    /**
     * Gets the value of the lowInclusive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLowInclusive() {
        return lowInclusive;
    }

    /**
     * Sets the value of the lowInclusive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLowInclusive(Boolean value) {
        this.lowInclusive = value;
    }

    /**
     * Gets the value of the high property.
     * 
     * @return
     *     possible object is
     *     {@link REALSlot }
     *     
     */
    public REALSlot getHigh() {
        return high;
    }

    /**
     * Sets the value of the high property.
     * 
     * @param value
     *     allowed object is
     *     {@link REALSlot }
     *     
     */
    public void setHigh(REALSlot value) {
        this.high = value;
    }

    /**
     * Gets the value of the highInclusive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHighInclusive() {
        return highInclusive;
    }

    /**
     * Sets the value of the highInclusive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHighInclusive(Boolean value) {
        this.highInclusive = value;
    }

}
