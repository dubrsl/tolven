
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PatientEncounter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PatientEncounter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="admissionReferralSourceCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="lengthOfStayQuantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="dischargeDispositionCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="preAdmitTestInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *         &lt;element name="specialCourtesiesCode" type="{urn:tolven-org:trim:4.0}SET_CESlot" minOccurs="0"/>
 *         &lt;element name="specialArrangementCode" type="{urn:tolven-org:trim:4.0}SET_CESlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PatientEncounter", propOrder = {
    "admissionReferralSourceCode",
    "lengthOfStayQuantity",
    "dischargeDispositionCode",
    "preAdmitTestInd",
    "specialCourtesiesCode",
    "specialArrangementCode"
})
public class PatientEncounter
    implements Serializable
{

    protected CESlot admissionReferralSourceCode;
    protected PQSlot lengthOfStayQuantity;
    protected CESlot dischargeDispositionCode;
    protected BLSlot preAdmitTestInd;
    protected SETCESlot specialCourtesiesCode;
    protected SETCESlot specialArrangementCode;

    /**
     * Gets the value of the admissionReferralSourceCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getAdmissionReferralSourceCode() {
        return admissionReferralSourceCode;
    }

    /**
     * Sets the value of the admissionReferralSourceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setAdmissionReferralSourceCode(CESlot value) {
        this.admissionReferralSourceCode = value;
    }

    /**
     * Gets the value of the lengthOfStayQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getLengthOfStayQuantity() {
        return lengthOfStayQuantity;
    }

    /**
     * Sets the value of the lengthOfStayQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setLengthOfStayQuantity(PQSlot value) {
        this.lengthOfStayQuantity = value;
    }

    /**
     * Gets the value of the dischargeDispositionCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getDischargeDispositionCode() {
        return dischargeDispositionCode;
    }

    /**
     * Sets the value of the dischargeDispositionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setDischargeDispositionCode(CESlot value) {
        this.dischargeDispositionCode = value;
    }

    /**
     * Gets the value of the preAdmitTestInd property.
     * 
     * @return
     *     possible object is
     *     {@link BLSlot }
     *     
     */
    public BLSlot getPreAdmitTestInd() {
        return preAdmitTestInd;
    }

    /**
     * Sets the value of the preAdmitTestInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BLSlot }
     *     
     */
    public void setPreAdmitTestInd(BLSlot value) {
        this.preAdmitTestInd = value;
    }

    /**
     * Gets the value of the specialCourtesiesCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCESlot }
     *     
     */
    public SETCESlot getSpecialCourtesiesCode() {
        return specialCourtesiesCode;
    }

    /**
     * Sets the value of the specialCourtesiesCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCESlot }
     *     
     */
    public void setSpecialCourtesiesCode(SETCESlot value) {
        this.specialCourtesiesCode = value;
    }

    /**
     * Gets the value of the specialArrangementCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCESlot }
     *     
     */
    public SETCESlot getSpecialArrangementCode() {
        return specialArrangementCode;
    }

    /**
     * Sets the value of the specialArrangementCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCESlot }
     *     
     */
    public void setSpecialArrangementCode(SETCESlot value) {
        this.specialArrangementCode = value;
    }

}
