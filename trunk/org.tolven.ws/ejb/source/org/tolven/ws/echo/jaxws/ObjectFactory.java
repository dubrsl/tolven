
package org.tolven.ws.echo.jaxws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tolven.ws.echo.jaxws package. 
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

    private final static QName _Echo_QNAME = new QName("http://tolven.org/echo", "echo");
    private final static QName _EchoResponse_QNAME = new QName("http://tolven.org/echo", "echoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tolven.ws.echo.jaxws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EchoRequest }
     * 
     */
    public EchoRequest createEchoRequest() {
        return new EchoRequest();
    }

    /**
     * Create an instance of {@link EchoResponse }
     * 
     */
    public EchoResponse createEchoResponse() {
        return new EchoResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchoRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/echo", name = "echo")
    public JAXBElement<EchoRequest> createEcho(EchoRequest value) {
        return new JAXBElement<EchoRequest>(_Echo_QNAME, EchoRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EchoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/echo", name = "echoResponse")
    public JAXBElement<EchoResponse> createEchoResponse(EchoResponse value) {
        return new JAXBElement<EchoResponse>(_EchoResponse_QNAME, EchoResponse.class, null, value);
    }

}
