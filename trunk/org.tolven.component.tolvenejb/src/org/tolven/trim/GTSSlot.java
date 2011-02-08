
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GTSSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GTSSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *           &lt;element name="SET_TS" type="{urn:tolven-org:trim:4.0}TS" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="TS" type="{urn:tolven-org:trim:4.0}TS" minOccurs="0"/>
 *           &lt;element name="IVL_TS" type="{urn:tolven-org:trim:4.0}IVL_TS" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="PIVL" type="{urn:tolven-org:trim:4.0}PIVL" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GTSSlot", propOrder = {
    "ivlts",
    "ts",
    "setts",
    "_null",
    "pivl"
})
public class GTSSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "IVL_TS")
    protected IVLTS ivlts;
    @XmlElement(name = "TS")
    protected TS ts;
    @XmlElement(name = "SET_TS")
    protected List<TS> setts;
    @XmlElement(name = "null")
    protected NullFlavor _null;
    @XmlElement(name = "PIVL")
    protected PIVL pivl;

    /**
     * Gets the value of the ivlts property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTS }
     *     
     */
    public IVLTS getIVLTS() {
        return ivlts;
    }

    /**
     * Sets the value of the ivlts property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTS }
     *     
     */
    public void setIVLTS(IVLTS value) {
        this.ivlts = value;
    }

    /**
     * Gets the value of the ts property.
     * 
     * @return
     *     possible object is
     *     {@link TS }
     *     
     */
    public TS getTS() {
        return ts;
    }

    /**
     * Sets the value of the ts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TS }
     *     
     */
    public void setTS(TS value) {
        this.ts = value;
    }

    /**
     * Gets the value of the setts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TS }
     * 
     * 
     */
    public List<TS> getSETTS() {
        if (setts == null) {
            setts = new ArrayList<TS>();
        }
        return this.setts;
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
     * Gets the value of the pivl property.
     * 
     * @return
     *     possible object is
     *     {@link PIVL }
     *     
     */
    public PIVL getPIVL() {
        return pivl;
    }

    /**
     * Sets the value of the pivl property.
     * 
     * @param value
     *     allowed object is
     *     {@link PIVL }
     *     
     */
    public void setPIVL(PIVL value) {
        this.pivl = value;
    }

}
