
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SET_RTOSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SET_RTOSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice>
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor" minOccurs="0"/>
 *         &lt;element name="RTO" type="{urn:tolven-org:trim:4.0}RTO" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="REAL" type="{urn:tolven-org:trim:4.0}REAL" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="INT" type="{urn:tolven-org:trim:4.0}INT" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SET_RTOSlot", propOrder = {
    "ints",
    "reals",
    "rtos",
    "_null"
})
public class SETRTOSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "INT")
    protected List<INT> ints;
    @XmlElement(name = "REAL")
    protected List<REAL> reals;
    @XmlElement(name = "RTO")
    protected List<RTO> rtos;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the ints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getINTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link INT }
     * 
     * 
     */
    public List<INT> getINTS() {
        if (ints == null) {
            ints = new ArrayList<INT>();
        }
        return this.ints;
    }

    /**
     * Gets the value of the reals property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reals property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getREALS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link REAL }
     * 
     * 
     */
    public List<REAL> getREALS() {
        if (reals == null) {
            reals = new ArrayList<REAL>();
        }
        return this.reals;
    }

    /**
     * Gets the value of the rtos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rtos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRTOS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RTO }
     * 
     * 
     */
    public List<RTO> getRTOS() {
        if (rtos == null) {
            rtos = new ArrayList<RTO>();
        }
        return this.rtos;
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
