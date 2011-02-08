
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
 * <p>Java class for XAccount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XAccount">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="disableAutoRefresh" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="emailFormat" type="{urn:tolven-org:accountuser:1.0}XEmailFormat" minOccurs="0"/>
 *         &lt;element name="enableBackButton" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="locale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="manualMetadataUpdate" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="timeZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="property" type="{urn:tolven-org:accountuser:1.0}XProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="role" type="{urn:tolven-org:accountuser:1.0}XRole" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="accountUser" type="{urn:tolven-org:accountuser:1.0}XAccountUser" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="accountType" type="{urn:tolven-org:accountuser:1.0}XAccountType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="action" type="{urn:tolven-org:accountuser:1.0}XAction" />
 *       &lt;attribute name="result" type="{urn:tolven-org:accountuser:1.0}XResult" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XAccount", propOrder = {
    "disableAutoRefresh",
    "emailFormat",
    "enableBackButton",
    "locale",
    "manualMetadataUpdate",
    "timeZone",
    "title",
    "properties",
    "roles",
    "accountUsers",
    "accountType"
})
public class XAccount
    implements Serializable
{

    @XmlElement(defaultValue = "false")
    protected Boolean disableAutoRefresh;
    @XmlElement(defaultValue = "html")
    protected XEmailFormat emailFormat;
    @XmlElement(defaultValue = "true")
    protected Boolean enableBackButton;
    protected String locale;
    @XmlElement(defaultValue = "false")
    protected Boolean manualMetadataUpdate;
    protected String timeZone;
    protected String title;
    @XmlElement(name = "property")
    protected List<XProperty> properties;
    @XmlElement(name = "role")
    protected List<XRole> roles;
    @XmlElement(name = "accountUser")
    protected List<XAccountUser> accountUsers;
    protected XAccountType accountType;
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

    /**
     * Gets the value of the disableAutoRefresh property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisableAutoRefresh() {
        return disableAutoRefresh;
    }

    /**
     * Sets the value of the disableAutoRefresh property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableAutoRefresh(Boolean value) {
        this.disableAutoRefresh = value;
    }

    /**
     * Gets the value of the emailFormat property.
     * 
     * @return
     *     possible object is
     *     {@link XEmailFormat }
     *     
     */
    public XEmailFormat getEmailFormat() {
        return emailFormat;
    }

    /**
     * Sets the value of the emailFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link XEmailFormat }
     *     
     */
    public void setEmailFormat(XEmailFormat value) {
        this.emailFormat = value;
    }

    /**
     * Gets the value of the enableBackButton property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnableBackButton() {
        return enableBackButton;
    }

    /**
     * Sets the value of the enableBackButton property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnableBackButton(Boolean value) {
        this.enableBackButton = value;
    }

    /**
     * Gets the value of the locale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the value of the locale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocale(String value) {
        this.locale = value;
    }

    /**
     * Gets the value of the manualMetadataUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isManualMetadataUpdate() {
        return manualMetadataUpdate;
    }

    /**
     * Sets the value of the manualMetadataUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setManualMetadataUpdate(Boolean value) {
        this.manualMetadataUpdate = value;
    }

    /**
     * Gets the value of the timeZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the value of the timeZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeZone(String value) {
        this.timeZone = value;
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
     * Gets the value of the roles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XRole }
     * 
     * 
     */
    public List<XRole> getRoles() {
        if (roles == null) {
            roles = new ArrayList<XRole>();
        }
        return this.roles;
    }

    /**
     * Gets the value of the accountUsers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountUsers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountUsers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XAccountUser }
     * 
     * 
     */
    public List<XAccountUser> getAccountUsers() {
        if (accountUsers == null) {
            accountUsers = new ArrayList<XAccountUser>();
        }
        return this.accountUsers;
    }

    /**
     * Gets the value of the accountType property.
     * 
     * @return
     *     possible object is
     *     {@link XAccountType }
     *     
     */
    public XAccountType getAccountType() {
        return accountType;
    }

    /**
     * Sets the value of the accountType property.
     * 
     * @param value
     *     allowed object is
     *     {@link XAccountType }
     *     
     */
    public void setAccountType(XAccountType value) {
        this.accountType = value;
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

}
