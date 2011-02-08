
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NonPersonLivingSubject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NonPersonLivingSubject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="strainText" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="genderStatusCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NonPersonLivingSubject", propOrder = {
    "strainText",
    "genderStatusCode"
})
public class NonPersonLivingSubject
    implements Serializable
{

    protected EDSlot strainText;
    protected CESlot genderStatusCode;

    /**
     * Gets the value of the strainText property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getStrainText() {
        return strainText;
    }

    /**
     * Sets the value of the strainText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setStrainText(EDSlot value) {
        this.strainText = value;
    }

    /**
     * Gets the value of the genderStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getGenderStatusCode() {
        return genderStatusCode;
    }

    /**
     * Sets the value of the genderStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setGenderStatusCode(CESlot value) {
        this.genderStatusCode = value;
    }

}
