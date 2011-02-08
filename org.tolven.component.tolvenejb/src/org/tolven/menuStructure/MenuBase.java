
package org.tolven.menuStructure;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MenuBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MenuBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="menuEventHandler" type="{urn:tolven-org:menuStructure:1.0}MenuEventHandler" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="page" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="visible" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sequence" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuBase", propOrder = {
    "menuEventHandler"
})
@XmlSeeAlso({
    Placeholder.class,
    Menu.class,
    Instance.class,
    Portlet.class,
    Portal.class,
    Action.class,
    TrimList.class,
    List.class,
    Entry.class,
    Calendar.class,
    Timeline.class,
    Band.class
})
public abstract class MenuBase implements Serializable
{

    protected MenuEventHandler menuEventHandler;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute
    protected String path;
    @XmlAttribute
    protected String title;
    @XmlAttribute
    protected String page;
    @XmlAttribute
    protected String visible;
    @XmlAttribute
    protected Long sequence;

    /**
     * Gets the value of the menuEventHandler property.
     * 
     * @return
     *     possible object is
     *     {@link MenuEventHandler }
     *     
     */
    public MenuEventHandler getMenuEventHandler() {
        return menuEventHandler;
    }

    /**
     * Sets the value of the menuEventHandler property.
     * 
     * @param value
     *     allowed object is
     *     {@link MenuEventHandler }
     *     
     */
    public void setMenuEventHandler(MenuEventHandler value) {
        this.menuEventHandler = value;
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
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the page property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPage(String value) {
        this.page = value;
    }

    /**
     * Gets the value of the visible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisible() {
        return visible;
    }

    /**
     * Sets the value of the visible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisible(String value) {
        this.visible = value;
    }

    /**
     * Gets the value of the sequence property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSequence(Long value) {
        this.sequence = value;
    }

}
