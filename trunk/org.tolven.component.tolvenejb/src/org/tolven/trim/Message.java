
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Message complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Message">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sender" type="{urn:tolven-org:trim:4.0}Party"/>
 *         &lt;element name="receiver" type="{urn:tolven-org:trim:4.0}Party"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message", propOrder = {
    "sender",
    "receiver"
})
public class Message
    implements Serializable
{

    @XmlElement(required = true)
    protected Party sender;
    @XmlElement(required = true)
    protected Party receiver;

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setSender(Party value) {
        this.sender = value;
    }

    /**
     * Gets the value of the receiver property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setReceiver(Party value) {
        this.receiver = value;
    }

}
