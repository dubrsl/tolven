
package org.tolven.ws.echo.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "echo", namespace = "http://tolven.org/echo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "echo", namespace = "http://tolven.org/echo")
public class EchoRequest {

    @XmlElement(name = "string", namespace = "http://tolven.org/echo")
    private String string;

    /**
     * 
     * @return
     *     returns String
     */
    public String getString() {
        return this.string;
    }

    /**
     * 
     * @param string
     *     the value for the string property
     */
    public void setString(String string) {
        this.string = string;
    }

}
