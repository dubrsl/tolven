
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
 * <p>Java class for TrimList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrimList">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:menuStructure:1.0}MenuBase">
 *       &lt;sequence>
 *         &lt;element name="column" type="{urn:tolven-org:menuStructure:1.0}Column" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="placeholder" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="initialSort" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrimList", propOrder = {
    "columns"
})
public class TrimList
    extends MenuBase
    implements Serializable
{

    @XmlElement(name = "column")
    protected List<Column> columns;
    @XmlAttribute
    protected String placeholder;
    @XmlAttribute
    protected String initialSort;

    /**
     * Gets the value of the columns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the columns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumns().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Column }
     * 
     * 
     */
    public List<Column> getColumns() {
        if (columns == null) {
            columns = new ArrayList<Column>();
        }
        return this.columns;
    }

    /**
     * Gets the value of the placeholder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * Sets the value of the placeholder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaceholder(String value) {
        this.placeholder = value;
    }

    /**
     * Gets the value of the initialSort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitialSort() {
        return initialSort;
    }

    /**
     * Sets the value of the initialSort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitialSort(String value) {
        this.initialSort = value;
    }

}
