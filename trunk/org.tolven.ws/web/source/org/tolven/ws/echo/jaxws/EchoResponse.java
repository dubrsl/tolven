
package org.tolven.ws.echo.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "echoResponse", namespace = "http://tolven.org/echo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "echoResponse", namespace = "http://tolven.org/echo")
public class EchoResponse {

    @XmlElement(name = "return", namespace = "http://tolven.org/echo")
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
