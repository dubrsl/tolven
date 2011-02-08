
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParticipationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ParticipationType">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="ADM"/>
 *     &lt;enumeration value="ATND"/>
 *     &lt;enumeration value="AUT"/>
 *     &lt;enumeration value="AUTHEN"/>
 *     &lt;enumeration value="BBY"/>
 *     &lt;enumeration value="BEN"/>
 *     &lt;enumeration value="CAGNT"/>
 *     &lt;enumeration value="CALLBCK"/>
 *     &lt;enumeration value="CON"/>
 *     &lt;enumeration value="COV"/>
 *     &lt;enumeration value="CSM"/>
 *     &lt;enumeration value="CST"/>
 *     &lt;enumeration value="DEV"/>
 *     &lt;enumeration value="DIR"/>
 *     &lt;enumeration value="DIS"/>
 *     &lt;enumeration value="DIST"/>
 *     &lt;enumeration value="DON"/>
 *     &lt;enumeration value="DST"/>
 *     &lt;enumeration value="ELOC"/>
 *     &lt;enumeration value="ENT"/>
 *     &lt;enumeration value="ESC"/>
 *     &lt;enumeration value="GUAR"/>
 *     &lt;enumeration value="HLD"/>
 *     &lt;enumeration value="IND"/>
 *     &lt;enumeration value="INF"/>
 *     &lt;enumeration value="IRCP"/>
 *     &lt;enumeration value="LA"/>
 *     &lt;enumeration value="LOC"/>
 *     &lt;enumeration value="NOT"/>
 *     &lt;enumeration value="NRD"/>
 *     &lt;enumeration value="ORG"/>
 *     &lt;enumeration value="PART"/>
 *     &lt;enumeration value="PPRF"/>
 *     &lt;enumeration value="PRCP"/>
 *     &lt;enumeration value="PRD"/>
 *     &lt;enumeration value="PRF"/>
 *     &lt;enumeration value="RCT"/>
 *     &lt;enumeration value="RCV"/>
 *     &lt;enumeration value="RDV"/>
 *     &lt;enumeration value="REF"/>
 *     &lt;enumeration value="REFB"/>
 *     &lt;enumeration value="REFT"/>
 *     &lt;enumeration value="RESP"/>
 *     &lt;enumeration value="RML"/>
 *     &lt;enumeration value="SBJ"/>
 *     &lt;enumeration value="SPC"/>
 *     &lt;enumeration value="SPRF"/>
 *     &lt;enumeration value="TRANS"/>
 *     &lt;enumeration value="TRC"/>
 *     &lt;enumeration value="VIA"/>
 *     &lt;enumeration value="VRF"/>
 *     &lt;enumeration value="WIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ParticipationType")
@XmlEnum
public enum ParticipationType {

    ADM,
    ATND,
    AUT,
    AUTHEN,
    BBY,
    BEN,
    CAGNT,
    CALLBCK,
    CON,
    COV,
    CSM,
    CST,
    DEV,
    DIR,
    DIS,
    DIST,
    DON,
    DST,
    ELOC,
    ENT,
    ESC,
    GUAR,
    HLD,
    IND,
    INF,
    IRCP,
    LA,
    LOC,
    NOT,
    NRD,
    ORG,
    PART,
    PPRF,
    PRCP,
    PRD,
    PRF,
    RCT,
    RCV,
    RDV,
    REF,
    REFB,
    REFT,
    RESP,
    RML,
    SBJ,
    SPC,
    SPRF,
    TRANS,
    TRC,
    VIA,
    VRF,
    WIT;

    public String value() {
        return name();
    }

    public static ParticipationType fromValue(String v) {
        return valueOf(v);
    }

}
