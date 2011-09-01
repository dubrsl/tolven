
package org.tolven.ws.trim.jaxws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tolven.ws.trim.jaxws package. 
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

    private final static QName _WebServiceTrim_QNAME = new QName("http://tolven.org/trim", "webServiceTrim");
    private final static QName _SubmitTrim_QNAME = new QName("http://tolven.org/trim", "submitTrim");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tolven.ws.trim.jaxws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WebServiceTrim }
     * 
     */
    public WebServiceTrim createWebServiceTrim() {
        return new WebServiceTrim();
    }

    /**
     * Create an instance of {@link SubmitTrimRequest }
     * 
     */
    public SubmitTrimRequest createSubmitTrimRequest() {
        return new SubmitTrimRequest();
    }

    /**
     * Create an instance of {@link WebServiceField }
     * 
     */
    public WebServiceField createWebServiceField() {
        return new WebServiceField();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WebServiceTrim }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/trim", name = "webServiceTrim")
    public JAXBElement<WebServiceTrim> createWebServiceTrim(WebServiceTrim value) {
        return new JAXBElement<WebServiceTrim>(_WebServiceTrim_QNAME, WebServiceTrim.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitTrimRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/trim", name = "submitTrim")
    public JAXBElement<SubmitTrimRequest> createSubmitTrim(SubmitTrimRequest value) {
        return new JAXBElement<SubmitTrimRequest>(_SubmitTrim_QNAME, SubmitTrimRequest.class, null, value);
    }

}
