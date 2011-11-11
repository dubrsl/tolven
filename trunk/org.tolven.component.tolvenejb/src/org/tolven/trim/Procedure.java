
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Procedure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Procedure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="methodCode" type="{urn:tolven-org:trim:4.0}SET_CESlot" minOccurs="0"/>
 *         &lt;element name="approachSiteCode" type="{urn:tolven-org:trim:4.0}SET_CDSlot" minOccurs="0"/>
 *         &lt;element name="targetSiteCode" type="{urn:tolven-org:trim:4.0}SET_CDSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Procedure", propOrder = {
    "methodCode",
    "approachSiteCode",
    "targetSiteCode"
})
public class Procedure
    implements Serializable
{

    protected SETCESlot methodCode;
    protected SETCDSlot approachSiteCode;
    protected SETCDSlot targetSiteCode;

    /**
     * Gets the value of the methodCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCESlot }
     *     
     */
    public SETCESlot getMethodCode() {
        return methodCode;
    }

    /**
     * Sets the value of the methodCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCESlot }
     *     
     */
    public void setMethodCode(SETCESlot value) {
        this.methodCode = value;
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
     * Gets the value of the targetSiteCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCDSlot }
     *     
     */
    public SETCDSlot getTargetSiteCode() {
        return targetSiteCode;
    }

    /**
     * Sets the value of the targetSiteCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCDSlot }
     *     
     */
    public void setTargetSiteCode(SETCDSlot value) {
        this.targetSiteCode = value;
    }

}
