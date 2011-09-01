
package org.tolven.trim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActStatus">
 *   &lt;restriction base="{urn:tolven-org:trim:4.0}cs">
 *     &lt;enumeration value="nullified"/>
 *     &lt;enumeration value="obsolete"/>
 *     &lt;enumeration value="normal"/>
 *     &lt;enumeration value="aborted"/>
 *     &lt;enumeration value="active"/>
 *     &lt;enumeration value="cancelled"/>
 *     &lt;enumeration value="completed"/>
 *     &lt;enumeration value="held"/>
 *     &lt;enumeration value="new"/>
 *     &lt;enumeration value="suspended"/>
 *     &lt;enumeration value="resolved"/>
 *     &lt;enumeration value="inactive"/>
 *     &lt;enumeration value="edit"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActStatus")
@XmlEnum
public enum ActStatus {

    @XmlEnumValue("nullified")
    NULLIFIED("nullified"),
    @XmlEnumValue("obsolete")
    OBSOLETE("obsolete"),
    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("aborted")
    ABORTED("aborted"),
    @XmlEnumValue("active")
    ACTIVE("active"),
    @XmlEnumValue("cancelled")
    CANCELLED("cancelled"),
    @XmlEnumValue("completed")
    COMPLETED("completed"),
    @XmlEnumValue("held")
    HELD("held"),
    @XmlEnumValue("new")
    NEW("new"),
    @XmlEnumValue("suspended")
    SUSPENDED("suspended"),
    @XmlEnumValue("resolved")
    RESOLVED("resolved"),
    @XmlEnumValue("inactive")
    INACTIVE("inactive"),
    @XmlEnumValue("edit")
    EDIT("edit");
	/** CCHIT merge
	 * Created a new transition EDIT
	 * @author Vineetha
	 * added on 1/21/2011
	 */	
    private final String value;

    ActStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActStatus fromValue(String v) {
        for (ActStatus c: ActStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
