
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IVL_PQ complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IVL_PQ">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}DataType">
 *       &lt;sequence>
 *         &lt;element name="low" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="lowInclusive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="high" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="highInclusive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="center" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="width" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IVL_PQ", propOrder = {
    "low",
    "lowInclusive",
    "high",
    "highInclusive",
    "center",
    "width"
})
public class IVLPQ
    extends DataType
    implements Serializable
{

    protected PQSlot low;
    protected Boolean lowInclusive;
    protected PQSlot high;
    protected Boolean highInclusive;
    protected PQSlot center;
    protected PQSlot width;

    /**
     * Gets the value of the low property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getLow() {
        return low;
    }

    /**
     * Sets the value of the low property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setLow(PQSlot value) {
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
     *     {@link PQSlot }
     *     
     */
    public PQSlot getHigh() {
        return high;
    }

    /**
     * Sets the value of the high property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setHigh(PQSlot value) {
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

    /**
     * Gets the value of the center property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getCenter() {
        return center;
    }

    /**
     * Sets the value of the center property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setCenter(PQSlot value) {
        this.center = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setWidth(PQSlot value) {
        this.width = value;
    }

}
