
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Observation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Observation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="value" type="{urn:tolven-org:trim:4.0}ObservationValueSlot" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="interpretationCode" type="{urn:tolven-org:trim:4.0}SET_CDSlot" minOccurs="0"/>
 *         &lt;element name="methodCode" type="{urn:tolven-org:trim:4.0}SET_CESlot" minOccurs="0"/>
 *         &lt;element name="targetSiteCode" type="{urn:tolven-org:trim:4.0}SET_CDSlot" minOccurs="0"/>
 *         &lt;element name="publicHealthCase" type="{urn:tolven-org:trim:4.0}PublicHealthCase" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Observation", propOrder = {
    "values",
    "interpretationCode",
    "methodCode",
    "targetSiteCode",
    "publicHealthCase"
})
public class Observation
    implements Serializable
{

    @XmlElement(name = "value")
    protected List<ObservationValueSlot> values;
    protected SETCDSlot interpretationCode;
    protected SETCESlot methodCode;
    protected SETCDSlot targetSiteCode;
    protected PublicHealthCase publicHealthCase;

    /**
     * Gets the value of the values property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the values property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObservationValueSlot }
     * 
     * 
     */
    public List<ObservationValueSlot> getValues() {
        if (values == null) {
            values = new ArrayList<ObservationValueSlot>();
        }
        return this.values;
    }

    /**
     * Gets the value of the interpretationCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCDSlot }
     *     
     */
    public SETCDSlot getInterpretationCode() {
        return interpretationCode;
    }

    /**
     * Sets the value of the interpretationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCDSlot }
     *     
     */
    public void setInterpretationCode(SETCDSlot value) {
        this.interpretationCode = value;
    }

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

    /**
     * Gets the value of the publicHealthCase property.
     * 
     * @return
     *     possible object is
     *     {@link PublicHealthCase }
     *     
     */
    public PublicHealthCase getPublicHealthCase() {
        return publicHealthCase;
    }

    /**
     * Sets the value of the publicHealthCase property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublicHealthCase }
     *     
     */
    public void setPublicHealthCase(PublicHealthCase value) {
        this.publicHealthCase = value;
    }

}
