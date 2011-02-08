
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RTO">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}DataType">
 *       &lt;sequence>
 *         &lt;element name="numerator" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="denominator" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RTO", propOrder = {
    "numerator",
    "denominator"
})
public class RTO
    extends DataType
    implements Serializable
{

    protected double numerator;
    protected double denominator;

    /**
     * Gets the value of the numerator property.
     * 
     */
    public double getNumerator() {
        return numerator;
    }

    /**
     * Sets the value of the numerator property.
     * 
     */
    public void setNumerator(double value) {
        this.numerator = value;
    }

    /**
     * Gets the value of the denominator property.
     * 
     */
    public double getDenominator() {
        return denominator;
    }

    /**
     * Sets the value of the denominator property.
     * 
     */
    public void setDenominator(double value) {
        this.denominator = value;
    }

}
