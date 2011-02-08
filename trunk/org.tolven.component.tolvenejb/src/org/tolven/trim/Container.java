
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Container complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Container">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="capacityQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="capTypeCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="bottomDeltaQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="heightQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="separatorTypeCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="diameterQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="barrierDeltaQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Container", propOrder = {
    "capacityQuantity",
    "capTypeCode",
    "bottomDeltaQuantity",
    "heightQuantity",
    "separatorTypeCode",
    "diameterQuantity",
    "barrierDeltaQuantity"
})
public class Container
    implements Serializable
{

    protected PQSlot capacityQuantity;
    protected CESlot capTypeCode;
    protected PQSlot bottomDeltaQuantity;
    protected PQSlot heightQuantity;
    protected CESlot separatorTypeCode;
    protected PQSlot diameterQuantity;
    protected PQSlot barrierDeltaQuantity;

    /**
     * Gets the value of the capacityQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getCapacityQuantity() {
        return capacityQuantity;
    }

    /**
     * Sets the value of the capacityQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setCapacityQuantity(PQSlot value) {
        this.capacityQuantity = value;
    }

    /**
     * Gets the value of the capTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getCapTypeCode() {
        return capTypeCode;
    }

    /**
     * Sets the value of the capTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setCapTypeCode(CESlot value) {
        this.capTypeCode = value;
    }

    /**
     * Gets the value of the bottomDeltaQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getBottomDeltaQuantity() {
        return bottomDeltaQuantity;
    }

    /**
     * Sets the value of the bottomDeltaQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setBottomDeltaQuantity(PQSlot value) {
        this.bottomDeltaQuantity = value;
    }

    /**
     * Gets the value of the heightQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getHeightQuantity() {
        return heightQuantity;
    }

    /**
     * Sets the value of the heightQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setHeightQuantity(PQSlot value) {
        this.heightQuantity = value;
    }

    /**
     * Gets the value of the separatorTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getSeparatorTypeCode() {
        return separatorTypeCode;
    }

    /**
     * Sets the value of the separatorTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setSeparatorTypeCode(CESlot value) {
        this.separatorTypeCode = value;
    }

    /**
     * Gets the value of the diameterQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getDiameterQuantity() {
        return diameterQuantity;
    }

    /**
     * Sets the value of the diameterQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setDiameterQuantity(PQSlot value) {
        this.diameterQuantity = value;
    }

    /**
     * Gets the value of the barrierDeltaQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getBarrierDeltaQuantity() {
        return barrierDeltaQuantity;
    }

    /**
     * Sets the value of the barrierDeltaQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setBarrierDeltaQuantity(PQSlot value) {
        this.barrierDeltaQuantity = value;
    }

}
