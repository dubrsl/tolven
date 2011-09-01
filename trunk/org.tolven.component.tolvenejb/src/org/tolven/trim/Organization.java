
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Organization complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Organization">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addr" type="{urn:tolven-org:trim:4.0}ADSlot" minOccurs="0"/>
 *         &lt;element name="standardIndustryClassCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Organization", propOrder = {
    "addr",
    "standardIndustryClassCode"
})
public class Organization
    implements Serializable
{

    protected ADSlot addr;
    protected CESlot standardIndustryClassCode;

    /**
     * Gets the value of the addr property.
     * 
     * @return
     *     possible object is
     *     {@link ADSlot }
     *     
     */
    public ADSlot getAddr() {
        return addr;
    }

    /**
     * Sets the value of the addr property.
     * 
     * @param value
     *     allowed object is
     *     {@link ADSlot }
     *     
     */
    public void setAddr(ADSlot value) {
        this.addr = value;
    }

    /**
     * Gets the value of the standardIndustryClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getStandardIndustryClassCode() {
        return standardIndustryClassCode;
    }

    /**
     * Sets the value of the standardIndustryClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setStandardIndustryClassCode(CESlot value) {
        this.standardIndustryClassCode = value;
    }

}
