
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Participation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Participation">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}InfrastructureRoot">
 *       &lt;sequence>
 *         &lt;element name="functionCode" type="{urn:tolven-org:trim:4.0}CDSlot" minOccurs="0"/>
 *         &lt;element name="negationInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *         &lt;element name="noteText" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="time" type="{urn:tolven-org:trim:4.0}IVL_TS" minOccurs="0"/>
 *         &lt;element name="modeCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="awarenessCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="signatureText" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="performInd" type="{urn:tolven-org:trim:4.0}BL" minOccurs="0"/>
 *         &lt;element name="substitutionConditionCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}Name" />
 *       &lt;attribute name="typeCode" use="required" type="{urn:tolven-org:trim:4.0}ParticipationType" />
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="repeating" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="contextControlCode" type="{urn:tolven-org:trim:4.0}ContextControl" />
 *       &lt;attribute name="sequenceNumber" type="{urn:tolven-org:trim:4.0}int" />
 *       &lt;attribute name="subsetCode" type="{urn:tolven-org:trim:4.0}ParticipationSubset" />
 *       &lt;attribute name="signatureCode" type="{urn:tolven-org:trim:4.0}ParticipationSignature" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Participation", propOrder = {
    "functionCode",
    "negationInd",
    "noteText",
    "time",
    "modeCode",
    "awarenessCode",
    "signatureText",
    "performInd",
    "substitutionConditionCode"
})
@XmlSeeAlso({
    ActParticipation.class,
    RoleParticipation.class
})
public abstract class Participation
    extends InfrastructureRoot
    implements Serializable
{

    protected CDSlot functionCode;
    protected BLSlot negationInd;
    protected EDSlot noteText;
    protected IVLTS time;
    protected CESlot modeCode;
    protected CESlot awarenessCode;
    protected EDSlot signatureText;
    protected BL performInd;
    protected CESlot substitutionConditionCode;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "Name")
    protected String name;
    @XmlAttribute(required = true)
    protected ParticipationType typeCode;
    @XmlAttribute
    protected Boolean optional;
    @XmlAttribute
    protected Boolean repeating;
    @XmlAttribute
    protected ContextControl contextControlCode;
    @XmlAttribute
    protected Integer sequenceNumber;
    @XmlAttribute
    protected ParticipationSubset subsetCode;
    @XmlAttribute
    protected ParticipationSignature signatureCode;

    /**
     * Gets the value of the functionCode property.
     * 
     * @return
     *     possible object is
     *     {@link CDSlot }
     *     
     */
    public CDSlot getFunctionCode() {
        return functionCode;
    }

    /**
     * Sets the value of the functionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CDSlot }
     *     
     */
    public void setFunctionCode(CDSlot value) {
        this.functionCode = value;
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
     * Gets the value of the noteText property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getNoteText() {
        return noteText;
    }

    /**
     * Sets the value of the noteText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setNoteText(EDSlot value) {
        this.noteText = value;
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link IVLTS }
     *     
     */
    public IVLTS getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLTS }
     *     
     */
    public void setTime(IVLTS value) {
        this.time = value;
    }

    /**
     * Gets the value of the modeCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getModeCode() {
        return modeCode;
    }

    /**
     * Sets the value of the modeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setModeCode(CESlot value) {
        this.modeCode = value;
    }

    /**
     * Gets the value of the awarenessCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getAwarenessCode() {
        return awarenessCode;
    }

    /**
     * Sets the value of the awarenessCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setAwarenessCode(CESlot value) {
        this.awarenessCode = value;
    }

    /**
     * Gets the value of the signatureText property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getSignatureText() {
        return signatureText;
    }

    /**
     * Sets the value of the signatureText property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setSignatureText(EDSlot value) {
        this.signatureText = value;
    }

    /**
     * Gets the value of the performInd property.
     * 
     * @return
     *     possible object is
     *     {@link BL }
     *     
     */
    public BL getPerformInd() {
        return performInd;
    }

    /**
     * Sets the value of the performInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BL }
     *     
     */
    public void setPerformInd(BL value) {
        this.performInd = value;
    }

    /**
     * Gets the value of the substitutionConditionCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getSubstitutionConditionCode() {
        return substitutionConditionCode;
    }

    /**
     * Sets the value of the substitutionConditionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setSubstitutionConditionCode(CESlot value) {
        this.substitutionConditionCode = value;
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
     * Gets the value of the typeCode property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipationType }
     *     
     */
    public ParticipationType getTypeCode() {
        return typeCode;
    }

    /**
     * Sets the value of the typeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipationType }
     *     
     */
    public void setTypeCode(ParticipationType value) {
        this.typeCode = value;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOptional() {
        return optional;
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
     * Gets the value of the repeating property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRepeating() {
        return repeating;
    }

    /**
     * Sets the value of the repeating property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRepeating(Boolean value) {
        this.repeating = value;
    }

    /**
     * Gets the value of the contextControlCode property.
     * 
     * @return
     *     possible object is
     *     {@link ContextControl }
     *     
     */
    public ContextControl getContextControlCode() {
        return contextControlCode;
    }

    /**
     * Sets the value of the contextControlCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextControl }
     *     
     */
    public void setContextControlCode(ContextControl value) {
        this.contextControlCode = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSequenceNumber(Integer value) {
        this.sequenceNumber = value;
    }

    /**
     * Gets the value of the subsetCode property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipationSubset }
     *     
     */
    public ParticipationSubset getSubsetCode() {
        return subsetCode;
    }

    /**
     * Sets the value of the subsetCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipationSubset }
     *     
     */
    public void setSubsetCode(ParticipationSubset value) {
        this.subsetCode = value;
    }

    /**
     * Gets the value of the signatureCode property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipationSignature }
     *     
     */
    public ParticipationSignature getSignatureCode() {
        return signatureCode;
    }

    /**
     * Sets the value of the signatureCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipationSignature }
     *     
     */
    public void setSignatureCode(ParticipationSignature value) {
        this.signatureCode = value;
    }

}
