
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Employee complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Employee">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jobCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="jobTitleName" type="{urn:tolven-org:trim:4.0}SCSlot" minOccurs="0"/>
 *         &lt;element name="jobClassCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="occupationCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="salaryTypeCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="salaryQuantity" type="{urn:tolven-org:trim:4.0}MOSlot" minOccurs="0"/>
 *         &lt;element name="hazardExposureText" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="protectiveEquipmentText" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Employee", propOrder = {
    "jobCode",
    "jobTitleName",
    "jobClassCode",
    "occupationCode",
    "salaryTypeCode",
    "salaryQuantity",
    "hazardExposureText",
    "protectiveEquipmentText"
})
public class Employee
    implements Serializable
{

    protected CESlot jobCode;
    protected SCSlot jobTitleName;
    protected CESlot jobClassCode;
    protected CESlot occupationCode;
    protected CESlot salaryTypeCode;
    protected MOSlot salaryQuantity;
    protected EDSlot hazardExposureText;
    protected EDSlot protectiveEquipmentText;

    /**
     * Gets the value of the jobCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getJobCode() {
        return jobCode;
    }

    /**
     * Sets the value of the jobCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setJobCode(CESlot value) {
        this.jobCode = value;
    }

    /**
     * Gets the value of the jobTitleName property.
     * 
     * @return
     *     possible object is
     *     {@link SCSlot }
     *     
     */
    public SCSlot getJobTitleName() {
        return jobTitleName;
    }

    /**
     * Sets the value of the jobTitleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link SCSlot }
     *     
     */
    public void setJobTitleName(SCSlot value) {
        this.jobTitleName = value;
    }

    /**
     * Gets the value of the jobClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getJobClassCode() {
        return jobClassCode;
    }

    /**
     * Sets the value of the jobClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setJobClassCode(CESlot value) {
        this.jobClassCode = value;
    }

    /**
     * Gets the value of the occupationCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getOccupationCode() {
        return occupationCode;
    }

    /**
     * Sets the value of the occupationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setOccupationCode(CESlot value) {
        this.occupationCode = value;
    }

    /**
     * Gets the value of the salaryTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getSalaryTypeCode() {
        return salaryTypeCode;
    }

    /**
     * Sets the value of the salaryTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setSalaryTypeCode(CESlot value) {
        this.salaryTypeCode = value;
    }

    /**
     * Gets the value of the salaryQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link MOSlot }
     *     
     */
    public MOSlot getSalaryQuantity() {
        return salaryQuantity;
    }

    /**
     * Sets the value of the salaryQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link MOSlot }
     *     
     */
    public void setSalaryQuantity(MOSlot value) {
        this.salaryQuantity = value;
    }

    /**
     * Gets the value of the hazardExposureText property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getHazardExposureText() {
        return hazardExposureText;
    }

    /**
     * Sets the value of the hazardExposureText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setHazardExposureText(EDSlot value) {
        this.hazardExposureText = value;
    }

    /**
     * Gets the value of the protectiveEquipmentText property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getProtectiveEquipmentText() {
        return protectiveEquipmentText;
    }

    /**
     * Sets the value of the protectiveEquipmentText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setProtectiveEquipmentText(EDSlot value) {
        this.protectiveEquipmentText = value;
    }

}
