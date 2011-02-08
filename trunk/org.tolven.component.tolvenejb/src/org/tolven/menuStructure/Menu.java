
package org.tolven.menuStructure;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Menu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Menu">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:menuStructure:1.0}MenuBase">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="menu" type="{urn:tolven-org:menuStructure:1.0}Menu"/>
 *         &lt;element name="portal" type="{urn:tolven-org:menuStructure:1.0}Portal"/>
 *         &lt;element name="timeline" type="{urn:tolven-org:menuStructure:1.0}Timeline"/>
 *         &lt;element name="calendar" type="{urn:tolven-org:menuStructure:1.0}Calendar"/>
 *         &lt;element name="list" type="{urn:tolven-org:menuStructure:1.0}List"/>
 *         &lt;element name="trimList" type="{urn:tolven-org:menuStructure:1.0}TrimList"/>
 *         &lt;element name="instance" type="{urn:tolven-org:menuStructure:1.0}Instance"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Menu", propOrder = {
    "menusAndPortalsAndTimelines"
})
public class Menu
    extends MenuBase
    implements Serializable
{

    @XmlElements({
        @XmlElement(name = "timeline", type = Timeline.class),
        @XmlElement(name = "portal", type = Portal.class),
        @XmlElement(name = "calendar", type = Calendar.class),
        @XmlElement(name = "trimList", type = TrimList.class),
        @XmlElement(name = "list", type = org.tolven.menuStructure.List.class),
        @XmlElement(name = "menu", type = Menu.class),
        @XmlElement(name = "instance", type = Instance.class)
    })
    protected java.util.List<MenuBase> menusAndPortalsAndTimelines;

    /**
     * Gets the value of the menusAndPortalsAndTimelines property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the menusAndPortalsAndTimelines property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMenusAndPortalsAndTimelines().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Timeline }
     * {@link Portal }
     * {@link Calendar }
     * {@link TrimList }
     * {@link org.tolven.menuStructure.List }
     * {@link Menu }
     * {@link Instance }
     * 
     * 
     */
    public java.util.List<MenuBase> getMenusAndPortalsAndTimelines() {
        if (menusAndPortalsAndTimelines == null) {
            menusAndPortalsAndTimelines = new ArrayList<MenuBase>();
        }
        return this.menusAndPortalsAndTimelines;
    }

}
