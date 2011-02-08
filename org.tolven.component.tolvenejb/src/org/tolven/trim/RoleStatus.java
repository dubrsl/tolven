
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoleStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RoleStatus">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="nullified"/>
 *     &lt;enumeration value="normal"/>
 *     &lt;enumeration value="active"/>
 *     &lt;enumeration value="cancelled"/>
 *     &lt;enumeration value="pending"/>
 *     &lt;enumeration value="suspended"/>
 *     &lt;enumeration value="terminated"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RoleStatus")
@XmlEnum
public enum RoleStatus {

    @XmlEnumValue("nullified")
    NULLIFIED("nullified"),
    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("active")
    ACTIVE("active"),
    @XmlEnumValue("cancelled")
    CANCELLED("cancelled"),
    @XmlEnumValue("pending")
    PENDING("pending"),
    @XmlEnumValue("suspended")
    SUSPENDED("suspended"),
    @XmlEnumValue("terminated")
    TERMINATED("terminated");
    private final String value;

    RoleStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RoleStatus fromValue(String v) {
        for (RoleStatus c: RoleStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
