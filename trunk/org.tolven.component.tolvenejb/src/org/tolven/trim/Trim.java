
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Trim complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Trim">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extends" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="abstract" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="author" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="searchPhrase" type="{urn:tolven-org:trim:4.0}SearchPhrase" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="drilldown" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="menu" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="element" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tolvenId" type="{urn:tolven-org:trim:4.0}TolvenId" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tolvenEventId" type="{urn:tolven-org:trim:4.0}TolvenId" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="replacesTolvenId" type="{urn:tolven-org:trim:4.0}TolvenId" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="application" type="{urn:tolven-org:trim:4.0}Application" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="origin" type="{urn:tolven-org:trim:4.0}CopyTo" minOccurs="0"/>
 *         &lt;element name="copyTo" type="{urn:tolven-org:trim:4.0}CopyTo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="message" type="{urn:tolven-org:trim:4.0}Message" minOccurs="0"/>
 *         &lt;element name="transitions" type="{urn:tolven-org:trim:4.0}Transitions" minOccurs="0"/>
 *         &lt;element name="transition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="act" type="{urn:tolven-org:trim:4.0}Act" minOccurs="0"/>
 *         &lt;element name="valueSet" type="{urn:tolven-org:trim:4.0}ValueSet" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="field" type="{urn:tolven-org:trim:4.0}Field" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="formData" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="confirm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="patientLinkId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="unused" type="{urn:tolven-org:trim:4.0}Unused" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Trim", propOrder = {
    "_extends",
    "_abstract",
    "name",
    "description",
    "author",
    "searchPhrases",
    "page",
    "drilldown",
    "menus",
    "element",
    "tolvenIds",
    "tolvenEventIds",
    "replacesTolvenIds",
    "reference",
    "applications",
    "origin",
    "copyTos",
    "message",
    "transitions",
    "transition",
    "act",
    "valueSets",
    "fields",
    "formData",
    "confirm",
    "patientLinkId",
    "unused"
})
@XmlRootElement(name = "trim")
public class Trim
    implements Serializable
{

    @XmlElement(name = "extends")
    protected String _extends;
    @XmlElement(name = "abstract")
    protected Boolean _abstract;
    protected String name;
    protected String description;
    protected String author;
    @XmlElement(name = "searchPhrase")
    protected List<SearchPhrase> searchPhrases;
    protected String page;
    protected String drilldown;
    @XmlElement(name = "menu")
    protected List<String> menus;
    protected String element;
    @XmlElement(name = "tolvenId")
    protected List<TolvenId> tolvenIds;
    @XmlElement(name = "tolvenEventId")
    protected List<TolvenId> tolvenEventIds;
    @XmlElement(name = "replacesTolvenId")
    protected List<TolvenId> replacesTolvenIds;
    @XmlSchemaType(name = "anyURI")
    protected String reference;
    @XmlElement(name = "application")
    protected List<Application> applications;
    protected CopyTo origin;
    @XmlElement(name = "copyTo")
    protected List<CopyTo> copyTos;
    protected Message message;
    protected Transitions transitions;
    protected String transition;
    protected Act act;
    @XmlElement(name = "valueSet")
    protected List<ValueSet> valueSets;
    @XmlElement(name = "field")
    protected List<Field> fields;
    protected byte[] formData;
    protected String confirm;
    protected Long patientLinkId;
    protected Unused unused;

    /**
     * Gets the value of the extends property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtends() {
        return _extends;
    }

    /**
     * Sets the value of the extends property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtends(String value) {
        this._extends = value;
    }

    /**
     * Gets the value of the abstract property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAbstract() {
        return _abstract;
    }

    /**
     * Sets the value of the abstract property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAbstract(Boolean value) {
        this._abstract = value;
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
     * Gets the value of the author property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the value of the author property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthor(String value) {
        this.author = value;
    }

    /**
     * Gets the value of the searchPhrases property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchPhrases property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchPhrases().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SearchPhrase }
     * 
     * 
     */
    public List<SearchPhrase> getSearchPhrases() {
        if (searchPhrases == null) {
            searchPhrases = new ArrayList<SearchPhrase>();
        }
        return this.searchPhrases;
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
     * Gets the value of the drilldown property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrilldown() {
        return drilldown;
    }

    /**
     * Sets the value of the drilldown property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrilldown(String value) {
        this.drilldown = value;
    }

    /**
     * Gets the value of the menus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the menus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMenus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMenus() {
        if (menus == null) {
            menus = new ArrayList<String>();
        }
        return this.menus;
    }

    /**
     * Gets the value of the element property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElement() {
        return element;
    }

    /**
     * Sets the value of the element property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElement(String value) {
        this.element = value;
    }

    /**
     * Gets the value of the tolvenIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tolvenIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTolvenIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TolvenId }
     * 
     * 
     */
    public List<TolvenId> getTolvenIds() {
        if (tolvenIds == null) {
            tolvenIds = new ArrayList<TolvenId>();
        }
        return this.tolvenIds;
    }

    /**
     * Gets the value of the tolvenEventIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tolvenEventIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTolvenEventIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TolvenId }
     * 
     * 
     */
    public List<TolvenId> getTolvenEventIds() {
        if (tolvenEventIds == null) {
            tolvenEventIds = new ArrayList<TolvenId>();
        }
        return this.tolvenEventIds;
    }

    /**
     * Gets the value of the replacesTolvenIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the replacesTolvenIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReplacesTolvenIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TolvenId }
     * 
     * 
     */
    public List<TolvenId> getReplacesTolvenIds() {
        if (replacesTolvenIds == null) {
            replacesTolvenIds = new ArrayList<TolvenId>();
        }
        return this.replacesTolvenIds;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the applications property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the applications property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApplications().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Application }
     * 
     * 
     */
    public List<Application> getApplications() {
        if (applications == null) {
            applications = new ArrayList<Application>();
        }
        return this.applications;
    }

    /**
     * Gets the value of the origin property.
     * 
     * @return
     *     possible object is
     *     {@link CopyTo }
     *     
     */
    public CopyTo getOrigin() {
        return origin;
    }

    /**
     * Sets the value of the origin property.
     * 
     * @param value
     *     allowed object is
     *     {@link CopyTo }
     *     
     */
    public void setOrigin(CopyTo value) {
        this.origin = value;
    }

    /**
     * Gets the value of the copyTos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the copyTos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCopyTos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CopyTo }
     * 
     * 
     */
    public List<CopyTo> getCopyTos() {
        if (copyTos == null) {
            copyTos = new ArrayList<CopyTo>();
        }
        return this.copyTos;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link Message }
     *     
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link Message }
     *     
     */
    public void setMessage(Message value) {
        this.message = value;
    }

    /**
     * Gets the value of the transitions property.
     * 
     * @return
     *     possible object is
     *     {@link Transitions }
     *     
     */
    public Transitions getTransitions() {
        return transitions;
    }

    /**
     * Sets the value of the transitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Transitions }
     *     
     */
    public void setTransitions(Transitions value) {
        this.transitions = value;
    }

    /**
     * Gets the value of the transition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransition() {
        return transition;
    }

    /**
     * Sets the value of the transition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransition(String value) {
        this.transition = value;
    }

    /**
     * Gets the value of the act property.
     * 
     * @return
     *     possible object is
     *     {@link Act }
     *     
     */
    public Act getAct() {
        return act;
    }

    /**
     * Sets the value of the act property.
     * 
     * @param value
     *     allowed object is
     *     {@link Act }
     *     
     */
    public void setAct(Act value) {
        this.act = value;
    }

    /**
     * Gets the value of the valueSets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valueSets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValueSets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueSet }
     * 
     * 
     */
    public List<ValueSet> getValueSets() {
        if (valueSets == null) {
            valueSets = new ArrayList<ValueSet>();
        }
        return this.valueSets;
    }

    /**
     * Gets the value of the fields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Field }
     * 
     * 
     */
    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<Field>();
        }
        return this.fields;
    }

    /**
     * Gets the value of the formData property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFormData() {
        return formData;
    }

    /**
     * Sets the value of the formData property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFormData(byte[] value) {
        this.formData = ((byte[]) value);
    }

    /**
     * Gets the value of the confirm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfirm() {
        return confirm;
    }

    /**
     * Sets the value of the confirm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirm(String value) {
        this.confirm = value;
    }

    /**
     * Gets the value of the patientLinkId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPatientLinkId() {
        return patientLinkId;
    }

    /**
     * Sets the value of the patientLinkId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPatientLinkId(Long value) {
        this.patientLinkId = value;
    }

    /**
     * Gets the value of the unused property.
     * 
     * @return
     *     possible object is
     *     {@link Unused }
     *     
     */
    public Unused getUnused() {
        return unused;
    }

    /**
     * Sets the value of the unused property.
     * 
     * @param value
     *     allowed object is
     *     {@link Unused }
     *     
     */
    public void setUnused(Unused value) {
        this.unused = value;
    }

}
