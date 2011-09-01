
package org.tolven.ws.trim.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "submitTrim", namespace = "http://tolven.org/trim")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "submitTrim", namespace = "http://tolven.org/trim")
public class SubmitTrimRequest {

    @XmlElement(name = "webServiceTrim", namespace = "http://tolven.org/trim")
    private org.tolven.ws.trim.WebServiceTrim webServiceTrim;

    /**
     * 
     * @return
     *     returns WebServiceTrim
     */
    public org.tolven.ws.trim.WebServiceTrim getWebServiceTrim() {
        return this.webServiceTrim;
    }

    /**
     * 
     * @param webServiceTrim
     *     the value for the webServiceTrim property
     */
    public void setWebServiceTrim(org.tolven.ws.trim.WebServiceTrim webServiceTrim) {
        this.webServiceTrim = webServiceTrim;
    }

}
