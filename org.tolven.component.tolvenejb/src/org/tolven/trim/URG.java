
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for URG complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="URG">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}DataType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URG")
@XmlSeeAlso({
    URGREAL.class,
    URGPQ.class,
    URGINT.class,
    URGTS.class
})
public abstract class URG
    extends DataType
    implements Serializable
{


}
