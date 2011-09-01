//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.07.22 at 03:20:48 PM PDT 
//


package org.tolven.config.model.credential.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CertificateInfoDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CertificateInfoDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="group" type="{urn:tolven-org:credentials:1.0}CertificateGroupDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="trustStore" type="{urn:tolven-org:credentials:1.0}TrustStoreDetail" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateInfoDetail", propOrder = {
    "group",
    "trustStore"
})
public class CertificateInfoDetail {

    protected List<CertificateGroupDetail> group;
    protected List<TrustStoreDetail> trustStore;

    /**
     * Gets the value of the group property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the group property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CertificateGroupDetail }
     * 
     * 
     */
    public List<CertificateGroupDetail> getGroup() {
        if (group == null) {
            group = new ArrayList<CertificateGroupDetail>();
        }
        return this.group;
    }

    /**
     * Gets the value of the trustStore property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trustStore property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrustStore().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrustStoreDetail }
     * 
     * 
     */
    public List<TrustStoreDetail> getTrustStore() {
        if (trustStore == null) {
            trustStore = new ArrayList<TrustStoreDetail>();
        }
        return this.trustStore;
    }

}
