
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActRelationshipType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActRelationshipType">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="APND"/>
 *     &lt;enumeration value="ARR"/>
 *     &lt;enumeration value="AUTH"/>
 *     &lt;enumeration value="CAUS"/>
 *     &lt;enumeration value="CHRG"/>
 *     &lt;enumeration value="CIND"/>
 *     &lt;enumeration value="COMP"/>
 *     &lt;enumeration value="COST"/>
 *     &lt;enumeration value="COVBY"/>
 *     &lt;enumeration value="CREDIT"/>
 *     &lt;enumeration value="CTRLV"/>
 *     &lt;enumeration value="DEBIT"/>
 *     &lt;enumeration value="DEP"/>
 *     &lt;enumeration value="DOC"/>
 *     &lt;enumeration value="DRIV"/>
 *     &lt;enumeration value="ELNK"/>
 *     &lt;enumeration value="EVID"/>
 *     &lt;enumeration value="EXPL"/>
 *     &lt;enumeration value="FLFS"/>
 *     &lt;enumeration value="GEN"/>
 *     &lt;enumeration value="GEVL"/>
 *     &lt;enumeration value="GOAL"/>
 *     &lt;enumeration value="INST"/>
 *     &lt;enumeration value="ITEMSLOC"/>
 *     &lt;enumeration value="LIMIT"/>
 *     &lt;enumeration value="MFST"/>
 *     &lt;enumeration value="MITGT"/>
 *     &lt;enumeration value="MTCH"/>
 *     &lt;enumeration value="NAME"/>
 *     &lt;enumeration value="OBJC"/>
 *     &lt;enumeration value="OBJF"/>
 *     &lt;enumeration value="OCCR"/>
 *     &lt;enumeration value="OPTN"/>
 *     &lt;enumeration value="OREF"/>
 *     &lt;enumeration value="OUTC"/>
 *     &lt;enumeration value="PERT"/>
 *     &lt;enumeration value="PRCN"/>
 *     &lt;enumeration value="PREV"/>
 *     &lt;enumeration value="REFR"/>
 *     &lt;enumeration value="REFV"/>
 *     &lt;enumeration value="REV"/>
 *     &lt;enumeration value="RISK"/>
 *     &lt;enumeration value="RPLC"/>
 *     &lt;enumeration value="RSON"/>
 *     &lt;enumeration value="SAS"/>
 *     &lt;enumeration value="SCH"/>
 *     &lt;enumeration value="SEQL"/>
 *     &lt;enumeration value="SPRT"/>
 *     &lt;enumeration value="SPRTBND"/>
 *     &lt;enumeration value="SUBJ"/>
 *     &lt;enumeration value="SUCC"/>
 *     &lt;enumeration value="SUMM"/>
 *     &lt;enumeration value="TRIG"/>
 *     &lt;enumeration value="UPDT"/>
 *     &lt;enumeration value="VRXCRPT"/>
 *     &lt;enumeration value="XCRPT"/>
 *     &lt;enumeration value="XFRM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActRelationshipType")
@XmlEnum
public enum ActRelationshipType {

    APND,
    ARR,
    AUTH,
    CAUS,
    CHRG,
    CIND,
    COMP,
    COST,
    COVBY,
    CREDIT,
    CTRLV,
    DEBIT,
    DEP,
    DOC,
    DRIV,
    ELNK,
    EVID,
    EXPL,
    FLFS,
    GEN,
    GEVL,
    GOAL,
    INST,
    ITEMSLOC,
    LIMIT,
    MFST,
    MITGT,
    MTCH,
    NAME,
    OBJC,
    OBJF,
    OCCR,
    OPTN,
    OREF,
    OUTC,
    PERT,
    PRCN,
    PREV,
    REFR,
    REFV,
    REV,
    RISK,
    RPLC,
    RSON,
    SAS,
    SCH,
    SEQL,
    SPRT,
    SPRTBND,
    SUBJ,
    SUCC,
    SUMM,
    TRIG,
    UPDT,
    VRXCRPT,
    XCRPT,
    XFRM;

    public String value() {
        return name();
    }

    public static ActRelationshipType fromValue(String v) {
        return valueOf(v);
    }

}
