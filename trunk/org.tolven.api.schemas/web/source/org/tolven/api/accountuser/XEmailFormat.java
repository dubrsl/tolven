
package org.tolven.api.accountuser;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XEmailFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="XEmailFormat">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="html"/>
 *     &lt;enumeration value="text"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "XEmailFormat")
@XmlEnum
public enum XEmailFormat {

    @XmlEnumValue("html")
    HTML("html"),
    @XmlEnumValue("text")
    TEXT("text");
    private final String value;

    XEmailFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XEmailFormat fromValue(String v) {
        for (XEmailFormat c: XEmailFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
