
package org.tolven.menuStructure;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Extends complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Extends">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="band" type="{urn:tolven-org:menuStructure:1.0}Band"/>
 *           &lt;element name="calendar" type="{urn:tolven-org:menuStructure:1.0}Calendar"/>
 *           &lt;element name="instance" type="{urn:tolven-org:menuStructure:1.0}Instance"/>
 *           &lt;element name="entry" type="{urn:tolven-org:menuStructure:1.0}Entry"/>
 *           &lt;element name="list" type="{urn:tolven-org:menuStructure:1.0}List"/>
 *           &lt;element name="menu" type="{urn:tolven-org:menuStructure:1.0}Menu"/>
 *           &lt;element name="portal" type="{urn:tolven-org:menuStructure:1.0}Portal"/>
 *           &lt;element name="portlet" type="{urn:tolven-org:menuStructure:1.0}Portlet"/>
 *           &lt;element name="timeline" type="{urn:tolven-org:menuStructure:1.0}Timeline"/>
 *           &lt;element name="trimList" type="{urn:tolven-org:menuStructure:1.0}TrimList"/>
 *           &lt;element name="placeholder" type="{urn:tolven-org:menuStructure:1.0}Placeholder"/>
 *           &lt;element name="field" type="{urn:tolven-org:menuStructure:1.0}PlaceholderField"/>
 *           &lt;element name="column" type="{urn:tolven-org:menuStructure:1.0}Column"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="before" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="after" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Extends", propOrder = {
    "column",
    "field",
    "placeholder",
    "trimList",
    "timeline",
    "portlet",
    "portal",
    "menu",
    "list",
    "entry",
    "instance",
    "calendar",
    "band"
})
public class Extends
    implements Serializable
{

    protected Column column;
    protected PlaceholderField field;
    protected Placeholder placeholder;
    protected TrimList trimList;
    protected Timeline timeline;
    protected Portlet portlet;
    protected Portal portal;
    protected Menu menu;
    protected List list;
    protected Entry entry;
    protected Instance instance;
    protected Calendar calendar;
    protected Band band;
    @XmlAttribute
    protected String path;
    @XmlAttribute
    protected Boolean optional;
    @XmlAttribute
    protected String before;
    @XmlAttribute
    protected String after;

    /**
     * Gets the value of the column property.
     * 
     * @return
     *     possible object is
     *     {@link Column }
     *     
     */
    public Column getColumn() {
        return column;
    }

    /**
     * Sets the value of the column property.
     * 
     * @param value
     *     allowed object is
     *     {@link Column }
     *     
     */
    public void setColumn(Column value) {
        this.column = value;
    }

    /**
     * Gets the value of the field property.
     * 
     * @return
     *     possible object is
     *     {@link PlaceholderField }
     *     
     */
    public PlaceholderField getField() {
        return field;
    }

    /**
     * Sets the value of the field property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlaceholderField }
     *     
     */
    public void setField(PlaceholderField value) {
        this.field = value;
    }

    /**
     * Gets the value of the placeholder property.
     * 
     * @return
     *     possible object is
     *     {@link Placeholder }
     *     
     */
    public Placeholder getPlaceholder() {
        return placeholder;
    }

    /**
     * Sets the value of the placeholder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Placeholder }
     *     
     */
    public void setPlaceholder(Placeholder value) {
        this.placeholder = value;
    }

    /**
     * Gets the value of the trimList property.
     * 
     * @return
     *     possible object is
     *     {@link TrimList }
     *     
     */
    public TrimList getTrimList() {
        return trimList;
    }

    /**
     * Sets the value of the trimList property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrimList }
     *     
     */
    public void setTrimList(TrimList value) {
        this.trimList = value;
    }

    /**
     * Gets the value of the timeline property.
     * 
     * @return
     *     possible object is
     *     {@link Timeline }
     *     
     */
    public Timeline getTimeline() {
        return timeline;
    }

    /**
     * Sets the value of the timeline property.
     * 
     * @param value
     *     allowed object is
     *     {@link Timeline }
     *     
     */
    public void setTimeline(Timeline value) {
        this.timeline = value;
    }

    /**
     * Gets the value of the portlet property.
     * 
     * @return
     *     possible object is
     *     {@link Portlet }
     *     
     */
    public Portlet getPortlet() {
        return portlet;
    }

    /**
     * Sets the value of the portlet property.
     * 
     * @param value
     *     allowed object is
     *     {@link Portlet }
     *     
     */
    public void setPortlet(Portlet value) {
        this.portlet = value;
    }

    /**
     * Gets the value of the portal property.
     * 
     * @return
     *     possible object is
     *     {@link Portal }
     *     
     */
    public Portal getPortal() {
        return portal;
    }

    /**
     * Sets the value of the portal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Portal }
     *     
     */
    public void setPortal(Portal value) {
        this.portal = value;
    }

    /**
     * Gets the value of the menu property.
     * 
     * @return
     *     possible object is
     *     {@link Menu }
     *     
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Sets the value of the menu property.
     * 
     * @param value
     *     allowed object is
     *     {@link Menu }
     *     
     */
    public void setMenu(Menu value) {
        this.menu = value;
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link List }
     *     
     */
    public List getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link List }
     *     
     */
    public void setList(List value) {
        this.list = value;
    }

    /**
     * Gets the value of the entry property.
     * 
     * @return
     *     possible object is
     *     {@link Entry }
     *     
     */
    public Entry getEntry() {
        return entry;
    }

    /**
     * Sets the value of the entry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Entry }
     *     
     */
    public void setEntry(Entry value) {
        this.entry = value;
    }

    /**
     * Gets the value of the instance property.
     * 
     * @return
     *     possible object is
     *     {@link Instance }
     *     
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Sets the value of the instance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Instance }
     *     
     */
    public void setInstance(Instance value) {
        this.instance = value;
    }

    /**
     * Gets the value of the calendar property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * Sets the value of the calendar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setCalendar(Calendar value) {
        this.calendar = value;
    }

    /**
     * Gets the value of the band property.
     * 
     * @return
     *     possible object is
     *     {@link Band }
     *     
     */
    public Band getBand() {
        return band;
    }

    /**
     * Sets the value of the band property.
     * 
     * @param value
     *     allowed object is
     *     {@link Band }
     *     
     */
    public void setBand(Band value) {
        this.band = value;
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
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isOptional() {
        if (optional == null) {
            return false;
        } else {
            return optional;
        }
    }

    /**
     * Sets the value of the optional property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptional(Boolean value) {
        this.optional = value;
    }

    /**
     * Gets the value of the before property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBefore() {
        return before;
    }

    /**
     * Sets the value of the before property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBefore(String value) {
        this.before = value;
    }

    /**
     * Gets the value of the after property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAfter() {
        return after;
    }

    /**
     * Sets the value of the after property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAfter(String value) {
        this.after = value;
    }

}
