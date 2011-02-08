
package org.tolven.api.accountuser;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XQuery.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="XQuery">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="properties"/>
 *     &lt;enumeration value="accountUser"/>
 *     &lt;enumeration value="accountUser.account"/>
 *     &lt;enumeration value="accountUser.accountType"/>
 *     &lt;enumeration value="accountUser.tolvenUser"/>
 *     &lt;enumeration value="accountUser.tolvenUser.tolvenPerson"/>
 *     &lt;enumeration value="accountTypes"/>
 *     &lt;enumeration value="accountType.properties"/>
 *     &lt;enumeration value="accounts"/>
 *     &lt;enumeration value="account.properties"/>
 *     &lt;enumeration value="account.roles"/>
 *     &lt;enumeration value="accountUsers"/>
 *     &lt;enumeration value="accountUser.properties"/>
 *     &lt;enumeration value="accountUser.roles"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "XQuery")
@XmlEnum
public enum XQuery {

    @XmlEnumValue("properties")
    PROPERTIES("properties"),
    @XmlEnumValue("accountUser")
    ACCOUNT_USER("accountUser"),
    @XmlEnumValue("accountUser.account")
    ACCOUNT_USER_ACCOUNT("accountUser.account"),
    @XmlEnumValue("accountUser.accountType")
    ACCOUNT_USER_ACCOUNT_TYPE("accountUser.accountType"),
    @XmlEnumValue("accountUser.tolvenUser")
    ACCOUNT_USER_TOLVEN_USER("accountUser.tolvenUser"),
    @XmlEnumValue("accountUser.tolvenUser.tolvenPerson")
    ACCOUNT_USER_TOLVEN_USER_TOLVEN_PERSON("accountUser.tolvenUser.tolvenPerson"),
    @XmlEnumValue("accountTypes")
    ACCOUNT_TYPES("accountTypes"),
    @XmlEnumValue("accountType.properties")
    ACCOUNT_TYPE_PROPERTIES("accountType.properties"),
    @XmlEnumValue("accounts")
    ACCOUNTS("accounts"),
    @XmlEnumValue("account.properties")
    ACCOUNT_PROPERTIES("account.properties"),
    @XmlEnumValue("account.roles")
    ACCOUNT_ROLES("account.roles"),
    @XmlEnumValue("accountUsers")
    ACCOUNT_USERS("accountUsers"),
    @XmlEnumValue("accountUser.properties")
    ACCOUNT_USER_PROPERTIES("accountUser.properties"),
    @XmlEnumValue("accountUser.roles")
    ACCOUNT_USER_ROLES("accountUser.roles");
    private final String value;

    XQuery(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XQuery fromValue(String v) {
        for (XQuery c: XQuery.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
