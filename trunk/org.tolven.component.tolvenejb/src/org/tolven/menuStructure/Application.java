
package org.tolven.menuStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Application complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Application">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="depends" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="rules" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="property" type="{urn:tolven-org:menuStructure:1.0}AccountProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="notice" type="{urn:tolven-org:menuStructure:1.0}Notice" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="trimMenu" type="{urn:tolven-org:menuStructure:1.0}Placeholder" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="menu" type="{urn:tolven-org:menuStructure:1.0}Menu" minOccurs="0"/>
 *         &lt;element name="placeholder" type="{urn:tolven-org:menuStructure:1.0}Placeholder" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="extends" type="{urn:tolven-org:menuStructure:1.0}Extends" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="homePage" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="logo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="css" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="creatable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="createAccountPage" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Application", propOrder = {
    "depends",
    "rules",
    "properties",
    "notices",
    "trimMenus",
    "menu",
    "placeholders",
    "_extends"
})
@XmlRootElement(name = "application")
public class Application
    implements Serializable
{

    protected List<String> depends;
    protected List<String> rules;
    @XmlElement(name = "property")
    protected List<AccountProperty> properties;
    @XmlElement(name = "notice")
    protected List<Notice> notices;
    @XmlElement(name = "trimMenu")
    protected List<Placeholder> trimMenus;
    protected Menu menu;
    @XmlElement(name = "placeholder")
    protected List<Placeholder> placeholders;
    @XmlElement(name = "extends")
    protected List<Extends> _extends;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String title;
    @XmlAttribute
    protected String homePage;
    @XmlAttribute
    protected String logo;
    @XmlAttribute
    protected String css;
    @XmlAttribute
    protected Boolean creatable;
    @XmlAttribute
    protected String createAccountPage;

    /**
     * Gets the value of the depends property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the depends property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDepends().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDepends() {
        if (depends == null) {
            depends = new ArrayList<String>();
        }
        return this.depends;
    }

    /**
     * Gets the value of the rules property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rules property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRules().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRules() {
        if (rules == null) {
            rules = new ArrayList<String>();
        }
        return this.rules;
    }

    /**
     * Gets the value of the properties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the properties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountProperty }
     * 
     * 
     */
    public List<AccountProperty> getProperties() {
        if (properties == null) {
            properties = new ArrayList<AccountProperty>();
        }
        return this.properties;
    }

    /**
     * Gets the value of the notices property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notices property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotices().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Notice }
     * 
     * 
     */
    public List<Notice> getNotices() {
        if (notices == null) {
            notices = new ArrayList<Notice>();
        }
        return this.notices;
    }

    /**
     * Gets the value of the trimMenus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trimMenus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrimMenus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Placeholder }
     * 
     * 
     */
    public List<Placeholder> getTrimMenus() {
        if (trimMenus == null) {
            trimMenus = new ArrayList<Placeholder>();
        }
        return this.trimMenus;
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
     * Gets the value of the placeholders property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the placeholders property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlaceholders().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Placeholder }
     * 
     * 
     */
    public List<Placeholder> getPlaceholders() {
        if (placeholders == null) {
            placeholders = new ArrayList<Placeholder>();
        }
        return this.placeholders;
    }

    /**
     * Gets the value of the extends property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extends property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtends().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Extends }
     * 
     * 
     */
    public List<Extends> getExtends() {
        if (_extends == null) {
            _extends = new ArrayList<Extends>();
        }
        return this._extends;
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
     * Gets the value of the homePage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomePage() {
        return homePage;
    }

    /**
     * Sets the value of the homePage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomePage(String value) {
        this.homePage = value;
    }

    /**
     * Gets the value of the logo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Sets the value of the logo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogo(String value) {
        this.logo = value;
    }

    /**
     * Gets the value of the css property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCss() {
        return css;
    }

    /**
     * Sets the value of the css property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCss(String value) {
        this.css = value;
    }

    /**
     * Gets the value of the creatable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCreatable() {
        return creatable;
    }

    /**
     * Sets the value of the creatable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCreatable(Boolean value) {
        this.creatable = value;
    }

    /**
     * Gets the value of the createAccountPage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateAccountPage() {
        return createAccountPage;
    }

    /**
     * Sets the value of the createAccountPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateAccountPage(String value) {
        this.createAccountPage = value;
    }

}
