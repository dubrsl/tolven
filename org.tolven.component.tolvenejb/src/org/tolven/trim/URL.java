
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for URL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="URL">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}DataType">
 *       &lt;sequence>
 *         &lt;element name="use" type="{urn:tolven-org:trim:4.0}TelecommunicationAddressUse" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="useablePeriod" type="{urn:tolven-org:trim:4.0}IVL_TS" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="value" type="{urn:tolven-org:trim:4.0}st"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URL", propOrder = {
    "uses",
    "useablePeriods",
    "value"
})
@XmlSeeAlso({
    TEL.class
})
public class URL
    extends DataType
    implements Serializable
{

    @XmlElement(name = "use")
    protected List<TelecommunicationAddressUse> uses;
    @XmlElement(name = "useablePeriod")
    protected List<IVLTS> useablePeriods;
    @XmlElement(required = true)
    protected String value;

    /**
     * Gets the value of the uses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TelecommunicationAddressUse }
     * 
     * 
     */
    public List<TelecommunicationAddressUse> getUses() {
        if (uses == null) {
            uses = new ArrayList<TelecommunicationAddressUse>();
        }
        return this.uses;
    }

    /**
     * Gets the value of the useablePeriods property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the useablePeriods property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUseablePeriods().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IVLTS }
     * 
     * 
     */
    public List<IVLTS> getUseablePeriods() {
        if (useablePeriods == null) {
            useablePeriods = new ArrayList<IVLTS>();
        }
        return this.useablePeriods;
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

}
