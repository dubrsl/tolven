
package org.tolven.menuStructure;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColumnType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ColumnType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="date"/>
 *     &lt;enumeration value="long"/>
 *     &lt;enumeration value="double"/>
 *     &lt;enumeration value="placeholder"/>
 *     &lt;enumeration value="AD"/>
 *     &lt;enumeration value="CD"/>
 *     &lt;enumeration value="ED"/>
 *     &lt;enumeration value="INT"/>
 *     &lt;enumeration value="PQ"/>
 *     &lt;enumeration value="REAL"/>
 *     &lt;enumeration value="TEL"/>
 *     &lt;enumeration value="TS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ColumnType")
@XmlEnum
public enum ColumnType {

    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("date")
    DATE("date"),
    @XmlEnumValue("long")
    LONG("long"),
    @XmlEnumValue("double")
    DOUBLE("double"),
    @XmlEnumValue("placeholder")
    PLACEHOLDER("placeholder"),
    AD("AD"),
    CD("CD"),
    ED("ED"),
    INT("INT"),
    PQ("PQ"),
    REAL("REAL"),
    TEL("TEL"),
    TS("TS");
    private final String value;

    ColumnType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColumnType fromValue(String v) {
        for (ColumnType c: ColumnType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
