
package org.tolven.menuStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Portal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Portal">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:menuStructure:1.0}MenuBase">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="portletColumn" type="{urn:tolven-org:menuStructure:1.0}PortletColumn"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Portal", propOrder = {
    "portletColumns"
})
public class Portal
    extends MenuBase
    implements Serializable
{

    @XmlElement(name = "portletColumn")
    protected List<PortletColumn> portletColumns;

    /**
     * Gets the value of the portletColumns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the portletColumns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPortletColumns().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PortletColumn }
     * 
     * 
     */
    public List<PortletColumn> getPortletColumns() {
        if (portletColumns == null) {
            portletColumns = new ArrayList<PortletColumn>();
        }
        return this.portletColumns;
    }

}
