
package org.tolven.ws.trim.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "createTolvenMessageResponse", namespace = "http://tolven.org/trim")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createTolvenMessageResponse", namespace = "http://tolven.org/trim")
public class CreateTolvenMessageResponse {

    @XmlElement(name = "return", namespace = "")
    private org.tolven.doc.bean.TolvenMessageWithAttachments _return;

    /**
     * 
     * @return
     *     returns TolvenMessageWithAttachments
     */
    public org.tolven.doc.bean.TolvenMessageWithAttachments getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(org.tolven.doc.bean.TolvenMessageWithAttachments _return) {
        this._return = _return;
    }

}
