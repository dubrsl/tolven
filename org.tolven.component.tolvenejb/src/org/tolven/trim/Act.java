
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Act complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Act">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}InfrastructureRoot">
 *       &lt;sequence>
 *         &lt;element name="sendTo" type="{urn:tolven-org:trim:4.0}Party" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="transitions" type="{urn:tolven-org:trim:4.0}Transitions" minOccurs="0"/>
 *         &lt;element name="transition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{urn:tolven-org:trim:4.0}SET_IISlot" minOccurs="0"/>
 *         &lt;element name="code" type="{urn:tolven-org:trim:4.0}CDSlot" minOccurs="0"/>
 *         &lt;element name="negationInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *         &lt;element name="statusCode" type="{urn:tolven-org:trim:4.0}ActStatus" minOccurs="0"/>
 *         &lt;element name="derivationExpr" type="{urn:tolven-org:trim:4.0}STSlot" minOccurs="0"/>
 *         &lt;element name="title" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="text" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="effectiveTime" type="{urn:tolven-org:trim:4.0}GTSSlot" minOccurs="0"/>
 *         &lt;element name="activityTime" type="{urn:tolven-org:trim:4.0}GTSSlot" minOccurs="0"/>
 *         &lt;element name="availabilityTime" type="{urn:tolven-org:trim:4.0}TSSlot" minOccurs="0"/>
 *         &lt;element name="priorityCode" type="{urn:tolven-org:trim:4.0}SET_CESlot" minOccurs="0"/>
 *         &lt;element name="confidentialityCode" type="{urn:tolven-org:trim:4.0}SET_CESlot" minOccurs="0"/>
 *         &lt;element name="repeatNumber" type="{urn:tolven-org:trim:4.0}IVLINTSlot" minOccurs="0"/>
 *         &lt;element name="interruptibleInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *         &lt;element name="independentInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *         &lt;element name="uncertaintyCode" type="{urn:tolven-org:trim:4.0}CSSlot" minOccurs="0"/>
 *         &lt;element name="reasonCode" type="{urn:tolven-org:trim:4.0}SET_CESlot" minOccurs="0"/>
 *         &lt;element name="languageCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="observation" type="{urn:tolven-org:trim:4.0}Observation" minOccurs="0"/>
 *         &lt;element name="patientEncounter" type="{urn:tolven-org:trim:4.0}PatientEncounter" minOccurs="0"/>
 *         &lt;element name="procedure" type="{urn:tolven-org:trim:4.0}Procedure" minOccurs="0"/>
 *         &lt;element name="substanceAdministration" type="{urn:tolven-org:trim:4.0}SubstanceAdministration" minOccurs="0"/>
 *         &lt;element name="supply" type="{urn:tolven-org:trim:4.0}Supply" minOccurs="0"/>
 *         &lt;element name="participation" type="{urn:tolven-org:trim:4.0}ActParticipation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="relationship" type="{urn:tolven-org:trim:4.0}ActRelationship" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="compute" type="{urn:tolven-org:trim:4.0}Compute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="classCode" use="required" type="{urn:tolven-org:trim:4.0}ActClass" />
 *       &lt;attribute name="moodCode" use="required" type="{urn:tolven-org:trim:4.0}ActMood" />
 *       &lt;attribute name="levelCode" type="{urn:tolven-org:trim:4.0}cs" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Act", propOrder = {
    "sendTos",
    "transitions",
    "transition",
    "id",
    "code",
    "negationInd",
    "statusCode",
    "derivationExpr",
    "title",
    "text",
    "effectiveTime",
    "activityTime",
    "availabilityTime",
    "priorityCode",
    "confidentialityCode",
    "repeatNumber",
    "interruptibleInd",
    "independentInd",
    "uncertaintyCode",
    "reasonCode",
    "languageCode",
    "observation",
    "patientEncounter",
    "procedure",
    "substanceAdministration",
    "supply",
    "participations",
    "relationships",
    "computes"
})
public class Act
    extends InfrastructureRoot
    implements Serializable
{

    @XmlElement(name = "sendTo")
    protected List<Party> sendTos;
    protected Transitions transitions;
    protected String transition;
    protected SETIISlot id;
    protected CDSlot code;
    protected BLSlot negationInd;
    protected ActStatus statusCode;
    protected STSlot derivationExpr;
    protected EDSlot title;
    protected EDSlot text;
    protected GTSSlot effectiveTime;
    protected GTSSlot activityTime;
    protected TSSlot availabilityTime;
    protected SETCESlot priorityCode;
    protected SETCESlot confidentialityCode;
    protected IVLINTSlot repeatNumber;
    protected BLSlot interruptibleInd;
    protected BLSlot independentInd;
    protected CSSlot uncertaintyCode;
    protected SETCESlot reasonCode;
    protected CESlot languageCode;
    protected Observation observation;
    protected PatientEncounter patientEncounter;
    protected Procedure procedure;
    protected SubstanceAdministration substanceAdministration;
    protected Supply supply;
    @XmlElement(name = "participation")
    protected List<ActParticipation> participations;
    @XmlElement(name = "relationship")
    protected List<ActRelationship> relationships;
    @XmlElement(name = "compute")
    protected List<Compute> computes;
    @XmlAttribute(required = true)
    protected ActClass classCode;
    @XmlAttribute(required = true)
    protected ActMood moodCode;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String levelCode;

    /**
     * Gets the value of the sendTos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sendTos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSendTos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Party }
     * 
     * 
     */
    public List<Party> getSendTos() {
        if (sendTos == null) {
            sendTos = new ArrayList<Party>();
        }
        return this.sendTos;
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
     *     {@link CDSlot }
     *     
     */
    public CDSlot getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link CDSlot }
     *     
     */
    public void setCode(CDSlot value) {
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
     * Gets the value of the statusCode property.
     * 
     * @return
     *     possible object is
     *     {@link ActStatus }
     *     
     */
    public ActStatus getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActStatus }
     *     
     */
    public void setStatusCode(ActStatus value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the derivationExpr property.
     * 
     * @return
     *     possible object is
     *     {@link STSlot }
     *     
     */
    public STSlot getDerivationExpr() {
        return derivationExpr;
    }

    /**
     * Sets the value of the derivationExpr property.
     * 
     * @param value
     *     allowed object is
     *     {@link STSlot }
     *     
     */
    public void setDerivationExpr(STSlot value) {
        this.derivationExpr = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setTitle(EDSlot value) {
        this.title = value;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setText(EDSlot value) {
        this.text = value;
    }

    /**
     * Gets the value of the effectiveTime property.
     * 
     * @return
     *     possible object is
     *     {@link GTSSlot }
     *     
     */
    public GTSSlot getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * Sets the value of the effectiveTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link GTSSlot }
     *     
     */
    public void setEffectiveTime(GTSSlot value) {
        this.effectiveTime = value;
    }

    /**
     * Gets the value of the activityTime property.
     * 
     * @return
     *     possible object is
     *     {@link GTSSlot }
     *     
     */
    public GTSSlot getActivityTime() {
        return activityTime;
    }

    /**
     * Sets the value of the activityTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link GTSSlot }
     *     
     */
    public void setActivityTime(GTSSlot value) {
        this.activityTime = value;
    }

    /**
     * Gets the value of the availabilityTime property.
     * 
     * @return
     *     possible object is
     *     {@link TSSlot }
     *     
     */
    public TSSlot getAvailabilityTime() {
        return availabilityTime;
    }

    /**
     * Sets the value of the availabilityTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TSSlot }
     *     
     */
    public void setAvailabilityTime(TSSlot value) {
        this.availabilityTime = value;
    }

    /**
     * Gets the value of the priorityCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCESlot }
     *     
     */
    public SETCESlot getPriorityCode() {
        return priorityCode;
    }

    /**
     * Sets the value of the priorityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCESlot }
     *     
     */
    public void setPriorityCode(SETCESlot value) {
        this.priorityCode = value;
    }

    /**
     * Gets the value of the confidentialityCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCESlot }
     *     
     */
    public SETCESlot getConfidentialityCode() {
        return confidentialityCode;
    }

    /**
     * Sets the value of the confidentialityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCESlot }
     *     
     */
    public void setConfidentialityCode(SETCESlot value) {
        this.confidentialityCode = value;
    }

    /**
     * Gets the value of the repeatNumber property.
     * 
     * @return
     *     possible object is
     *     {@link IVLINTSlot }
     *     
     */
    public IVLINTSlot getRepeatNumber() {
        return repeatNumber;
    }

    /**
     * Sets the value of the repeatNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLINTSlot }
     *     
     */
    public void setRepeatNumber(IVLINTSlot value) {
        this.repeatNumber = value;
    }

    /**
     * Gets the value of the interruptibleInd property.
     * 
     * @return
     *     possible object is
     *     {@link BLSlot }
     *     
     */
    public BLSlot getInterruptibleInd() {
        return interruptibleInd;
    }

    /**
     * Sets the value of the interruptibleInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BLSlot }
     *     
     */
    public void setInterruptibleInd(BLSlot value) {
        this.interruptibleInd = value;
    }

    /**
     * Gets the value of the independentInd property.
     * 
     * @return
     *     possible object is
     *     {@link BLSlot }
     *     
     */
    public BLSlot getIndependentInd() {
        return independentInd;
    }

    /**
     * Sets the value of the independentInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BLSlot }
     *     
     */
    public void setIndependentInd(BLSlot value) {
        this.independentInd = value;
    }

    /**
     * Gets the value of the uncertaintyCode property.
     * 
     * @return
     *     possible object is
     *     {@link CSSlot }
     *     
     */
    public CSSlot getUncertaintyCode() {
        return uncertaintyCode;
    }

    /**
     * Sets the value of the uncertaintyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CSSlot }
     *     
     */
    public void setUncertaintyCode(CSSlot value) {
        this.uncertaintyCode = value;
    }

    /**
     * Gets the value of the reasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link SETCESlot }
     *     
     */
    public SETCESlot getReasonCode() {
        return reasonCode;
    }

    /**
     * Sets the value of the reasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETCESlot }
     *     
     */
    public void setReasonCode(SETCESlot value) {
        this.reasonCode = value;
    }

    /**
     * Gets the value of the languageCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the value of the languageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setLanguageCode(CESlot value) {
        this.languageCode = value;
    }

    /**
     * Gets the value of the observation property.
     * 
     * @return
     *     possible object is
     *     {@link Observation }
     *     
     */
    public Observation getObservation() {
        return observation;
    }

    /**
     * Sets the value of the observation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Observation }
     *     
     */
    public void setObservation(Observation value) {
        this.observation = value;
    }

    /**
     * Gets the value of the patientEncounter property.
     * 
     * @return
     *     possible object is
     *     {@link PatientEncounter }
     *     
     */
    public PatientEncounter getPatientEncounter() {
        return patientEncounter;
    }

    /**
     * Sets the value of the patientEncounter property.
     * 
     * @param value
     *     allowed object is
     *     {@link PatientEncounter }
     *     
     */
    public void setPatientEncounter(PatientEncounter value) {
        this.patientEncounter = value;
    }

    /**
     * Gets the value of the procedure property.
     * 
     * @return
     *     possible object is
     *     {@link Procedure }
     *     
     */
    public Procedure getProcedure() {
        return procedure;
    }

    /**
     * Sets the value of the procedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link Procedure }
     *     
     */
    public void setProcedure(Procedure value) {
        this.procedure = value;
    }

    /**
     * Gets the value of the substanceAdministration property.
     * 
     * @return
     *     possible object is
     *     {@link SubstanceAdministration }
     *     
     */
    public SubstanceAdministration getSubstanceAdministration() {
        return substanceAdministration;
    }

    /**
     * Sets the value of the substanceAdministration property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubstanceAdministration }
     *     
     */
    public void setSubstanceAdministration(SubstanceAdministration value) {
        this.substanceAdministration = value;
    }

    /**
     * Gets the value of the supply property.
     * 
     * @return
     *     possible object is
     *     {@link Supply }
     *     
     */
    public Supply getSupply() {
        return supply;
    }

    /**
     * Sets the value of the supply property.
     * 
     * @param value
     *     allowed object is
     *     {@link Supply }
     *     
     */
    public void setSupply(Supply value) {
        this.supply = value;
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
     * {@link ActParticipation }
     * 
     * 
     */
    public List<ActParticipation> getParticipations() {
        if (participations == null) {
            participations = new ArrayList<ActParticipation>();
        }
        return this.participations;
    }

    /**
     * Gets the value of the relationships property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationships property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationships().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActRelationship }
     * 
     * 
     */
    public List<ActRelationship> getRelationships() {
        if (relationships == null) {
            relationships = new ArrayList<ActRelationship>();
        }
        return this.relationships;
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
     *     {@link ActClass }
     *     
     */
    public ActClass getClassCode() {
        return classCode;
    }

    /**
     * Sets the value of the classCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActClass }
     *     
     */
    public void setClassCode(ActClass value) {
        this.classCode = value;
    }

    /**
     * Gets the value of the moodCode property.
     * 
     * @return
     *     possible object is
     *     {@link ActMood }
     *     
     */
    public ActMood getMoodCode() {
        return moodCode;
    }

    /**
     * Sets the value of the moodCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActMood }
     *     
     */
    public void setMoodCode(ActMood value) {
        this.moodCode = value;
    }

    /**
     * Gets the value of the levelCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevelCode() {
        return levelCode;
    }

    /**
     * Sets the value of the levelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevelCode(String value) {
        this.levelCode = value;
    }

}
