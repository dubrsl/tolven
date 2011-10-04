//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.04 at 05:06:14 PM PDT 
//


package org.tolven.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JoinAccountInvitation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JoinAccountInvitation">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:admin:1.0}InvitationDetail">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="accountId" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="accountPermission" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="tolvenUserId" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JoinAccountInvitation")
public class JoinAccountInvitation
    extends InvitationDetail
{

    @XmlAttribute
    protected Long accountId;
    @XmlAttribute
    protected Boolean accountPermission;
    @XmlAttribute
    protected Long tolvenUserId;

    /**
     * Gets the value of the accountId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAccountId(Long value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the accountPermission property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAccountPermission() {
        return accountPermission;
    }

    /**
     * Sets the value of the accountPermission property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAccountPermission(Boolean value) {
        this.accountPermission = value;
    }

    /**
     * Gets the value of the tolvenUserId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTolvenUserId() {
        return tolvenUserId;
    }

    /**
     * Sets the value of the tolvenUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTolvenUserId(Long value) {
        this.tolvenUserId = value;
    }

}