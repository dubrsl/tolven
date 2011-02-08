
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 * 				All RIM-object attributes attributes are represented by a slot.
 * 				A slot is a wrapper around a normal HL3 V3 Datatype. Slots contain
 * 				additional information, facets, about the contained datatype such as
 * 				the label to use, how the attribute might be bound to some
 * 				object external to the slot (and typically the RIM object).
 * 				The valueSet facet is used to define a constrained list of
 * 				choices for the value facet. Additional metadata defined validation
 * 				criteria. For example, certain dates may require that the data be in the future,
 * 				such as when scheduling an appointment. Or in the past, such as when recording
 * 				a historical event. The new facet is used to initialize fields when
 * 				a trim is instantiated, such as to initialize a date in a new
 * 				observation to today's date.
 * 				Slots primarily carry the value facet of the slot. Each slot definition typically allows
 * 				one or more HL7 datatypes depending on the constraints for that type. For example,
 * 				an ST can be substituted for an ED. A null flavor can almost always be substituted 
 * 				for any other datatype. While a slot defines the absolute allowable values, a valueSet can
 * 				be used to constrain the set of choices (if more than one is allowed) for a specific 
 * 				attribute.
 * 			
 * 
 * <p>Java class for Slot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Slot">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="bind" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="label" type="{urn:tolven-org:trim:4.0}LabelFacet" minOccurs="0"/>
 *         &lt;element name="new" type="{urn:tolven-org:trim:4.0}NewFacet" minOccurs="0"/>
 *         &lt;element name="validate" type="{urn:tolven-org:trim:4.0}ValidateFacet" minOccurs="0"/>
 *         &lt;element name="valueSet" type="{http://www.w3.org/2001/XMLSchema}Name" minOccurs="0"/>
 *         &lt;element name="originalText" type="{urn:tolven-org:trim:4.0}st" minOccurs="0"/>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="datatype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Slot", propOrder = {
    "froms",
    "bind",
    "label",
    "_new",
    "validate",
    "valueSet",
    "originalText",
    "errors"
})
@XmlSeeAlso({
    CSSlot.class,
    CESlot.class,
    IISlot.class,
    BLSlot.class,
    INTSlot.class,
    PQSlot.class,
    GTSSlot.class,
    EDSlot.class,
    TSSlot.class,
    REALSlot.class,
    STSlot.class,
    TELSlot.class,
    MOSlot.class,
    SETCDSlot.class,
    ENSlot.class,
    SETRTOSlot.class,
    IVLINTSlot.class,
    SETEDSlot.class,
    SCSlot.class,
    SETIISlot.class,
    IVLPQSlot.class,
    ObservationValueSlot.class,
    ENXPSlot.class,
    ADXPSlot.class,
    SETPQSlot.class,
    ADSlot.class,
    IVLTSSlot.class,
    RTOSlot.class,
    SETCESlot.class,
    CDSlot.class
})
public abstract class Slot
    implements Serializable
{

    @XmlElement(name = "from")
    protected List<String> froms;
    protected String bind;
    protected LabelFacet label;
    @XmlElement(name = "new")
    protected NewFacet _new;
    protected ValidateFacet validate;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "Name")
    protected String valueSet;
    protected String originalText;
    @XmlElement(name = "error")
    protected List<String> errors;
    @XmlAttribute
    protected String datatype;

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
     * Gets the value of the bind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBind() {
        return bind;
    }

    /**
     * Sets the value of the bind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBind(String value) {
        this.bind = value;
    }

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
     * Gets the value of the new property.
     * 
     * @return
     *     possible object is
     *     {@link NewFacet }
     *     
     */
    public NewFacet getNew() {
        return _new;
    }

    /**
     * Sets the value of the new property.
     * 
     * @param value
     *     allowed object is
     *     {@link NewFacet }
     *     
     */
    public void setNew(NewFacet value) {
        this._new = value;
    }

    /**
     * Gets the value of the validate property.
     * 
     * @return
     *     possible object is
     *     {@link ValidateFacet }
     *     
     */
    public ValidateFacet getValidate() {
        return validate;
    }

    /**
     * Sets the value of the validate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidateFacet }
     *     
     */
    public void setValidate(ValidateFacet value) {
        this.validate = value;
    }

    /**
     * Gets the value of the valueSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueSet() {
        return valueSet;
    }

    /**
     * Sets the value of the valueSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueSet(String value) {
        this.valueSet = value;
    }

    /**
     * Gets the value of the originalText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * Sets the value of the originalText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalText(String value) {
        this.originalText = value;
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
     * Gets the value of the datatype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * Sets the value of the datatype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatatype(String value) {
        this.datatype = value;
    }

}
