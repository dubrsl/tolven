
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PublicHealthCase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PublicHealthCase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="detectionMethodCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="transmissionModeCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="diseaseImportedCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PublicHealthCase", propOrder = {
    "detectionMethodCode",
    "transmissionModeCode",
    "diseaseImportedCode"
})
public class PublicHealthCase
    implements Serializable
{

    protected CESlot detectionMethodCode;
    protected CESlot transmissionModeCode;
    protected CESlot diseaseImportedCode;

    /**
     * Gets the value of the detectionMethodCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getDetectionMethodCode() {
        return detectionMethodCode;
    }

    /**
     * Sets the value of the detectionMethodCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setDetectionMethodCode(CESlot value) {
        this.detectionMethodCode = value;
    }

    /**
     * Gets the value of the transmissionModeCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getTransmissionModeCode() {
        return transmissionModeCode;
    }

    /**
     * Sets the value of the transmissionModeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setTransmissionModeCode(CESlot value) {
        this.transmissionModeCode = value;
    }

    /**
     * Gets the value of the diseaseImportedCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getDiseaseImportedCode() {
        return diseaseImportedCode;
    }

    /**
     * Sets the value of the diseaseImportedCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setDiseaseImportedCode(CESlot value) {
        this.diseaseImportedCode = value;
    }

}
