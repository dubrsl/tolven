
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntityDeterminer.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EntityDeterminer">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="INSTANCE"/>
 *     &lt;enumeration value="KIND"/>
 *     &lt;enumeration value="QUANTIFIED_KIND"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EntityDeterminer")
@XmlEnum
public enum EntityDeterminer {

    INSTANCE,
    KIND,
    QUANTIFIED_KIND;

    public String value() {
        return name();
    }

    public static EntityDeterminer fromValue(String v) {
        return valueOf(v);
    }

}
