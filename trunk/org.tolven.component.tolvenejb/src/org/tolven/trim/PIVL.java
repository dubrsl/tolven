
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PIVL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PIVL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="phaseLow" type="{urn:tolven-org:trim:4.0}TSSlot" minOccurs="0"/>
 *         &lt;element name="phaseHigh" type="{urn:tolven-org:trim:4.0}TSSlot" minOccurs="0"/>
 *         &lt;element name="period" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="frequencyRepeat" type="{urn:tolven-org:trim:4.0}INTSlot" minOccurs="0"/>
 *         &lt;element name="frequencyTime" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="count" type="{urn:tolven-org:trim:4.0}INTSlot" minOccurs="0"/>
 *         &lt;element name="displayFrequencyInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *         &lt;element name="alignment" type="{urn:tolven-org:trim:4.0}CSSlot" minOccurs="0"/>
 *         &lt;element name="isFlexible" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PIVL", propOrder = {
    "phaseLow",
    "phaseHigh",
    "period",
    "frequencyRepeat",
    "frequencyTime",
    "count",
    "displayFrequencyInd",
    "alignment",
    "isFlexible"
})
public class PIVL
    implements Serializable
{

    protected TSSlot phaseLow;
    protected TSSlot phaseHigh;
    protected PQSlot period;
    protected INTSlot frequencyRepeat;
    protected PQSlot frequencyTime;
    protected INTSlot count;
    protected BLSlot displayFrequencyInd;
    protected CSSlot alignment;
    protected BLSlot isFlexible;

    /**
     * Gets the value of the phaseLow property.
     * 
     * @return
     *     possible object is
     *     {@link TSSlot }
     *     
     */
    public TSSlot getPhaseLow() {
        return phaseLow;
    }

    /**
     * Sets the value of the phaseLow property.
     * 
     * @param value
     *     allowed object is
     *     {@link TSSlot }
     *     
     */
    public void setPhaseLow(TSSlot value) {
        this.phaseLow = value;
    }

    /**
     * Gets the value of the phaseHigh property.
     * 
     * @return
     *     possible object is
     *     {@link TSSlot }
     *     
     */
    public TSSlot getPhaseHigh() {
        return phaseHigh;
    }

    /**
     * Sets the value of the phaseHigh property.
     * 
     * @param value
     *     allowed object is
     *     {@link TSSlot }
     *     
     */
    public void setPhaseHigh(TSSlot value) {
        this.phaseHigh = value;
    }

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setPeriod(PQSlot value) {
        this.period = value;
    }

    /**
     * Gets the value of the frequencyRepeat property.
     * 
     * @return
     *     possible object is
     *     {@link INTSlot }
     *     
     */
    public INTSlot getFrequencyRepeat() {
        return frequencyRepeat;
    }

    /**
     * Sets the value of the frequencyRepeat property.
     * 
     * @param value
     *     allowed object is
     *     {@link INTSlot }
     *     
     */
    public void setFrequencyRepeat(INTSlot value) {
        this.frequencyRepeat = value;
    }

    /**
     * Gets the value of the frequencyTime property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getFrequencyTime() {
        return frequencyTime;
    }

    /**
     * Sets the value of the frequencyTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setFrequencyTime(PQSlot value) {
        this.frequencyTime = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link INTSlot }
     *     
     */
    public INTSlot getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link INTSlot }
     *     
     */
    public void setCount(INTSlot value) {
        this.count = value;
    }

    /**
     * Gets the value of the displayFrequencyInd property.
     * 
     * @return
     *     possible object is
     *     {@link BLSlot }
     *     
     */
    public BLSlot getDisplayFrequencyInd() {
        return displayFrequencyInd;
    }

    /**
     * Sets the value of the displayFrequencyInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BLSlot }
     *     
     */
    public void setDisplayFrequencyInd(BLSlot value) {
        this.displayFrequencyInd = value;
    }

    /**
     * Gets the value of the alignment property.
     * 
     * @return
     *     possible object is
     *     {@link CSSlot }
     *     
     */
    public CSSlot getAlignment() {
        return alignment;
    }

    /**
     * Sets the value of the alignment property.
     * 
     * @param value
     *     allowed object is
     *     {@link CSSlot }
     *     
     */
    public void setAlignment(CSSlot value) {
        this.alignment = value;
    }

    /**
     * Gets the value of the isFlexible property.
     * 
     * @return
     *     possible object is
     *     {@link BLSlot }
     *     
     */
    public BLSlot getIsFlexible() {
        return isFlexible;
    }

    /**
     * Sets the value of the isFlexible property.
     * 
     * @param value
     *     allowed object is
     *     {@link BLSlot }
     *     
     */
    public void setIsFlexible(BLSlot value) {
        this.isFlexible = value;
    }

}
