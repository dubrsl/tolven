
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Entity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Entity">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}InfrastructureRoot">
 *       &lt;sequence>
 *         &lt;element name="transitions" type="{urn:tolven-org:trim:4.0}Transitions" minOccurs="0"/>
 *         &lt;element name="transition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{urn:tolven-org:trim:4.0}SET_IISlot" minOccurs="0"/>
 *         &lt;element name="code" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="statusCode" type="{urn:tolven-org:trim:4.0}EntityStatus" minOccurs="0"/>
 *         &lt;element name="quantity" type="{urn:tolven-org:trim:4.0}PQSlot" minOccurs="0"/>
 *         &lt;element name="name" type="{urn:tolven-org:trim:4.0}ENSlot" minOccurs="0"/>
 *         &lt;element name="desc" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="existenceTime" type="{urn:tolven-org:trim:4.0}IVL_TSSlot" minOccurs="0"/>
 *         &lt;element name="telecom" type="{urn:tolven-org:trim:4.0}TELSlot" minOccurs="0"/>
 *         &lt;element name="riskCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="handlingCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="material" type="{urn:tolven-org:trim:4.0}Material" minOccurs="0"/>
 *         &lt;element name="manufacturedMaterial" type="{urn:tolven-org:trim:4.0}ManufacturedMaterial" minOccurs="0"/>
 *         &lt;element name="device" type="{urn:tolven-org:trim:4.0}Device" minOccurs="0"/>
 *         &lt;element name="container" type="{urn:tolven-org:trim:4.0}Container" minOccurs="0"/>
 *         &lt;element name="livingSubject" type="{urn:tolven-org:trim:4.0}LivingSubject" minOccurs="0"/>
 *         &lt;element name="nonPersonLivingSubject" type="{urn:tolven-org:trim:4.0}NonPersonLivingSubject" minOccurs="0"/>
 *         &lt;element name="organization" type="{urn:tolven-org:trim:4.0}Organization" minOccurs="0"/>
 *         &lt;element name="place" type="{urn:tolven-org:trim:4.0}Place" minOccurs="0"/>
 *         &lt;element name="person" type="{urn:tolven-org:trim:4.0}Person" minOccurs="0"/>
 *         &lt;element name="playedRole" type="{urn:tolven-org:trim:4.0}Role" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="scopedRole" type="{urn:tolven-org:trim:4.0}Role" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="languageCommunication" type="{urn:tolven-org:trim:4.0}LanguageCommunication" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="compute" type="{urn:tolven-org:trim:4.0}Compute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="classCode" use="required" type="{urn:tolven-org:trim:4.0}EntityClass" />
 *       &lt;attribute name="determinerCode" use="required" type="{urn:tolven-org:trim:4.0}EntityDeterminer" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Entity", propOrder = {
    "transitions",
    "transition",
    "id",
    "code",
    "statusCode",
    "quantity",
    "name",
    "desc",
    "existenceTime",
    "telecom",
    "riskCode",
    "handlingCode",
    "material",
    "manufacturedMaterial",
    "device",
    "container",
    "livingSubject",
    "nonPersonLivingSubject",
    "organization",
    "place",
    "person",
    "playedRoles",
    "scopedRoles",
    "languageCommunications",
    "computes"
})
public class Entity
    extends InfrastructureRoot
    implements Serializable
{

    protected Transitions transitions;
    protected String transition;
    protected SETIISlot id;
    protected CESlot code;
    protected EntityStatus statusCode;
    protected PQSlot quantity;
    protected ENSlot name;
    protected EDSlot desc;
    protected IVLTSSlot existenceTime;
    protected TELSlot telecom;
    protected CESlot riskCode;
    protected CESlot handlingCode;
    protected Material material;
    protected ManufacturedMaterial manufacturedMaterial;
    protected Device device;
    protected Container container;
    protected LivingSubject livingSubject;
    protected NonPersonLivingSubject nonPersonLivingSubject;
    protected Organization organization;
    protected Place place;
    protected Person person;
    @XmlElement(name = "playedRole", nillable = true)
    protected List<Role> playedRoles;
    @XmlElement(name = "scopedRole", nillable = true)
    protected List<Role> scopedRoles;
    @XmlElement(name = "languageCommunication", nillable = true)
    protected List<LanguageCommunication> languageCommunications;
    @XmlElement(name = "compute")
    protected List<Compute> computes;
    @XmlAttribute(required = true)
    protected EntityClass classCode;
    @XmlAttribute(required = true)
    protected EntityDeterminer determinerCode;

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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link SETIISlot }
     *     
     */
    public SETIISlot getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETIISlot }
     *     
     */
    public void setId(SETIISlot value) {
        this.id = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setCode(CESlot value) {
        this.code = value;
    }

    /**
     * Gets the value of the statusCode property.
     * 
     * @return
     *     possible object is
     *     {@link EntityStatus }
     *     
     */
    public EntityStatus getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityStatus }
     *     
     */
    public void setStatusCode(EntityStatus value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQSlot }
     *     
     */
    public PQSlot getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQSlot }
     *     
     */
    public void setQuantity(PQSlot value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link ENSlot }
     *     
     */
    public ENSlot getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link ENSlot }
     *     
     */
    public void setName(ENSlot value) {
        this.name = value;
    }

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setDesc(EDSlot value) {
        this.desc = value;
    }

    /**
     * Gets the value of the existenceTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTSSlot }
     *     
     */
    public IVLTSSlot getExistenceTime() {
        return existenceTime;
    }

    /**
     * Sets the value of the existenceTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTSSlot }
     *     
     */
    public void setExistenceTime(IVLTSSlot value) {
        this.existenceTime = value;
    }

    /**
     * Gets the value of the telecom property.
     * 
     * @return
     *     possible object is
     *     {@link TELSlot }
     *     
     */
    public TELSlot getTelecom() {
        return telecom;
    }

    /**
     * Sets the value of the telecom property.
     * 
     * @param value
     *     allowed object is
     *     {@link TELSlot }
     *     
     */
    public void setTelecom(TELSlot value) {
        this.telecom = value;
    }

    /**
     * Gets the value of the riskCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getRiskCode() {
        return riskCode;
    }

    /**
     * Sets the value of the riskCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setRiskCode(CESlot value) {
        this.riskCode = value;
    }

    /**
     * Gets the value of the handlingCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getHandlingCode() {
        return handlingCode;
    }

    /**
     * Sets the value of the handlingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setHandlingCode(CESlot value) {
        this.handlingCode = value;
    }

    /**
     * Gets the value of the material property.
     * 
     * @return
     *     possible object is
     *     {@link Material }
     *     
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the value of the material property.
     * 
     * @param value
     *     allowed object is
     *     {@link Material }
     *     
     */
    public void setMaterial(Material value) {
        this.material = value;
    }

    /**
     * Gets the value of the manufacturedMaterial property.
     * 
     * @return
     *     possible object is
     *     {@link ManufacturedMaterial }
     *     
     */
    public ManufacturedMaterial getManufacturedMaterial() {
        return manufacturedMaterial;
    }

    /**
     * Sets the value of the manufacturedMaterial property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManufacturedMaterial }
     *     
     */
    public void setManufacturedMaterial(ManufacturedMaterial value) {
        this.manufacturedMaterial = value;
    }

    /**
     * Gets the value of the device property.
     * 
     * @return
     *     possible object is
     *     {@link Device }
     *     
     */
    public Device getDevice() {
        return device;
    }

    /**
     * Sets the value of the device property.
     * 
     * @param value
     *     allowed object is
     *     {@link Device }
     *     
     */
    public void setDevice(Device value) {
        this.device = value;
    }

    /**
     * Gets the value of the container property.
     * 
     * @return
     *     possible object is
     *     {@link Container }
     *     
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Sets the value of the container property.
     * 
     * @param value
     *     allowed object is
     *     {@link Container }
     *     
     */
    public void setContainer(Container value) {
        this.container = value;
    }

    /**
     * Gets the value of the livingSubject property.
     * 
     * @return
     *     possible object is
     *     {@link LivingSubject }
     *     
     */
    public LivingSubject getLivingSubject() {
        return livingSubject;
    }

    /**
     * Sets the value of the livingSubject property.
     * 
     * @param value
     *     allowed object is
     *     {@link LivingSubject }
     *     
     */
    public void setLivingSubject(LivingSubject value) {
        this.livingSubject = value;
    }

    /**
     * Gets the value of the nonPersonLivingSubject property.
     * 
     * @return
     *     possible object is
     *     {@link NonPersonLivingSubject }
     *     
     */
    public NonPersonLivingSubject getNonPersonLivingSubject() {
        return nonPersonLivingSubject;
    }

    /**
     * Sets the value of the nonPersonLivingSubject property.
     * 
     * @param value
     *     allowed object is
     *     {@link NonPersonLivingSubject }
     *     
     */
    public void setNonPersonLivingSubject(NonPersonLivingSubject value) {
        this.nonPersonLivingSubject = value;
    }

    /**
     * Gets the value of the organization property.
     * 
     * @return
     *     possible object is
     *     {@link Organization }
     *     
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the value of the organization property.
     * 
     * @param value
     *     allowed object is
     *     {@link Organization }
     *     
     */
    public void setOrganization(Organization value) {
        this.organization = value;
    }

    /**
     * Gets the value of the place property.
     * 
     * @return
     *     possible object is
     *     {@link Place }
     *     
     */
    public Place getPlace() {
        return place;
    }

    /**
     * Sets the value of the place property.
     * 
     * @param value
     *     allowed object is
     *     {@link Place }
     *     
     */
    public void setPlace(Place value) {
        this.place = value;
    }

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link Person }
     *     
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person }
     *     
     */
    public void setPerson(Person value) {
        this.person = value;
    }

    /**
     * Gets the value of the playedRoles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the playedRoles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlayedRoles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Role }
     * 
     * 
     */
    public List<Role> getPlayedRoles() {
        if (playedRoles == null) {
            playedRoles = new ArrayList<Role>();
        }
        return this.playedRoles;
    }

    /**
     * Gets the value of the scopedRoles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the scopedRoles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScopedRoles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Role }
     * 
     * 
     */
    public List<Role> getScopedRoles() {
        if (scopedRoles == null) {
            scopedRoles = new ArrayList<Role>();
        }
        return this.scopedRoles;
    }

    /**
     * Gets the value of the languageCommunications property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the languageCommunications property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLanguageCommunications().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LanguageCommunication }
     * 
     * 
     */
    public List<LanguageCommunication> getLanguageCommunications() {
        if (languageCommunications == null) {
            languageCommunications = new ArrayList<LanguageCommunication>();
        }
        return this.languageCommunications;
    }

    /**
     * Gets the value of the computes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the computes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComputes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Compute }
     * 
     * 
     */
    public List<Compute> getComputes() {
        if (computes == null) {
            computes = new ArrayList<Compute>();
        }
        return this.computes;
    }

    /**
     * Gets the value of the classCode property.
     * 
     * @return
     *     possible object is
     *     {@link EntityClass }
     *     
     */
    public EntityClass getClassCode() {
        return classCode;
    }

    /**
     * Sets the value of the classCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityClass }
     *     
     */
    public void setClassCode(EntityClass value) {
        this.classCode = value;
    }

    /**
     * Gets the value of the determinerCode property.
     * 
     * @return
     *     possible object is
     *     {@link EntityDeterminer }
     *     
     */
    public EntityDeterminer getDeterminerCode() {
        return determinerCode;
    }

    /**
     * Sets the value of the determinerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityDeterminer }
     *     
     */
    public void setDeterminerCode(EntityDeterminer value) {
        this.determinerCode = value;
    }

}
