
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActClass">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="ACCM"/>
 *     &lt;enumeration value="ACCT"/>
 *     &lt;enumeration value="ACSN"/>
 *     &lt;enumeration value="ACT"/>
 *     &lt;enumeration value="ACTN"/>
 *     &lt;enumeration value="ADJUD"/>
 *     &lt;enumeration value="ALRT"/>
 *     &lt;enumeration value="BATTERY"/>
 *     &lt;enumeration value="CACT"/>
 *     &lt;enumeration value="CASE"/>
 *     &lt;enumeration value="CATEGORY"/>
 *     &lt;enumeration value="CDALVLONE"/>
 *     &lt;enumeration value="CLNTRL"/>
 *     &lt;enumeration value="CLUSTER"/>
 *     &lt;enumeration value="CNOD"/>
 *     &lt;enumeration value="CNTRCT"/>
 *     &lt;enumeration value="COMPOSITION"/>
 *     &lt;enumeration value="COND"/>
 *     &lt;enumeration value="CONS"/>
 *     &lt;enumeration value="CONTREG"/>
 *     &lt;enumeration value="COV"/>
 *     &lt;enumeration value="CTTEVENT"/>
 *     &lt;enumeration value="DGIMG"/>
 *     &lt;enumeration value="DIET"/>
 *     &lt;enumeration value="DISPACT"/>
 *     &lt;enumeration value="DOC"/>
 *     &lt;enumeration value="DOCBODY"/>
 *     &lt;enumeration value="DOCCLIN"/>
 *     &lt;enumeration value="DOCSECT"/>
 *     &lt;enumeration value="EHR"/>
 *     &lt;enumeration value="ENC"/>
 *     &lt;enumeration value="ENTRY"/>
 *     &lt;enumeration value="EXTRACT"/>
 *     &lt;enumeration value="FCNTRCT"/>
 *     &lt;enumeration value="FOLDER"/>
 *     &lt;enumeration value="INC"/>
 *     &lt;enumeration value="INFO"/>
 *     &lt;enumeration value="INFRM"/>
 *     &lt;enumeration value="INVE"/>
 *     &lt;enumeration value="INVSTG"/>
 *     &lt;enumeration value="LIST"/>
 *     &lt;enumeration value="MPROT"/>
 *     &lt;enumeration value="OBS"/>
 *     &lt;enumeration value="OBSCOR"/>
 *     &lt;enumeration value="OBSSER"/>
 *     &lt;enumeration value="ORGANIZER"/>
 *     &lt;enumeration value="OUTB"/>
 *     &lt;enumeration value="PCPR"/>
 *     &lt;enumeration value="PROC"/>
 *     &lt;enumeration value="REG"/>
 *     &lt;enumeration value="REV"/>
 *     &lt;enumeration value="ROIBND"/>
 *     &lt;enumeration value="ROIOVL"/>
 *     &lt;enumeration value="SBADM"/>
 *     &lt;enumeration value="SPCOBS"/>
 *     &lt;enumeration value="SPCTRT"/>
 *     &lt;enumeration value="SPECCOLLECT"/>
 *     &lt;enumeration value="SPLY"/>
 *     &lt;enumeration value="STC"/>
 *     &lt;enumeration value="STORE"/>
 *     &lt;enumeration value="SUBST"/>
 *     &lt;enumeration value="TOPIC"/>
 *     &lt;enumeration value="TRNS"/>
 *     &lt;enumeration value="VERIF"/>
 *     &lt;enumeration value="XACT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActClass")
@XmlEnum
public enum ActClass {

    ACCM,
    ACCT,
    ACSN,
    ACT,
    ACTN,
    ADJUD,
    ALRT,
    BATTERY,
    CACT,
    CASE,
    CATEGORY,
    CDALVLONE,
    CLNTRL,
    CLUSTER,
    CNOD,
    CNTRCT,
    COMPOSITION,
    COND,
    CONS,
    CONTREG,
    COV,
    CTTEVENT,
    DGIMG,
    DIET,
    DISPACT,
    DOC,
    DOCBODY,
    DOCCLIN,
    DOCSECT,
    EHR,
    ENC,
    ENTRY,
    EXTRACT,
    FCNTRCT,
    FOLDER,
    INC,
    INFO,
    INFRM,
    INVE,
    INVSTG,
    LIST,
    MPROT,
    OBS,
    OBSCOR,
    OBSSER,
    ORGANIZER,
    OUTB,
    PCPR,
    PROC,
    REG,
    REV,
    ROIBND,
    ROIOVL,
    SBADM,
    SPCOBS,
    SPCTRT,
    SPECCOLLECT,
    SPLY,
    STC,
    STORE,
    SUBST,
    TOPIC,
    TRNS,
    VERIF,
    XACT;

    public String value() {
        return name();
    }

    public static ActClass fromValue(String v) {
        return valueOf(v);
    }

}
