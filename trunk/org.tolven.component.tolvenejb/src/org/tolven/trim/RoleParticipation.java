
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoleParticipation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RoleParticipation">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Participation">
 *       &lt;sequence>
 *         &lt;element name="act" type="{urn:tolven-org:trim:4.0}Act" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoleParticipation", propOrder = {
    "act"
})
public class RoleParticipation
    extends Participation
    implements Serializable
{

    protected Act act;

    /**
     * Gets the value of the act property.
     * 
     * @return
     *     possible object is
     *     {@link Act }
     *     
     */
    public Act getAct() {
        return act;
    }

    /**
     * Sets the value of the act property.
     * 
     * @param value
     *     allowed object is
     *     {@link Act }
     *     
     */
    public void setAct(Act value) {
        this.act = value;
    }

}
