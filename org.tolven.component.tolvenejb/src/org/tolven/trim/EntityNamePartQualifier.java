
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntityNamePartQualifier.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EntityNamePartQualifier">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="LS"/>
 *     &lt;enumeration value="IN"/>
 *     &lt;enumeration value="TITLE"/>
 *     &lt;enumeration value="AC"/>
 *     &lt;enumeration value="NB"/>
 *     &lt;enumeration value="PR"/>
 *     &lt;enumeration value="VV"/>
 *     &lt;enumeration value="AD"/>
 *     &lt;enumeration value="BR"/>
 *     &lt;enumeration value="SP"/>
 *     &lt;enumeration value="CL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EntityNamePartQualifier")
@XmlEnum
public enum EntityNamePartQualifier {

    LS,
    IN,
    TITLE,
    AC,
    NB,
    PR,
    VV,
    AD,
    BR,
    SP,
    CL;

    public String value() {
        return name();
    }

    public static EntityNamePartQualifier fromValue(String v) {
        return valueOf(v);
    }

}
