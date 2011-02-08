
package org.tolven.ws.document.jaxws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tolven.ws.document.jaxws package. 
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

    private final static QName _QueueMessage_QNAME = new QName("http://tolven.org/document", "queueMessage");
    private final static QName _TestResponse_QNAME = new QName("http://tolven.org/document", "testResponse");
    private final static QName _QueueMessageResponse_QNAME = new QName("http://tolven.org/document", "queueMessageResponse");
    private final static QName _Test_QNAME = new QName("http://tolven.org/document", "test");
    private final static QName _QueueMessageRequestPayload_QNAME = new QName("", "payload");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tolven.ws.document.jaxws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link QueueMessageResponse }
     * 
     */
    public QueueMessageResponse createQueueMessageResponse() {
        return new QueueMessageResponse();
    }

    /**
     * Create an instance of {@link QueueMessageRequest }
     * 
     */
    public QueueMessageRequest createQueueMessageRequest() {
        return new QueueMessageRequest();
    }

    /**
     * Create an instance of {@link TestRequest }
     * 
     */
    public TestRequest createTestRequest() {
        return new TestRequest();
    }

    /**
     * Create an instance of {@link TestResponse }
     * 
     */
    public TestResponse createTestResponse() {
        return new TestResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueueMessageRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/document", name = "queueMessage")
    public JAXBElement<QueueMessageRequest> createQueueMessage(QueueMessageRequest value) {
        return new JAXBElement<QueueMessageRequest>(_QueueMessage_QNAME, QueueMessageRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/document", name = "testResponse")
    public JAXBElement<TestResponse> createTestResponse(TestResponse value) {
        return new JAXBElement<TestResponse>(_TestResponse_QNAME, TestResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueueMessageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/document", name = "queueMessageResponse")
    public JAXBElement<QueueMessageResponse> createQueueMessageResponse(QueueMessageResponse value) {
        return new JAXBElement<QueueMessageResponse>(_QueueMessageResponse_QNAME, QueueMessageResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tolven.org/document", name = "test")
    public JAXBElement<TestRequest> createTest(TestRequest value) {
        return new JAXBElement<TestRequest>(_Test_QNAME, TestRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "payload", scope = QueueMessageRequest.class)
    public JAXBElement<byte[]> createQueueMessageRequestPayload(byte[] value) {
        return new JAXBElement<byte[]>(_QueueMessageRequestPayload_QNAME, byte[].class, QueueMessageRequest.class, ((byte[]) value));
    }

}
