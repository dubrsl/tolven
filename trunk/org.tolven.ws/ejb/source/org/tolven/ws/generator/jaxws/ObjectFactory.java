
package org.tolven.ws.generator.jaxws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tolven.ws.generator.jaxws package. 
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

    private final static QName _GenerateCCRXMLResponse_QNAME = new QName("http://tolven.org/generator", "generateCCRXMLResponse");
    private final static QName _GenerateCCRXML_QNAME = new QName("http://tolven.org/generator", "generateCCRXML");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tolven.ws.generator.jaxws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GenerateCCRXMLRequest }
     * 
     */
    public GenerateCCRXMLRequest createGenerateCCRXMLRequest() {
        return new GenerateCCRXMLRequest();
    }

    /**
     * Create an instance of {@link GenerateCCRXMLResponse }
     * 
     */
    public GenerateCCRXMLResponse createGenerateCCRXMLResponse() {
        return new GenerateCCRXMLResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateCCRXMLResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/generator", name = "generateCCRXMLResponse")
    public JAXBElement<GenerateCCRXMLResponse> createGenerateCCRXMLResponse(GenerateCCRXMLResponse value) {
        return new JAXBElement<GenerateCCRXMLResponse>(_GenerateCCRXMLResponse_QNAME, GenerateCCRXMLResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateCCRXMLRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/generator", name = "generateCCRXML")
    public JAXBElement<GenerateCCRXMLRequest> createGenerateCCRXML(GenerateCCRXMLRequest value) {
        return new JAXBElement<GenerateCCRXMLRequest>(_GenerateCCRXML_QNAME, GenerateCCRXMLRequest.class, null, value);
    }

}
