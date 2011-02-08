
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				The abstract base of all RIM objects.
 * 			
 * 
 * <p>Java class for InfrastructureRoot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InfrastructureRoot">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="label" type="{urn:tolven-org:trim:4.0}LabelFacet" minOccurs="0"/>
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="bind" type="{urn:tolven-org:trim:4.0}BindTo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="realmCode" type="{urn:tolven-org:trim:4.0}RealmCode" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="typeId" type="{urn:tolven-org:trim:4.0}IISlot" minOccurs="0"/>
 *         &lt;element name="update" type="{urn:tolven-org:trim:4.0}UpdateCode" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="internalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="drilldown" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="accountShare" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="valueSet" type="{urn:tolven-org:trim:4.0}ValueSet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="sourceTrim" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfrastructureRoot", propOrder = {
    "label",
    "froms",
    "binds",
    "realmCodes",
    "typeId",
    "updates",
    "internalId",
    "page",
    "drilldown",
    "errors",
    "accountShares",
    "valueSets"
})
@XmlSeeAlso({
    Act.class,
    Participation.class,
    ActRelationship.class,
    Role.class,
    Entity.class
})
public abstract class InfrastructureRoot
    implements Serializable
{

    protected LabelFacet label;
    @XmlElement(name = "from")
    protected List<String> froms;
    @XmlElement(name = "bind")
    protected List<BindTo> binds;
    @XmlElement(name = "realmCode")
    protected List<RealmCode> realmCodes;
    protected IISlot typeId;
    @XmlElement(name = "update")
    protected List<UpdateCode> updates;
    protected String internalId;
    protected String page;
    protected String drilldown;
    @XmlElement(name = "error")
    protected List<String> errors;
    @XmlElement(name = "accountShare")
    protected List<String> accountShares;
    @XmlElement(name = "valueSet")
    protected List<ValueSet> valueSets;
    @XmlAttribute
    protected Boolean enabled;
    @XmlAttribute
    protected String sourceTrim;

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link LabelFacet }
     *     
     */
    public LabelFacet getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelFacet }
     *     
     */
    public void setLabel(LabelFacet value) {
        this.label = value;
    }

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
     * Gets the value of the binds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the binds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBinds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BindTo }
     * 
     * 
     */
    public List<BindTo> getBinds() {
        if (binds == null) {
            binds = new ArrayList<BindTo>();
        }
        return this.binds;
    }

    /**
     * Gets the value of the realmCodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the realmCodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRealmCodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RealmCode }
     * 
     * 
     */
    public List<RealmCode> getRealmCodes() {
        if (realmCodes == null) {
            realmCodes = new ArrayList<RealmCode>();
        }
        return this.realmCodes;
    }

    /**
     * Gets the value of the typeId property.
     * 
     * @return
     *     possible object is
     *     {@link IISlot }
     *     
     */
    public IISlot getTypeId() {
        return typeId;
    }

    /**
     * Sets the value of the typeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link IISlot }
     *     
     */
    public void setTypeId(IISlot value) {
        this.typeId = value;
    }

    /**
     * Gets the value of the updates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the updates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUpdates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UpdateCode }
     * 
     * 
     */
    public List<UpdateCode> getUpdates() {
        if (updates == null) {
            updates = new ArrayList<UpdateCode>();
        }
        return this.updates;
    }

    /**
     * Gets the value of the internalId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternalId() {
        return internalId;
    }

    /**
     * Sets the value of the internalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternalId(String value) {
        this.internalId = value;
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
     * Gets the value of the errors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getErrors() {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        return this.errors;
    }

    /**
     * Gets the value of the accountShares property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountShares property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountShares().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAccountShares() {
        if (accountShares == null) {
            accountShares = new ArrayList<String>();
        }
        return this.accountShares;
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
     * Gets the value of the enabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEnabled() {
        if (enabled == null) {
            return true;
        } else {
            return enabled;
        }
    }

    /**
     * Sets the value of the enabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnabled(Boolean value) {
        this.enabled = value;
    }

    /**
     * Gets the value of the sourceTrim property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceTrim() {
        return sourceTrim;
    }

    /**
     * Sets the value of the sourceTrim property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceTrim(String value) {
        this.sourceTrim = value;
    }

}
