
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SET_CESlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SET_CESlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="CE" type="{urn:tolven-org:trim:4.0}CE" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SET_CESlot", propOrder = {
    "ces",
    "_null"
})
public class SETCESlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "CE")
    protected List<CE> ces;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the ces property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ces property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCES().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CE }
     * 
     * 
     */
    public List<CE> getCES() {
        if (ces == null) {
            ces = new ArrayList<CE>();
        }
        return this.ces;
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
