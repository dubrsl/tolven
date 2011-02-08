
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AD">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}DataType">
 *       &lt;sequence>
 *         &lt;element name="use" type="{urn:tolven-org:trim:4.0}AddressUse" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor"/>
 *           &lt;element name="part" type="{urn:tolven-org:trim:4.0}ADXPSlot" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="useablePeriod" type="{urn:tolven-org:trim:4.0}GTSSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AD", propOrder = {
    "uses",
    "parts",
    "_null",
    "useablePeriod"
})
public class AD
    extends DataType
    implements Serializable
{

    @XmlElement(name = "use")
    protected List<AddressUse> uses;
    @XmlElement(name = "part")
    protected List<ADXPSlot> parts;
    @XmlElement(name = "null")
    protected NullFlavor _null;
    protected GTSSlot useablePeriod;

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
     * {@link AddressUse }
     * 
     * 
     */
    public List<AddressUse> getUses() {
        if (uses == null) {
            uses = new ArrayList<AddressUse>();
        }
        return this.uses;
    }

    /**
     * Gets the value of the parts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ADXPSlot }
     * 
     * 
     */
    public List<ADXPSlot> getParts() {
        if (parts == null) {
            parts = new ArrayList<ADXPSlot>();
        }
        return this.parts;
    }

    /**
     * Gets the value of the null property.
     * 
     * @return
     *     possible object is
     *     {@link NullFlavor }
     *     
     */
    public NullFlavor getNull() {
        return _null;
    }

    /**
     * Sets the value of the null property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullFlavor }
     *     
     */
    public void setNull(NullFlavor value) {
        this._null = value;
    }

    /**
     * Gets the value of the useablePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link GTSSlot }
     *     
     */
    public GTSSlot getUseablePeriod() {
        return useablePeriod;
    }

    /**
     * Sets the value of the useablePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link GTSSlot }
     *     
     */
    public void setUseablePeriod(GTSSlot value) {
        this.useablePeriod = value;
    }

}
