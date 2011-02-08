
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConcreteDatatype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ConcreteDatatype">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="BL"/>
 *     &lt;enumeration value="TS"/>
 *     &lt;enumeration value="ST"/>
 *     &lt;enumeration value="ED"/>
 *     &lt;enumeration value="IVLTS"/>
 *     &lt;enumeration value="PQ"/>
 *     &lt;enumeration value="INT"/>
 *     &lt;enumeration value="REAL"/>
 *     &lt;enumeration value="II"/>
 *     &lt;enumeration value="CD"/>
 *     &lt;enumeration value="CE"/>
 *     &lt;enumeration value="CS"/>
 *     &lt;enumeration value="CO"/>
 *     &lt;enumeration value="IVLPQ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ConcreteDatatype")
@XmlEnum
public enum ConcreteDatatype {

    BL,
    TS,
    ST,
    ED,
    IVLTS,
    PQ,
    INT,
    REAL,
    II,
    CD,
    CE,
    CS,
    CO,
    IVLPQ;

    public String value() {
        return name();
    }

    public static ConcreteDatatype fromValue(String v) {
        return valueOf(v);
    }

}
