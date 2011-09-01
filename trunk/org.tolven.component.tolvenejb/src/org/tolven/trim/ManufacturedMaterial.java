
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ManufacturedMaterial complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ManufacturedMaterial">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="expirationTime" type="{urn:tolven-org:trim:4.0}IVL_TS" minOccurs="0"/>
 *         &lt;element name="lotNumberText" type="{urn:tolven-org:trim:4.0}STSlot" minOccurs="0"/>
 *         &lt;element name="stabilityTime" type="{urn:tolven-org:trim:4.0}IVL_TS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManufacturedMaterial", propOrder = {
    "expirationTime",
    "lotNumberText",
    "stabilityTime"
})
public class ManufacturedMaterial
    implements Serializable
{

    protected IVLTS expirationTime;
    protected STSlot lotNumberText;
    protected IVLTS stabilityTime;

    /**
     * Gets the value of the expirationTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTS }
     *     
     */
    public IVLTS getExpirationTime() {
        return expirationTime;
    }

    /**
     * Sets the value of the expirationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTS }
     *     
     */
    public void setExpirationTime(IVLTS value) {
        this.expirationTime = value;
    }

    /**
     * Gets the value of the lotNumberText property.
     * 
     * @return
     *     possible object is
     *     {@link STSlot }
     *     
     */
    public STSlot getLotNumberText() {
        return lotNumberText;
    }

    /**
     * Sets the value of the lotNumberText property.
     * 
     * @param value
     *     allowed object is
     *     {@link STSlot }
     *     
     */
    public void setLotNumberText(STSlot value) {
        this.lotNumberText = value;
    }

    /**
     * Gets the value of the stabilityTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTS }
     *     
     */
    public IVLTS getStabilityTime() {
        return stabilityTime;
    }

    /**
     * Sets the value of the stabilityTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTS }
     *     
     */
    public void setStabilityTime(IVLTS value) {
        this.stabilityTime = value;
    }

}
