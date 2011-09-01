
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Place complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Place">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mobileInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *         &lt;element name="directionsText" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="gpsText" type="{urn:tolven-org:trim:4.0}STSlot" minOccurs="0"/>
 *         &lt;element name="addr" type="{urn:tolven-org:trim:4.0}ADSlot" minOccurs="0"/>
 *         &lt;element name="positionText" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Place", propOrder = {
    "mobileInd",
    "directionsText",
    "gpsText",
    "addr",
    "positionText"
})
public class Place
    implements Serializable
{

    protected BLSlot mobileInd;
    protected EDSlot directionsText;
    protected STSlot gpsText;
    protected ADSlot addr;
    protected EDSlot positionText;

    /**
     * Gets the value of the mobileInd property.
     * 
     * @return
     *     possible object is
     *     {@link BLSlot }
     *     
     */
    public BLSlot getMobileInd() {
        return mobileInd;
    }

    /**
     * Sets the value of the mobileInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BLSlot }
     *     
     */
    public void setMobileInd(BLSlot value) {
        this.mobileInd = value;
    }

    /**
     * Gets the value of the directionsText property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getDirectionsText() {
        return directionsText;
    }

    /**
     * Sets the value of the directionsText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setDirectionsText(EDSlot value) {
        this.directionsText = value;
    }

    /**
     * Gets the value of the gpsText property.
     * 
     * @return
     *     possible object is
     *     {@link STSlot }
     *     
     */
    public STSlot getGpsText() {
        return gpsText;
    }

    /**
     * Sets the value of the gpsText property.
     * 
     * @param value
     *     allowed object is
     *     {@link STSlot }
     *     
     */
    public void setGpsText(STSlot value) {
        this.gpsText = value;
    }

    /**
     * Gets the value of the addr property.
     * 
     * @return
     *     possible object is
     *     {@link ADSlot }
     *     
     */
    public ADSlot getAddr() {
        return addr;
    }

    /**
     * Sets the value of the addr property.
     * 
     * @param value
     *     allowed object is
     *     {@link ADSlot }
     *     
     */
    public void setAddr(ADSlot value) {
        this.addr = value;
    }

    /**
     * Gets the value of the positionText property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getPositionText() {
        return positionText;
    }

    /**
     * Sets the value of the positionText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setPositionText(EDSlot value) {
        this.positionText = value;
    }

}
