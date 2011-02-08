
package org.tolven.ws.document.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "processDocument", namespace = "http://tolven.org/document")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processDocument", namespace = "http://tolven.org/document", propOrder = {
    "payload",
    "xmlns",
    "accountId"
})
public class ProcessDocumentRequest {

    @XmlElement(name = "payload", namespace = "http://tolven.org/document", nillable = true)
    private byte[] payload;
    @XmlElement(name = "xmlns", namespace = "http://tolven.org/document")
    private String xmlns;
    @XmlElement(name = "accountId", namespace = "http://tolven.org/document")
    private long accountId;

    /**
     * 
     * @return
     *     returns byte[]
     */
    public byte[] getPayload() {
        return this.payload;
    }

    /**
     * 
     * @param payload
     *     the value for the payload property
     */
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getXmlns() {
        return this.xmlns;
    }

    /**
     * 
     * @param xmlns
     *     the value for the xmlns property
     */
    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    /**
     * 
     * @return
     *     returns long
     */
    public long getAccountId() {
        return this.accountId;
    }

    /**
     * 
     * @param accountId
     *     the value for the accountId property
     */
    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

}
