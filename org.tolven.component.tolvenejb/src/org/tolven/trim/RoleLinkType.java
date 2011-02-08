
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoleLinkType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RoleLinkType">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="REL"/>
 *     &lt;enumeration value="DIRAUTH"/>
 *     &lt;enumeration value="INDAUTH"/>
 *     &lt;enumeration value="PART"/>
 *     &lt;enumeration value="BACKUP"/>
 *     &lt;enumeration value="REPL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RoleLinkType")
@XmlEnum
public enum RoleLinkType {

    REL,
    DIRAUTH,
    INDAUTH,
    PART,
    BACKUP,
    REPL;

    public String value() {
        return name();
    }

    public static RoleLinkType fromValue(String v) {
        return valueOf(v);
    }

}
