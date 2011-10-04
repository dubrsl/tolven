/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.naming;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;

/**
 * This object is little more than an LDAP inetOrgPerson subset arranged for convenient bean access. It facilitates 
 * the integration between LDAP, Tolven TolvenUser and HL7 Person object (when associated with a Tolven TolvenUser object).
 * It is a rare example of a Value Object being used by Tolven. 
 * 
 * @author John Churin
 */
public class TolvenPerson implements Serializable {

    private Attribute objectClassAttribute;
    private String dn; // Distinguishing Name
    private String countryName;
    private String jpegPhoto; // Photo
    private String referenceCode; // When needed
    private String emailFormat = "html";
    private Map<String, Attribute> attributes;
    private Set<String> immutableAttributeIds;

    /**
     * Creates a new instance of TolvenPerson
     */
    public TolvenPerson() {
    }

    /**
     * Construct a TolvenPerson bean from an LDAP SearchResult.
     */
    public TolvenPerson(SearchResult rslt) {
        this(rslt.getNameInNamespace(), rslt.getAttributes());
    }

    /**
     * Construct a TolvenPerson bean from a DN and Attributes. Note: We don't copy password from LDAP to TolvenPerson.
     */
    public TolvenPerson(String dn, Attributes attrs) {
        // This is the primary key (called distinguishing name)
        setDn(dn);
        NamingEnumeration<String> namingEnum = attrs.getIDs();
        while (namingEnum.hasMoreElements()) {
            String attrID = null;
            try {
                attrID = namingEnum.next();
            } catch (NamingException ex) {
                throw new RuntimeException("Could not obtain next enumeration", ex);
            }
            // Deal with RFC4523
            if ("userCertificate;binary".equalsIgnoreCase(attrID)) {
                Object obj = null;
                try {
                    obj = attrs.get(attrID).get();
                } catch (NamingException ex) {
                    throw new RuntimeException("Could not get attribute userCertificate;binary", ex);
                }
                Attribute attr = new BasicAttribute("userCertificate");
                attr.add(obj);
                getAttributes().put("userCertificate", attr);
            } else {
                getAttributes().put(attrID, attrs.get(attrID));
            }
        }
    }

    /**
     * Create a directory attribute structure from this bean's properties
     * If changesOnly is true, then only those attributes that can change are included.
     * @throws NoSuchAlgorithmException 
     */
    public Attributes dirAttributes(boolean changesOnly) {
        Attributes attrs = new BasicAttributes(true); // case-ignore
        if (!changesOnly) {
            attrs.put(getObjectClassAttribute());
            for (String attrID : getAttributes().keySet()) {
                if (getImmutableAttributeIds().contains(attrID)) {
                    Attribute attribute = getAttributes().get(attrID);
                    if (attribute != null) {
                        attrs.put(getAttributes().get(attrID));
                    }
                }
            }
        }
        for (String attrID : getAttributes().keySet()) {
            if (!getImmutableAttributeIds().contains(attrID)) {
                Attribute attribute = getAttributes().get(attrID);
                if (attribute != null) {
                    if ("userCertificate".equalsIgnoreCase(attrID)) {
                        Object obj = null;
                        try {
                            obj = attribute.get();
                        } catch (NamingException ex) {
                            throw new RuntimeException("Could not get attribute userCertificate", ex);
                        }
                        Attribute attr = new BasicAttribute("userCertificate;binary");
                        attr.add(obj);
                        attrs.put(attr);
                    } else {
                        attrs.put(attribute);
                    }
                }
            }
        }
        /*
         * This version of TolvenPerson no longer sends the password to LDAP using the attribute contained here.
         * The password, along with the DN, should be explicit parameters
         * So, the following section is commented out to prevent confusion
         */
        /*
        // Send Hashed Password instead of the plain text version
        if (getUserPassword() != null) {
            Attribute userPassword = new BasicAttribute("userPassword");
            try {
                userPassword.add(SSHA.encodePassword(getUserPassword().toCharArray()));
            } catch (Exception ex) {
                throw new RuntimeException("Could not encode user password", ex);
            }
            attrs.put(userPassword);
        }
        */
        return attrs;
    }
    
