
package org.tolven.ws.trim.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "submitMessage", namespace = "http://tolven.org/trim")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "submitMessage", namespace = "http://tolven.org/trim")
public class SubmitMessage {

    @XmlElement(name = "arg0", namespace = "")
    private org.tolven.doc.bean.TolvenMessageWithAttachments arg0;

    /**
     * 
     * @return
     *     returns TolvenMessageWithAttachments
     */
    public org.tolven.doc.bean.TolvenMessageWithAttachments getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(org.tolven.doc.bean.TolvenMessageWithAttachments arg0) {
        this.arg0 = arg0;
    }

}
