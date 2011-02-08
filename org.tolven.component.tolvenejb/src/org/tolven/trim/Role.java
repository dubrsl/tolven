
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
 * <p>Java class for Role complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Role">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}InfrastructureRoot">
 *       &lt;sequence>
 *         &lt;element name="transitions" type="{urn:tolven-org:trim:4.0}Transitions" minOccurs="0"/>
 *         &lt;element name="transition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{urn:tolven-org:trim:4.0}SET_IISlot" minOccurs="0"/>
 *         &lt;element name="code" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="negationInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *         &lt;element name="name" type="{urn:tolven-org:trim:4.0}ENSlot" minOccurs="0"/>
 *         &lt;element name="addr" type="{urn:tolven-org:trim:4.0}ADSlot" minOccurs="0"/>
 *         &lt;element name="telecom" type="{urn:tolven-org:trim:4.0}TELSlot" minOccurs="0"/>
 *         &lt;element name="statusCode" type="{urn:tolven-org:trim:4.0}RoleStatus" minOccurs="0"/>
 *         &lt;element name="effectiveTime" type="{urn:tolven-org:trim:4.0}IVL_TSSlot" minOccurs="0"/>
 *         &lt;element name="certificateText" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="confidentiality" type="{urn:tolven-org:trim:4.0}SET_CESlot" minOccurs="0"/>
 *         &lt;element name="quantity" type="{urn:tolven-org:trim:4.0}RTOSlot" minOccurs="0"/>
 *         &lt;element name="positionNumber" type="{urn:tolven-org:trim:4.0}INTSlot" minOccurs="0"/>
 *         &lt;element name="access" type="{urn:tolven-org:trim:4.0}Access" minOccurs="0"/>
 *         &lt;element name="employee" type="{urn:tolven-org:trim:4.0}Employee" minOccurs="0"/>
 *         &lt;element name="licensedEntity" type="{urn:tolven-org:trim:4.0}LicensedEntity" minOccurs="0"/>
 *         &lt;element name="patient" type="{urn:tolven-org:trim:4.0}Patient" minOccurs="0"/>
 *         &lt;element name="qualifiedEntity" type="{urn:tolven-org:trim:4.0}QualifiedEntity" minOccurs="0"/>
 *         &lt;element name="player" type="{urn:tolven-org:trim:4.0}Entity" minOccurs="0"/>
 *         &lt;element name="scoper" type="{urn:tolven-org:trim:4.0}Entity" minOccurs="0"/>
 *         &lt;element name="participation" type="{urn:tolven-org:trim:4.0}RoleParticipation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="compute" type="{urn:tolven-org:trim:4.0}Compute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="classCode" use="required" type="{urn:tolven-org:trim:4.0}RoleClass" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Role", propOrder = {
    "transitions",
    "transition",
    "id",
    "code",
    "negationInd",
    "name",
    "addr",
    "telecom",
    "statusCode",
    "effectiveTime",
    "certificateText",
    "confidentiality",
    "quantity",
    "positionNumber",
    "access",
    "employee",
    "licensedEntity",
    "patient",
    "qualifiedEntity",
    "player",
    "scoper",
    "participations",
    "computes"
})
public class Role
    extends InfrastructureRoot
    implements Serializable
{

    protected Transitions transitions;
    protected String transition;
    protected SETIISlot id;
    protected CESlot code;
    protected BLSlot negationInd;
    protected ENSlot name;
    protected ADSlot addr;
    protected TELSlot telecom;
    protected RoleStatus statusCode;
    protected IVLTSSlot effectiveTime;
    protected EDSlot certificateText;
    protected SETCESlot confidentiality;
    protected RTOSlot quantity;
    protected INTSlot positionNumber;
    protected Access access;
    protected Employee employee;
    protected LicensedEntity licensedEntity;
    protected Patient patient;
    protected QualifiedEntity qualifiedEntity;
    protected Entity player;
    protected Entity scoper;
    @XmlElement(name = "participation")
    protected List<RoleParticipation> participations;
    @XmlElement(name = "compute")
    protected List<Compute> computes;
    @XmlAttribute(required = true)
    protected RoleClass classCode;

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
     * Gets the value of the negationInd property.
     * 
     * @return
     *     possible object is
     *     {@link BLSlot }
     *     
     */
    public BLSlot getNegationInd() {
        return negationInd;
    }

    /**
     * Sets the value of the negationInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BLSlot }
     *     
     */
    public void setNegationInd(BLSlot value) {
        this.negationInd = value;
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
     * Gets the value of the addr property.
     * 
     * @return
     *     possible object is
     *     {@link ADSlot }
     *     
     */
    public ADSlot getAddr() {
        return addr;
    }

    /**
     * Sets the value of the addr property.
     * 
     * @param value
     *     allowed object is
     *     {@link ADSlot }
     *     
     */
    public void setAddr(ADSlot value) {
        this.addr = value;
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
     * Gets the value of the statusCode property.
     * 
     * @return
     *     possible object is
     *     {@link RoleStatus }
     *     
     */
    public RoleStatus getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoleStatus }
     *     
     */
    public void setStatusCode(RoleStatus value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the effectiveTime property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTSSlot }
     *     
     */
    public IVLTSSlot getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * Sets the value of the effectiveTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTSSlot }
     *     
     */
    public void setEffectiveTime(IVLTSSlot value) {
        this.effectiveTime = value;
    }

    /**
     * Gets the value of the certificateText property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getCertificateText() {
        return certificateText;
    }

    /**
     * Sets the value of the certificateText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setCertificateText(EDSlot value) {
        this.certificateText = value;
    }

    /**
     * Gets the value of the confidentiality property.
     * 
     * @return
     *     possible object is
     *     {@link SETCESlot }
     *     
     */
    public SETCESlot getConfidentiality() {
        return confidentiality;
    }

    /**
     * Sets the value of the confidentiality property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCESlot }
     *     
     */
    public void setConfidentiality(SETCESlot value) {
        this.confidentiality = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link RTOSlot }
     *     
     */
    public RTOSlot getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link RTOSlot }
     *     
     */
    public void setQuantity(RTOSlot value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the positionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link INTSlot }
     *     
     */
    public INTSlot getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the value of the positionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link INTSlot }
     *     
     */
    public void setPositionNumber(INTSlot value) {
        this.positionNumber = value;
    }

    /**
     * Gets the value of the access property.
     * 
     * @return
     *     possible object is
     *     {@link Access }
     *     
     */
    public Access getAccess() {
        return access;
    }

    /**
     * Sets the value of the access property.
     * 
     * @param value
     *     allowed object is
     *     {@link Access }
     *     
     */
    public void setAccess(Access value) {
        this.access = value;
    }

    /**
     * Gets the value of the employee property.
     * 
     * @return
     *     possible object is
     *     {@link Employee }
     *     
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets the value of the employee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Employee }
     *     
     */
    public void setEmployee(Employee value) {
        this.employee = value;
    }

    /**
     * Gets the value of the licensedEntity property.
     * 
     * @return
     *     possible object is
     *     {@link LicensedEntity }
     *     
     */
    public LicensedEntity getLicensedEntity() {
        return licensedEntity;
    }

    /**
     * Sets the value of the licensedEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link LicensedEntity }
     *     
     */
    public void setLicensedEntity(LicensedEntity value) {
        this.licensedEntity = value;
    }

    /**
     * Gets the value of the patient property.
     * 
     * @return
     *     possible object is
     *     {@link Patient }
     *     
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the value of the patient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Patient }
     *     
     */
    public void setPatient(Patient value) {
        this.patient = value;
    }

    /**
     * Gets the value of the qualifiedEntity property.
     * 
     * @return
     *     possible object is
     *     {@link QualifiedEntity }
     *     
     */
    public QualifiedEntity getQualifiedEntity() {
        return qualifiedEntity;
    }

    /**
     * Sets the value of the qualifiedEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QualifiedEntity }
     *     
     */
    public void setQualifiedEntity(QualifiedEntity value) {
        this.qualifiedEntity = value;
    }

    /**
     * Gets the value of the player property.
     * 
     * @return
     *     possible object is
     *     {@link Entity }
     *     
     */
    public Entity getPlayer() {
        return player;
    }

    /**
     * Sets the value of the player property.
     * 
     * @param value
     *     allowed object is
     *     {@link Entity }
     *     
     */
    public void setPlayer(Entity value) {
        this.player = value;
    }

    /**
     * Gets the value of the scoper property.
     * 
     * @return
     *     possible object is
     *     {@link Entity }
     *     
     */
    public Entity getScoper() {
        return scoper;
    }

    /**
     * Sets the value of the scoper property.
     * 
     * @param value
     *     allowed object is
     *     {@link Entity }
     *     
     */
    public void setScoper(Entity value) {
        this.scoper = value;
    }

    /**
     * Gets the value of the participations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoleParticipation }
     * 
     * 
     */
    public List<RoleParticipation> getParticipations() {
        if (participations == null) {
            participations = new ArrayList<RoleParticipation>();
        }
        return this.participations;
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
     *     {@link RoleClass }
     *     
     */
    public RoleClass getClassCode() {
        return classCode;
    }

    /**
     * Sets the value of the classCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoleClass }
     *     
     */
    public void setClassCode(RoleClass value) {
        this.classCode = value;
    }

}
