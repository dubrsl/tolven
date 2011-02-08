
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntityStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EntityStatus">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="nullified"/>
 *     &lt;enumeration value="normal"/>
 *     &lt;enumeration value="active"/>
 *     &lt;enumeration value="terminated"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EntityStatus")
@XmlEnum
public enum EntityStatus {

    @XmlEnumValue("nullified")
    NULLIFIED("nullified"),
    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("active")
    ACTIVE("active"),
    @XmlEnumValue("terminated")
    TERMINATED("terminated");
    private final String value;

    EntityStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EntityStatus fromValue(String v) {
        for (EntityStatus c: EntityStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
