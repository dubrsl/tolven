//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.04 at 05:06:14 PM PDT 
//


package org.tolven.admin;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tolven.admin package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Details_QNAME = new QName("urn:tolven-org:admin:1.0", "details");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tolven.admin
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JoinNewAccountInvitation }
     * 
     */
    public JoinNewAccountInvitation createJoinNewAccountInvitation() {
        return new JoinNewAccountInvitation();
    }

    /**
     * Create an instance of {@link JoinAccountInvitation }
     * 
     */
    public JoinAccountInvitation createJoinAccountInvitation() {
        return new JoinAccountInvitation();
    }

    /**
     * Create an instance of {@link Details }
     * 
     */
    public Details createDetails() {
        return new Details();
    }

    /**
     * Create an instance of {@link ActivateInvitation }
     * 
     */
    public ActivateInvitation createActivateInvitation() {
        return new ActivateInvitation();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Details }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:tolven-org:admin:1.0", name = "details")
    public JAXBElement<Details> createDetails(Details value) {
        return new JAXBElement<Details>(_Details_QNAME, Details.class, null, value);
    }

}