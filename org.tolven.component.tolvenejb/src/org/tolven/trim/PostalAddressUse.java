
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PostalAddressUse.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PostalAddressUse">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="ABC"/>
 *     &lt;enumeration value="IDE"/>
 *     &lt;enumeration value="SYL"/>
 *     &lt;enumeration value="BAD"/>
 *     &lt;enumeration value="TMP"/>
 *     &lt;enumeration value="PHYS"/>
 *     &lt;enumeration value="PST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PostalAddressUse")
@XmlEnum
public enum PostalAddressUse {

    ABC,
    IDE,
    SYL,
    BAD,
    TMP,
    PHYS,
    PST;

    public String value() {
        return name();
    }

    public static PostalAddressUse fromValue(String v) {
        return valueOf(v);
    }

}
