
package org.tolven.ws.trim.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "addTrimAsPayload", namespace = "http://tolven.org/trim")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addTrimAsPayload", namespace = "http://tolven.org/trim", propOrder = {
    "arg0",
    "arg1"
})
public class AddTrimAsPayload {

    @XmlElement(name = "arg0", namespace = "")
    private org.tolven.trim.Trim arg0;
    @XmlElement(name = "arg1", namespace = "")
    private org.tolven.doc.bean.TolvenMessage arg1;

    /**
     * 
     * @return
     *     returns Trim
     */
    public org.tolven.trim.Trim getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(org.tolven.trim.Trim arg0) {
        this.arg0 = arg0;
    }

    /**
     * 
     * @return
     *     returns TolvenMessage
     */
    public org.tolven.doc.bean.TolvenMessage getArg1() {
        return this.arg1;
    }

    /**
     * 
     * @param arg1
     *     the value for the arg1 property
     */
    public void setArg1(org.tolven.doc.bean.TolvenMessage arg1) {
        this.arg1 = arg1;
    }

}
