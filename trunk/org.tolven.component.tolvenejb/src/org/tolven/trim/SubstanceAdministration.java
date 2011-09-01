
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SubstanceAdministration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubstanceAdministration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="routeCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="approachSiteCode" type="{urn:tolven-org:trim:4.0}SET_CDSlot" minOccurs="0"/>
 *         &lt;element name="doseQuantity" type="{urn:tolven-org:trim:4.0}IVL_PQSlot" minOccurs="0"/>
 *         &lt;element name="rateQuantity" type="{urn:tolven-org:trim:4.0}IVL_PQSlot" minOccurs="0"/>
 *         &lt;element name="doseCheckQuantity" type="{urn:tolven-org:trim:4.0}SET_RTOSlot" minOccurs="0"/>
 *         &lt;element name="maxDoseQuantity" type="{urn:tolven-org:trim:4.0}SET_RTOSlot" minOccurs="0"/>
 *         &lt;element name="administrationUnitCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="methodCode" type="{urn:tolven-org:trim:4.0}SET_CDSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubstanceAdministration", propOrder = {
    "routeCode",
    "approachSiteCode",
    "doseQuantity",
    "rateQuantity",
    "doseCheckQuantity",
    "maxDoseQuantity",
    "administrationUnitCode",
    "methodCode"
})
public class SubstanceAdministration
    implements Serializable
{

    protected CESlot routeCode;
    protected SETCDSlot approachSiteCode;
    protected IVLPQSlot doseQuantity;
    protected IVLPQSlot rateQuantity;
    protected SETRTOSlot doseCheckQuantity;
    protected SETRTOSlot maxDoseQuantity;
    protected CESlot administrationUnitCode;
    protected SETCDSlot methodCode;

    /**
     * Gets the value of the routeCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getRouteCode() {
        return routeCode;
    }

    /**
     * Sets the value of the routeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setRouteCode(CESlot value) {
        this.routeCode = value;
    }

    /**
     * Gets the value of the approachSiteCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCDSlot }
     *     
     */
    public SETCDSlot getApproachSiteCode() {
        return approachSiteCode;
    }

    /**
     * Sets the value of the approachSiteCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCDSlot }
     *     
     */
    public void setApproachSiteCode(SETCDSlot value) {
        this.approachSiteCode = value;
    }

    /**
     * Gets the value of the doseQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link IVLPQSlot }
     *     
     */
    public IVLPQSlot getDoseQuantity() {
        return doseQuantity;
    }

    /**
     * Sets the value of the doseQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLPQSlot }
     *     
     */
    public void setDoseQuantity(IVLPQSlot value) {
        this.doseQuantity = value;
    }

    /**
     * Gets the value of the rateQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link IVLPQSlot }
     *     
     */
    public IVLPQSlot getRateQuantity() {
        return rateQuantity;
    }

    /**
     * Sets the value of the rateQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLPQSlot }
     *     
     */
    public void setRateQuantity(IVLPQSlot value) {
        this.rateQuantity = value;
    }

    /**
     * Gets the value of the doseCheckQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link SETRTOSlot }
     *     
     */
    public SETRTOSlot getDoseCheckQuantity() {
        return doseCheckQuantity;
    }

    /**
     * Sets the value of the doseCheckQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETRTOSlot }
     *     
     */
    public void setDoseCheckQuantity(SETRTOSlot value) {
        this.doseCheckQuantity = value;
    }

    /**
     * Gets the value of the maxDoseQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link SETRTOSlot }
     *     
     */
    public SETRTOSlot getMaxDoseQuantity() {
        return maxDoseQuantity;
    }

    /**
     * Sets the value of the maxDoseQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETRTOSlot }
     *     
     */
    public void setMaxDoseQuantity(SETRTOSlot value) {
        this.maxDoseQuantity = value;
    }

    /**
     * Gets the value of the administrationUnitCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getAdministrationUnitCode() {
        return administrationUnitCode;
    }

    /**
     * Sets the value of the administrationUnitCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setAdministrationUnitCode(CESlot value) {
        this.administrationUnitCode = value;
    }

    /**
     * Gets the value of the methodCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCDSlot }
     *     
     */
    public SETCDSlot getMethodCode() {
        return methodCode;
    }

    /**
     * Sets the value of the methodCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCDSlot }
     *     
     */
    public void setMethodCode(SETCDSlot value) {
        this.methodCode = value;
    }

}
