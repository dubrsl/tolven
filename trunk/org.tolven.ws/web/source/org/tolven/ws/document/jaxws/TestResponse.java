
package org.tolven.ws.document.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "testResponse", namespace = "http://tolven.org/document")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "testResponse", namespace = "http://tolven.org/document")
public class TestResponse {

    @XmlElement(name = "return", namespace = "http://tolven.org/document")
    private String _return;

    /**
     * 
     * @return
     *     returns String
     */
    public String getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(String _return) {
        this._return = _return;
    }

}
