
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetOperator.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SetOperator">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="H"/>
 *     &lt;enumeration value="E"/>
 *     &lt;enumeration value="I"/>
 *     &lt;enumeration value="A"/>
 *     &lt;enumeration value="P"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SetOperator")
@XmlEnum
public enum SetOperator {

    H,
    E,
    I,
    A,
    P;

    public String value() {
        return name();
    }

    public static SetOperator fromValue(String v) {
        return valueOf(v);
    }

}
