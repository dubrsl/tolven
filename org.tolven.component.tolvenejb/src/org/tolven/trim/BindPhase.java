
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BindPhase.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BindPhase">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="parse"/>
 *     &lt;enumeration value="create"/>
 *     &lt;enumeration value="request"/>
 *     &lt;enumeration value="receive"/>
 *     &lt;enumeration value="send"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BindPhase")
@XmlEnum
public enum BindPhase {

    @XmlEnumValue("parse")
    PARSE("parse"),
    @XmlEnumValue("create")
    CREATE("create"),
    @XmlEnumValue("request")
    REQUEST("request"),
    @XmlEnumValue("receive")
    RECEIVE("receive"),
    @XmlEnumValue("send")
    SEND("send");
    private final String value;

    BindPhase(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BindPhase fromValue(String v) {
        for (BindPhase c: BindPhase.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
