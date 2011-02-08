
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddressPartType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AddressPartType">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="AL"/>
 *     &lt;enumeration value="ADL"/>
 *     &lt;enumeration value="UNIT"/>
 *     &lt;enumeration value="UNID"/>
 *     &lt;enumeration value="DAL"/>
 *     &lt;enumeration value="DINSTA"/>
 *     &lt;enumeration value="DINSTQ"/>
 *     &lt;enumeration value="DINST"/>
 *     &lt;enumeration value="DMOD"/>
 *     &lt;enumeration value="DMODID"/>
 *     &lt;enumeration value="SAL"/>
 *     &lt;enumeration value="BNR"/>
 *     &lt;enumeration value="BNN"/>
 *     &lt;enumeration value="BNS"/>
 *     &lt;enumeration value="STR"/>
 *     &lt;enumeration value="STB"/>
 *     &lt;enumeration value="STTYP"/>
 *     &lt;enumeration value="DIR"/>
 *     &lt;enumeration value="INT"/>
 *     &lt;enumeration value="CAR"/>
 *     &lt;enumeration value="CEN"/>
 *     &lt;enumeration value="CNT"/>
 *     &lt;enumeration value="CPA"/>
 *     &lt;enumeration value="DEL"/>
 *     &lt;enumeration value="CTY"/>
 *     &lt;enumeration value="POB"/>
 *     &lt;enumeration value="ZIP"/>
 *     &lt;enumeration value="PRE"/>
 *     &lt;enumeration value="STA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AddressPartType")
@XmlEnum
public enum AddressPartType {

    AL,
    ADL,
    UNIT,
    UNID,
    DAL,
    DINSTA,
    DINSTQ,
    DINST,
    DMOD,
    DMODID,
    SAL,
    BNR,
    BNN,
    BNS,
    STR,
    STB,
    STTYP,
    DIR,
    INT,
    CAR,
    CEN,
    CNT,
    CPA,
    DEL,
    CTY,
    POB,
    ZIP,
    PRE,
    STA;

    public String value() {
        return name();
    }

    public static AddressPartType fromValue(String v) {
        return valueOf(v);
    }

}
