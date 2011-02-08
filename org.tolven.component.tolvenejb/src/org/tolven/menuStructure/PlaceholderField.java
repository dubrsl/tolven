
package org.tolven.menuStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PlaceholderField complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PlaceholderField">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="internal" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{urn:tolven-org:menuStructure:1.0}ColumnType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlaceholderField", propOrder = {
    "froms"
})
public class PlaceholderField
    implements Serializable
{

    @XmlElement(name = "from")
    protected List<String> froms;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String internal;
    @XmlAttribute
    protected ColumnType type;

    /**
     * Gets the value of the froms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the froms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFroms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFroms() {
        if (froms == null) {
            froms = new ArrayList<String>();
        }
        return this.froms;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the internal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternal() {
        return internal;
    }

    /**
     * Sets the value of the internal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternal(String value) {
        this.internal = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ColumnType }
     *     
     */
    public ColumnType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColumnType }
     *     
     */
    public void setType(ColumnType value) {
        this.type = value;
    }

}
