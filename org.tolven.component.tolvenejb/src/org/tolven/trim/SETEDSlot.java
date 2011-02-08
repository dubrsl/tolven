
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SET_EDSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SET_EDSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="ST" type="{urn:tolven-org:trim:4.0}ST" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ED" type="{urn:tolven-org:trim:4.0}ED" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SET_EDSlot", propOrder = {
    "eds",
    "sts",
    "_null"
})
public class SETEDSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "ED")
    protected List<ED> eds;
    @XmlElement(name = "ST")
    protected List<ST> sts;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the eds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEDS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ED }
     * 
     * 
     */
    public List<ED> getEDS() {
        if (eds == null) {
            eds = new ArrayList<ED>();
        }
        return this.eds;
    }

    /**
     * Gets the value of the sts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ST }
     * 
     * 
     */
    public List<ST> getSTS() {
        if (sts == null) {
            sts = new ArrayList<ST>();
        }
        return this.sts;
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

}
