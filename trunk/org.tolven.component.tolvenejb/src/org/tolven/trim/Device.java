
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Device complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Device">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="manufacturerModelName" type="{urn:tolven-org:trim:4.0}SCSlot" minOccurs="0"/>
 *         &lt;element name="localRemoteControlStateCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="lastCalibrationTime" type="{urn:tolven-org:trim:4.0}TSSlot" minOccurs="0"/>
 *         &lt;element name="softwareNam" type="{urn:tolven-org:trim:4.0}SCSlot" minOccurs="0"/>
 *         &lt;element name="alertLevelCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Device", propOrder = {
    "manufacturerModelName",
    "localRemoteControlStateCode",
    "lastCalibrationTime",
    "softwareNam",
    "alertLevelCode"
})
public class Device
    implements Serializable
{

    protected SCSlot manufacturerModelName;
    protected CESlot localRemoteControlStateCode;
    protected TSSlot lastCalibrationTime;
    protected SCSlot softwareNam;
    protected CESlot alertLevelCode;

    /**
     * Gets the value of the manufacturerModelName property.
     * 
     * @return
     *     possible object is
     *     {@link SCSlot }
     *     
     */
    public SCSlot getManufacturerModelName() {
        return manufacturerModelName;
    }

    /**
     * Sets the value of the manufacturerModelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link SCSlot }
     *     
     */
    public void setManufacturerModelName(SCSlot value) {
        this.manufacturerModelName = value;
    }

    /**
     * Gets the value of the localRemoteControlStateCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getLocalRemoteControlStateCode() {
        return localRemoteControlStateCode;
    }

    /**
     * Sets the value of the localRemoteControlStateCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setLocalRemoteControlStateCode(CESlot value) {
        this.localRemoteControlStateCode = value;
    }

    /**
     * Gets the value of the lastCalibrationTime property.
     * 
     * @return
     *     possible object is
     *     {@link TSSlot }
     *     
     */
    public TSSlot getLastCalibrationTime() {
        return lastCalibrationTime;
    }

    /**
     * Sets the value of the lastCalibrationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TSSlot }
     *     
     */
    public void setLastCalibrationTime(TSSlot value) {
        this.lastCalibrationTime = value;
    }

    /**
     * Gets the value of the softwareNam property.
     * 
     * @return
     *     possible object is
     *     {@link SCSlot }
     *     
     */
    public SCSlot getSoftwareNam() {
        return softwareNam;
    }

    /**
     * Sets the value of the softwareNam property.
     * 
     * @param value
     *     allowed object is
     *     {@link SCSlot }
     *     
     */
    public void setSoftwareNam(SCSlot value) {
        this.softwareNam = value;
    }

    /**
     * Gets the value of the alertLevelCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getAlertLevelCode() {
        return alertLevelCode;
    }

    /**
     * Sets the value of the alertLevelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setAlertLevelCode(CESlot value) {
        this.alertLevelCode = value;
    }

}
