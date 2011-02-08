
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LicensedEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LicensedEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="recertificationTime" type="{urn:tolven-org:trim:4.0}TSSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LicensedEntity", propOrder = {
    "recertificationTime"
})
public class LicensedEntity
    implements Serializable
{

    protected TSSlot recertificationTime;

    /**
     * Gets the value of the recertificationTime property.
     * 
     * @return
     *     possible object is
     *     {@link TSSlot }
     *     
     */
    public TSSlot getRecertificationTime() {
        return recertificationTime;
    }

    /**
     * Sets the value of the recertificationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TSSlot }
     *     
     */
    public void setRecertificationTime(TSSlot value) {
        this.recertificationTime = value;
    }

}