    public Attribute getObjectClassAttribute() {
        if (objectClassAttribute == null) {
            objectClassAttribute = new BasicAttribute("objectClass");
            objectClassAttribute.add("inetOrgPerson");
            objectClassAttribute.add("organizationalPerson");
            objectClassAttribute.add("person");
            objectClassAttribute.add("top");
        }
        return objectClassAttribute;
    }

    public void setObjectClassAttribute(Attribute objectClassAttribute) {
        this.objectClassAttribute = objectClassAttribute;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getEmailFormat() {
        return emailFormat;
    }

    public void setEmailFormat(String emailFormat) {
        this.emailFormat = emailFormat;
    }

    public String getUid() {
        return (String) getAttributeValue("uid");
    }

    public void setUid(String uid) {
        setAttributeValue("uid", uid);
    }

    public String getSn() {
        return (String) getAttributeValue("sn");
    }

    public void setSn(String sn) {
        setAttributeValue("sn", sn);
    }

    public String getCn() {
        return (String) getAttributeValue("cn");
    }

    public void setCn(String cn) {
        setAttributeValue("cn", cn);
    }

    public String getGivenName() {
        return (String) getAttributeValue("givenName");
    }

    public void setGivenName(String givenName) {
        setAttributeValue("givenName", givenName);
    }

    public List<String> getMail() {
        // Mail is an ordered collection of email addresses
        Attribute mail = getAttribute("mail");
        List<String> mailList = new ArrayList<String>();
        if (mail != null) {
            try {
                NamingEnumeration<?> e = mail.getAll();
                //                setMail(new ArrayList<String>(2));
                while (e.hasMore()) {
                    mailList.add(e.next().toString());
                }
            } catch (NamingException ex) {
                throw new RuntimeException("Could not get mail from attribute", ex);
            }
        }
        return mailList;
    }

    public String getPrimaryMail() {
        Object mail = getAttributeValue("mail");
        if (mail == null) {
            return null;
        } else {
            return mail.toString();
        }
        //        Attribute attribute = getAttribute("mail");
        //        // If no email addresses, return null
        //        if (attribute == null || attribute.size()==0) {
        //          return null;
        //        }
        //        return attribute.get(0).toString();
    }

    /**
     * Set the value of the first email address without touching others, if present.
     * @param mail
     */
    public void setPrimaryMail(String mail) {
        setAttributeValue("mail", mail);
        //        Attribute attribute = getAttribute("mail");
        //        // Make sure there is a list of email addresses
        //        if (attribute == null) {
        //            attribute = new BasicAttribute("mail");
        //            getAttributes().put("mail", attribute);
        //        }
        //        if (attribute.size()==0) {
        //            attribute.add(mail);
        //        } else {
        //            attribute.set(0,mail);
        //        }
    }

    public void setMail(List<String> mailList) {
        Attribute attribute = getAttribute("mail");
        if (attribute == null) {
            attribute = new BasicAttribute("mail");
            getAttributes().put("mail", attribute);
        } else {
            attribute.clear();
        }
        for (String mail : mailList) {
            attribute.add(mail);
        }
    }

    /**
     * Add an eMail item to this context.
     */
    public void addMail(String item) {
        Attribute attribute = getAttribute("mail");
        if (attribute == null) {
            attribute = new BasicAttribute("mail");
            getAttributes().put("mail", attribute);
        }
        if (!attribute.contains(item)) {
            attribute.add(item);
        }
    }

    public String getJpegPhoto() {
        return jpegPhoto;
    }

    public void setJpegPhoto(String jpegPhoto) {
        this.jpegPhoto = jpegPhoto;
    }

    public Map<String, Attribute> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Attribute>();
        }
        return attributes;
    }

    public void setAttributes(Map<String, Attribute> attributes) {
        this.attributes = attributes;
    }

    public Set<String> getImmutableAttributeIds() {
        if (immutableAttributeIds == null) {
            immutableAttributeIds = new HashSet<String>();
            immutableAttributeIds.add("uid");
            //            immutableAttributeIds.add("mail"); // [JMC] Mail is mutable at this level though the application might prevent it.
        }
        return immutableAttributeIds;
    }

    public void setImmutableAttributeIds(Set<String> immutableAttributeIds) {
        this.immutableAttributeIds = immutableAttributeIds;
    }

    public Attribute getAttribute(String attrID) {
        return getAttributes().get(attrID);
    }

    public Object getAttributeValue(String attrID) {
        Attribute attribute = getAttributes().get(attrID);
        if (attribute == null) {
            return null;
        } else {
            try {
                return attribute.get();
            } catch (NamingException ex) {
                throw new RuntimeException("Could not get attribute: " + attribute.getID(), ex);
            }
        }
    }

    public void setAttributeValue(String attrID, Object value) {
        if (value == null) {
            removeAttribute(attrID);
        } else {
            Attribute attribute = getAttributes().get(attrID);
            if (attribute == null) {
                attribute = new BasicAttribute(attrID);
                getAttributes().put(attrID, attribute);
            } else {
                attribute.clear();
            }
            attribute.add(value);
        }
    }

    public void putAttribute(Attribute attribute) {
        getAttributes().put(attribute.getID(), attribute);
    }

    public void removeAttribute(String attrID) {
        getAttributes().remove(attrID);
    }

    public void addImmutableAttributeId(String aString) {
        getImmutableAttributeIds().add(aString);
    }

    public void removeImmutableAttributeId(String aString) {
        getImmutableAttributeIds().remove(aString);
    }

    /**
     * Equals method compares distinguishing name
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof TolvenPerson))
            return false;
        if (getDn() == null)
            return false;
        return getDn().equals((TolvenPerson) obj);
    }

    /**
     * Debugging String
     */
    public String toString() {
        StringBuffer sb;

        sb = new StringBuffer(100);
        sb.append("[");
        sb.append(getDn());
        sb.append("]");
        sb.append(" uid: ");
        sb.append(getUid());
        sb.append(", cn: ");
        sb.append(getCn());
        sb.append(", sn: ");
        sb.append(getSn());
        sb.append(", givenName: ");
        sb.append(getGivenName());
        sb.append(", mail: ");
        for (String mail : getMail()) {
            sb.append(mail + ", ");
        }
        return sb.toString();
    }

    /**
     * HashCode is based on DistinguishingName
     */
    public int hashCode() {
        if (getDn() == null)
            return 0;
        return getDn().hashCode();
    }

    public String getUserPassword() {
        return (String) getAttributeValue("userPassword");
    }

    /**
     * Only set the userPassword as a SSHA, never the actual password. The attribute for userPassword
     * is never sent to LDAP, and is retrieved as a SSHA if the attribute is requested.
     * 
     * When stored in LDAP, the password is encoded using SSHA. In other words, the
     * plain-text password is never stored and therefore cannot be recovered from LDAP.
     * @param userPassword
     */
    public void setUserPassword(String userPassword) {
        setAttributeValue("userPassword", userPassword);
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    /**
     * Set the reference code, but make sure it's trimmed and lowercase and zero length changed to null
     * @param referenceCode
     */
    public void setReferenceCode(String referenceCode) {
        if (referenceCode == null)
            this.referenceCode = null;
        else {
            this.referenceCode = referenceCode.trim().toLowerCase();
            if (this.referenceCode.length() == 0)
                this.referenceCode = null;
        }
    }

    public String getOrganizationUnitName() {
        return (String) getAttributeValue("ou");
    }

    public void setOrganizationUnitName(String ou) {
        setAttributeValue("ou", ou);
    }

    public String getOrganizationName() {
        return (String) getAttributeValue("o");
    }

    public void setOrganizationName(String o) {
        setAttributeValue("o", o);
    }

    public String getStateOrProvince() {
        return (String) getAttributeValue("st");
    }

    public void setStateOrProvince(String st) {
        setAttributeValue("st", st);
    }

    public String getCountryName() {
        //TODO: Tolven LDAP schema needs to be changed to store countryName
        return countryName;
    }

    public void setCountryName(String countryName) {
        //TODO: Tolven LDAP schema needs to be changed to store countryName
        this.countryName = countryName;
    }

    public byte[] getUserCertificate() {
        //TODO Note that userCertificate does not currently work as like it should, when the LDAP ctx has the following entry
        //so it must be suffixed
        return (byte[]) getAttributeValue("userCertificate");
    }

    public void setUserCertificate(byte[] userCertificate) {
        setAttributeValue("userCertificate", userCertificate);
    }

    public byte[] getUserPKCS12() {
        return (byte[]) getAttributeValue("userPKCS12");
    }

    public void setUserPKCS12(byte[] userPKCS12) {
        setAttributeValue("userPKCS12", userPKCS12);
    }

}
