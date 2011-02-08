
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 * 				A value set defines a formal list of choices for a value. 
 * 				While the allowed datatypes are constrained at the schema level by the 
 * 				specific type of slot, a value set provides a more specific, and often 
 * 				larger set of choices specific to a TRIM. For example, the observation
 * 				trim might suggest a physical quantity (PQ) while a Weight assessment
 * 				might list two specific PQs, one with a unit of kilograms (kg), another with a unit of
 * 				pounds (lb).
 * 				A common use for value sets is for the selection of coded data types. In these
 * 				cases, the choices do not require further input: selecting among the choices 
 * 				completes the slot. Of course in the case of physical quantities, the choice
 * 				still requires input, the value itself. 
 * 				Mixing datatypes is also possible in a valueSet. For example, the value set for 
 * 				the value of a weight assessment might contain an exceptional value (null flavor)
 * 				that indicates that the value could not be obtained as well as choices 
 * 				for kg and lb PQ datatypes.
 * 				A value set can also reference other value sets. The end user will typically see the
 * 				fully resolved value set.
 * 			
 * 
 * <p>Java class for ValueSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValueSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="bind" type="{urn:tolven-org:trim:4.0}BindTo"/>
 *           &lt;element name="AD" type="{urn:tolven-org:trim:4.0}AD"/>
 *           &lt;element name="CD" type="{urn:tolven-org:trim:4.0}CD"/>
 *           &lt;element name="CE" type="{urn:tolven-org:trim:4.0}CE"/>
 *           &lt;element name="CS" type="{urn:tolven-org:trim:4.0}CS"/>
 *           &lt;element name="CV" type="{urn:tolven-org:trim:4.0}CV"/>
 *           &lt;element name="ED" type="{urn:tolven-org:trim:4.0}ED"/>
 *           &lt;element name="EN" type="{urn:tolven-org:trim:4.0}EN"/>
 *           &lt;element name="II" type="{urn:tolven-org:trim:4.0}II"/>
 *           &lt;element name="INT" type="{urn:tolven-org:trim:4.0}INT"/>
 *           &lt;element name="IVLPQ" type="{urn:tolven-org:trim:4.0}IVL_PQ"/>
 *           &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor"/>
 *           &lt;element name="PQ" type="{urn:tolven-org:trim:4.0}PQ"/>
 *           &lt;element name="RTO" type="{urn:tolven-org:trim:4.0}RTO"/>
 *           &lt;element name="ST" type="{urn:tolven-org:trim:4.0}ST"/>
 *           &lt;element name="TS" type="{urn:tolven-org:trim:4.0}TS"/>
 *           &lt;element name="URG_TS" type="{urn:tolven-org:trim:4.0}URG_TS"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}Name" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueSet", propOrder = {
    "bindsAndADSAndCDS"
})
public class ValueSet
    implements Serializable
{

    @XmlElements({
        @XmlElement(name = "TS", type = TS.class),
        @XmlElement(name = "ED", type = ED.class),
        @XmlElement(name = "CS", type = CS.class),
        @XmlElement(name = "bind", type = BindTo.class),
        @XmlElement(name = "II", type = II.class),
        @XmlElement(name = "null", type = NullFlavor.class),
        @XmlElement(name = "CE", type = CE.class),
        @XmlElement(name = "INT", type = INT.class),
        @XmlElement(name = "IVLPQ", type = IVLPQ.class),
        @XmlElement(name = "PQ", type = PQ.class),
        @XmlElement(name = "RTO", type = RTO.class),
        @XmlElement(name = "CV", type = CV.class),
        @XmlElement(name = "URG_TS", type = URGTS.class),
        @XmlElement(name = "ST", type = ST.class),
        @XmlElement(name = "EN", type = EN.class),
        @XmlElement(name = "AD", type = AD.class),
        @XmlElement(name = "CD", type = CD.class)
    })
    protected List<Object> bindsAndADSAndCDS;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "Name")
    protected String name;

    /**
     * Gets the value of the bindsAndADSAndCDS property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bindsAndADSAndCDS property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBindsAndADSAndCDS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TS }
     * {@link ED }
     * {@link CS }
     * {@link BindTo }
     * {@link II }
     * {@link NullFlavor }
     * {@link CE }
     * {@link INT }
     * {@link IVLPQ }
     * {@link PQ }
     * {@link RTO }
     * {@link CV }
     * {@link URGTS }
     * {@link ST }
     * {@link EN }
     * {@link AD }
     * {@link CD }
     * 
     * 
     */
    public List<Object> getBindsAndADSAndCDS() {
        if (bindsAndADSAndCDS == null) {
            bindsAndADSAndCDS = new ArrayList<Object>();
        }
        return this.bindsAndADSAndCDS;
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

}
