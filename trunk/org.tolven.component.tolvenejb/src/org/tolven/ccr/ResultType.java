//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.04.15 at 03:24:02 PM PDT 
//


package org.tolven.ccr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:astm-org:CCR}CCRCodedDataObjectType">
 *       &lt;sequence>
 *         &lt;element name="Procedure" type="{urn:astm-org:CCR}ProcedureType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Substance" type="{urn:astm-org:CCR}CodedDescriptionType" minOccurs="0"/>
 *         &lt;element name="Test" type="{urn:astm-org:CCR}TestType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultType", propOrder = {
    "procedure",
    "substance",
    "test"
})
public class ResultType
    extends CCRCodedDataObjectType
{

    @XmlElement(name = "Procedure")
    protected List<ProcedureType> procedure;
    @XmlElement(name = "Substance")
    protected CodedDescriptionType substance;
    @XmlElement(name = "Test")
    protected List<TestType> test;

    /**
     * Gets the value of the procedure property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the procedure property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcedure().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcedureType }
     * 
     * 
     */
    public List<ProcedureType> getProcedure() {
        if (procedure == null) {
            procedure = new ArrayList<ProcedureType>();
        }
        return this.procedure;
    }

    /**
     * Gets the value of the substance property.
     * 
     * @return
     *     possible object is
     *     {@link CodedDescriptionType }
     *     
     */
    public CodedDescriptionType getSubstance() {
        return substance;
    }

    /**
     * Sets the value of the substance property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodedDescriptionType }
     *     
     */
    public void setSubstance(CodedDescriptionType value) {
        this.substance = value;
    }

    /**
     * Gets the value of the test property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the test property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TestType }
     * 
     * 
     */
    public List<TestType> getTest() {
        if (test == null) {
            test = new ArrayList<TestType>();
        }
        return this.test;
    }

}
