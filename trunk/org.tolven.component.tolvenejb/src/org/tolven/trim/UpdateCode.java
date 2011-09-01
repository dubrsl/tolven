
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UpdateCode">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="REF"/>
 *     &lt;enumeration value="OVL"/>
 *     &lt;enumeration value="UPD"/>
 *     &lt;enumeration value="CREF"/>
 *     &lt;enumeration value="COVL"/>
 *     &lt;enumeration value="CUPD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UpdateCode")
@XmlEnum
public enum UpdateCode {

    REF,
    OVL,
    UPD,
    CREF,
    COVL,
    CUPD;

    public String value() {
        return name();
    }

    public static UpdateCode fromValue(String v) {
        return valueOf(v);
    }

}
