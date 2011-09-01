
package org.tolven.ws.generator.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "generateCCRXML", namespace = "http://tolven.org/generator")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "generateCCRXML", namespace = "http://tolven.org/generator")
public class GenerateCCRXMLRequest {

    @XmlElement(name = "startYear", namespace = "http://tolven.org/generator")
    private int startYear;

    /**
     * 
     * @return
     *     returns int
     */
    public int getStartYear() {
        return this.startYear;
    }

    /**
     * 
     * @param startYear
     *     the value for the startYear property
     */
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

}
