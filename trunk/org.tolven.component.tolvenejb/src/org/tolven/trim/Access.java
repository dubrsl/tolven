
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Access complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Access">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="approachSiteCode" type="{urn:tolven-org:trim:4.0}CDSlot" minOccurs="0"/>
 *         &lt;element name="targetSiteCode" type="{urn:tolven-org:trim:4.0}CDSlot" minOccurs="0"/>
 *         &lt;element name="gaugeQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Access", propOrder = {
    "approachSiteCode",
    "targetSiteCode",
    "gaugeQuantity"
})
public class Access
    implements Serializable
{

    protected CDSlot approachSiteCode;
    protected CDSlot targetSiteCode;
    protected PQSlot gaugeQuantity;

    /**
     * Gets the value of the approachSiteCode property.
     * 
     * @return
     *     possible object is
     *     {@link CDSlot }
     *     
     */
    public CDSlot getApproachSiteCode() {
        return approachSiteCode;
    }

    /**
     * Sets the value of the approachSiteCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CDSlot }
     *     
     */
    public void setApproachSiteCode(CDSlot value) {
        this.approachSiteCode = value;
    }

    /**
     * Gets the value of the targetSiteCode property.
     * 
     * @return
     *     possible object is
     *     {@link CDSlot }
     *     
     */
    public CDSlot getTargetSiteCode() {
        return targetSiteCode;
    }

    /**
     * Sets the value of the targetSiteCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CDSlot }
     *     
     */
    public void setTargetSiteCode(CDSlot value) {
        this.targetSiteCode = value;
    }

    /**
     * Gets the value of the gaugeQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getGaugeQuantity() {
        return gaugeQuantity;
    }

    /**
     * Sets the value of the gaugeQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setGaugeQuantity(PQSlot value) {
        this.gaugeQuantity = value;
    }

}
