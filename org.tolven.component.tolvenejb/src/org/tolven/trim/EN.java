
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EN complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EN">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}DataType">
 *       &lt;sequence>
 *         &lt;element name="use" type="{urn:tolven-org:trim:4.0}EntityNameUse" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor"/>
 *           &lt;element name="part" type="{urn:tolven-org:trim:4.0}ENXPSlot" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="validTime" type="{urn:tolven-org:trim:4.0}IVL_TSSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EN", propOrder = {
    "uses",
    "parts",
    "_null",
    "validTime"
})
public class EN
    extends DataType
    implements Serializable
{

    @XmlElement(name = "use")
    protected List<EntityNameUse> uses;
    @XmlElement(name = "part")
    protected List<ENXPSlot> parts;
    @XmlElement(name = "null")
    protected NullFlavor _null;
    protected IVLTSSlot validTime;

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
     * {@link EntityNameUse }
     * 
     * 
     */
    public List<EntityNameUse> getUses() {
        if (uses == null) {
            uses = new ArrayList<EntityNameUse>();
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
     * {@link ENXPSlot }
     * 
     * 
     */
    public List<ENXPSlot> getParts() {
        if (parts == null) {
            parts = new ArrayList<ENXPSlot>();
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
     * Gets the value of the validTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTSSlot }
     *     
     */
    public IVLTSSlot getValidTime() {
        return validTime;
    }

    /**
     * Sets the value of the validTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTSSlot }
     *     
     */
    public void setValidTime(IVLTSSlot value) {
        this.validTime = value;
    }

}
