
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="label" type="{urn:tolven-org:trim:4.0}LabelFacet" minOccurs="0"/>
 *         &lt;element name="originalText" type="{urn:tolven-org:trim:4.0}st" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataType", propOrder = {
    "label",
    "originalText"
})
@XmlSeeAlso({
    ED.class,
    IVLTS.class,
    EN.class,
    IVLPQ.class,
    URL.class,
    NullFlavor.class,
    IVLREAL.class,
    URG.class,
    II.class,
    IVLINT.class,
    AD.class,
    RTO.class,
    QTY.class,
    BL.class,
    CS.class,
    CR.class
})
public class DataType implements Serializable
{

    protected LabelFacet label;
    protected String originalText;

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

}
