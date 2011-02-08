
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddressUse.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AddressUse">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="BAD"/>
 *     &lt;enumeration value="TMP"/>
 *     &lt;enumeration value="H"/>
 *     &lt;enumeration value="HP"/>
 *     &lt;enumeration value="HV"/>
 *     &lt;enumeration value="WP"/>
 *     &lt;enumeration value="DIR"/>
 *     &lt;enumeration value="PUB"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AddressUse")
@XmlEnum
public enum AddressUse {

    BAD,
    TMP,
    H,
    HP,
    HV,
    WP,
    DIR,
    PUB;

    public String value() {
        return name();
    }

    public static AddressUse fromValue(String v) {
        return valueOf(v);
    }

}
