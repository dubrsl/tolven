
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TELSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TELSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor"/>
 *         &lt;element name="URL" type="{urn:tolven-org:trim:4.0}URL"/>
 *         &lt;element name="TEL" type="{urn:tolven-org:trim:4.0}TEL"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TELSlot", propOrder = {
    "nullsAndURLSAndTELS"
})
public class TELSlot
    extends Slot
    implements Serializable
{

    @XmlElements({
        @XmlElement(name = "TEL", type = TEL.class),
        @XmlElement(name = "URL", type = URL.class),
        @XmlElement(name = "null", type = NullFlavor.class)
    })
    protected List<DataType> nullsAndURLSAndTELS;

    /**
     * Gets the value of the nullsAndURLSAndTELS property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nullsAndURLSAndTELS property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNullsAndURLSAndTELS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TEL }
     * {@link URL }
     * {@link NullFlavor }
     * 
     * 
     */
    public List<DataType> getNullsAndURLSAndTELS() {
        if (nullsAndURLSAndTELS == null) {
            nullsAndURLSAndTELS = new ArrayList<DataType>();
        }
        return this.nullsAndURLSAndTELS;
    }

}
