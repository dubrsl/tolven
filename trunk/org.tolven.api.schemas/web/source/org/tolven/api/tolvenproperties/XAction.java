
package org.tolven.api.tolvenproperties;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XAction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="XAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="query"/>
 *     &lt;enumeration value="create"/>
 *     &lt;enumeration value="update"/>
 *     &lt;enumeration value="delete"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "XAction")
@XmlEnum
public enum XAction {

    @XmlEnumValue("query")
    QUERY("query"),
    @XmlEnumValue("create")
    CREATE("create"),
    @XmlEnumValue("update")
    UPDATE("update"),
    @XmlEnumValue("delete")
    DELETE("delete");
    private final String value;

    XAction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XAction fromValue(String v) {
        for (XAction c: XAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
