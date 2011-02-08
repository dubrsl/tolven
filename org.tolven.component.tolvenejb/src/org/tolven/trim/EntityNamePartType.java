
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntityNamePartType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EntityNamePartType">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="DEL"/>
 *     &lt;enumeration value="FAM"/>
 *     &lt;enumeration value="GIV"/>
 *     &lt;enumeration value="PFX"/>
 *     &lt;enumeration value="SFX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EntityNamePartType")
@XmlEnum
public enum EntityNamePartType {

    DEL,
    FAM,
    GIV,
    PFX,
    SFX;

    public String value() {
        return name();
    }

    public static EntityNamePartType fromValue(String v) {
        return valueOf(v);
    }

}
