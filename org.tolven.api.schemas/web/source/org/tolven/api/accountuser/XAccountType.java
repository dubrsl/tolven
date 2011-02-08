
package org.tolven.api.accountuser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for XAccountType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XAccountType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userCanCreate" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="status" type="{urn:tolven-org:accountuser:1.0}XStatus" minOccurs="0"/>
 *         &lt;element name="property" type="{urn:tolven-org:accountuser:1.0}XProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="account" type="{urn:tolven-org:accountuser:1.0}XAccount" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="logo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CSS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="action" type="{urn:tolven-org:accountuser:1.0}XAction" />
 *       &lt;attribute name="result" type="{urn:tolven-org:accountuser:1.0}XResult" />
 *       &lt;attribute name="knownType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XAccountType", propOrder = {
    "description",
    "userCanCreate",
    "status",
    "properties",
    "accounts",
    "logo",
    "css"
})
public class XAccountType
    implements Serializable
{

    protected String description;
    @XmlElement(defaultValue = "false")
    protected Boolean userCanCreate;
    protected XStatus status;
    @XmlElement(name = "property")
    protected List<XProperty> properties;
    @XmlElement(name = "account")
    protected List<XAccount> accounts;
    protected String logo;
    @XmlElement(name = "CSS")
    protected String css;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected Long id;
    @XmlAttribute
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date timestamp;
    @XmlAttribute
    protected XAction action;
    @XmlAttribute
    protected XResult result;
    @XmlAttribute
    protected String knownType;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the userCanCreate property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUserCanCreate() {
        return userCanCreate;
    }

    /**
     * Sets the value of the userCanCreate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUserCanCreate(Boolean value) {
        this.userCanCreate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link XStatus }
     *     
     */
    public XStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link XStatus }
     *     
     */
    public void setStatus(XStatus value) {
        this.status = value;
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
     * {@link XProperty }
     * 
     * 
     */
    public List<XProperty> getProperties() {
        if (properties == null) {
            properties = new ArrayList<XProperty>();
        }
        return this.properties;
    }

    /**
     * Gets the value of the accounts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accounts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccounts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XAccount }
     * 
     * 
     */
    public List<XAccount> getAccounts() {
        if (accounts == null) {
            accounts = new ArrayList<XAccount>();
        }
        return this.accounts;
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
    public String getCSS() {
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
    public void setCSS(String value) {
        this.css = value;
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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(Date value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link XAction }
     *     
     */
    public XAction getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link XAction }
     *     
     */
    public void setAction(XAction value) {
        this.action = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link XResult }
     *     
     */
    public XResult getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link XResult }
     *     
     */
    public void setResult(XResult value) {
        this.result = value;
    }

    /**
     * Gets the value of the knownType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKnownType() {
        return knownType;
    }

    /**
     * Sets the value of the knownType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKnownType(String value) {
        this.knownType = value;
    }

}
