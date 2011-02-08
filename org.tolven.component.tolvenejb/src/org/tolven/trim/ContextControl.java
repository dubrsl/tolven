
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContextControl.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ContextControl">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="AN"/>
 *     &lt;enumeration value="AP"/>
 *     &lt;enumeration value="ON"/>
 *     &lt;enumeration value="OP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ContextControl")
@XmlEnum
public enum ContextControl {

    AN,
    AP,
    ON,
    OP;

    public String value() {
        return name();
    }

    public static ContextControl fromValue(String v) {
        return valueOf(v);
    }

}
