
package org.tolven.api.accountuser;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tolven.api.accountuser package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tolven.api.accountuser
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XProperty }
     * 
     */
    public XProperty createXProperty() {
        return new XProperty();
    }

    /**
     * Create an instance of {@link XAttribute }
     * 
     */
    public XAttribute createXAttribute() {
        return new XAttribute();
    }

    /**
     * Create an instance of {@link XTolvenUser }
     * 
     */
    public XTolvenUser createXTolvenUser() {
        return new XTolvenUser();
    }

    /**
     * Create an instance of {@link XAccount }
     * 
     */
    public XAccount createXAccount() {
        return new XAccount();
    }

    /**
     * Create an instance of {@link XTolvenPerson }
     * 
     */
    public XTolvenPerson createXTolvenPerson() {
        return new XTolvenPerson();
    }

    /**
     * Create an instance of {@link XRole }
     * 
     */
    public XRole createXRole() {
        return new XRole();
    }

    /**
     * Create an instance of {@link XAccountType }
     * 
     */
    public XAccountType createXAccountType() {
        return new XAccountType();
    }

    /**
     * Create an instance of {@link XAccountUser }
     * 
     */
    public XAccountUser createXAccountUser() {
        return new XAccountUser();
    }

}
