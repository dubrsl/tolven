
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Diet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Diet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="energyQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="carbohydrateQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Diet", propOrder = {
    "energyQuantity",
    "carbohydrateQuantity"
})
public class Diet
    implements Serializable
{

    protected PQSlot energyQuantity;
    protected PQSlot carbohydrateQuantity;

    /**
     * Gets the value of the energyQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getEnergyQuantity() {
        return energyQuantity;
    }

    /**
     * Sets the value of the energyQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setEnergyQuantity(PQSlot value) {
        this.energyQuantity = value;
    }

    /**
     * Gets the value of the carbohydrateQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getCarbohydrateQuantity() {
        return carbohydrateQuantity;
    }

    /**
     * Sets the value of the carbohydrateQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setCarbohydrateQuantity(PQSlot value) {
        this.carbohydrateQuantity = value;
    }

}
