//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.04.15 at 03:24:02 PM PDT 
//


package org.tolven.ccr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IntervalType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntervalType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:astm-org:CCR}Description" minOccurs="0"/>
 *         &lt;group ref="{urn:astm-org:CCR}MeasureGroup"/>
 *         &lt;element name="IntervalSequencePosition" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="VariableIntervalModifier" type="{urn:astm-org:CCR}CodedDescriptionType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntervalType", propOrder = {
    "description",
    "value",
    "units",
    "code",
    "intervalSequencePosition",
    "variableIntervalModifier"
})
public class IntervalType {

    @XmlElement(name = "Description")
    protected CodedDescriptionType description;
    @XmlElement(name = "Value")
    protected String value;
    @XmlElement(name = "Units")
    protected org.tolven.ccr.NormalType.Units units;
    @XmlElement(name = "Code")
    protected List<CodeType> code;
    @XmlElement(name = "IntervalSequencePosition")
    protected BigInteger intervalSequencePosition;
    @XmlElement(name = "VariableIntervalModifier")
    protected CodedDescriptionType variableIntervalModifier;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link CodedDescriptionType }
     *     
     */
    public CodedDescriptionType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodedDescriptionType }
     *     
     */
    public void setDescription(CodedDescriptionType value) {
        this.description = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the units property.
     * 
     * @return
     *     possible object is
     *     {@link org.tolven.ccr.NormalType.Units }
     *     
     */
    public org.tolven.ccr.NormalType.Units getUnits() {
        return units;
    }

    /**
     * Sets the value of the units property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.tolven.ccr.NormalType.Units }
     *     
     */
    public void setUnits(org.tolven.ccr.NormalType.Units value) {
        this.units = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the code property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodeType }
     * 
     * 
     */
    public List<CodeType> getCode() {
        if (code == null) {
            code = new ArrayList<CodeType>();
        }
        return this.code;
    }

    /**
     * Gets the value of the intervalSequencePosition property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIntervalSequencePosition() {
        return intervalSequencePosition;
    }

    /**
     * Sets the value of the intervalSequencePosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIntervalSequencePosition(BigInteger value) {
        this.intervalSequencePosition = value;
    }

    /**
     * Gets the value of the variableIntervalModifier property.
     * 
     * @return
     *     possible object is
     *     {@link CodedDescriptionType }
     *     
     */
    public CodedDescriptionType getVariableIntervalModifier() {
        return variableIntervalModifier;
    }

    /**
     * Sets the value of the variableIntervalModifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodedDescriptionType }
     *     
     */
    public void setVariableIntervalModifier(CodedDescriptionType value) {
        this.variableIntervalModifier = value;
    }

}
