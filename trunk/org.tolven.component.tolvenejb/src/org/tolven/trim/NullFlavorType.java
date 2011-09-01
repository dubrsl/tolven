
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NullFlavorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NullFlavorType">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="OTH"/>
 *     &lt;enumeration value="NINF"/>
 *     &lt;enumeration value="PINF"/>
 *     &lt;enumeration value="ASKU"/>
 *     &lt;enumeration value="NAV"/>
 *     &lt;enumeration value="UNK"/>
 *     &lt;enumeration value="QS"/>
 *     &lt;enumeration value="NASK"/>
 *     &lt;enumeration value="TRC"/>
 *     &lt;enumeration value="NI"/>
 *     &lt;enumeration value="MSK"/>
 *     &lt;enumeration value="NA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NullFlavorType")
@XmlEnum
public enum NullFlavorType {

    OTH,
    NINF,
    PINF,
    ASKU,
    NAV,
    UNK,
    QS,
    NASK,
    TRC,
    NI,
    MSK,
    NA;

    public String value() {
        return name();
    }

    public static NullFlavorType fromValue(String v) {
        return valueOf(v);
    }

}
