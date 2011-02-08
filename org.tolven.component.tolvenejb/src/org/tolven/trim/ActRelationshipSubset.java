
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActRelationshipSubset.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActRelationshipSubset">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="MAX"/>
 *     &lt;enumeration value="MIN"/>
 *     &lt;enumeration value="SUM"/>
 *     &lt;enumeration value="FUTURE"/>
 *     &lt;enumeration value="LAST"/>
 *     &lt;enumeration value="NEXT"/>
 *     &lt;enumeration value="FUTSUM"/>
 *     &lt;enumeration value="PAST"/>
 *     &lt;enumeration value="FIRST"/>
 *     &lt;enumeration value="RECENT"/>
 *     &lt;enumeration value="PREVSUM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActRelationshipSubset")
@XmlEnum
public enum ActRelationshipSubset {

    MAX,
    MIN,
    SUM,
    FUTURE,
    LAST,
    NEXT,
    FUTSUM,
    PAST,
    FIRST,
    RECENT,
    PREVSUM;

    public String value() {
        return name();
    }

    public static ActRelationshipSubset fromValue(String v) {
        return valueOf(v);
    }

}
