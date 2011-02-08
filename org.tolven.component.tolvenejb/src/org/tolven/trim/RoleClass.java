
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoleClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RoleClass">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="ACCESS"/>
 *     &lt;enumeration value="ACTI"/>
 *     &lt;enumeration value="ACTM"/>
 *     &lt;enumeration value="ADMM"/>
 *     &lt;enumeration value="ADTV"/>
 *     &lt;enumeration value="AFFL"/>
 *     &lt;enumeration value="AGNT"/>
 *     &lt;enumeration value="ALQT"/>
 *     &lt;enumeration value="ASSIGNED"/>
 *     &lt;enumeration value="BASE"/>
 *     &lt;enumeration value="BIRTHPL"/>
 *     &lt;enumeration value="CAREGIVER"/>
 *     &lt;enumeration value="CASEBJ"/>
 *     &lt;enumeration value="CIT"/>
 *     &lt;enumeration value="CLAIM"/>
 *     &lt;enumeration value="COLR"/>
 *     &lt;enumeration value="COMPAR"/>
 *     &lt;enumeration value="CON"/>
 *     &lt;enumeration value="CONT"/>
 *     &lt;enumeration value="COVPTY"/>
 *     &lt;enumeration value="CRINV"/>
 *     &lt;enumeration value="CRSPNSR"/>
 *     &lt;enumeration value="DSDLOC"/>
 *     &lt;enumeration value="DST"/>
 *     &lt;enumeration value="ECON"/>
 *     &lt;enumeration value="EMP"/>
 *     &lt;enumeration value="EXPR"/>
 *     &lt;enumeration value="FLVR"/>
 *     &lt;enumeration value="GEN"/>
 *     &lt;enumeration value="GRIC"/>
 *     &lt;enumeration value="GUAR"/>
 *     &lt;enumeration value="GUARD"/>
 *     &lt;enumeration value="HLD"/>
 *     &lt;enumeration value="HLTHCHRT"/>
 *     &lt;enumeration value="IACT"/>
 *     &lt;enumeration value="IDENT"/>
 *     &lt;enumeration value="INDIV"/>
 *     &lt;enumeration value="INGR"/>
 *     &lt;enumeration value="INST"/>
 *     &lt;enumeration value="INVSBJ"/>
 *     &lt;enumeration value="ISDLOC"/>
 *     &lt;enumeration value="ISLT"/>
 *     &lt;enumeration value="LIC"/>
 *     &lt;enumeration value="LOCE"/>
 *     &lt;enumeration value="MANU"/>
 *     &lt;enumeration value="MBR"/>
 *     &lt;enumeration value="MIL"/>
 *     &lt;enumeration value="MNT"/>
 *     &lt;enumeration value="NAMED"/>
 *     &lt;enumeration value="NOK"/>
 *     &lt;enumeration value="OWN"/>
 *     &lt;enumeration value="PART"/>
 *     &lt;enumeration value="PAT"/>
 *     &lt;enumeration value="PAYEE"/>
 *     &lt;enumeration value="PAYOR"/>
 *     &lt;enumeration value="POLHOLD"/>
 *     &lt;enumeration value="PROG"/>
 *     &lt;enumeration value="PROV"/>
 *     &lt;enumeration value="PRS"/>
 *     &lt;enumeration value="PRSV"/>
 *     &lt;enumeration value="QUAL"/>
 *     &lt;enumeration value="RESBJ"/>
 *     &lt;enumeration value="RET"/>
 *     &lt;enumeration value="RGPR"/>
 *     &lt;enumeration value="ROL"/>
 *     &lt;enumeration value="SAME"/>
 *     &lt;enumeration value="SDLOC"/>
 *     &lt;enumeration value="SGNOFF"/>
 *     &lt;enumeration value="SPEC"/>
 *     &lt;enumeration value="SPNSR"/>
 *     &lt;enumeration value="STBL"/>
 *     &lt;enumeration value="STD"/>
 *     &lt;enumeration value="STOR"/>
 *     &lt;enumeration value="SUBS"/>
 *     &lt;enumeration value="SUBSCR"/>
 *     &lt;enumeration value="SUBY"/>
 *     &lt;enumeration value="TERR"/>
 *     &lt;enumeration value="THER"/>
 *     &lt;enumeration value="UNDWRT"/>
 *     &lt;enumeration value="WRTE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RoleClass")
@XmlEnum
public enum RoleClass {

    ACCESS,
    ACTI,
    ACTM,
    ADMM,
    ADTV,
    AFFL,
    AGNT,
    ALQT,
    ASSIGNED,
    BASE,
    BIRTHPL,
    CAREGIVER,
    CASEBJ,
    CIT,
    CLAIM,
    COLR,
    COMPAR,
    CON,
    CONT,
    COVPTY,
    CRINV,
    CRSPNSR,
    DSDLOC,
    DST,
    ECON,
    EMP,
    EXPR,
    FLVR,
    GEN,
    GRIC,
    GUAR,
    GUARD,
    HLD,
    HLTHCHRT,
    IACT,
    IDENT,
    INDIV,
    INGR,
    INST,
    INVSBJ,
    ISDLOC,
    ISLT,
    LIC,
    LOCE,
    MANU,
    MBR,
    MIL,
    MNT,
    NAMED,
    NOK,
    OWN,
    PART,
    PAT,
    PAYEE,
    PAYOR,
    POLHOLD,
    PROG,
    PROV,
    PRS,
    PRSV,
    QUAL,
    RESBJ,
    RET,
    RGPR,
    ROL,
    SAME,
    SDLOC,
    SGNOFF,
    SPEC,
    SPNSR,
    STBL,
    STD,
    STOR,
    SUBS,
    SUBSCR,
    SUBY,
    TERR,
    THER,
    UNDWRT,
    WRTE;

    public String value() {
        return name();
    }

    public static RoleClass fromValue(String v) {
        return valueOf(v);
    }

}
