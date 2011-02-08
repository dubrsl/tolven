
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActMood.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActMood">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="APT"/>
 *     &lt;enumeration value="ARQ"/>
 *     &lt;enumeration value="DEF"/>
 *     &lt;enumeration value="EVN"/>
 *     &lt;enumeration value="EVN.CRT"/>
 *     &lt;enumeration value="EXPEC"/>
 *     &lt;enumeration value="GOL"/>
 *     &lt;enumeration value="INT"/>
 *     &lt;enumeration value="OPT"/>
 *     &lt;enumeration value="PERM"/>
 *     &lt;enumeration value="PERMRQ"/>
 *     &lt;enumeration value="PRMS"/>
 *     &lt;enumeration value="PRP"/>
 *     &lt;enumeration value="RMD"/>
 *     &lt;enumeration value="RQO"/>
 *     &lt;enumeration value="RSK"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActMood")
@XmlEnum
public enum ActMood {

    APT("APT"),
    ARQ("ARQ"),
    DEF("DEF"),
    EVN("EVN"),
    @XmlEnumValue("EVN.CRT")
    EVN_CRT("EVN.CRT"),
    EXPEC("EXPEC"),
    GOL("GOL"),
    INT("INT"),
    OPT("OPT"),
    PERM("PERM"),
    PERMRQ("PERMRQ"),
    PRMS("PRMS"),
    PRP("PRP"),
    RMD("RMD"),
    RQO("RQO"),
    RSK("RSK");
    private final String value;

    ActMood(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActMood fromValue(String v) {
        for (ActMood c: ActMood.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
