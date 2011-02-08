
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Supply complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Supply">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="quantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="expectedUseTime" type="{urn:tolven-org:trim:4.0}IVL_TSSlot" minOccurs="0"/>
 *         &lt;element name="diet" type="{urn:tolven-org:trim:4.0}Diet" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Supply", propOrder = {
    "quantity",
    "expectedUseTime",
    "diet"
})
public class Supply
    implements Serializable
{

    protected PQSlot quantity;
    protected IVLTSSlot expectedUseTime;
    protected Diet diet;

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setQuantity(PQSlot value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the expectedUseTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTSSlot }
     *     
     */
    public IVLTSSlot getExpectedUseTime() {
        return expectedUseTime;
    }

    /**
     * Sets the value of the expectedUseTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTSSlot }
     *     
     */
    public void setExpectedUseTime(IVLTSSlot value) {
        this.expectedUseTime = value;
    }

    /**
     * Gets the value of the diet property.
     * 
     * @return
     *     possible object is
     *     {@link Diet }
     *     
     */
    public Diet getDiet() {
        return diet;
    }

    /**
     * Sets the value of the diet property.
     * 
     * @param value
     *     allowed object is
     *     {@link Diet }
     *     
     */
    public void setDiet(Diet value) {
        this.diet = value;
    }

}
