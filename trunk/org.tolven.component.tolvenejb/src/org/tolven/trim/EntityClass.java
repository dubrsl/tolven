
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntityClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EntityClass">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="ENT"/>
 *     &lt;enumeration value="RGRP"/>
 *     &lt;enumeration value="HCE"/>
 *     &lt;enumeration value="LIV"/>
 *     &lt;enumeration value="PSN"/>
 *     &lt;enumeration value="NLIV"/>
 *     &lt;enumeration value="ANM"/>
 *     &lt;enumeration value="MIC"/>
 *     &lt;enumeration value="PLNT"/>
 *     &lt;enumeration value="MAT"/>
 *     &lt;enumeration value="CHEM"/>
 *     &lt;enumeration value="FOOD"/>
 *     &lt;enumeration value="MMAT"/>
 *     &lt;enumeration value="CONT"/>
 *     &lt;enumeration value="HOLD"/>
 *     &lt;enumeration value="DEV"/>
 *     &lt;enumeration value="CER"/>
 *     &lt;enumeration value="MODDV"/>
 *     &lt;enumeration value="PLC"/>
 *     &lt;enumeration value="CITY"/>
 *     &lt;enumeration value="COUNTRY"/>
 *     &lt;enumeration value="COUNTY"/>
 *     &lt;enumeration value="PROVINCE"/>
 *     &lt;enumeration value="ORG"/>
 *     &lt;enumeration value="PUB"/>
 *     &lt;enumeration value="STATE"/>
 *     &lt;enumeration value="NAT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EntityClass")
@XmlEnum
public enum EntityClass {

    ENT,
    RGRP,
    HCE,
    LIV,
    PSN,
    NLIV,
    ANM,
    MIC,
    PLNT,
    MAT,
    CHEM,
    FOOD,
    MMAT,
    CONT,
    HOLD,
    DEV,
    CER,
    MODDV,
    PLC,
    CITY,
    COUNTRY,
    COUNTY,
    PROVINCE,
    ORG,
    PUB,
    STATE,
    NAT;

    public String value() {
        return name();
    }

    public static EntityClass fromValue(String v) {
        return valueOf(v);
    }

}
